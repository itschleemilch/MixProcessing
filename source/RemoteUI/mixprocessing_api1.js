function log(output) {
	console.log(output);
}

function ajaxCallback() {
	var jsonData = JSON.parse(this.responseText);
	log( JSON.stringify(jsonData) );
    window[jsonData.callback](jsonData);
}

function ApiCall(callBackFunction, remoteScript) {
    var rxUrl = "api/api1/" + callBackFunction + "?" + escape(remoteScript);
	ajax.open("get", rxUrl, true);
	ajax.send();
}

/**********************************************
 * Initalise Ajax 
 **********************************************/
if( ! window.XMLHttpRequest ) {
		alert("Browser not supported.");
}
else {
	ajax = new XMLHttpRequest();
	ajax.onload = ajaxCallback;
}

function test1Callback(json) { alert( JSON.stringify(json) );}