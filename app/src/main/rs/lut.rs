#pragma version (1)
#pragma rs java_package_name ( fr.ubordeaux.pimp)
#pragma rs_fp_relaxed

#define LUT_SIZE 256
#define NBR_COLOR_CHANS 3

#include "utils.rs"

//the min & max values of the bitmap (calculated with the findMinMax script)
uchar2 minMaxRGB[NBR_COLOR_CHANS];

//the new min & max RGB values of the bitmap (after the dynamic extension)
int2 newMinMaxRGB[NBR_COLOR_CHANS];

//LUT for the 3 RGB channels
static uchar3 lutRGB [LUT_SIZE];

//LUT for the value channel
static uchar lutValue[LUT_SIZE];

//Computes a RGB LUT
void RS_KERNEL computeLutRGB(uchar in, uint32_t x){
    lutRGB[x].r = truncate(newMinMaxRGB[0].x + (newMinMaxRGB[0].y - newMinMaxRGB[0].x) * (x - minMaxRGB[0].x) / (minMaxRGB[0].y - minMaxRGB[0].x));
    lutRGB[x].g = truncate(newMinMaxRGB[1].x + (newMinMaxRGB[1].y - newMinMaxRGB[1].x) * (x - minMaxRGB[1].x) / (minMaxRGB[1].y - minMaxRGB[1].x));
    lutRGB[x].b = truncate(newMinMaxRGB[2].x + (newMinMaxRGB[2].y - newMinMaxRGB[2].x) * (x - minMaxRGB[2].x) / (minMaxRGB[2].y - minMaxRGB[2].x));
}

//Computes a single value LUT
void RS_KERNEL computeLutValue(uchar in, uint32_t x){
    lutValue[x] = truncate(newMinMaxRGB[0].x + (newMinMaxRGB[0].y - newMinMaxRGB[0].x) * (x - minMaxRGB[0].x) / (minMaxRGB[0].y - minMaxRGB[0].x));
}


uchar4 RS_KERNEL assignLutRGB(uchar4 in){
    uchar4 out;
    if (in.a == 0) return in;
    out.r =  lutRGB[in.r].r;
    out.g =  lutRGB[in.g].g;
    out.b =  lutRGB[in.b].b;

    out.a = in.a;

    return out;
}
