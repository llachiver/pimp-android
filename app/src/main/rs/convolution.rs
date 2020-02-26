#pragma version (1)
#pragma rs java_package_name (fr.ubordeaux.pimp)
#pragma rs_fp_imprecise
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

//Classic 2D convolution
uchar4 RS_KERNEL conv2d(uchar4 in, uint32_t x, uint32_t y)
{
    uchar4 ret = 0;
    uint32_t kx, ky;
    float4 temp = 0;

    uint32_t kIndex = 0;
    for(ky = y - kCenterY; ky <= y + kCenterY ;ky++) //Columns
    {
        for(kx = x - kCenterX; kx <= x + kCenterX ;kx++) //Rows
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



uchar4 RS_KERNEL conv2dEdges(uchar4 in, uint32_t x, uint32_t y)
{
    uchar4 ret = 0;
    uint32_t kx, ky;
    float4 sum = 0;
    float4 tempX = 0;
    float4 tempY = 0;
    float4 pixelf;
    uint32_t kIndex = 0;
    for(ky = y - kCenterY; ky <= y + kCenterY ;ky++) //Columns
    {
        for(kx = x - kCenterX; kx <= x + kCenterX ;kx++) //Rows
        {

            pixelf = rsUnpackColor8888( rsGetElementAt_uchar4(pIn, kx, ky));
            tempX += pixelf * kernelX[kIndex]; //X operator
            tempY += pixelf * kernelY[kIndex]; //Y Operator
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

void RS_KERNEL extendPadding(uchar4 in, uint32_t x, uint32_t y){

    //Not a border
    if (!(x == kCenterX || y == kCenterY || x == (width - 1) - kCenterX ||  y == (height - 1) - kCenterY))
        return;
    if (x == kCenterX && y == kCenterY)
    { //Up left corner expand 90° degrees
        for (int x2 = kCenterX; x2 >= 0; x2--)
        {
            for (int y2 = kCenterY; y2 >= 0; y2--)
            {

                rsSetElementAt_uchar4(pOut, in, x2, y2);
            }
        }
        return;
    }else if (x == (width - kCenterX) - 1 && y == kCenterY) //Up right corner expand 90° degrees
    {
        for (int x2 = (width - kCenterX) - 1; x2 < width; x2++)
        {
            for (int y2 = kCenterY ; y2 >= 0; y2--)
            {

                rsSetElementAt_uchar4(pOut, in, x2, y2);
            }
        }
        return;
    }else if (x == kCenterX && y == (height - kCenterY) - 1) //Down left corner expand 90° degrees
    {
        for (int x2 = kCenterX; x2 >= 0; x2--)
        {
            for (int y2 = (height - kCenterY) - 1; y2 < height ; y2++)
            {
                rsSetElementAt_uchar4(pOut, in, x2, y2);
            }
        }
        return;
    }else if (x == (width - kCenterX) - 1 && y == (height - kCenterY) - 1) //Down right corner expand 90° degrees
    {
         for (int x2 = (width - kCenterX) - 1; x2 < width; x2++)
         {
             for (int y2 = (height - kCenterY) - 1; y2 < height ; y2++)
             {
                 rsSetElementAt_uchar4(pOut, in, x2, y2);
             }
         }
         return;
    }else if (x == kCenterX){ //Left just expand in line
        for (int x2 = kCenterX - 1; x2 >= 0; x2--)
        {
            rsSetElementAt_uchar4(pOut, in, x2, y);
        }
        return;
    } else if (x == (width - kCenterX) - 1) //right just expand in line
    {
        for (int x2 = (width - kCenterX); x2 < width; x2++)
        {
            rsSetElementAt_uchar4(pOut, in, x2, y);
        }
        return;
    } else if (y == kCenterY) { //Up just expand in line
        for (int y2 = kCenterY - 1; y2 >= 0; y2--)
        {
            rsSetElementAt_uchar4(pOut, in, x, y2);
        }
        return;

    } else if (y == (height - kCenterY) - 1)
    {//down just expand in line
        for (int y2 = (height - kCenterY); y2 < height ; y2++)
        {

            rsSetElementAt_uchar4(pOut, in, x, y2);
        }
        return;
    }



}

//////////////Indexing optimizations
static rs_script_call_t convolveOpts1dX(){
    rs_script_call_t optsX = {0};
    optsX.xStart = kCenterX;
    optsX.xEnd = (width - kCenterX);
    return optsX;
}

static rs_script_call_t convolveOpts1dY(){
    rs_script_call_t optsY = {0};
    optsY.yStart = kCenterY;
    optsY.yEnd = height - kCenterY;
    return optsY;
}

static rs_script_call_t convolveOpts2d(){
    rs_script_call_t opts = {0};
    opts.xStart = kCenterX;
    opts.xEnd = (width - kCenterX);
    opts.yStart = kCenterY;
    opts.yEnd = height - kCenterY;
    return opts;
}

void extendPaddingScript(rs_allocation image){
    //Call only in useful index
    rs_script_call_t opts = convolveOpts2d();
    opts.xStart = kCenterX;
    opts.xEnd = (width - kCenterX);
    opts.yStart = kCenterY;
    opts.yEnd = height - kCenterY;
    rsForEachWithOptions(extendPadding, &opts, image);
}

void setup(){
    kCenterX = kWidth/2;
    kCenterY = kHeight/2;
}

void convolutionSeparable(rs_allocation inputImage, rs_allocation outputImage){
    setup(); //Init kCenters

    //the result of the first convolution (the horizontal one)
    pTmp = rsCreateAllocation_uchar4(width,height);
    rs_script_call_t optsX = convolveOpts1dX();
    rs_script_call_t optsY = convolveOpts1dY();
    rsForEachWithOptions(conv2dX, &optsX, inputImage, pTmp);
    rsForEachWithOptions(conv2dY,&optsY , pTmp,  outputImage);
    rsClearObject(&pTmp);
    extendPaddingScript(outputImage);

}


void edgeDetection(rs_allocation inputImage, rs_allocation outputImage){
    setup(); //Init kCenters
    rs_script_call_t opts = convolveOpts2d();
    rsForEachWithOptions(conv2dEdges, &opts, inputImage,outputImage);
    extendPaddingScript(outputImage);
}

void convolve2d(rs_allocation inputImage, rs_allocation outputImage){
    setup(); //Init kCenters
    rs_script_call_t opts = convolveOpts2d();
    rsForEachWithOptions(conv2d, &opts ,inputImage,outputImage);
    extendPaddingScript(outputImage);


}


