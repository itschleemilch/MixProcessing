#MixProcessing User Manual

This is an early version of the user manual. Please contribute to improve this document!

##Output Window

###Mode
* **Normal:** Renders for displaying.
* **Editing:** Adds channels' border outlines and names.

###Shortcuts
* [F1] Open the user manual.
* [F2] Switch to channel edit mode.
* [F5] Show / hide debug log.
* [F10] Make window 50% opaque.
* [F11] Show / hide mouse cursur.
* [F12] Make window frameless.
 * If the window was maximized, the window will switch to fullscreen mode.

Source File: mixprocessing.RenderFrame

##Managing Channels
###Creating a new Channel
* Switch to editing mode.
* Double click anywhere.
* Add further points by clicking.
* Double click at the last point.

###Renaming
* Using scripting: channelRename('oldName', 'newName')

###Removing
* Using scripting: channelRemove('name')

###Grouping
* Using scripting: channelCreateGroup('groupName', 'channel1', 'channel2', ...)

###Planned Features
* Loading, Saving channel configuration.


##Managing Sketches
###Loading precompiled Sketches
Put you  *ExampleSketch.jar* to the *jarSource* sub-folder. The folder is accessable via "View" menu.
###Compile Sketches within MixProcessing
* Using scripting: systemLoad('c:/folder/Sketch/')

##User Scripts

Use the 'User Scripts' window to enter your commands. Integrated menus help you to find a command and show you how to use it.

###Method Reference
See: Remote JavaScript API / ScriptingApi Java Documentation.html

###Planned Features
* Loading, Saving, Calling script files.
* Compile Sketches with an user interface.

##Remote Scripting and Integrated Webserver

###Integrated Webserver
Automatic starts with MixProcessing. At a clean installation there are **no files** at the web server. Please use the template provided at *Remote JavaScript API* to start a new interface.

You can build your own control interfaces using HTML and JavaScript.

####Access and Setup
Run View > Web Interface.
####IP Filter
Per default only a local user can access the web server. To allow external clients to access, you have to add their IP address to the whitelist at the preferences folder.

##Settings
Key-Value files are stored in the sub-folder preferences. Accessing via 'View' menu.
