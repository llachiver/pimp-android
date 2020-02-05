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
     * Increase or decrease the image brightness following a factor.
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
     * Increase or decrease the image saturation following a factor.
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

        for(int i = 0 ; i < 3 ; i++) {
            Log.i("debugging", "min" + i + " : " + minMax[i].x);
            Log.i("debugging", "max" + i + " : " + minMax[i].y);
        }

        Allocation output = Allocation.createTyped(rs, input.getType());

        ScriptC_dynamicExtension sDynExtension = new ScriptC_dynamicExtension(rs);
        sDynExtension.set_minMaxRGB(minMax);
        sDynExtension.set_factor(factor);
        sDynExtension.invoke_dynamicExtensionRGB(input, output);
        output.copyTo(bmp);



        rs.destroy();

    }

}
