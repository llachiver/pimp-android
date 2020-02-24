# pragma version (1)
# pragma rs java_package_name ( fr.ubordeaux.pimp)
#include "utils.rs"

double randh;
double tolerance;

uchar4 RS_KERNEL keepColor (uchar4 in){
    const float4 pixelf = rsUnpackColor8888 ( in ) ;
    const float4 hsv = RGBtoHSV (pixelf);
    if (hsv.s0 >= randh - tolerance && hsv.s0 <= randh + tolerance ){
    float4 pixel = HSVtoRGB (hsv);
    return rsPackColorTo8888 ( pixel.s0, pixel.s1, pixel.s2, pixel.s3);
    }
    else{
        const float gray = dot(pixelf , weight);
        return rsPackColorTo8888 (gray, gray, gray, pixelf.a) ;
    }
}