#ifndef AbbozzaParser_h
#define AbbozzaParser_h

#include "Arduino.h"

class AbbozzaParser {
    
public:
    AbbozzaParser();
    void check();
    String parse_word();
    int parse_int();
    long parse_long();
    float parse_float();
    double parse_double();
    String parse_string();
    
    String getCmd();
    void execute();    

    void sendResponse(String resp);
    
private:
    void setCommand(String cmd);
    String buffer;
    String currentCommand;
    String remainder;
    String cmdId;
    String cmd;
};

#endif
