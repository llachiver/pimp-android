#pragma version (1)
#pragma rs java_package_name (fr.ubordeaux.pimp)
#pragma rs_fp_relaxed


uint32_t width, height;
uint32_t kWidth, kHeight;
static uint32_t kCenterX, kCenterY;
float kdiv; //For normalize pixel with total value of kernel
float* kernel;
bool normal;


void conv2d(const uchar4* in, uchar4* out, uint32_t x, uint32_t y){
    if(x < kCenterX || y < kCenterY){ *out = *in; return;}
    if ((x > width - kCenterX) || (y > height - kCenterY)){ *out = *in; return;}//Respect bounds

    uint8_t kx, ky;
    float4 temp = 0;
    const uchar4* kin = in - (kCenterX) - (kCenterY) * width;
    //rsDebug("Totona", &in);
    //You can do better than your code and java if you seek only in the good values range...
    for(kx = 0; kx < kWidth; kx++)
    {
        for(ky = 0; ky < kHeight;ky++)
        {
            temp += rsUnpackColor8888(kin[kx + (kWidth * ky)]) * kernel[kx + (kWidth * ky)];
        }
    }
    if (normal) temp /= kdiv; //Normalize
    temp.a = 1.00f;
    *out =  rsPackColorTo8888(temp);


}

void setup(){
    kCenterX = kWidth / 2;
    kCenterY = kHeight / 2;

}


