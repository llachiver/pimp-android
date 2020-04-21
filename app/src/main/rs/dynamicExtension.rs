#pragma version (1)
#pragma rs java_package_name (fr.ubordeaux.pimp)
#pragma rs_fp_relaxed

//Declare minMaxRGB, newMinMaxRGB, lutValue, lutValue and computeLut + assignLut functions.
#include "lut.rs"

//the contrast factor, controlled by the user with the seekbar
int factor;

void computeNewMinMaxRGB(){
    uchar middle;
    for(uchar i = 0; i < NBR_COLOR_CHANS; ++i){
        middle = (minMaxRGB[i].y - minMaxRGB[i].x)/2 + minMaxRGB[i].x;
        newMinMaxRGB[i].x = middle - factor;
        newMinMaxRGB[i].y = middle + factor;
    }
}

void dynamicExtensionRGB(rs_allocation inputImage, rs_allocation outputImage){
    computeNewMinMaxRGB();
    rs_allocation lutRGBIn = rsCreateAllocation_uchar(256);
    rsForEach(computeLutRGB, lutRGBIn);
    rsForEach(assignLutRGB, inputImage, outputImage);
}
