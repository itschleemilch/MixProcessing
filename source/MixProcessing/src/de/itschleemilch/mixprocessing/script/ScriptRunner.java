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
package de.itschleemilch.mixprocessing.script;

import de.itschleemilch.mixprocessing.EventManager;
import java.awt.TextArea;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

/**
 * Runs JavaScript Code to control MixProcessing 
 *
 * @author Sebastian Schleemilch
 */
public class ScriptRunner {
    private final ScriptEngine scriptingEngine;

    public ScriptRunner(final EventManager eventManager) {        
        /* Init Scripting API */
        ScriptEngineManager factory = new ScriptEngineManager();
        scriptingEngine = factory.getEngineByName("JavaScript");
        scriptingEngine.put("Api", eventManager); // access to EventManager through mp Variable
    }
    
    /**
     * Executes a JavaScript and outputs thrown Exceptions to TextArea.
     * Uses a seperate Thread.
     * @param jsScript source code
     * @param errorLog error output
     */
    public void exec(final String jsScript, final TextArea errorLog)
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    scriptingEngine.eval(jsScript);
                } catch (ScriptException e) {
                    errorLog.append("Error: ");
                    errorLog.append(e.getMessage());
                    errorLog.append(System.getProperty("line.separator"));
                    errorLog.append(System.getProperty("line.separator"));
                }
                errorLog.append("Executed.");
            }
        }).start();
    }
    
    /**
     * Remote API Call: Returns the methods parameters
     * @param jsScript
     * @return value or null
     * @throws ScriptException 
     */
    public Object remoteApiCall(String jsScript) throws ScriptException {
        jsScript = jsScript.trim();
        if(!jsScript.endsWith(";"))
            jsScript += ";";
        jsScript = "var rVal = " + jsScript;
        scriptingEngine.eval(jsScript);
        Object answer = null;
        try {
            answer = scriptingEngine.get("rVal");
        } catch (Exception e) {
        }
        return answer;
    }
}
