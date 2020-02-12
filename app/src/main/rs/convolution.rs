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
uint32_t kdivX; //For normalize pixel with total value of kernel
uint32_t kdivY; //For normalize pixel with total value of kernel
uint32_t kdiv; //For normalize pixel with total value of kernel
const int* kernel;
const int* kernelX;
const int* kernelY;
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
    float4 sum = 0;
    float4 tempX = 0;
    float4 tempY = 0;
    float4 pixelf;
    uint32_t kIndex = 0;
    for(ky = y - kCenterY; ky <= y + kCenterY ;ky++)
    {
        for(kx = x - kCenterX; kx <= x + kCenterX ;kx++)
        {

            pixelf = rsUnpackColor8888( rsGetElementAt_uchar4(pIn, kx, ky)); //Get only one channel cause greyscale image
            tempX += pixelf * kernelX[kIndex];
            tempY += pixelf * kernelY[kIndex];
            kIndex++;



        }
    }

    sum = fabs(tempX) + fabs(tempY);
    sum.a = 1.0f;

    return rsPackColorTo8888(sum);
}



uchar4 RS_KERNEL conv2dX(uchar4 in, uint32_t x, uint32_t y){

    float4 ret = 0;
    ret.a = 1.0f;
    if (!(x >= (kCenterX) && x < (width - kCenterX))){
        return rsPackColorTo8888(ret);
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
    if (normal) ret /= kdivX; //Normalize

    return rsPackColorTo8888(ret);
}


uchar4 RS_KERNEL conv2dY(uchar4 in, uint32_t x, uint32_t y){
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
        tmp = rsUnpackColor8888(rsGetElementAt_uchar4(pTmp, x, ky)); //Get only one channel cause greyscale image
        ret += tmp * kernelY[kIndex];
        kIndex++;
    }
    //??
    if (normal) ret /= kdivY; //Normalize

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
    pTmp = rsCreateAllocation_uchar4(width,height);

    rsForEach(conv2dX,inputImage, pTmp);
    rsForEach(conv2dY,pTmp, outputImage);
}


void sobelOperator(rs_allocation inputImage, rs_allocation outputImage){
    //rsForEach(grey, inputImage, outputImage); // Turn to gray
    setup(); //Init kCenters

    //We send back the image into input for gaining memory
    rsForEach(conv2dSobel,inputImage,outputImage);
}



