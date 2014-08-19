#![MixProcessing Logo](https://github.com/itschleemilch/MixProcessing/raw/master/images/MixProcessing-Logo_48x48.png) MixProcessing

MixProcessing is a software for mixing Processing sketches.

##What is MixProcessing?
>MixProcessing can be used to combine and mix Processing sketches to create new real-time animations. The output screen is subdivided into output channels, which are free ediable areas. The sketches can be controlled to render to any of the channels or a group af them. A mixer let you control your compsition. An automation interface can set sketch's parameters and public variables automatically.

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

##Help and Contact
* [MixProcessing Wiki](https://github.com/itschleemilch/MixProcessing/wiki)
* E-Mail: <github@it-schleemilch.de>
* Twitter: [Follow @mixprocessing](https://twitter.com/mixprocessing), [Tweet to @mixprocessing]("https://twitter.com/intent/tweet?screen_name=mixprocessing)

##Contribution
You are interested in contributing to MixProject? You are welcome! 

* [Reporting bugs](https://github.com/itschleemilch/MixProcessing/issues), [Wiki: Known Issues](https://github.com/itschleemilch/MixProcessing/wiki/Known-Issues)
* Writing a user guide
* [Helping with programming](https://github.com/itschleemilch/MixProcessing/wiki/Roadmap)
* [Help with designing](https://github.com/itschleemilch/MixProcessing/blob/design/source/Design/1%20Development/2014-08-20%20Briefing/Design-Tasks.md)
* Create Sketches for a visuals library

Please use the GitHub website, use Twitter or send me an e-mail to get in contact.

##Credits
* Thanks to the work of the [Processing](http://www.processing.org/) Team and their easy to learn programming environment.
* Thanks to [generative-gestaltung.de](http://generative-gestaltung.de/) for test sketches
* [MixProcessing's Website](http://itschleemilch.github.io/MixProcessing/)
