package fr.ubordeaux.pimp.filters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.renderscript.Allocation;
import android.renderscript.RenderScript;
import android.renderscript.Script;

import fr.ubordeaux.pimp.ScriptC_assignCLAHELuts;
import fr.ubordeaux.pimp.ScriptC_cumulativeHistogram;
import fr.ubordeaux.pimp.ScriptC_lut;

public class CLAHE {

    /**
     * Performs a CLAHE on the image
     * @param bmp the bitmap to modify
     * @param regNbrSide the number of regions on the side of the grid
     * @param clip the clip factor
     * @param context Execution context
     */
    public static void CLAHE(Bitmap bmp, int regNbrSide, float clip, Context context){
        if(regNbrSide < 3) return;
        if(clip < 0.0f) clip = 0.0f;

        int width = bmp.getWidth();
        int height = bmp.getHeight();

        RenderScript rs = RenderScript.create(context);
        Allocation input = Allocation.createFromBitmap(rs, bmp); //Bitmap input
        Allocation output = Allocation.createTyped(rs, input.getType()); //Bitmap output

        int regNbrX = regNbrSide;
        int regNbrY = regNbrSide;

        int regNbr = regNbrX*regNbrY;

        //The array containing the luts of each individual region
        short luts[][] = new short[regNbr][256];

        /* Actual size of contextual regions */
        int regSizeX = width/regNbrX;
        int regSizeY = height/regNbrY;

        //When we reach the borders of the image, the reg size changes to complete the image :
        int lastRegSizeX = regSizeX + (width - regNbrX*regSizeX);
        int lastRegSizeY = regSizeY + (height - regNbrY*regSizeY);

        //The number of bins in one region
        int regNbrBins = regSizeX*regSizeY;

        ScriptC_cumulativeHistogram histoScript = new ScriptC_cumulativeHistogram(rs);
        histoScript.set_clip(true);
        histoScript.set_slope(clip);
        histoScript.set_regSize(regNbrBins);

        //Used for launching the kernel on each region
        Script.LaunchOptions lo = new Script.LaunchOptions();

        int regIdx;
        //We get the LUTs for each region
        for(int y = 0 ; y < regNbrY ; y++){
            for(int x = 0 ; x < regNbrX ; x++){
                regIdx = x + y*regNbrX;

                //We delimit the execution of the script to the current region.
                if(x < regNbrX -1) lo.setX(x * regSizeX,x * regSizeX + regSizeX);
                else lo.setX(x * regSizeX,x * regSizeX + lastRegSizeX);
                if(y < regNbrY -1) lo.setY(y * regSizeY,y * regSizeY + regSizeY);
                else lo.setY(y * regSizeY,y * regSizeY + lastRegSizeY);

                //Check if we are on a border
                if(x == regNbrX - 1){
                    if(y == regNbr-1) histoScript.set_nbrBins(lastRegSizeX * lastRegSizeY);
                    else histoScript.set_nbrBins(lastRegSizeX * regSizeY);
                }
                else if(y == regNbrY - 1){
                    if(x == regNbr-1) histoScript.set_nbrBins(lastRegSizeX * lastRegSizeY);
                    else histoScript.set_nbrBins(lastRegSizeY * regSizeX);
                }
                else histoScript.set_nbrBins(regNbrBins);

                luts[regIdx] = histoScript.reduce_LUTCumulativeHistogram(input,lo).get();
            }
        }

        //Then we assign the LUT values to each region with the assignCLAHELut script.
        ScriptC_assignCLAHELuts lut = new ScriptC_assignCLAHELuts(rs);
        for(int y = 0 ; y < regNbrY ; y++){
            for(int x = 0 ; x < regNbrX ; x++){
                regIdx = x + y*regNbrX;

                //Limit the execution of the script to the current region
                if(x < regNbrX -1) lo.setX(x * regSizeX,x * regSizeX + regSizeX);
                else lo.setX(x * regSizeX,x * regSizeX + lastRegSizeX);
                if(y < regNbrY -1) lo.setY(y * regSizeY,y * regSizeY + regSizeY);
                else lo.setY(y * regSizeY,y * regSizeY + lastRegSizeY);

                //Set the lut of the current region
                lut.set_lutThis(luts[regIdx]);

                //Set the luts of all the surrounding regions
                if(y > 0) lut.set_lutN(luts[regIdx-regNbrX]);
                if(y < regNbrY-1) lut.set_lutS(luts[regIdx + regNbrX]);
                if(x > 0) lut.set_lutW(luts[regIdx - 1]);
                if(x < regNbrX-1) lut.set_lutE(luts[regIdx + 1]);
                if(y > 0 && x > 0) lut.set_lutNW(luts[regIdx - regNbrX - 1]);
                if(y > 0 && x < regNbrX-1) lut.set_lutNE(luts[regIdx - regNbrX + 1]);
                if(y < regNbrY - 1 && x > 0) lut.set_lutSW(luts[regIdx + regNbrX - 1]);
                if(y < regNbrY  - 1 && x < regNbrX - 1) lut.set_lutSE(luts[regIdx  + regNbrX + 1]);

                //The coordinates of the region
                lut.set_regIdxX(x);
                lut.set_regIdxY(y);

                lut.set_regNbrX(regNbrX);
                lut.set_regNbrY(regNbrY);

                if(x < regNbrX-1)lut.set_regSizeX(regSizeX);
                else lut.set_regSizeX(lastRegSizeX);
                if(y < regNbrY-1) lut.set_regSizeY(regSizeY);
                else lut.set_regSizeY(lastRegSizeY);

                //The absolute coordinates of the region center
                lut.set_regCenterX(x*regSizeX + regSizeX/2);
                lut.set_regCenterY(y*regSizeY + regSizeY/2);

                lut.forEach_assignLutHSV(input,output,lo);
            }
        }

        output.copyTo(bmp);
        histoScript.destroy();

        input.destroy();
        output.destroy();
        lut.destroy();
        rs.destroy();
    }


}
