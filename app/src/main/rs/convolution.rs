#pragma version (1)
#pragma rs java_package_name (fr.ubordeaux.pimp)
#pragma rs_fp_relaxed
#include "utils.rs"

//image input and output (used for kernel mapping)
rs_allocation pIn;
rs_allocation pOut;
//intermediate image after horizontal convolution
rs_allocation pTmp;

//**Filter parameters **/
uint32_t width, height;
uint32_t kWidth, kHeight;
static uint32_t kCenterX, kCenterY;

///////////////////////////////////////////////
float kdivX; //For normalize pixel with total value of kernel
float kdivY; //For normalize pixel with total value of kernel
float kdiv; //For normalize pixel with total value of kernel
//For classic convoltion-------
const float* kernel;
//--------------


const float* kernelX;
const float* kernelY;
//Normalize parameter must be set as true in the most of cases
bool normal;


uchar4 RS_KERNEL conv2d(uchar4 in, uint32_t x, uint32_t y)
{
    uchar4 ret = 0;
    if (!(x >= (kCenterX) && x < (width - kCenterX) && y >= (kCenterY) && y < (height - kCenterY))){
        ret.a = in.a;
        return ret;
    }
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
    //temp *= 1.6f;
    temp = fabs(temp);

    ret =  rsPackColorTo8888(temp);
    ret.a = in.a;
    return ret;
}



uchar4 RS_KERNEL conv2dEdges(uchar4 in, uint32_t x, uint32_t y) //Image must be gray!!
{
    uchar4 ret = 0;
    if (!(x >= (kCenterX) && x < (width - kCenterX) && y >= (kCenterY) && y < (height - kCenterY))){
        ret.a = in.a;
        return ret;
    }
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
    sum.a = 1.0f; //To assure good conversion

    ret = rsPackColorTo8888(sum);
    ret.a = in.a; //Get original alpha
    return ret;
}



uchar4 RS_KERNEL conv2dX(uchar4 in, uint32_t x, uint32_t y){
    uchar4 ret = 0;
    if (!(x >= (kCenterX) && x < (width - kCenterX))){
        ret.a = in.a;
        return ret;
    }
    uint32_t kx;
    float4 tmp = 0;
    uint32_t kIndex = 0;

    for(kx = x - kCenterX; kx <= x + kCenterX ;kx++)
    {
        tmp += rsUnpackColor8888( rsGetElementAt_uchar4(pIn, kx, y))* kernelX[kIndex];
        kIndex++;
    }
    if (normal) tmp /= kdivX; //Normalize

    tmp = fabs(tmp);

    ret = rsPackColorTo8888(tmp);
    ret.a = in.a;
    return ret;
}


uchar4 RS_KERNEL conv2dY(uchar4 in, uint32_t x, uint32_t y){
    uchar4 ret = 0;
    if (!(y >= (kCenterY) && y < (height - kCenterY))){
        ret.a = in.a;
        return ret;
    }
    uint32_t ky;
    float4 tmp = 0;
    uint32_t kIndex = 0;

    for(ky = y - kCenterY; ky <= y + kCenterY ;ky++)
    {
        tmp += rsUnpackColor8888(rsGetElementAt_uchar4(pTmp, x, ky)) * kernelY[kIndex];; //Get only one channel cause greyscale image
        kIndex++;
    }
    //??
    if (normal) tmp /= kdivY; //Normalize
    tmp = fabs(tmp);


    ret = rsPackColorTo8888(tmp);
    ret.a = in.a;
    return ret;
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
    rsClearObject(&pTmp);
    }


void edgeDetection(rs_allocation inputImage, rs_allocation outputImage){
    setup(); //Init kCenters

    rsForEach(conv2dEdges,inputImage,outputImage);
}



