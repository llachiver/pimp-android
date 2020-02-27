#pragma version(1)
#pragma rs java_package_name(fr.ubordeaux.pimp)
#pragma rs_fp_relaxed

//Truncates a color value for converting it into an unsigned char (from 0 to 255)
static char truncate(int value){
    return (uchar) (value < 0 ? 0 : (value > 255 ? 255 : value));
}

static float4 RGBtoHSV ( float4 pixelf ) {
    float k, cmin, cmax, delta, h, s, v;
    const float4 hsv;

    cmin = (pixelf.r < pixelf.g) ? pixelf.r : pixelf.g;
    cmin = (cmin  < pixelf.b) ? cmin  : pixelf.b;

    cmax = (pixelf.r > pixelf.g) ? pixelf.r : pixelf.g;
    cmax = (cmax  > pixelf.b) ? cmax  : pixelf.b;

    delta = cmax - cmin;

    if (cmax == 0) {
        h = 0; s = 0; v = 0;
    }else{

        if(cmax == pixelf.r) {
            h = ((((pixelf.g - pixelf.b) / delta)));
        }else if(cmax == pixelf.g) {
            h = ((((pixelf.b - pixelf.r) / delta) + 2.0f));
        }else if(cmax == pixelf.b) {
            h  = ( (((pixelf.r - pixelf.g) / delta) + 4.0f));
        }
        h *= 60;
       if (h < 0) { h += 360; }
       if (h == 360) { h = 0; }
       //h = rnd_hue; // Replacing for random hue

        s = (delta) / cmax; //Convert only saturation and value because hue will be replaced by random hue

        v = cmax;
    }
    hsv.s0 = h; hsv.s1 = s; hsv.s2 = v; hsv.s3 = pixelf.a;

    return hsv;


}

static float4 HSVtoRGB ( float4 hsv ) {
    float k, cmin, cmax, delta, h, s, v;
    const float4 pixelf;
    h = hsv.s0; s = hsv.s1; v = hsv.s2;
    if (s == 0) //achromatic gray
    {
        pixelf.r = v  ; pixelf.g = v ; pixelf.b = v ; pixelf.a = hsv.a;
    }else{

        // Conversion values

        float tempH = h;
        tempH /= 60.0f;
        int i = (int) floor(tempH);
        float f = tempH - i;
        float p = v * (1 - s);
        float q = v * (1 - s * f);
        float t = v * (1 - s * (1 - f));

        // There are 6 cases, one for every 60 degrees
        switch (i)
        {
            case 0:
                pixelf.r = v;
                pixelf.g = t;
                pixelf.b = p;
                break;

            case 1:
                pixelf.r = q;
                pixelf.g = v;
                pixelf.b = p;
                break;

            case 2:
                pixelf.r = p;
                pixelf.g = v;
                pixelf.b = t;
                break;

            case 3:
                pixelf.r = p;
                pixelf.g = q;
                pixelf.b = v;
                break;

            case 4:
                pixelf.r = t;
                pixelf.g = p;
                pixelf.b = v;
                break;

            // Case 5
            default:
                pixelf.r = v;
                pixelf.g = p;
                pixelf.b = q;
                break;
        }
    }
    pixelf.s3 = hsv.a;
    return pixelf;
}

//Gray constants
static const float4 weight = {0.299f, 0.587f, 0.114f, 0.0f};

uchar4 RS_KERNEL grey ( uchar4 in  ) {

    const float4 pixelf = rsUnpackColor8888 ( in ) ;

    const float gray = dot(pixelf , weight);


    uchar4 out = rsPackColorTo8888 ( gray , gray , gray , pixelf.a ) ;



    return out;
}

uchar4 RS_KERNEL invert (uchar4 in ) {
    uchar4 out = in;
    if (in.a == 0) return in;
    out.r = 255 - in.r;
    out.g = 255 - in.g;
    out.b = 255 - in.b;
    return out;


}