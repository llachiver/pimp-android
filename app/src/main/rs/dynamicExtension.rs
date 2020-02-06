#pragma version (1)
#pragma rs java_package_name ( com.example.ColorStudio)
#pragma rs_fp_relaxed

#define NBR_COLOR_CHANS 3
#define LUT_SIZE 256

#include "utils.rs"

//the contrast factor, controlled by the user with the seekbar
int factor;

//the min & max values of the bitmap (calculated with the findMinMax script)
uchar2 minMaxRGB[NBR_COLOR_CHANS];

//the new min & max RGB values of the bitmap (after the dynamic extension)
uchar2 newMinMaxRGB[NBR_COLOR_CHANS];

//the min & max values of the bitmap (calculated with the findMinMax script)
uchar2 minMaxGray;

//the new min & max of the value of the bitmap (after the dynamic extension)
uchar2 newMinMaxGray;

//LUT for the 3 RGB channels
static uchar3 lutRGB [LUT_SIZE];

//LUT for the value (max between 3 RGB channels)
uchar lutGray[LUT_SIZE];

void RS_KERNEL computeLutGray(uchar in, uint32_t x){
    lutGray[x] = newMinMaxGray.x + (newMinMaxGray.y - newMinMaxGray.x) * (x - minMaxGray.x) / (minMaxGray.y - minMaxGray.x);
}

void RS_KERNEL computeLutRGB(uchar in, uint32_t x){
    lutRGB[x].r = newMinMaxRGB[0].x + (newMinMaxRGB[0].y - newMinMaxRGB[0].x) * (x - minMaxRGB[0].x) / (minMaxRGB[0].y - minMaxRGB[0].x);
    lutRGB[x].g = newMinMaxRGB[1].x + (newMinMaxRGB[1].y - newMinMaxRGB[1].x) * (x - minMaxRGB[1].x) / (minMaxRGB[1].y - minMaxRGB[1].x);
    lutRGB[x].b = newMinMaxRGB[2].x + (newMinMaxRGB[2].y - newMinMaxRGB[2].x) * (x - minMaxRGB[2].x) / (minMaxRGB[2].y - minMaxRGB[2].x);
}


uchar4 RS_KERNEL assignLutGray(uchar4 in) {
    uchar4 out;
    out.r =  lutGray[in.r];
    out.g =  lutGray[in.g];
    out.b =  lutGray[in.b];

    out.a = in.a;

    return out;

}

uchar4 RS_KERNEL assignLutRGB(uchar4 in){
    uchar4 out;
    out.r =  lutRGB[in.r].r;
    out.g =  lutRGB[in.g].g;
    out.b =  lutRGB[in.b].b;

    out.a = in.a;

    return out;
}

void computeNewMinMaxGray(){
    uchar middle;
    middle = (minMaxGray.y - minMaxGray.x)/2 + minMaxGray.x;
    newMinMaxGray.x = middle - factor < 0 ? 0 : middle - factor;
    newMinMaxGray.y = middle + factor > 255 ? 255 : middle + factor;
}

void computeNewMinMaxRGB(){
    uchar middle;
    for(uchar i = 0; i < NBR_COLOR_CHANS; ++i){
        middle = (minMaxRGB[i].y - minMaxRGB[i].x)/2 + minMaxRGB[i].x;
        newMinMaxRGB[i].x = middle - factor < 0 ? 0 : middle - factor;
        newMinMaxRGB[i].y = middle + factor > 255 ? 255 : middle + factor;
    }
}

void dynamicExtensionRGB(rs_allocation inputImage, rs_allocation outputImage){
    computeNewMinMaxRGB();
    rs_allocation lutRGBIn = rsCreateAllocation_uchar(256);
    rsForEach(computeLutRGB, lutRGBIn);
    rsForEach(assignLutRGB, inputImage, outputImage);
}

void dynamicExtensionGray(rs_allocation inputImage, rs_allocation outputImage){
    computeNewMinMaxGray();
    rs_allocation lutGrayIn = rsCreateAllocation_uchar(256);
    rsForEach(computeLutGray, lutGrayIn);
    rsForEach(assignLutGray,inputImage,outputImage);
}



