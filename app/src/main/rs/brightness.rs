#pragma version (1)
#pragma rs java_package_name ( fr.ubordeaux.pimp)
#pragma rs_fp_relaxed

int factor;

uchar4 RS_KERNEL setBrightness ( uchar4 in  ) {

    uchar4 out;
    if (in.a == 0) return in;
    out.r = in.r + factor > 255 ? 255 : (in.r + factor < 0 ? 0 : in.r + factor);
    out.g = in.g + factor > 255 ? 255 : (in.g + factor < 0 ? 0 : in.g + factor);
    out.b = in.b + factor > 255 ? 255 : (in.b + factor < 0 ? 0 : in.b + factor);
    out.a = in.a;

    return out;
}