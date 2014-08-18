#![MixProcessing Logo](https://github.com/itschleemilch/MixProcessing/raw/master/images/MixProcessing-Logo_48x48.png) MixProcessing

This project aims to mix and control animations in real-time.

##What is MixProcessing?
>MixProcessing can be used for VJing Processing sketches / animations. The user defines output channels, which are free ediable areas on a output devices and can be grouped. The sketches can be controlled to render to any of the channels. A mixer let you control your compsition. Later there is a common interface for sketches planned, to set parameters (colors, speed, etc.) in real-time.

The technique of output channels can be used to create [mapping](http://en.wikipedia.org/wiki/Projection_mapping) ([example 1](http://www.vjseptum.com/wp-content/uploads/2013/01/Coliseum_VO_003.jpg)).

##Licence
This software is free software. The following licences are applied:
* Source Code, Documentation: [GPL Version 3](https://github.com/itschleemilch/MixProcessing/raw/master/LICENSE)
* Media Files: [Creative Commons Attribution Share Alike 3.0](https://creativecommons.org/licenses/by-sa/3.0/de/deed.en)

###Features
* Loading of compiled Processing Sketches
* Rendering to editable output areas (channels)
* Grouping channels
* Scripting Interface to control the composition
* (planned) Loading Processing Projects and Compiling them
* (planned) User Interface for Controlling the sketches and the channels
* (planned) Automation of Sketch's Variables
* (planned) Open Sound Control Interaction

The current version loades all sketches from a folder and renders them into editable output channels. The channels can be edited by double-clicking and clicking inside the output frame while beeing in the editor-mode. The skripting interface with its menus help to control the software. The graphical mixer isn't finished, yet.

![Demonstration of the current codebase](https://github.com/itschleemilch/MixProcessing/raw/master/images/2014-08-15_tech_demo2_output.jpg)  
View of the output (e.g. a projector)

![Edit mode](https://github.com/itschleemilch/MixProcessing/raw/master/images/2014-08-15_tech_demo2.jpg)  
Workspace to edit the channels and configure the composition with skripting.

##Installation
Currently there are no binary packages. There is a netbeans project file that will do the job. 
###Requirements
* Java 1.7 or higher
* Processing core library

##Help
* [MixProcessing Wiki](https://github.com/itschleemilch/MixProcessing/wiki)
* E-Mail: <github@it-schleemilch.de>

##Contribution
You are interested in contributing to MixProject? You are welcome! 

* [Reporting bugs](https://github.com/itschleemilch/MixProcessing/issues), [Wiki: Known Issues](https://github.com/itschleemilch/MixProcessing/wiki/Known-Issues)
* Writing a user guide
* [Helping with programming](https://github.com/itschleemilch/MixProcessing/wiki/Roadmap)
* Create Sketches for a visuals library

Please use the GitHub website or send me an e-mail to get in contact.

##Credits
Thanks to the work of the [Processing](http://www.processing.org/) Team and their easy to learn programming environment.  
Thanks to [generative-gestaltung.de](http://generative-gestaltung.de/) for test sketches  
[Project Page](http://itschleemilch.github.io/MixProcessing/)
