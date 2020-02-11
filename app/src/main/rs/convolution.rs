#pragma version (1)
#pragma rs java_package_name (fr.ubordeaux.pimp)
#pragma rs_fp_relaxed
#include "utils.rs"

//image input and output (used for kernel mapping)
rs_allocation pIn;
rs_allocation pOut;
//intermediate image after horizontal convolution
rs_allocation pTmp;

uint32_t width, height;
uint32_t kWidth, kHeight;
static uint32_t kCenterX, kCenterY;
float kdiv; //For normalize pixel with total value of kernel
const float* kernel;
const float* kernelX;
const float* kernelY;
bool normal;


uchar4 RS_KERNEL conv2d(uchar4 in, uint32_t x, uint32_t y)
{
    if (!(x >= (kCenterX) && x < (width - kCenterX) && y >= (kCenterY) && y < (height - kCenterY)))
        return rsPackColorTo8888(0.0f,0.0f,0.0f,1.0f);
    uint32_t kx, ky;
    float4 temp = 0;

    uint32_t kIndex = 0;
    for(ky = y - kCenterY; ky <= y + kCenterY ;ky++)
    {
        for(kx = x - kCenterX; kx <= x + kCenterX ;kx++)
        {

            temp += rsUnpackColor8888( rsGetElementAt_uchar4(pIn, kx, ky)) * kernel[kIndex];
            kIndex++;

        }
    }

    if (normal) temp /= kdiv; //Normalize
    temp = fabs(temp);

    temp.a = 1.00f;
    return rsPackColorTo8888(temp);
}



uchar4 RS_KERNEL conv2dSobel(uchar4 in, uint32_t x, uint32_t y) //Image must be gray!!
{
    if (!(x >= (kCenterX) && x < (width - kCenterX) && y >= (kCenterY) && y < (height - kCenterY)))
        return rsPackColorTo8888(0.0f,0.0f,0.0f,1.0f);
    uint32_t kx, ky;
    float sum = 0;
    float tempX = 0;
    float tempY = 0;
    float4 pixelf;
    uint32_t kIndex = 0;
    for(ky = y - kCenterY; ky <= y + kCenterY ;ky++)
    {
        for(kx = x - kCenterX; kx <= x + kCenterX ;kx++)
        {

            pixelf = rsUnpackColor8888( rsGetElementAt_uchar4(pOut, kx, ky)); //Get only one channel cause greyscale image
            tempX += pixelf.r * kernelX[kIndex];
            tempY += pixelf.r * kernelY[kIndex];
            kIndex++;



        }
    }

    sum = fabs(tempX) + fabs(tempY);

    return rsPackColorTo8888(sum,sum,sum, 1.00f);
}



float4 RS_KERNEL conv2dX(uchar4 in, uint32_t x, uint32_t y){

    float4 ret = 0;
    ret.a = 1.0f;
    if (!(x >= (kCenterX) && x < (width - kCenterX))){
        return ret;
    }
    uint32_t kx;
    float4 tmp = 0;
    uint32_t kIndex = 0;

    for(kx = x - kCenterX; kx <= x + kCenterX ;kx++)
    {
        tmp = rsUnpackColor8888( rsGetElementAt_uchar4(pIn, kx, y)); //Get only one channel cause greyscale image
        ret += tmp * kernelX[kIndex];
        kIndex++;
    }

    return ret;
}


uchar4 RS_KERNEL conv2dY(float4 in, uint32_t x, uint32_t y){
    float4 ret = 0;
    ret.a = 1.0f;
    if (!(y >= (kCenterY) && y < (height - kCenterY))){
        return rsPackColorTo8888(ret);
    }
    uint32_t ky;
    float4 tmp = 0;
    uint32_t kIndex = 0;

    for(ky = y - kCenterY; ky <= y + kCenterY ;ky++)
    {
        tmp = rsGetElementAt_float4(pTmp, x, ky); //Get only one channel cause greyscale image
        ret += tmp * kernelY[kIndex];
        kIndex++;
    }
    //??
    if (normal) ret /= kdiv;
    ret = fabs(ret);

    return rsPackColorTo8888(ret);
}


void setup(){
    kCenterX = kWidth/2;
    kCenterY = kHeight/2;
}

void convolutionSeparable(rs_allocation inputImage, rs_allocation outputImage){
    setup(); //Init kCenters

    //the result of the first convolution (the horizontal one)
    pTmp = rsCreateAllocation_float4(width,height);

    rsForEach(conv2dX,inputImage, pTmp);
    rsForEach(conv2dY,pTmp, outputImage);
}


void sobelOperator(rs_allocation inputImage, rs_allocation outputImage){
    rsForEach(grey, inputImage, outputImage); // Turn to gray
    setup(); //Init kCenters

    //We send back the image into input for gaining memory
    rsForEach(conv2dSobel,outputImage,inputImage);
}



