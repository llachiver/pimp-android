#pragma version (1)
#pragma rs java_package_name ( fr.ubordeaux.pimp)
#pragma rs_fp_relaxed


#include "utils.rs"

int selectedHue;
int tolerance;

uchar4 RS_KERNEL keepColor (uchar4 in){
    const float4 pixelf = rsUnpackColor8888 ( in ) ;
    const float4 hsv = RGBtoHSV (pixelf);
    if (hsv.s0 >= selectedHue - tolerance && hsv.s0 <= selectedHue + tolerance ){
        float4 pixel = HSVtoRGB (hsv);
        return rsPackColorTo8888 (pixel);
    }
    else{
        const float gray = dot(pixelf , weight);
        return rsPackColorTo8888 (gray, gray, gray, pixelf.a) ;
    }
}