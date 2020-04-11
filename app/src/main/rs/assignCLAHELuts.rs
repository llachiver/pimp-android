#pragma version (1)
#pragma rs java_package_name ( fr.ubordeaux.pimp)
#pragma rs_fp_relaxed

#define LUT_SIZE 256
#define NBR_COLOR_CHANS 3

#include "utils.rs"

//Number of regions in x and y (in the whole image)
int regNbrX, regNbrY;

//Actuel region indexes
int regIdxX, regIdxY;

//Size of the region
int regSizeX, regSizeY;

//Center of the actual region
int regCenterX, regCenterY;

//LUT of the actual region
uchar lutValue[LUT_SIZE];

//LUT of the surrounding cases
uchar lutNorth[LUT_SIZE];
uchar lutEast[LUT_SIZE];
uchar lutSouth[LUT_SIZE];
uchar lutWest[LUT_SIZE];


//Assign LUT fot HSV value
uchar4 RS_KERNEL assignLutHSV(uchar4 in, uint32_t x, uint32_t y){
    float4 out = rsUnpackColor8888(in);
    if (in.a == 0) return in;
    out = RGBtoHSV(out); //Change to HSV

    int xClose,xFar;

    uint32_t v = (uint32_t) (out.s2 * 255.0);

    if((regIdxY == 0 && y < regCenterY) ||  (regIdxY == regNbrY-1 && y > regCenterY)){
        float value;
        xClose = x > regCenterX ? x - regCenterX : regCenterX - x;
        xFar = regSizeX - xClose;
        if(x<regCenterX){
            value = xFar*lutValue[v] + xClose*lutWest[v];
        }
        else{
            value = xFar*lutValue[v] + xClose*lutEast[v];
        }
        //out.s0 = 0.0;
        //out.s1 = 1.0;
        out.s2 = value / ((float) regSizeX * 255.0);
    }
    else{
        out.s2 = lutValue[v] / 255.0;
    }

    out = HSVtoRGB(out);
    return rsPackColorTo8888(out.r , out.g , out.b , out.a);
}



//rsDebug("rsdebug : coeffs ",xCoef,xInvCoef,yCoef,yInvCoef);
    //out.s2 = lutValue[(uint32_t) (out.s2 * 255)]; //Search new HSV value
    //float value = (xCoef*lutWest[v] + xInvCoef*lutEast[v] + yCoef*lutSouth[v] + xInvCoef*lutNorth[v])/((float)(regSizeX*regSizeY));
    //rsDebug("rsdebug : luts ",lutWest[v],lutEast[v],lutSouth[v],lutNorth[v]);


//uint32_t xCoef = x - regIdxX*regSizeX;
//uint32_t xInvCoef = regSizeX - xCoef;
//uint32_t yCoef = y - regIdxY*regSizeY;
//uint32_t yInvCoef = regSizeY - yCoef;
