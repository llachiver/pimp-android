#pragma version(1)
#pragma rs java_package_name(fr.ubordeaux.pimp)

static float3 rgb2hsv( float4 rgb)
{
    float3 hsv;

    float r = rgb.r;
    float g = rgb.g;
    float b = rgb.b;

    float _max = max(max(r,g),b);
    float _min = min(min(r,g),b);

    float h = 0;
    float s,v;

    if(_max == _min)
        h=0;
    else if(_max == r)
        h =  ((int) (60 * (g-b)/(_max - _min) + 360)) % 360;
    else if(_max == g)
        h = 60 * (b-r)/(_max-_min) + 120;
    else if(_max == b)
        h = 60 * (r-g)/(_max-_min) + 240;

    s = (_max==0) ? 0 : (1-(_min/_max));
    v = _max;

    hsv.s0 = h;
    hsv.s1 = s;
    hsv.s2 = v;
    return hsv;
}

static float4 hsv2rgb(float3 hsv)
{
    float4 rgb;

    float h = hsv.s0;
    float s = hsv.s1;
    float v = hsv.s2;

    int ti;
    float f,l,m,n;

    int color = 0;

    ti = (int) (h/60)%6;
    f = h/60 - ti;
    l = v * (1-s);
    m = v * (1-f*s);
    n = v*(1-(1-f)*s);

    switch(ti){
        case 0:
            rgb.r = v;
            rgb.g = n;
            rgb.b = l;
            break;
        case 1 :
            rgb.r = m;
            rgb.g = v;
            rgb.b = l;
            break;
        case 2 :
            rgb.r = l;
            rgb.g = v;
            rgb.b = n;
            break;
        case 3 :
            rgb.r = l;
            rgb.g = m;
            rgb.b = v;
            break;
        case 4 :
            rgb.r = n;
            rgb.g = l;
            rgb.b = v;
            break;
        case 5 :
            rgb.r = v;
            rgb.g = l;
            rgb.b = m;
            break;
    }
    rgb.a = 1;
    return rgb;
}