package fr.ubordeaux.pimp.filters;

import android.content.Context;
import android.graphics.Bitmap;
import android.renderscript.Allocation;
import android.renderscript.Byte2;
import android.renderscript.RenderScript;
import android.renderscript.Short2;

import fr.ubordeaux.pimp.ScriptC_brightness;
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

    public static void findMinMax(Bitmap bmp, Context context){
        RenderScript rs = RenderScript.create(context);
        Allocation input = Allocation.createFromBitmap(rs, bmp);
        ScriptC_findMinMax script = new ScriptC_findMinMax(rs);
        Byte2 minmax;
        minmax = script.reduce_findMinMax(input).get();
        System.out.println("Min : " + minmax.x);
        System.out.println("Max : " + minmax.y);


    }

}
