package fr.ubordeaux.pimp.filters;

import android.content.Context;
import android.graphics.Bitmap;
import android.renderscript.Allocation;
import android.renderscript.RenderScript;
import android.renderscript.Short2;

import fr.ubordeaux.pimp.ScriptC_cumulativeHistogram;
import fr.ubordeaux.pimp.ScriptC_assignLut;
import fr.ubordeaux.pimp.ScriptC_brightness;
import fr.ubordeaux.pimp.ScriptC_dynamicExtension;
import fr.ubordeaux.pimp.ScriptC_findMinMax;
import fr.ubordeaux.pimp.ScriptC_saturation;

/**
 * Basic retouching like brightness, saturation and contrast.
 */
public class Retouching {

    /**
     * Sets the brightness of an image by adding a factor to the existing brightness.
     * @param bmp the bitmap to modify
     * @param factor the brightness factor between 0 and 255.
     * @param context
     */
    public static void setBrightness(Bitmap bmp, int factor, Context context){
        int newFactor = factor - 127;
        RenderScript rs = RenderScript.create(context);
        Allocation input = Allocation.createFromBitmap(rs, bmp); //Bitmap input
        Allocation output = Allocation.createTyped(rs, input.getType()); //Bitmap output

        ScriptC_brightness sBrightness = new ScriptC_brightness(rs);

        sBrightness.set_factor(newFactor);
        sBrightness.forEach_setBrightness(input, output);

        output.copyTo(bmp);

        input.destroy();
        output.destroy();
        sBrightness.destroy();
        rs.destroy();
    }

    /**
     * Sets the saturation of an image by multiplying a factor to the existing saturation.
     * @param bmp the bitmap to modify
     * @param factor the saturation factor between 0 and 255.
     * @param context
     */
    public static void setSaturation(Bitmap bmp, int factor, Context context){

        //We normalize the factor between -1 and 1.
        float factorRS = (factor-127)/127f;
        RenderScript rs = RenderScript.create(context);
        Allocation input = Allocation.createFromBitmap(rs, bmp); //Bitmap input
        Allocation output = Allocation.createTyped(rs, input.getType()); //Bitmap output

        ScriptC_saturation sBrightness = new ScriptC_saturation(rs);

        sBrightness.set_factor(factorRS);
        sBrightness.forEach_setSaturation(input, output);

        output.copyTo(bmp);

        sBrightness.destroy();
        input.destroy();
        output.destroy();
        rs.destroy();
    }

    /**
     * Extends the dynamic (histogram) of a coloured image in order to set up the contrast.
     * @param bmp the bitmap to modify
     * @param factor the contrast factor between 0 and 255.
     * @param context
     */
    public static void dynamicExtensionRGB(Bitmap bmp, int factor, Context context){
        RenderScript rs = RenderScript.create(context);
        Allocation input = Allocation.createFromBitmap(rs, bmp); //Bitmap input
        Allocation output = Allocation.createTyped(rs, input.getType()); //Bitmap output

        //We compute the min and max value of the image.
        ScriptC_findMinMax sMinMax = new ScriptC_findMinMax(rs);
        Short2[] minMax;
        sMinMax.set_valueMode(false);
        minMax = sMinMax.reduce_findMinMax(input).get();
        sMinMax.destroy();
        if (minMax[0].x == minMax[0].y && minMax[1].x == minMax[1].y && minMax[2].x == minMax[2].y) //Exit if only one color
            return;

        //We extend the dynamic
        ScriptC_dynamicExtension sDynExtension = new ScriptC_dynamicExtension(rs);
        sDynExtension.set_minMaxRGB(minMax);
        sDynExtension.set_factor(factor);
        sDynExtension.invoke_dynamicExtensionRGB(input, output);
        output.copyTo(bmp);

        input.destroy();
        output.destroy();
        rs.destroy();
    }

    /**
     * Equalizes the histogram of the image.
     * @param bmp the bitmap to modify
     * @param context
     */
    public static void histogramEqualization(Bitmap bmp, Context context){
        RenderScript rs = RenderScript.create(context); //Create base renderscript

        Allocation input = Allocation.createFromBitmap(rs, bmp); //Bitmap input
        Allocation output = Allocation.createTyped(rs, input.getType()); //Bitmap output

        //We compute the LUT extracted from the cumulative histogram.
        ScriptC_cumulativeHistogram histoScript = new ScriptC_cumulativeHistogram(rs);
        histoScript.set_size(bmp.getWidth() * bmp.getHeight());
        short[] LUTValue;
        LUTValue = histoScript.reduce_LUTCumulativeHistogram(input).get();
        histoScript.destroy();

        //Then we assign the LUT values to the image with the assignLut script.
        ScriptC_assignLut lut = new ScriptC_assignLut(rs);
        lut.set_lutSingle(LUTValue);
        lut.forEach_assignLutHSV(input,output);

        output.copyTo(bmp);

        input.destroy();
        output.destroy();
        lut.destroy();
        rs.destroy();
    }
}
