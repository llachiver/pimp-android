#pragma version (1)
#pragma rs java_package_name ( fr.ubordeaux.pimp)
#pragma rs_fp_relaxed
#include "utils.rs"

int selectedHue;
bool uniform;
uchar4 RS_KERNEL colorize ( uchar4 in  ) {

    const float4 pixelf = rsUnpackColor8888 ( in ) ;
    const float4 hsv = RGBtoHSV (pixelf);
    if (!uniform )
        hsv.s0 =(int) (( hsv.s0) + selectedHue) % 360 ;
    else
        hsv.s0 = selectedHue;
    float4 pixel = HSVtoRGB (hsv);
    return rsPackColorTo8888 ( pixel) ;
}