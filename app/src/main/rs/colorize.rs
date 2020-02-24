# pragma version (1)
# pragma rs java_package_name ( fr.ubordeaux.pimp)
#include "utils.rs"

double randh;

uchar4 RS_KERNEL colorize ( uchar4 in  ) {

const float4 pixelf = rsUnpackColor8888 ( in ) ;
const float4 hsv = RGBtoHSV (pixelf);
hsv.s0 = (int) randh;
float4 pixel = HSVtoRGB (hsv);
return rsPackColorTo8888 ( pixel.s0 , pixel.s1 , pixel.s2 , pixel.s3) ;
}