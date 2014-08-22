#Remote Scripting API
> MixProcessing includes a small webserver that can output files and receives remote API calls.

##Requirements
1. Setup webserver's file location
 * Open MixProcessing's programm folder
 * Open the file preferences/webserver.storage.txt
 * Enter a folder's path or use the path given in the file
 * Open the path and copy this demo files to the folder
2. Run MixProcessing
3. Open a webbrowser: [http://localhost:8080/](http://localhost:8080/)
 * You can change the port (here: 8080) with the setting in preferences/webserver.port.txt
 * Your computer may has already a service running on port 8080, please change to another port (e.g. 80, 8081, etc.)

##MixProcessing's JavaScript Library
* Location: mp_lib.js
* Library was automatic generated by a programm: *de.itschleemilch.mixprocessing.util.JS_API_Generator*