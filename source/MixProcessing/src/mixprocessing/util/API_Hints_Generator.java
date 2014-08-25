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
package mixprocessing.util;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.Comparator;
import mixprocessing.script.ApiMethodInfo;
import mixprocessing.script.ScriptingApi;

/**
 * Programm to generate the XML file for describing the insert-menu.
 * MixProcessing has to be compiled with the -parameters option.
 * Only works on Java 8+ machines.
 * Warning: Netbean's 'compile on save' option must be disabled.
 *
 * @author Sebastian Schleemilch
 * @see ScriptingApi
 */
public class API_Hints_Generator {
    
    public static Comparator<Method> getFieldComperator() {
        return new Comparator<Method>() {
            @Override
            public int compare(Method o1, Method o2) {
                final ApiMethodInfo info1 = o1.getAnnotation(ApiMethodInfo.class);
                final ApiMethodInfo info2 = o2.getAnnotation(ApiMethodInfo.class);
                String cat1 = "_Unsorted";
                String cat2 = "_Unsorted";
                String desc1 = o1.getName();
                String desc2 = o2.getName();
                if(info1 != null && info2 != null) {
                    cat1 = info1.category();
                    desc1 = info1.description();
                    cat2 = info2.category();
                    desc2 = info2.description();
                }
                else {
                    if(info1 == null) {
                        System.err.printf("Method has no @ApiMethodInfo annotation: $s()\n", o1.getName());
                    }
                    if(info2 == null) {
                        System.err.printf("Method has no @ApiMethodInfo annotation: $s()\n", o2.getName());
                    }
                }
                
                int comp1 = cat1.compareTo(cat2);
                if(comp1 != 0) {
                    return comp1;
                }
                else {
                    return desc1.compareTo(desc2);
                }
            }
        };
    }
    
    public static void main(String ... args) {
        Method[] methods = ScriptingApi.class.getDeclaredMethods();
        for (Method method : methods) {
            System.out.append(method.getName()).append(", ");
        } System.out.println();
        Arrays.sort(methods, getFieldComperator());
        for (Method method : methods) {
            System.out.append(method.getName()).append(", ");
        } System.out.println();
        
        System.out.println("<?xml version=\"1.0\"?>");
        //<item separator="true"/>
        //<menu text="Channel Manipulation">
        System.out.println("<scriptingHelp>");
        String lastCategory = null;
        for(Method m : methods) {
            String mName = m.getName();
            String mCategory = "Unsorted";
            String mDesc = m.getName();
            final ApiMethodInfo info1 = m.getAnnotation(ApiMethodInfo.class);
            if(info1 != null) {
                mCategory = info1.category();
                mDesc = info1.description();
                if(info1.ignore()) { // skip this method
                    continue;
                }
            }
            
            /* Category change? */
            if(lastCategory == null || !lastCategory.equals(mCategory)) {
                if(lastCategory != null) {
                    System.out.printf("\t</menu>\n");
                }
                System.out.printf("\t<menu text=\"%s\">\n", mCategory);
                lastCategory = mCategory;
            }
            
            System.out.printf("\t\t<item insert=\"Api.%s(", 
                    mName);
            
            Parameter[] params = m.getParameters();
            boolean first = true;
            for(Parameter param : params) {
                if(first) {
                    first = false;
                }
                else {
                    System.out.print(", ");
                }
                if(param.getType().equals(String.class)) {
                    System.out.append("'").append(param.getName()).append("'");
                }
                else if(param.getType().equals(Character.class)) {
                    System.out.append("'k'");
                }
                else {
                    System.out.print(param.getName());
                }
            }
            
            
            System.out.printf(");\">%s</item>\n", 
                    mDesc);
        }
        
        System.out.printf("\t</menu>\n");
        System.out.println("<!-- <item separator=\"true\"/> -->");
        System.out.println("</scriptingHelp>");
    }
}
