#pragma version (1)
#pragma rs java_package_name ( fr.ubordeaux.pimp)
#pragma rs_fp_relaxed


#include "utils.rs"

#define MIN_MAX_SIZE_RGB 3
#define LUT_SIZE 256

//LUT single for gray or HSV (Value)
uchar lutSingle[LUT_SIZE];

//LUT RGB with 3 channels
static uchar3 lutRGB [LUT_SIZE];

//Compute Lut
uchar4 RS_KERNEL assignLutSingle(uchar4 in){
    uchar4 out;
    out.rgb = lutSingle[in.r];
    out.a = in.a;
    return out;
}


//Compute LUT fot value HSV
uchar4 RS_KERNEL assignLutHSV(uchar4 in){
    float4 out = rsUnpackColor8888(in);
    out = RGBtoHSV(out); //Change to HSV
    //uint32_t tmp = (uint32_t) (out.s2 * 255);
    out.s2 = lutSingle[(uint32_t) (out.s2 * 255)]; //Search new HSV value
    out.s2 /= 255.0; //back to 0..1 range
    //rsDebug("v: ", tmp);
    out = HSVtoRGB(out);


    return rsPackColorTo8888(out.r , out.g , out.b , out.a);
}

//Assign LUT into image
uchar4 RS_KERNEL assignLutRGB(uchar4 in) {
    uchar4 out;
    out.r =  lutRGB[in.r].r;
    out.g =  lutRGB[in.g].g;
    out.b =  lutRGB[in.b].b;

    out.a = in.a;

    return out;

}

//LUT for RGB luminance computation
uchar4 RS_KERNEL assignLutRGBAverage(uchar4 in) {
    uchar4 out;
    out.r =  lutSingle[in.r];
    out.g =  lutSingle[in.g];
    out.b =  lutSingle[in.b];

    out.a = in.a;

    return out;

}