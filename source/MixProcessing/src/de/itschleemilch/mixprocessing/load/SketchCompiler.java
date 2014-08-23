/*
 MixProcessing - Live Mixing of Processing Sketches 
 https://github.com/itschleemilch/MixProcessing

 Copyright (c) 2014 Sebastian Schleemilch

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.itschleemilch.mixprocessing.load;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

/**
 * Compiles a Processing Sketch by it's given project folder or pde File.
 * Known Issue: Can not handle multi pde-file projects.
 *
 * @see <a href="http://openbook.galileocomputing.de/java7/1507_19_002.html">Programme mit der Compiler API übersetzen</a>
 * @author Sebastian Schleemilch
 */
public class SketchCompiler {

    public SketchCompiler() {
    }
    
    /**
     * Compiles a Processing Sketch in memory and returns the generated class.
     * @param pdeFile Sketch's project folder (containing .pde), or pde File
     * 
     * @return generated class of the sketch
     */
    public final Class<?> compileSketch(File pdeFile) {
        if(pdeFile.isDirectory())
        {
            pdeFile = new File(pdeFile, pdeFile.getName()+".pde");
        }
        if(pdeFile.exists() && pdeFile.isFile()) {
            final String className = getClassName(pdeFile);

            // Load Sketch's code:
            StringBuilder pdeText = new StringBuilder();
            try {
                List<String> lines = Files.readAllLines(pdeFile.toPath());
                for(String line : lines) {
                    pdeText.append(line);
                    pdeText.append('\n');
                }
            } catch (IOException e) {
                e.printStackTrace(System.err);
            }


            final String javaCode = preprocessSketch(className, pdeText.toString());
            final Class<?> compiled = compileCode(className, javaCode);
            
            if(compiled != null && 
                    compiled.getSuperclass().equals(PApplet2.class)) {
                return compiled;
            }
            else {
                return null;
            }
        }
        else {
            return null;
        }
    }
    
    /**
     * Preprocesses the source code of an sketch to make a full java class
     * out of it.
     * 
     * @param pdeSource plain processing code
     * @return java programm code
     */
    private String preprocessSketch(String className, String pdeSource) {
        final StringBuilder source = new StringBuilder();
        ArrayList<String> importDeclarations = new ArrayList<>();
        
        // remove all comments
        pdeSource = pdeSource.replaceAll("//.+", ""); // single line comments
        pdeSource = pdeSource.replaceAll( 
                "(?s)" + Pattern.quote("/*") + ".+?" + Pattern.quote("*/"), 
                ""); // multi line comments
        // fix false float format (without ending f):
        pdeSource = pdeSource.replaceAll("(\\d+\\.\\d+)([^fd])", "$1f$2");
        // collect all imports
        Pattern importPattern = Pattern.compile("import (.+?);");
        Matcher imports = importPattern.matcher(pdeSource);
        while(imports.find()) {
            importDeclarations.add( imports.group(1).trim() );
        }
        // remove all imports
        pdeSource = pdeSource.replaceAll("import .+?;", "");
        // remove all visibility modifiers
        pdeSource = pdeSource.replaceAll("(public|private|protected)", "");
        // set all methods public of type void
        Pattern publicReplacePattern = Pattern.compile("void\\s+(\\w+?)\\s*\\(", Pattern.MULTILINE);
        Matcher m1 = publicReplacePattern.matcher(pdeSource);
        String publicDeclared = m1.replaceAll("public void $1\\(");
        
        /* Generate Java source code */
        source.append( "import processing.core.*;\n" );
        for(String importItem : importDeclarations) {
            source.append("import ").append(importItem).append(";\n");
        }
        source.append( String.format("public class %s extends de.itschleemilch.mixprocessing.load.PApplet2 {\n", className) );
        source.append( publicDeclared );
        source.append("\n}");
        
        return source.toString();
    }
    
    /**
     * Returns class name from given pde File
     * @param pdeFile
     * @return 
     */
    private String getClassName(File pdeFile)
    {
        String n = pdeFile.getName();
        return n.substring(0, n.lastIndexOf('.'));
    }
    
    /**
     * Uses the Java Compiler API to compile a given code in memory. 
     * @param className
     * @param src
     * @return compiled class or null
     */
    private Class<?> compileCode(String className, String src) {
        JavaFileManager fileManager = null;
        try {
            JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
            MemClassLoader classLoader = new MemClassLoader();
            fileManager = new MemJavaFileManager( compiler, classLoader );
            JavaFileObject javaFile = new StringJavaFileObject( className, src );
            Collection<JavaFileObject> units = Collections.singleton( javaFile );
            JavaCompiler.CompilationTask task = compiler.getTask( null, fileManager, null, null, null, units );
            task.call();
            return Class.forName( className, true, classLoader );
        } 
        catch (ClassNotFoundException e) {
            //e.printStackTrace(System.err);
            System.err.println("Error while compiling: " + className);
            System.err.flush();
            System.err.println(src);
            return null;
        }
        finally {
            if(fileManager != null) {
                try {
                    fileManager.close();
                } catch (IOException e) {
                }
            }
        }
    }
    
    private class MemJavaFileManager extends
            ForwardingJavaFileManager<StandardJavaFileManager> {

        private final MemClassLoader classLoader;

        public MemJavaFileManager(JavaCompiler compiler, MemClassLoader classLoader) {
            super(compiler.getStandardFileManager(null, null, null));

            this.classLoader = classLoader;
        }

        @Override
        public JavaFileObject getJavaFileForOutput(Location location,
                String className,
                JavaFileObject.Kind kind,
                FileObject sibling) {
            MemJavaFileObject fileObject = new MemJavaFileObject(className);
            classLoader.addClassFile(fileObject);
            return fileObject;
        }
    }

    private class MemJavaFileObject extends SimpleJavaFileObject {

        private final ByteArrayOutputStream baos = new ByteArrayOutputStream(8192);
        private final String className;

        MemJavaFileObject(String className) {
            super(URI.create("string:///" + className.replace('.', '/') + Kind.CLASS.extension),
                    Kind.CLASS);
            this.className = className;
        }

        String getClassName() {
            return className;
        }

        byte[] getClassBytes() {
            return baos.toByteArray();
        }

        @Override
        public OutputStream openOutputStream() {
            return baos;
        }
    }

    private class MemClassLoader extends ClassLoader {

        private final Map<String, MemJavaFileObject> classFiles = new HashMap<>();

        public MemClassLoader() {
            super(ClassLoader.getSystemClassLoader());
        }

        public void addClassFile(MemJavaFileObject memJavaFileObject) {
            classFiles.put(memJavaFileObject.getClassName(), memJavaFileObject);
        }

        @Override
        protected Class<?> findClass(String name) throws ClassNotFoundException {
            MemJavaFileObject fileObject = classFiles.get(name);

            if (fileObject != null) {
                byte[] bytes = fileObject.getClassBytes();
                return defineClass(name, bytes, 0, bytes.length);
            }

            return super.findClass(name);
        }
    }
    
    private class StringJavaFileObject extends SimpleJavaFileObject {

        private final CharSequence code;

        public StringJavaFileObject(String name, CharSequence code) {
            super(URI.create("string:///" + name.replace('.', '/') + Kind.SOURCE.extension),
                    Kind.SOURCE);
            this.code = code;
        }

        @Override
        public CharSequence getCharContent(boolean ignoreEncodingErrors) {
            return code;
        }
    }
}
