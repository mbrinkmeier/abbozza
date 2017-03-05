#include "Arduino.h"
#include "abbozzaParser.h"

AbbozzaParser::AbbozzaParser() {
    buffer = "";
    Serial.begin(9600);
}


void AbbozzaParser::check() {
    int start, end;
    String newBuf;
    String prefix;
    if ( Serial.available() ) {
        // append string to buffer
        newBuf = Serial.readString();
        buffer.concat(newBuf);
        Serial.println("Buffer : '" + buffer + "'");
        // find next line
        do {
            currentLine = "";
            start = buffer.indexOf("[[");
            if ( start >= 0 ) {
                end = buffer.indexOf("]]");
                if ( end >= 0 ) {
                    prefix = buffer.substring(0,start);
                    currentLine = buffer.substring(start+2,end);
                    currentLine.replace('\n',' ');
                    currentLine.replace('\t',' ');
                    buffer.remove(0,end+2);
                    Serial.println("executing '" + currentLine + "'");
                    execute();                    
                }
            }
        } while ( currentLine.length() > 0 );
    }
}

String AbbozzaParser::parseWord() {
    int pos = currentLine.indexOf(' ');
    String command = currentLine.substring(0,pos);
    currentLine.remove(0,pos);
    currentLine.trim();    
    return command;
}

int AbbozzaParser::parseInt() {
    String word = parseWord();
    return word.toInt();
}

int AbbozzaParser::parseFloat() {
    String word = parseWord();
    return word.toFloat();
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