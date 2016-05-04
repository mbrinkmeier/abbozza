/**
 * @license
 * abbozza!
 * 
 * File: Connection.js
 * This object handles the communication with the abboza!-Server
 * 
 * Copyright 2015 Michael Brinkmeier
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

var Connection = {
}


Connection.getXML = function (path, successHandler, errorHandler) {

    if (window.XMLHttpRequest)
        xhttp = new XMLHttpRequest();
    else // code for IE5 and IE6
        xhttp = new ActiveXObject("Microsoft.XMLHTTP");

    xhttp.onload = function () {
        if (xhttp.status == 200) {
            if (successHandler)
                successHandler(this.responseXML);
        } else {
            if (errorHandler)
                errorHandler(this.responseXML);
        }
    }

    xhttp.responseType = "document";
    xhttp.open("GET", path, true);
    xhttp.send();

    return xhttp.responseXML;
}


Connection.getXMLSynced = function (path, successHandler, errorHandler) {

    if (window.XMLHttpRequest)
        xhttp = new XMLHttpRequest();
    else // code for IE5 and IE6
        xhttp = new ActiveXObject("Microsoft.XMLHTTP");

    xhttp.onload = function () {
        if (xhttp.status == 200) {
            if (successHandler)
                successHandler(this.responseXML);
        } else {
            if (errorHandler)
                errorHandler(this.responseXML);
        }
    }

    // xhttp.responseType="document";
    xhttp.open("GET", path, false);
    xhttp.send();

    return xhttp.responseXML;

}


Connection.sendXML = function (path, content, successHandler, errorHandler) {
    if (window.XMLHttpRequest)
        xhttp = new XMLHttpRequest();
    else // code for IE5 and IE6
        xhttp = new ActiveXObject("Microsoft.XMLHTTP");

    xhttp.onload = function () {
        if (xhttp.status == 200) {
            if (successHandler)
                successHandler(this.responseXML);
        } else {
            if (errorHandler)
                errorHandler(this.responseXML);
        }
    }

    xhttp.open("POST", path, true);
    xhttp.responseType = "";
    xhttp.send(content);

    return xhttp.responseText;
}


Connection.getText = function (path, successHandler, errorHandler) {
    if (window.XMLHttpRequest)
        xhttp = new XMLHttpRequest();
    else // code for IE5 and IE6
        xhttp = new ActiveXObject("Microsoft.XMLHTTP");

    xhttp.onload = function () {
        if (xhttp.status == 200) {
            if (successHandler)
                successHandler(this.responseText);
        } else {
            if (errorHandler)
                errorHandler(this.responseText);
        }
    }

    xhttp.responseType = "";
    xhttp.open("GET", path, true);
    xhttp.send();

    return xhttp.responseText;
}


Connection.getTextSynced = function (path, successHandler, errorHandler) {
    if (window.XMLHttpRequest)
        xhttp = new XMLHttpRequest();
    else // code for IE5 and IE6
        xhttp = new ActiveXObject("Microsoft.XMLHTTP");

    xhttp.onload = function () {
        if (xhttp.status == 200) {
            if (successHandler)
                successHandler(this.responseText);
        } else {
            if (errorHandler)
                errorHandler(this.responseText);
        }
    }

    xhttp.open("GET", path, false);
    xhttp.send();

    return xhttp.responseText;
}


Connection.sendText = function (path, content, successHandler, errorHandler) {
    if (window.XMLHttpRequest)
        xhttp = new XMLHttpRequest();
    else // code for IE5 and IE6
        xhttp = new ActiveXObject("Microsoft.XMLHTTP");

    xhttp.onload = function () {
        if (xhttp.status == 200) {
            if (successHandler)
                successHandler(this.responseText);
        } else {
            if (errorHandler)
                errorHandler(this.responseText);
        }
    }

    xhttp.open("POST", path, true);
    xhttp.responseType = "";
    xhttp.send(content);

    return xhttp.responseText;
}

/*
 if (window.XMLHttpRequest)
 {
 xhttp=new XMLHttpRequest();
 }
 else // code for IE5 and IE6
 {
 xhttp=new ActiveXObject("Microsoft.XMLHTTP");
 }
 
 var response = null;
 
 if ( showDialog ) {
 xhttp.onreadystatechange = function() {
 if (this.readyState == 4) {
 if ( this.responseType == "document" )
 console.log("xml : " + this.responseXml);
 else 
 console.log("text : "+ this.responseText);
 }
 }
 }
 
 if ( text != null ) {
 xhttp.open(method,path,true);
 xhttp.responseType=responseType;
 } else xhttp.open(method,path,false);
 // bb.append(this.response);
 if ( text != null ) {
 xhttp.send(text);
 } else { 
 xhttp.send();
 }
 
 if ( responseType =="document" ) {
 return xhttp.responseXml;
 } 
 return xhttp.responseText;
 */