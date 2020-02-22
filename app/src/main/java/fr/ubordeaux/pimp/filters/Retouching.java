package fr.ubordeaux.pimp.filters;

        import android.content.Context;
        import android.graphics.Bitmap;
        import android.icu.lang.UCharacter;
        import android.renderscript.Allocation;
        import android.renderscript.Byte2;
        import android.renderscript.RenderScript;
        import android.renderscript.Short2;
        import android.util.Log;

        import fr.ubordeaux.pimp.ScriptC_cummulativeHistogram;

        import fr.ubordeaux.pimp.ScriptC_assignLut;
        import fr.ubordeaux.pimp.ScriptC_brightness;
        import fr.ubordeaux.pimp.ScriptC_dynamicExtension;
        import fr.ubordeaux.pimp.ScriptC_findMinMax;
        import fr.ubordeaux.pimp.ScriptC_saturation;
        import fr.ubordeaux.pimp.ScriptC_utils;
        import fr.ubordeaux.pimp.io.BitmapIO;

public class Retouching {

    private Context context;

    private Context getContext(){
        return context;
    }

    /**
     * Sets the brightness of an image by adding a factor to the existing brightness.
     * @param bmp the image to modify
     * @param factor the brightness factor, whose range is based on the seekbar [-127 ; +127]
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
     * @param bmp the image to modify
     * @param factor the saturation factor, whose range is based on the seekbar [-127 ; +127]
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
     * @param bmp the image to modify
     * @param factor the contrast factor, whose range is based on the seekbar [0 ; 255]
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
     * Extends the dynamic (histogram) of a grayscale image in order to set up the contrast.
     * @param bmp the image to modify
     * @param factor the contrast factor, whose range is based on the seekbar [0 ; 255]
     * @param context
     */
    public static void dynamicExtensionGray(Bitmap bmp, int factor, Context context){
        RenderScript rs = RenderScript.create(context);
        Allocation input = Allocation.createFromBitmap(rs, bmp); //Bitmap input
        Allocation output = Allocation.createTyped(rs, input.getType()); //Bitmap output

        ScriptC_findMinMax sMinMax = new ScriptC_findMinMax(rs);
        Short2[] minMax;
        sMinMax.set_valueMode(true);
        minMax = sMinMax.reduce_findMinMax(input).get();
        sMinMax.destroy();
        if (minMax[0].x == minMax[0].y) //Exit if only one color
            return;

        ScriptC_dynamicExtension sDynExtension = new ScriptC_dynamicExtension(rs);
        sDynExtension.set_minMaxGray(minMax[0]);
        sDynExtension.set_factor(factor);
        sDynExtension.invoke_dynamicExtensionGray(input, output);
        output.copyTo(bmp);

        input.destroy();
        output.destroy();
        rs.destroy();
    }

    /**
     * Equalizes the histogram of the image.
     * @param bmp
     * @param context
     */
    public static void histogramEqualization(Bitmap bmp, Context context){
        RenderScript rs = RenderScript.create(context); //Create base renderscript

        Allocation input = Allocation.createFromBitmap(rs, bmp); //Bitmap input
        Allocation output = Allocation.createTyped(rs, input.getType()); //Bitmap output

        //We compute the LUT extracted from the cummulative histogram.
        ScriptC_cummulativeHistogram histoScript = new ScriptC_cummulativeHistogram(rs);
        histoScript.set_size(bmp.getWidth() * bmp.getHeight());
        short[] LUTValue;
        LUTValue = histoScript.reduce_LUTCummulativeHistogram(input).get();
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

    /**
     * Set an image to gray scale.
     * @param bmp Bmp to modify
     * @param context MainActivity Context
     */
    public static void toGray(Bitmap bmp, Context context){
        RenderScript rs = RenderScript.create(context);

        Allocation input = Allocation.createFromBitmap(rs, bmp);
        Allocation output = Allocation.createTyped(rs, input.getType());

        ScriptC_utils grayScript = new ScriptC_utils(rs);

        grayScript.forEach_grey(input,output);

        output.copyTo(bmp);

        input.destroy();
        output.destroy();
        rs.destroy();
        grayScript.destroy();

    }

    /**
     * Set invert to an image computing the difference of each 255 - pixel.
     * @param bmp Bmp to modify
     * @param context MainActivity Context
     */
    public static void invert(Bitmap bmp, Context context){
        RenderScript rs = RenderScript.create(context);

        Allocation input = Allocation.createFromBitmap(rs, bmp);
        Allocation output = Allocation.createTyped(rs, input.getType());

        ScriptC_utils invertScript = new ScriptC_utils(rs);

        invertScript.forEach_invert(input,output);

        output.copyTo(bmp);

        input.destroy();
        output.destroy();
        rs.destroy();
        invertScript.destroy();

    }

}
