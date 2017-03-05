#include "Arduino.h"
#include "abbozza.h"

void __measure(int val0, int val1, int val2, int val3, int val4)
{
    int val0, val1, val2, val3, val4;

    unsigned long timestamp = millis();

/*    
    if (pin0 == -1) val0 = 0;
    else val0 = ( pin0 < A0 ) ? digitalRead(pin0)*512 : analogRead(pin0);

    if (pin1 == -1) val1 = 0;
    else val1 = ( pin1 < A0 ) ? digitalRead(pin1)*512 : analogRead(pin1);

    if (pin2 == -1) val2 = 0;
    else val2 = ( pin2 < A0 ) ? digitalRead(pin2)*512 : analogRead(pin2);
    
    if (pin3 == -1) val3 = 0;
    else val3 = ( pin3 < A0 ) ? digitalRead(pin3)*512 : analogRead(pin3);
    
    if (pin4 == -1) val4 = 0;
    else val4 = ( pin4 < A0 ) ? digitalRead(pin4)*512 : analogRead(pin4);
*/
    
    Serial.println("[" + String(millis()) + "," + String(val0) + "," + String(val1) + "," 
       + String(val2) + "," + String(val3) + "," + String(val4) + "]");
}