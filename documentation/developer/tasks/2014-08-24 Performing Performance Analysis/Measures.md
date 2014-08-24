#Performing Performance Analysis (2014-08-24)

Used Codebase: Commit 6af3060. Action: Booting the software and it's welcome screens, sketch. No further action.

#Preparation
1. Giving each looping thread a name.
2. Performing Netbeans integrated Profiler.

#Results of Profiling

##Threads
- Webserver Thread runs all the time, caused by the blocking acception of new clients. This is no problem.
- Rendering Loop runs only at 2.3% - much better than expected.

##VM Telemetry
- Surviving object creation is almost at three.
- Relative garbage collector time is approx. 0%.
- Total count of threads is 11.

#Measures
- Removed object creation from rendering thread where possible.
- Consolided code.
- Removed second offscreen buffer for editing mode.
- Removed HashMap for Sketch-Channel association and moved setting to Sketch's class.