#2014-08-22 Design Task Briefing
* Persons: Sebastian Schleemilch (SC), Sebastian Spangler (SP)
* Location: University FHWS, Sanderring, Wuerzburg
* Related documents: [Design-Tasks](Design-Tasks.md), [User Tasks](User-Tasks.md), [Datamodel MixProcessing](Datamodel_MixProcessing.png), [Renoise Audio Workstation](http://renoise.com), [Figure (App)](https://www.propellerheads.se/products/figure/), [OSC: Open Sound Control](http://de.wikipedia.org/wiki/Open_Sound_Control)

##Summary
> Diskussion with possible volunteer SP: Features and audience of MixProcessing, some concepts of Renoise DAW, open design tasks for MixProcessing, snippets around the project, collaberation.

##Notes of the discussion
* Evaluation / user test through DJ.
* Possible UI requirements: Usage at clubs or dark areas (darkish design), Locating similar elements at the same place to easy orientation, displaying all the information and controls as clear as possible.
* Switchable views for divide channels (evaluation).
* Inteligent UI: Idea of color coded language. (?? to display what kind of information ??)
* Evaluating thumbnails for sketches or even a whole setup (settings for channels and sketches).
* Reduced input area to fire key and mouse events from extern (very cool!).
* Automation for sketch's variables and also the key events and the mouse position.
* Recording of UI actions to perform them collected (Examplification by SP: Switching between two virtual DJ decks).
* External programming interface: Open Sound Control. Possibilities: External sensors send data via OSC, synchronisation with DJ software (e.g. programmed MixProcessing scripts for a whole live set), triggering an script execution.
* Developing an HTML/Javascript UI prototype.
* Live interaction of the prototype with the software via scripting API - checking possibilities.

##Free additional notes by SC
* Integration of a little webserver into MixProcessing enables the HTML website to communicate in real-time (!).
* Possible techniques: Websockets API, AJAX.
* Advantages: 
 * UI could be designed and used totaly within the Browser
 * Easy development of the UI through HTML5 and JavaScript.
 * Fast usable application.
 * Enables remote access via network: Controlling MixProcessing from other devices (such as tablets) or other locations (e.g. central for multi MixProcessing installations)
 * Enables MixProcessing Users to implement their own specific control UI for their application
* Automation: May a good idea to realise them like in [CSS3 Transitions](http://www.w3schools.com/css/css3_transitions.asp)
 * Properties: Delay, Duration, target value, timing function
 * Extending Timing functions with oscillating functions (sin, square, triangle)
 * Easier to include into UI!