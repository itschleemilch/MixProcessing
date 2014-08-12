#MixProcessing

This project aims to live-mix (VJing) [Processing](http://www.processing.org/) sketches. 

##What is MixProcessing?
>MixProcessing can be used for VJing Processing sketches. The user defines output channels, which are free ediable areas on a output devices. The sketches can be controlled to render to any of the devices. A mixer let you control the output. Later there is a common interface for sketches planned, to set parameters (colors, speed, etc.) within MixProcessing live.

MixProcessing handels compiled Processing sketches, they have to be exported.

##Dependencies
The project needs the Processing core library.

##Status
Currently there is a tech demo, that loades all sketches from a subfolder and renders them onto a divided frame. Sketches have to use Java2D, P2D or P3D does not work.

![Demonstration of the current codebase](images/2014-08-12_tech_demo.jpg)

##Roadmap
* Building an interface for editing the channels
* Building a mixer interface
