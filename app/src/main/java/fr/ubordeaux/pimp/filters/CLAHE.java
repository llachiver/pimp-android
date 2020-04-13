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

    public static int mean(short lut[]){
        int sum=0;
        for(int i = 0 ; i < 256 ; i++)
            sum += lut[i];

        return sum/256;
    }



    public static void CLAHE(Bitmap bmp, Context context, int regionSize, float clip){

        int width = bmp.getWidth();
        int height = bmp.getHeight();

        RenderScript rs = RenderScript.create(context);
        Allocation input = Allocation.createFromBitmap(rs, bmp); //Bitmap input
        Allocation output = Allocation.createTyped(rs, input.getType());

        int regNbrX = regionSize;
        int regNbrY = regionSize;
        int regNbr = regNbrX*regNbrY;

        short luts[][] = new short[regNbr][256];

        /* Actual size of contextual regions */
        int regSizeX = width/regNbrX;
        int regSizeY = height/regNbrY;
        int regNbrBins = regSizeX*regSizeY;

        //We compute the LUT extracted from the cumulative histogram.
        Script.LaunchOptions lo = new Script.LaunchOptions();

        int regIdx;

        ScriptC_cumulativeHistogram histoScript = new ScriptC_cumulativeHistogram(rs);
        histoScript.set_clip(true);
        histoScript.set_slope(clip);
        histoScript.set_regSize(regNbrBins);

        for(int y = 0 ; y < regNbrY ; y++){
            for(int x = 0 ; x < regNbrX ; x++){
                regIdx = x + y*regNbrX;
                lo.setX(x * regSizeX,x * regSizeX + regSizeX);
                lo.setY(y * regSizeY,y * regSizeY + regSizeY);
                histoScript.set_nbrBins(regNbrBins);
                luts[regIdx] = histoScript.reduce_LUTCumulativeHistogram(input,lo).get();
                //System.out.println("lut n° " + regIdx + " : " + mean(luts[regIdx]));
            }
        }
        ScriptC_assignCLAHELuts lut = new ScriptC_assignCLAHELuts(rs);
        for(int y = 0 ; y < regNbrY ; y++){
            for(int x = 0 ; x < regNbrX ; x++){
                regIdx = x + y*regNbrX;
                lo.setX(x * regSizeX,x * regSizeX + regSizeX);
                lo.setY(y * regSizeY,y * regSizeY + regSizeY);

                lut.set_lutThis(luts[regIdx]);
                if(y > 0) lut.set_lutN(luts[regIdx-regNbrX]);
                if(y < regNbrY-1) lut.set_lutS(luts[regIdx + regNbrX]);
                if(x>0) lut.set_lutW(luts[regIdx - 1]);
                if(x < regNbrX-1) lut.set_lutE(luts[regIdx + 1]);
                if(y > 0 && x > 0) lut.set_lutNW(luts[regIdx - regNbrX - 1]);
                if(y > 0 && x < regNbrX-1) lut.set_lutNE(luts[regIdx - regNbrX + 1]);
                if(y < regNbrY - 1 && x > 0) lut.set_lutSW(luts[regIdx + regNbrX - 1]);
                if(y < regNbrY  - 1 && x < regNbrX - 1) lut.set_lutSE(luts[regIdx  + regNbrX + 1]);

                lut.set_regIdxX(x);
                lut.set_regIdxY(y);
                lut.set_regNbrX(regNbrX);
                lut.set_regNbrY(regNbrY);
                lut.set_regSizeX(regSizeX);
                lut.set_regSizeY(regSizeY);
                lut.set_regCenterX(x*regSizeX + regSizeX/2);
                lut.set_regCenterY(y*regSizeY + regSizeY/2);

                lut.forEach_assignLutHSV(input,output,lo);
            }
        }
        //Then we assign the LUT values to the image with the assignLut script.
        /**/

        output.copyTo(bmp);
        histoScript.destroy();

        input.destroy();
        output.destroy();
        lut.destroy();
        rs.destroy();
    }


}
