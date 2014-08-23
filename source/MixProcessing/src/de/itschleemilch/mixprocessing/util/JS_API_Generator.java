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
package de.itschleemilch.mixprocessing.util;

import de.itschleemilch.mixprocessing.script.ScriptingApi;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;

/**
 * Tool to generate a remote API for Javascript.
 * 
 * Usage: Compile the Class ScriptingAPI with javac parameter "-parameters"
 * to geht the full parameter names instead of "arg0, arg1, ...". Run this code.
 *
 * @author Sebastian Schleemilch
 * @see ScriptingApi
 */
public class JS_API_Generator {
    private final Method[] methods;

    public JS_API_Generator() {
        println("/* START OF MIXPROCESSING JAVASCRIPT LIBRARY */");
        /* Output static header */
        outputStaticResource("res/JS_API_STATIC_HEADER.js");
        /* Collect API methods without plain object methods */
        methods = ScriptingApi.class.getDeclaredMethods();
        sortMethods();
        
        /* Analysis */
        for(Method method : methods) {
            /*
            String mName = method.getName();
            Parameter[] params = method.getParameters();
            for(Parameter param : params) {
                String pName = param.getName();
                String type = param.getType().getName();
            }
            */
            outputMethodComment(method);
            outputMethod(method);
            println();
        }
        outputStaticResource("res/JS_API_STATIC_FOOTER.js");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd', 'HH:mm");
        println("/* DATE OF GENERATION:", dateFormat.format(new Date()), " */");
    }
    
    /**
     * 
     * @param method 
     */
    private void outputMethodComment(Method method) {
        println("/**");
        
        String mName = method.getName();
        println(" * Method: ", mName);
        println(" *");
        
        Parameter[] params = method.getParameters();
        for(Parameter param : params) {
            String pName = param.getName();
            String pType = getShortType( param.getType() );
            println(" * @param ", pName, " type=", pType);
        }
        
        String pReturnType = getShortType( method.getReturnType() );
        println(" * @return ", pReturnType);
        println("*/");
    }
    
    private void outputMethod(Method method) {
        String mName = method.getName();
        print("Api.", mName, " = function (");
        //print("function ", mName, "( ");
        
        /* parameter list */
        Parameter[] params = method.getParameters();
        boolean firstElement = true;
        for(Parameter param : params) {
            String pName = param.getName();
            if(firstElement)
                firstElement = false;
            else
                print(", ");
            print(pName);
        }
        /* add callback function */
        if(!firstElement)
            print(", ");
        print("callbackFunction");
        
        println(") {");
        println("\t", "\"use strict\";");
        println("\t", "var request = \"", generateApiCall(method), "\";");
        println("\t", "this.apiCall(request, callbackFunction);");
        println("};");
    }
    
    private String generateApiCall(Method method) {
        StringBuilder out = new StringBuilder();
        out.append("Api.").append(method.getName()).append("(");
        
        /* parameter list */
        Parameter[] params = method.getParameters();
        boolean firstElement = true;
        for(Parameter param : params) {
            String pName = param.getName();
            Class<?> pType = param.getType();
            if(firstElement)
                firstElement = false;
            else
                out.append(", ");
            if(pType.equals(String.class))
                out.append("'\" + ").append(pName).append(" + \"'");
            else
                out.append("\" + ").append(pName).append(" + \"");
        }
        out.append(");");
        return out.toString();
    }
    
    /**
     * Outputs primitive types and shorted Classes (without package name)
     * @param type
     * @return 
     */
    private String getShortType(Class<?> type) {
        String typeName = type.getName();
        if(typeName.contains("."))
                return typeName.substring(typeName.lastIndexOf('.')+1);
        else
            return typeName;
    }
    
    private void sortMethods() {
        Comparator<Method> methodComp = new Comparator<Method>() {
            @Override
            public int compare(Method o1, Method o2) {
                return o1.getName().compareTo(o2.getName());
            }
        };
        Arrays.sort(methods, methodComp);
    }
    
    private void outputStaticResource(String path) {
        InputStream headerIn = getClass().getResourceAsStream(path);
        try {
            BufferedReader reader = new BufferedReader(
                new InputStreamReader(headerIn) );
            String line;
            while( (line=reader.readLine()) != null ) {
                println(line);
            }
        }
        catch(IOException e) {
            e.printStackTrace(System.err);
        }
        finally {
            try {
                headerIn.close();
            }
            catch(IOException e) {
                e.printStackTrace(System.err);
            }
        }
    }
    
    private void println(String ... args) {
        for (String string : args) {
            System.out.print(string);
        }
        System.out.println();
    }
    
    private void print(String ... args) {
        for (String string : args) {
            System.out.print(string);
        }
    }
    
    
    public static void main(String ... args) {
        new JS_API_Generator();
    }
    
}
