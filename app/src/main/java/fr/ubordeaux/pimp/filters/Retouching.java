package fr.ubordeaux.pimp.filters;

import android.content.Context;
import android.graphics.Bitmap;
import android.renderscript.Allocation;
import android.renderscript.RenderScript;

import fr.ubordeaux.pimp.ScriptC_brightness;
import fr.ubordeaux.pimp.ScriptC_saturation;

public class Retouching {

    private Context context;

    private Context getContext(){
        return context;
    }

    /**
     * Changes the image brightness following a factor.
     * @param bmp the image to modify
     * @param factor the brightness factor
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
     * Changes the image saturation following a factor.
     * @param bmp the image to modify
     * @param factor the brightness factor
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

}
