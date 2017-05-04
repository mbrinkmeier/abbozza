#ifndef _ABBOZZA_H
#define _ABBOZZA_H

const uint8_t __heart[] __attribute__ ((aligned (4))) = { 0xff,0xff,5,0,5,0, 0,255,0,255,0, 255,0,255,0,255, 255,0,0,0,255, 0,255,0,255,0, 0,0,255,0,0 };
MicroBitImage Image_HEART((ImageData*) __heart);

const uint8_t __small_heart[] __attribute__ ((aligned (4))) = { 0xff,0xff,5,0,5,0, 0,0,0,0,0, 0,255,0,255,0, 0,255,255,255,0, 0,0,255,0,0, 0,0,0,0,0 };
MicroBitImage Image_SMALL_HEART((ImageData*) __small_heart);

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

MicroBitImage __abz_image;

void __set_abz_image() {
}

#endif
