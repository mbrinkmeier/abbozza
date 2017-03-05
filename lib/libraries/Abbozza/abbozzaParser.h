#ifndef AbbozzaParser_h
#define AbbozzaParser_h

#include "Arduino.h"

class AbbozzaParser {
    
public:
   AbbozzaParser();
   void check();
    
private:
   void execute();
   String parseWord();
   int parseInt();
   String buffer;
   String currentLine;
   
};

#endif
