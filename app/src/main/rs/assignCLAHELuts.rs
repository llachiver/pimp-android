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

static bool corner(int x, int y){
    return (regIdxY == 0 &&
                ((regIdxX == 0 && x < regCenterX && y < regCenterY) ||
                  (regIdxX == (regNbrX-1) && x > regCenterX && y < regCenterY))) ||
           (regIdxY == (regNbrY-1) &&
                  ((regIdxX == 0 && x < regCenterX && y > regCenterY)||
                  (regIdxX == (regNbrX-1) && x > regCenterX && y > regCenterY)));
}

static bool top_or_bottom(int x, int y){
    return ((regIdxY == 0 && y < regCenterY) || (regIdxY == regNbrY-1 && y > regCenterY)) && (x > regSizeX/2 && x < (regNbrX*regSizeX + regSizeX/2));
}

static bool left_or_right(int x, int y){
    return (regIdxX == 0 && x < regCenterX) || (regIdxX == regNbrX-1 && x > regCenterX) && (y > regSizeY/2 && y < (regNbrY*regSizeY + regSizeY/2));
}


//Assign LUT fot HSV value
uchar4 RS_KERNEL assignLutHSV(uchar4 in, uint32_t x, uint32_t y){
    float4 out = rsUnpackColor8888(in);
    if (in.a == 0) return in;

    out = RGBtoHSV(out); //Change to HSV
    uint32_t v = (uint32_t) (out.s2 * 255.0);

    if(corner(x, y)){
        out.s2 = lutValue[v] / 255.0;
    }
    else{
        //The final terms of the interpolation
        int x1, x2, y1, y2;
        //The normalization factor for the interpolation
        float normFactor;

        if(top_or_bottom(x,y)){ //linear interpolation
            int xCoeffHeavy,xCoeffLight;
            xCoeffLight = abs(((int) x) - regCenterX);
            xCoeffHeavy = regSizeX - xCoeffLight;
            x1 = xCoeffHeavy*lutValue[v];
            x2 = x < regCenterX ? xCoeffLight*lutWest[v] : xCoeffLight*lutEast[v];
            y1 = 0 ; y2 = 0;
            normFactor = (float) regSizeX * 255.0;

        }
        else if(left_or_right(x,y)){ //linear interpolation
            int yCoeffHeavy, yCoeffLight;
            yCoeffLight = abs(((int) y) - regCenterY);
            yCoeffHeavy = regSizeY - yCoeffLight;
            y1 = yCoeffHeavy*lutValue[v];
            y2 = y < regCenterY ? yCoeffLight*lutNorth[v] : yCoeffLight*lutSouth[v];
            x1 = 0 ; x2 = 0;
            normFactor = (float) regSizeY * 255.0;
        }
        else{ //bilinear interpolation
            int yCoeffHeavy, yCoeffLight;
                        yCoeffLight = abs(((int) y) - regCenterY);
                        yCoeffHeavy = regSizeY - yCoeffLight;
                        y1 = yCoeffHeavy*lutValue[v];
                        y2 = y < regCenterY ? yCoeffLight*lutNorth[v] : yCoeffLight*lutSouth[v];
            normFactor = (float) regSizeY * 255.0;
            //rsDebug("value", (x1 + x2 + y1 + y2), normFactor);
        }

        out.s2 = (x1 + x2 + y1 + y2) / normFactor;
        //rsDebug("out : ", out.s2);
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
