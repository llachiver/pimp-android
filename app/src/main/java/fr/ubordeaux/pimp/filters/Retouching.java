package fr.ubordeaux.pimp.filters;

        import android.content.Context;
        import android.graphics.Bitmap;
        import android.icu.lang.UCharacter;
        import android.renderscript.Allocation;
        import android.renderscript.Byte2;
        import android.renderscript.RenderScript;
        import android.renderscript.Short2;
        import android.util.Log;

        import fr.ubordeaux.pimp.ScriptC_brightness;
        import fr.ubordeaux.pimp.ScriptC_dynamicExtension;
        import fr.ubordeaux.pimp.ScriptC_findMinMax;
        import fr.ubordeaux.pimp.ScriptC_saturation;

public class Retouching {

    private Context context;

    private Context getContext(){
        return context;
    }

    /**
     * Sets the brightness of an image by adding a factor to the existing luminance.
     * @param bmp the image to modify
     * @param factor the brightness factor, whose range is based on the seekbar [-127 ; +127]
     */
    public static void setBrightness(Bitmap bmp, int factor, Context context){
        RenderScript rs = RenderScript.create(context);
        Allocation input = Allocation.createFromBitmap(rs, bmp);
        Allocation output = Allocation.createTyped(rs, input.getType());

        ScriptC_brightness sBrightness = new ScriptC_brightness(rs);

        sBrightness.set_factor(factor);
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
        RenderScript rs = RenderScript.create(context);
        Allocation input = Allocation.createFromBitmap(rs, bmp);
        Allocation output = Allocation.createTyped(rs, input.getType());

        ScriptC_saturation sBrightness = new ScriptC_saturation(rs);

        sBrightness.set_factor(factor);
        sBrightness.forEach_setSaturation(input, output);

        output.copyTo(bmp);

        input.destroy();
        output.destroy();
        sBrightness.destroy();
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
        Allocation input = Allocation.createFromBitmap(rs, bmp);

        ScriptC_findMinMax sMinMax = new ScriptC_findMinMax(rs);
        Short2[] minMax;
        sMinMax.set_luminanceMode(false);
        minMax = sMinMax.reduce_findMinMax(input).get();
        sMinMax.destroy();
        if (minMax[0].x == minMax[0].y && minMax[1].x == minMax[1].y && minMax[2].x == minMax[2].y) //Exit if only one color
            return;

        Allocation output = Allocation.createTyped(rs, input.getType());

        ScriptC_dynamicExtension sDynExtension = new ScriptC_dynamicExtension(rs);
        sDynExtension.set_minMaxRGB(minMax);
        sDynExtension.set_factor(factor);
        sDynExtension.invoke_dynamicExtensionRGB(input, output);
        output.copyTo(bmp);

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
        Allocation input = Allocation.createFromBitmap(rs, bmp);

        ScriptC_findMinMax sMinMax = new ScriptC_findMinMax(rs);
        Short2[] minMax;
        sMinMax.set_luminanceMode(true);
        minMax = sMinMax.reduce_findMinMax(input).get();
        sMinMax.destroy();
        if (minMax[0].x == minMax[0].y) //Exit if only one color
            return;

        Allocation output = Allocation.createTyped(rs, input.getType());

        ScriptC_dynamicExtension sDynExtension = new ScriptC_dynamicExtension(rs);
        sDynExtension.set_minMaxGray(minMax[0]);
        sDynExtension.set_factor(factor);
        sDynExtension.invoke_dynamicExtensionGray(input, output);
        output.copyTo(bmp);

        rs.destroy();
    }
}
