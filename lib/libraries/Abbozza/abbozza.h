#ifndef abbozza_h
#define abbozza_h

#include "Arduino.h"

class LiquidCrystal {
    
public:
    LiquidCrystal();
    
    void begin(uint8_t cols, uint8_t rows);

    void clear();
    void home();

    void noDisplay();
    void display();
    void noBlink();
    void blink();
    void noCursor();
    void cursor();
    void scrollDisplayLeft();
    void scrollDisplayRight();
    void leftToRight();
    void rightToLeft();
    void autoscroll();
    void noAutoscroll();

    void createChar(uint8_t, uint8_t[]);
    void begin(int cols, int rows);
    void clear();
    void home();
    void setCursor(int col, int row);

    void write(char c);
    size_t write(uint8_t);
    /*
    size_t write(const char *str) {
      if (str == NULL) return 0;
      return write((const uint8_t *)str, strlen(str));
    }
    size_t write(const uint8_t *buffer, size_t size);
    size_t write(const char *buffer, size_t size) {
      return write((const uint8_t *)buffer, size);
    }*/
    
    size_t print(const String &);
    size_t print(const char[]);
    size_t print(char);
    size_t print(unsigned char);
    size_t print(int);
    size_t print(unsigned int);
    size_t print(long);
    size_t print(unsigned long);
    size_t print(double);
    size_t print(const Printable&);

};

#endif