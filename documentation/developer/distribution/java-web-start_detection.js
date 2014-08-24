// ADD MIT LICENCE


var javaWebStartInstalled = false;
/* Web Start Test 1 */
if (navigator.mimeTypes['application/x-java-jnlp-file']) {
	javaWebStartInstalled = true;
}
/* Web Start Test 2 */
try {
	new ActiveXObject('JavaWebStart.isInstalled');
	javaWebStartInstalled = true;
} catch (e) {
}

/* Evaluation of Web Start Detection */
if (javaWebStartInstalled) {
    document.writeln("Java Web Start detected");
} else {
    document.writeln("Java Web Start NOT detected");
}