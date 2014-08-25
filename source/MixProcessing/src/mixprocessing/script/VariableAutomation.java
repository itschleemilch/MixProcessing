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
package mixprocessing.script;

import mixprocessing.sketches.Sketch;

/**
 *
 * @author Sebastian Schleemilch
 */
public class VariableAutomation extends Thread {
    private final ScriptingApi api;
    private final Sketch target;
    private final String varName;
    private final Object initialValue, finalValue;
    private final long delay;
    private final long duration; // time at ms after delay
    private final TimingFunction tFunction;
    private final long periode; // for oscillating functions or steps
    
    private final double initValueD;
    private final double finalValueD;

    /**
     * Note: Call start() after creation.
     * @param api
     * @param target
     * @param varName
     * @param finalValue
     * @param delay
     * @param duration
     * @param periode
     * @param tFunction 
     */
    public VariableAutomation(ScriptingApi api, Sketch target, String varName, 
            Object finalValue, long delay, long duration, long periode, 
            TimingFunction tFunction) {
        this.api = api;
        
        this.target = target;
        this.varName = varName;
        
        this.initialValue = api.sketchVarGet(target.getName(), varName);
        this.finalValue = finalValue;
        
        this.delay = delay;
        this.duration = duration;
        this.tFunction = tFunction;
        this.periode = periode;
        
        initValueD = getDoubleValue(initialValue);
        finalValueD = getDoubleValue(finalValue);
    }

    @Override
    public void run() {
        setName("MP VarAutomation: " + target.getName() + "." + varName);
        
        try {
            sleep(delay);
        } catch (InterruptedException e) {
            e.printStackTrace(System.err);
        }
        
        long start = System.currentTimeMillis();
        long diff = 0;
        while(diff < duration) {
            final double singlePeriodeDiff;
            final double dutyCycle;
            final int periodeCount;
            if(periode>0) {
                singlePeriodeDiff = diff % periode;
                dutyCycle = singlePeriodeDiff / ((double)periode);
                periodeCount = (int) (diff / periode);
            }
            else {
                singlePeriodeDiff = diff;
                dutyCycle = singlePeriodeDiff / ((double)duration);
                periodeCount = 0;
            }
            
            /* Perform timing functions */
            switch(tFunction) {
                case LINEAR:
                    Object newValueLin = getInterimValue(dutyCycle);
                    api.sketchVar(target.getName(), varName, newValueLin);
                    break;
                    
                case EASE:
                case EASE_IN:
                case EASE_OUT:
                    double easedDutyCycle = 0;
                    if(tFunction == TimingFunction.EASE_IN) {
                        easedDutyCycle = Math.pow(dutyCycle, 2);
                    } else if(tFunction == TimingFunction.EASE_OUT) {
                        easedDutyCycle = Math.sqrt(dutyCycle);
                    } else {
                        easedDutyCycle = 0.5d + Math.atan(50d*dutyCycle-15d) / Math.PI;
                    }
                    Object newValueEased = getInterimValue(easedDutyCycle);
                    api.sketchVar(target.getName(), varName, newValueEased);
                    break;
                    
                case STEPS:
                    double maxPeriodes = (periode>0) ? duration / periode : 1;
                    double stepCycle = periodeCount / maxPeriodes;
                    Object newValueStep = getInterimValue(stepCycle);
                    api.sketchVar(target.getName(), varName, newValueStep);
                    break;
                    
                case ALTERNATING:
                    if(periodeCount % 2 == 0) {
                        api.sketchVar(target.getName(), varName, initialValue);
                    } else {
                        api.sketchVar(target.getName(), varName, finalValue);
                    }
                    break;
                default:
                    break;
            }
            try {
                sleep(10); // run with ~100 Hz
            } catch (InterruptedException e) {
                e.printStackTrace(System.err);
            }
            /* Update difference measure */
            diff = System.currentTimeMillis() - start;
        }
        
        api.sketchVar(target.getName(), varName, finalValue);
    }
    
    /**
     * Calculates the interim value between inital and final value 
     * based on linear interpolation.
     * @param dutyCycle [0;1]
     * @return 
     */
    private Object getInterimValue(double dutyCycle) {
        /* Check input */
        if(dutyCycle < 0d) {
            dutyCycle = 0d;
        }
        else if(dutyCycle > 1.0d) {
            dutyCycle = 1.0d;
        }
        /* perform interpolation */
        double interimValue = initValueD + (finalValueD-initValueD)*dutyCycle;
        /* cast to correct output data type */
        Object answer = null;
        if(initialValue instanceof Integer) {
            answer = ((Double) interimValue).intValue();
        }
        else if(initialValue instanceof Float) {
            answer = ((Double) interimValue).floatValue();
        }
        else if(initialValue instanceof Double) {
            answer = interimValue;
        }
        else {
            System.err.println("Datatype of " + varName + " not supported.");
        }
        
        return answer;
    }
    
    private double getDoubleValue(Object data) {
        if(data instanceof Double) {            
            return ((double) data);
        }
        else if(data instanceof Integer) {            
            return ((Integer) data).doubleValue();
        }
        else if(data instanceof Float) {            
            return ((Float) data).doubleValue();
        }
        else {
            return 0d;
        }
    }
    
    public static TimingFunction getTimingFunctionByName(String name) {
        name = name.trim().toUpperCase();
        TimingFunction[] functions = TimingFunction.values();
        for (TimingFunction tFunc : functions) {
            if(tFunc.toString().equals(name)) {
                return tFunc;
            }
        }
        System.err.println("There is no such timing function: " + name);
        return TimingFunction.LINEAR;
    }
    
    public enum TimingFunction {
        EASE, EASE_IN, EASE_OUT, LINEAR, STEPS, ALTERNATING
    }
}
