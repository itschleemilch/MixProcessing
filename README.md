#![MixProcessing Logo](https://github.com/itschleemilch/MixProcessing/raw/master/images/MixProcessing-Logo_48x48.png) MixProcessing

This project aims to mix [Processing](http://www.processing.org/) sketches live (also called [VJing](http://en.wikipedia.org/wiki/VJing)). 

##What is MixProcessing?
>MixProcessing can be used for VJing Processing sketches. The user defines output channels, which are free ediable areas on a output devices. The sketches can be controlled to render to any of the devices. A mixer let you control the output. Later there is a common interface for sketches planned, to set parameters (colors, speed, etc.) within MixProcessing live.

The technique of output channels also can be used to create [mapping](http://en.wikipedia.org/wiki/Projection_mapping) ([example 1](http://www.vjseptum.com/wp-content/uploads/2013/01/Coliseum_VO_003.jpg)).

MixProcessing handels compiled Processing sketches, they have to be exported.

##Dependencies
The project needs the Processing core library.

##Status
* Loading of compiled Processing Sketches
* Drawing of new output areas (channels)
* Several controls over the sketches and the channels
* Scripting Interface to control the setup

The current tech demo loades all sketches from a subfolder and renders them into editable output channels. The channels can be edited by double-clicking and clicking inside the output frame while beeing in the editor-mode. The mixer isn't finished, yet. The skripting interface and the menus help to control the software.

![Demonstration of the current codebase](https://github.com/itschleemilch/MixProcessing/raw/master/images/2014-08-15_tech_demo2_output.jpg)

![Edit mode](https://github.com/itschleemilch/MixProcessing/raw/master/images/2014-08-15_tech_demo2.jpg)

##Known Issues
* Only supports Java2D mode (P2D, P3D will not work)
* The size() function must not be called from the sketch - please remove it before exporting the sketch.

##Roadmap
* Building an interface for editing the channels
* Building a mixer interface
* Define software interface for sketch-settings
* Building an interface for editing sketch-settings
* Building an automation for editing the sketch-settings

---
[Project Page](http://itschleemilch.github.io/MixProcessing/) * [Processing](http://www.processing.org/) * Thanks to [generative-gestaltung.de](http://generative-gestaltung.de/)
