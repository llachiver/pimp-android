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
uchar lutThis[LUT_SIZE];

//LUT of the surrounding cases
uchar lutN[LUT_SIZE];
uchar lutE[LUT_SIZE];
uchar lutS[LUT_SIZE];
uchar lutW[LUT_SIZE];
uchar lutNW[LUT_SIZE];
uchar lutNE[LUT_SIZE];
uchar lutSW[LUT_SIZE];
uchar lutSE[LUT_SIZE];

static bool corner(int x, int y){
    return (regIdxY == 0 &&
                ((regIdxX == 0 && x <= regCenterX && y <= regCenterY) ||
                  (regIdxX == (regNbrX-1) && x >= regCenterX && y <= regCenterY))) ||
           (regIdxY == (regNbrY-1) &&
                  ((regIdxX == 0 && x <= regCenterX && y >= regCenterY)||
                  (regIdxX == (regNbrX-1) && x >= regCenterX && y >= regCenterY)));
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
        out.s2 = lutThis[v] / 255.0;
    }
    else{
        if(top_or_bottom(x,y)){ //linear interpolation
            int x1, x2;
            int xCoeffHeavy,xCoeffLight;
            xCoeffLight = abs(((int) x) - regCenterX);
            xCoeffHeavy = regSizeX - xCoeffLight;
            x1 = xCoeffHeavy*lutThis[v];
            x2 = x < regCenterX ? xCoeffLight*lutW[v] : xCoeffLight*lutE[v];
            out.s2 = (x1 + x2) / ((float) regSizeX * 255.0);
        }
        else if(left_or_right(x,y)){ //linear interpolation
            int y1,y2;
            int yCoeffHeavy, yCoeffLight;
            yCoeffLight = abs(((int) y) - regCenterY);
            yCoeffHeavy = regSizeY - yCoeffLight;
            y1 = yCoeffHeavy*lutThis[v];
            y2 = y < regCenterY ? yCoeffLight*lutN[v] : yCoeffLight*lutS[v];
            out.s2 = (y1 + y2) / ((float) regSizeY * 255.0);
        }
        else{ //bilinear interpolation
            int xCoeffLight, xCoeffHeavy;
            int yCoeffLight, yCoeffHeavy;
            int xy1,xy2;
            int xy;
            xCoeffLight = abs(((int) x) - regCenterX);
            xCoeffHeavy = regSizeX - xCoeffLight;
            yCoeffLight = abs(((int) y) - regCenterY);
            yCoeffHeavy = regSizeY - yCoeffLight;

            if(y < regCenterY){
                if(x < regCenterX){ //top left corner
                    xy1 = xCoeffLight*lutNW[v] + xCoeffHeavy*lutN[v];
                    xy2 = xCoeffLight*lutW[v] + xCoeffHeavy*lutThis[v];
                }
                else{             //top right corner
                    xy1 = xCoeffLight*lutNE[v] + xCoeffHeavy*lutN[v];
                    xy2 = xCoeffLight*lutE[v] + xCoeffHeavy*lutThis[v];
                }
                xy = yCoeffLight * xy1 + yCoeffHeavy * xy2; //max regSizeY*regSizeX*255
            } else{
                if(x < regCenterX){  //bottom left corner
                    xy1 = xCoeffLight*lutW[v] + xCoeffHeavy*lutThis[v];
                    xy2 = xCoeffLight*lutSW[v] + xCoeffHeavy*lutS[v];
                }
                else{             //bottom right corner
                    xy1 = xCoeffLight*lutE[v] + xCoeffHeavy*lutThis[v];
                    xy2 = xCoeffLight*lutSE[v] + xCoeffHeavy*lutS[v];
                }
                xy = yCoeffHeavy * xy1 + yCoeffLight * xy2;
            }
            out.s2 = xy / ((float) regSizeX*regSizeY*255);
            rsDebug("out : ", out.s2);
        }
    }

    out = HSVtoRGB(out);
    return rsPackColorTo8888(out.r , out.g , out.b , out.a);
}
