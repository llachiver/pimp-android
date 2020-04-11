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

    public static void CLAHE(Bitmap bmp, Context context){

        int width = bmp.getWidth();
        int height = bmp.getHeight();

        RenderScript rs = RenderScript.create(context);
        Allocation input = Allocation.createFromBitmap(rs, bmp); //Bitmap input
        Allocation output = Allocation.createTyped(rs, input.getType());

        int regNbrX = 3;
        int regNbrY = 3;
        int regNbr = regNbrX*regNbrY;

        short luts[][] = new short[regNbr][256];

        /* Actual size of contextual regions */
        int regSizeX = width/regNbrX;
        int regSizeY = height/regNbrY;
        int regNbrBins = regSizeX*regSizeY;

        //We compute the LUT extracted from the cumulative histogram.
        Script.LaunchOptions lo = new Script.LaunchOptions();

        int regIdx;

        for(int y = 0 ; y < regNbrY ; y++){
            for(int x = 0 ; x < regNbrX ; x++){
                ScriptC_cumulativeHistogram histoScript = new ScriptC_cumulativeHistogram(rs);
                regIdx = x + y*regNbrX;
                lo.setX(x * regSizeX,x * regSizeX + regSizeX);
                lo.setY(y * regSizeY,y * regSizeY + regSizeY);
                histoScript.set_nbrBins(regNbrBins);
                luts[regIdx] = histoScript.reduce_LUTCumulativeHistogram(input,lo).get();
                histoScript.destroy();
                System.out.println("lut n° " + regIdx + " : " + mean(luts[regIdx]));
            }
        }

        for(int y = 0 ; y < regNbrY ; y++){
            for(int x = 0 ; x < regNbrX ; x++){
                regIdx = x + y*regNbrX;
                ScriptC_assignCLAHELuts lut = new ScriptC_assignCLAHELuts(rs);
                lo.setX(x * regSizeX,x * regSizeX + regSizeX);
                lo.setY(y * regSizeY,y * regSizeY + regSizeY);

                if(y > 0) lut.set_lutNorth(luts[regIdx-regNbrX]);
                else lut.set_lutNorth(luts[regIdx]);
                if(y < regNbrY-1) lut.set_lutSouth(luts[regIdx+regNbrX]);
                else lut.set_lutSouth(luts[regIdx]);
                if(x > 0) lut.set_lutWest(luts[regIdx - 1]);
                else lut.set_lutWest(luts[regIdx]);
                if(x < regNbrX-1) lut.set_lutEast(luts[regIdx + 1]);
                else lut.set_lutEast(luts[regIdx]);

                lut.set_input(input);

                lut.set_lutValue(luts[regIdx]);
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

        input.destroy();
        output.destroy();
        //lut.destroy();
        rs.destroy();
    }


}