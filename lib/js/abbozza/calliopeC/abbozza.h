#ifndef _ABBOZZA_H
#define _ABBOZZA_H

// MicroBitI2C __abz_i2c = MicroBitI2C(I2C_SDA0, I2C_SCL0);

// MicroBitAccelerometer __abz_accelerometer = MicroBitAccelerometer(__abz_i2c);
// MicroBitCompass __abz_compass = MicroBitCompass(__abz_i2c);
// MicroBitThermometer __abz_thermometer;


/**
 * Pins
 */

/*
MicroBitPin Pin0(MICROBIT_ID_IO_P0, MICROBIT_PIN_P0, PIN_CAPABILITY_ALL);
MicroBitPin Pin1(MICROBIT_ID_IO_P1, MICROBIT_PIN_P1, PIN_CAPABILITY_ALL);
MicroBitPin Pin2(MICROBIT_ID_IO_P2, MICROBIT_PIN_P2, PIN_CAPABILITY_ALL);
MicroBitPin Pin3(MICROBIT_ID_IO_P3, MICROBIT_PIN_P3, PIN_CAPABILITY_ANALOG);
MicroBitPin Pin4(MICROBIT_ID_IO_P4, MICROBIT_PIN_P4, PIN_CAPABILITY_ANALOG);
MicroBitPin Pin5(MICROBIT_ID_IO_P5, MICROBIT_PIN_P5, PIN_CAPABILITY_DIGITAL);
MicroBitPin Pin6(MICROBIT_ID_IO_P6, MICROBIT_PIN_P6, PIN_CAPABILITY_DIGITAL);
MicroBitPin Pin7(MICROBIT_ID_IO_P7, MICROBIT_PIN_P7, PIN_CAPABILITY_DIGITAL);
MicroBitPin Pin8(MICROBIT_ID_IO_P8, MICROBIT_PIN_P8, PIN_CAPABILITY_DIGITAL);
MicroBitPin Pin9(MICROBIT_ID_IO_P9, MICROBIT_PIN_P9, PIN_CAPABILITY_DIGITAL);
MicroBitPin Pin10(MICROBIT_ID_IO_P10, MICROBIT_PIN_P10, PIN_CAPABILITY_ANALOG);
MicroBitPin Pin11(MICROBIT_ID_IO_P11, MICROBIT_PIN_P11, PIN_CAPABILITY_DIGITAL);
MicroBitPin Pin12(MICROBIT_ID_IO_P12, MICROBIT_PIN_P12, PIN_CAPABILITY_DIGITAL);
MicroBitPin Pin13(MICROBIT_ID_IO_P13, MICROBIT_PIN_P13, PIN_CAPABILITY_DIGITAL);
MicroBitPin Pin14(MICROBIT_ID_IO_P14, MICROBIT_PIN_P14, PIN_CAPABILITY_DIGITAL);
MicroBitPin Pin15(MICROBIT_ID_IO_P15, MICROBIT_PIN_P15, PIN_CAPABILITY_DIGITAL);
MicroBitPin Pin16(MICROBIT_ID_IO_P16, MICROBIT_PIN_P16, PIN_CAPABILITY_DIGITAL);
// MicroBitPin Pin17(MICROBIT_ID_IO_P17, MICROBIT_PIN_P17, PIN_CAPABILITY_DIGITAL);
// MicroBitPin Pin18(MICROBIT_ID_IO_P18, MICROBIT_PIN_P18, PIN_CAPABILITY_DIGITAL);
MicroBitPin Pin19(MICROBIT_ID_IO_P19, MICROBIT_PIN_P19, PIN_CAPABILITY_DIGITAL);
MicroBitPin Pin20(MICROBIT_ID_IO_P20, MICROBIT_PIN_P20, PIN_CAPABILITY_DIGITAL);

MicroBit Pin[21];

Pin[0] = Pin0;
Pin[1] = Pin1;
Pin[2] = Pin2;
Pin[3] = Pin3;
Pin[4] = Pin4;
Pin[5] = Pin5;
Pin[6] = Pin6;
Pin[7] = Pin7;
Pin[8] = Pin8;
Pin[9] = Pin9;
Pin[10] = Pin10;
Pin[11] = Pin11;
Pin[12] = Pin12;
Pin[13] = Pin13;
Pin[14] = Pin14;
Pin[15] = Pin15;
Pin[16] = Pin16;
// Pin[17] = Pin17;
// Pin[18] = Pin18;
Pin[19] = Pin19;
Pin[20] = Pin20;
*/
        
/**
 * Images
 */

const uint8_t __heart[] __attribute__ ((aligned (4))) = { 0xff,0xff,5,0,5,0, 0,255,0,255,0, 255,0,255,0,255, 255,0,0,0,255, 0,255,0,255,0, 0,0,255,0,0 };
MicroBitImage Image_HEART((ImageData*) __heart);

const uint8_t __small_heart[] __attribute__ ((aligned (4))) = { 0xff,0xff,5,0,5,0, 0,0,0,0,0, 0,255,0,255,0, 0,255,255,255,0, 0,0,255,0,0, 0,0,0,0,0 };
MicroBitImage Image_HEART_SMALL((ImageData*) __small_heart);

const uint8_t __happy[] __attribute__ ((aligned (4))) = { 0xff,0xff,5,0,5,0, 0,0,0,0,0, 0,255,0,255,0, 0,0,0,0,0, 255,0,0,0,255, 0,255,255,255,0 };
MicroBitImage Image_HAPPY((ImageData*) __happy);

const uint8_t __smiley[] __attribute__ ((aligned (4))) = { 0xff,0xff,5,0,5,0,  0,0,0,0,0, 0,255,0,255,0, 0,0,0,0,0, 0,255,255,255,0, 0,0,0,0,0 };
MicroBitImage Image_SMILEY((ImageData*) __smiley);

const uint8_t __sad[] __attribute__ ((aligned (4))) = { 0xff,0xff,5,0,5,0, 0,0,0,0,0, 0,255,0,255,0, 0,0,0,0,0, 0,255,255,255,0, 255,0,0,0,255 };
MicroBitImage Image_SAD((ImageData*) __sad);

const uint8_t __surprised[] __attribute__ ((aligned (4))) = { 0xff,0xff,5,0,5,0, 0,0,0,0,0, 0,255,0,255,0, 0,0,0,0,0, 0,0,255,0,0, 0,0,0,0,0 };
MicroBitImage Image_SURPRISED((ImageData*) __surprised);

const uint8_t __beard[] __attribute__ ((aligned (4))) = { 0xff,0xff,5,0,5,0, 0,0,0,0,0, 0,255,0,255,0, 0,0,0,0,0, 0,255,255,255,0, 0,255,0,255,0 };
MicroBitImage Image_BEARD((ImageData*) __beard);

const uint8_t __moustache[] __attribute__ ((aligned (4))) = { 0xff, 0xff, 5, 0, 5,0, 0,0,0,0,0, 0,255,0,255,0, 0,0,0,0,0, 255,255,0,255,255, 0,0,255,0,0 };
MicroBitImage Image_MOUSTACHE((ImageData*) __moustache);

const uint8_t __yes[] __attribute__ ((aligned (4))) = { 0xff, 0xff, 5, 0, 5,0, 0,0,0,0,0, 0,0,0,0,255, 0,0,0,255,0, 255,0,255,0,0, 0,255,0,0,0 };
MicroBitImage Image_YES((ImageData*) __yes);

const uint8_t __no[] __attribute__ ((aligned (4))) = { 0xff, 0xff, 5, 0, 5,0, 255,0,0,0,255, 0,255,0,255,0, 0,0,255,0,0, 0,255,0,255,0, 255,0,0,0,255 };
MicroBitImage Image_NO((ImageData*) __no);

uint8_t __abz_image_data[31] = { 0xff,0xff,5,0,5,0, 0,0,0,0,0, 0,0,0,0,0, 0,0,0,0,0, 0,0,0,0,0, 0,0,0,0,0};

#endif
