#pragma version (1)
#pragma rs java_package_name (fr.ubordeaux.pimp)
#pragma rs_fp_relaxed

rs_allocation pIn;
uint32_t width, height;
uint32_t kWidth, kHeight;
static uint32_t kCenterX, kCenterY;
float kdiv; //For normalize pixel with total value of kernel
float* kernel;
bool normal;
bool mirrorPadding;


uchar4 RS_KERNEL conv2d(uchar4 in, uint32_t x, uint32_t y)
{
    if (!(x >= (kCenterX-1) && x <= (width - kCenterX) && y >= (kCenterY-1) && y <= (height - kCenterY)))
    {
        if (!mirrorPadding)
            return rsPackColorTo8888(0.0f,0.0f,0.0f,0.0f);
        return in;
    }
    uint32_t kx, ky;
    float4 temp = 0;
  //  const uchar4* kin = in - (kCenterX) - (kCenterY) * width; //Bugging shit
    //rsDebug("kIn", *kin);
    //You can do better than your code and java if you seek only in the good values range...
    uint32_t kIndex = 0;
    for(ky = y - kCenterY; ky <= y + kCenterY ;ky++)
    {
        for(kx = x - kCenterX; kx <= x + kCenterX ;kx++)
        {
           //if (kx >= (kCenterX-1) && kx <= (width - kCenterX) && ky >= (kCenterY-1) && ky <= (height - kCenterY))
           //{
                temp += rsUnpackColor8888( rsGetElementAt_uchar4(pIn, kx, ky)) * kernel[kIndex];
                kIndex++;
           //}else
                //rsDebug("TATA",x,y);

        }
    }

    if (normal) temp /= kdiv; //Normalize
    temp = fabs(temp);

    temp.a = 1.00f;
    return rsPackColorTo8888(temp);
}

void setup(){
    kCenterX = kWidth/2;
    kCenterY = kHeight/2;
}

