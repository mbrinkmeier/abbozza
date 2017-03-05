#include "Arduino.h"
#include "abbozzaParser.h"

AbbozzaParser::AbbozzaParser() {
    buffer = "";
    Serial.begin(9600);
}


void AbbozzaParser::check() {
    String newBuf;
    if ( Serial.available() ) {
        // append string to buffer
        newBuf = Serial.readString();
        buffer.concat(newBuf);
        // find next line
        int pos = buffer.indexOf('\n');
        while ( pos >= 0 ) {
            currentLine = buffer.substring(0,pos);
            buffer.remove(0,pos+1);
            if ( (currentLine.charAt(0) == '>') && (currentLine.charAt(1) == '>')) {
                currentLine.remove(0,2);
                execute();
            }
            pos = buffer.indexOf('\n');
        }
    }
}

String AbbozzaParser::parseWord() {
    int pos = line.indexOf(' ');
    String command = line.substring(0,pos);
    currentLine.remove(0,pos);
    currentLine.trim();    
    return command;
}

int AbbozzaParser::parseInt() {
    String word = getWord();
    return word.toInt();
}

void AbbozzaParser::execute() {
  String command;
  String arg;
  int pos, pin, value;
  // Get commamd from string
  currentLine.trim();

  // pos = line.indexOf(' ');
  // command = line.substring(0,pos);
  // line.remove(0,pos);
  // line.trim();

  command = parseWord();
  command.toUpperCase();
  
  if ( command.equals("DSET") ) {
    // pos = line.indexOf(' ');
    // arg = line.substring(0,pos);
    // line.remove(0,pos);
    // line.trim();    
    // pin = arg.toInt();
    pin = parseInt();
        
    // pos = line.indexOf(' ');
    // arg = line.substring(0,pos);
    // line.remove(0,pos);
    // line.trim();    
    // value = arg.toInt();
    value = parseInt();
    
    pinMode(pin,OUTPUT);
    if ( value > 0 ) {
      digitalWrite(pin,HIGH);
    } else {
      digitalWrite(pin,LOW);
    }
  } else if ( command.equals("ASET") ) {
    // pos = line.indexOf(' ');
    // arg = line.substring(0,pos);
    // line.remove(0,pos);
    // line.trim();    
    // pin = arg.toInt();
      pin = parseInt();

    // pos = line.indexOf(' ');
    // arg = line.substring(0,pos);
    // line.remove(0,pos);
    // line.trim();    
    // value = arg.toInt();
      value = parseInt();

    pinMode(pin,OUTPUT);
    analogWrite(pin,value);
  } else if ( command.equals("DGET") ) {
    // pos = line.indexOf(' ');
    // arg = line.substring(0,pos);
    // line.remove(0,pos);
    // line.trim();    
    // pin = arg.toInt();
      pin = parseInt();

    pinMode(pin,INPUT);
    value = digitalRead(pin) > 0 ? 1 : 0;
    Serial.println("<< DVAL " + String(pin) + " " + String(value));
  }  else if ( command.equals("AGET") ) {
    // pos = line.indexOf(' ');
    // arg = line.substring(0,pos);
    // line.remove(0,pos);
    // line.trim();    
    // pin = arg.toInt();
      pin = parseInt();

    pinMode(pin,INPUT);
    value = analogRead(pin);
    Serial.println("<< AVAL " + String(pin) + " " + String(value));
  }
}