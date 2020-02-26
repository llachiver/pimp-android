#pragma version (1)
#pragma rs java_package_name (fr.ubordeaux.pimp)
#pragma rs_fp_imprecise
#include "utils.rs"

float factor;

uchar4 RS_KERNEL setSaturation ( uchar4 in  ) {
    float4 hsv = RGBtoHSV(rsUnpackColor8888(in));

    //If we increase the saturation...
    if(factor >= 0){
        //(1 - hsv.s1) is the remaining space for a total saturation.
        //We multiply the factor by hsv.s1 at the end to avoid saturating grayscale.
        hsv.s1 = hsv.s1 + factor * (1 - hsv.s1) * hsv.s1;
    }
    //...If we decrease it
    else{
        hsv.s1 = hsv.s1 + factor * hsv.s1;
    }

    return rsPackColorTo8888(HSVtoRGB(hsv));
}