/*
MixProcessing - Live Mixing of Processing Sketches 
https://github.com/itschleemilch/MixProcessing

Copyright (c) 2014 Sebastian Schleemilch

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

/**
 * JS Library to do the basic communication with the MixProcessing Control Server
 *
 */
function log(output) {
	console.log(output);
}

function ajaxCallback() {
	var jsonData = JSON.parse(this.responseText);
	log( JSON.stringify(jsonData) );
    window[jsonData.callback](jsonData);
}

function ApiCall(remoteCall, callBackFunction) {
    var rxUrl = "api/api1/" + callBackFunction + "?" + escape(remoteCall);
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