#Distribution via Java Web Start
> Java Web Start [...] is a framework [...] that allows users to start application software for the Java Platform directly from the Internet using a web browser. Some key benefits of this technology include seamless version updating for globally distributed applications [...]. 

Source: Wikipedia: [Java Web Start](http://en.wikipedia.org/wiki/Java_Web_Start) (Date: 24.08.2014)

##Technolgy Documentation
* [Java Web Start Guide](http://docs.oracle.com/javase/7/docs/technotes/guides/javaws/developersguide/contents.html)
* [Avoiding Unnecessary Update Checks, The Java Tutorials](http://docs.oracle.com/javase/tutorial/deployment/deploymentInDepth/avoidingUnnecessaryUpdateChecks.html)
* [Oracle's Home of Java Web Start Technology](http://www.oracle.com/technetwork/java/javase/javawebstart/index.html)

##Requirements

###Client Side
* Desktop OS (Windows, Linux, Mac OS)
* Java Installation, Version 1.7+
* Correct file accociation: javaws to *.jnlp
* Detect Java Installation: [Java Rich Internet Applications Deployment Advice](http://docs.oracle.com/javase/8/docs/technotes/guides/jweb/deployment_advice.html)
* Site exception: Base URL must be added to the local exception list to allow execution, because the app is not signed. (see: [How do I control when an untrusted applet or application runs in my web browser?](http://www.java.com/en/download/help/jcp_security.xml), example on windows: [java-web-start_site-exception_windows.jpg](java-web-start_site-exception_windows.jpg))

###Server Side

####Setup
* MIME Type for *.jnlp files: application/x-java-jnlp-file

####Files
* HTML Site: Link to JNLP File, Javascript Detection: Java Webstart Support. Reference: [Creating the Web Page That Launches the Application](http://docs.oracle.com/javase/8/docs/technotes/guides/javaws/developersguide/launch.html)
* JNLP File
* Packaged Programm and Libraries

##Distribution of an Release
1. Check Netbeans settings.
2. Perform build process.
3. Edit launch.jnlp file
 1. Remove codebase-attribute from parental jnlp tag.
 2. Check if *version download protocol* is enabled (see Avoiding Unnecessary Update Checks)
  * Rename all jar files to: name_Vx.jar where x is an number. (DynamicTreeDemo.jar to DynamicTreeDemo__V1.0.jar)
  * Set the version parameter for each jar-tag (e.g. version="2.0")
3. Copy all files from dist/ folder to server.
4. Use the template [java-web-start_template.html](java-web-start_template.html) to generate an website.
