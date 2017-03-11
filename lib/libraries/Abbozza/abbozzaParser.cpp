#include "Arduino.h"
#include "abbozzaParser.h"

AbbozzaParser::AbbozzaParser() {
    buffer = "";
    currentCommand = "";
    remainder = "";
    Serial.begin(9600);
}


void AbbozzaParser::check() {
    int start, end;

    String newBuf;
    String prefix;
    String currentLine;
    if ( Serial.available() ) {
        // append string to buffer
        newBuf = Serial.readString();
        buffer.concat(newBuf);
        Serial.println("Buffer : '" + buffer + "'");
        // find next command
        currentLine = "";
        start = buffer.indexOf("[[");
        if ( start >= 0 ) {
            end = buffer.indexOf("]]");
            if ( end >= 0 ) {
                prefix = buffer.substring(0,start);
                remainder.concat(prefix);
                currentLine = buffer.substring(start+2,end);
                currentLine.replace('\n',' ');
                currentLine.replace('\t',' ');
                currentLine.trim();
                buffer.remove(0,end+2);
                setCommand(currentLine);
            }
        }
    }
}


void AbbozzaParser::setCommand(String cmd) {
    currentCommand = cmd;
    cmdId = "";
    cmd = "";
    if ( currentCommand.charAt(0) == '_' ) {
        cmdId = parse_word();
    }
    cmd = parse_word();
    cmd.toUpperCase();
}


void AbbozzaParser::sendResponse(String resp) {
   resp = "[[ " + cmdId + " " + resp + " ]]";
   Serial.println(resp);
   cmdId = "";
}


String AbbozzaParser::parse_word() {
    int pos = currentCommand.indexOf(' ');
    String word = currentCommand.substring(0,pos);
    currentCommand.remove(0,pos);
    currentCommand.trim();    
    return word;
}

String AbbozzaParser::parse_string() {
    int pos;
    String result = "";
    if ( currentCommand.charAt(0) != '"') return "";
    do {
        pos = currentCommand.indexOf('"',pos+1);
    } while ( (pos != -1) && (currentCommand.charAt(pos-1) == '\\' ));
    if ( pos == -1 ) pos = currentCommand.length();
    result = currentCommand.substring(1,pos);
    currentCommand.remove(0,pos+1);
    return result;
}


String AbbozzaParser::getCmd() {
    return cmd;
}
int AbbozzaParser::parse_int() {
    String word = parse_word();
    return (int) word.toInt();
}

long AbbozzaParser::parse_long() {
    String word = parse_word();
    return word.toInt();
}

float AbbozzaParser::parse_float() {
    String word = parse_word();
    return word.toFloat();
}

double AbbozzaParser::parse_double() {
    String word = parse_word();
    return word.toFloat();
}

void AbbozzaParser::execute() {
  String command;
  String arg;
  int pos, pin, value;

  if ( cmd.equals("DSET") ) {
    pin = parse_int();
        
    value = parse_int();
    
    pinMode(pin,OUTPUT);
    if ( value > 0 ) {
      digitalWrite(pin,HIGH);
    } else {
      digitalWrite(pin,LOW);
    }
  } else if ( cmd.equals("ASET") ) {
      pin = parse_int();

      value = parse_int();

    pinMode(pin,OUTPUT);
    analogWrite(pin,value);
  } else if ( cmd.equals("DGET") ) {
      pin = parse_int();

    pinMode(pin,INPUT);
    value = digitalRead(pin) > 0 ? 1 : 0;
    sendResponse("DVAL " + String(pin) + " " + String(value));
  }  else if ( cmd.equals("AGET") ) {
    pin = parse_int();

    pinMode(pin,INPUT);
    value = analogRead(pin);
    sendResponse("AVAL " + String(pin) + " " + String(value));
  }
}