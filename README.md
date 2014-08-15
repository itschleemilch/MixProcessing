#![MixProcessing Logo](https://github.com/itschleemilch/MixProcessing/raw/master/images/MixProcessing-Logo_48x48.png) MixProcessing

This project aims to mix [Processing](http://www.processing.org/) sketches live (also called [VJing](http://en.wikipedia.org/wiki/VJing)). 

##What is MixProcessing?
>MixProcessing can be used for VJing Processing sketches. The user defines output channels, which are free ediable areas on a output devices. The sketches can be controlled to render to any of the devices. A mixer let you control the output. Later there is a common interface for sketches planned, to set parameters (colors, speed, etc.) within MixProcessing live.

The technique of output channels also can be used to create [mapping](http://en.wikipedia.org/wiki/Projection_mapping) ([example 1](http://www.vjseptum.com/wp-content/uploads/2013/01/Coliseum_VO_003.jpg)).

MixProcessing handels compiled Processing sketches, they have to be exported.

##Dependencies
The project needs the Processing core library.

##Status
Currently there is a tech demo, that loades all sketches from a subfolder and renders them into three fixed output areas. 

![Demonstration of the current codebase](https://github.com/itschleemilch/MixProcessing/raw/master/images/2014-08-14_tech_demo.jpg)

##Known Issues
* Only supports Java2D mode (P2D, P3D will not work)
* The size() function must not be called from the sketch - please remove it before exporting the sketch.

##Roadmap
* Building an interface for editing the channels
* Building a mixer interface
* Define software interface for sketch-settings
* Build interface for editing sketch-settings

---
[Project Page](http://itschleemilch.github.io/MixProcessing/) * [Processing](http://www.processing.org/) * Thanks to [generative-gestaltung.de](http://generative-gestaltung.de/)
