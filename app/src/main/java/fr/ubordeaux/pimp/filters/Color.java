package fr.ubordeaux.pimp.filters;

import android.content.Context;
import android.graphics.Bitmap;
import android.renderscript.Allocation;
import android.renderscript.RenderScript;
import fr.ubordeaux.pimp.ScriptC_colorize;
import fr.ubordeaux.pimp.ScriptC_keepColor;
import fr.ubordeaux.pimp.ScriptC_utils;

/**
 * All color and hue-related effects.
 */
public class Color {

    /**
     * Set an image to gray scale.
     * @param bmp Bmp to modify
     * @param context Execution context
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
     * @param context Execution context
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

    /**
     * Change the image's hue to the one chosen by the user
     * @param bmp Bmp to modify
     * @param context Execution context
     * @param hue hue that is selected by the user
     */
    public static void colorize ( Bitmap bmp , int hue, Context context, boolean uniform) {

        RenderScript rs = RenderScript.create ( context ) ;
        Allocation input = Allocation.createFromBitmap ( rs , bmp ) ;
        Allocation output = Allocation.createTyped ( rs , input.getType () ) ;

        ScriptC_colorize colorize = new ScriptC_colorize ( rs ) ;
        colorize.set_selectedHue(hue);
        colorize.set_uniform(uniform);
        colorize.forEach_colorize ( input , output ) ;
        output.copyTo ( bmp ) ;
        input.destroy () ;
        output.destroy () ;
        colorize.destroy () ;
        rs.destroy () ;
    }

    /**
     * Keep the image's hue with tolerance level that is chosen by the user
     * @param bmp Bmp to modify
     * @param hue hue that is selected by the user
     * @param tolerance the degree of tolerance chosen by the user
     * @param context Execution context
     */
    public static void keepColor ( Bitmap bmp , int hue, int tolerance, Context context) {

        RenderScript rs = RenderScript.create ( context ) ;
        Allocation input = Allocation.createFromBitmap ( rs , bmp ) ;
        Allocation output = Allocation.createTyped ( rs , input.getType () ) ;

        ScriptC_keepColor keepColor = new ScriptC_keepColor ( rs ) ;
        keepColor.set_selectedHue(hue);
        keepColor.set_tolerance(tolerance);
        keepColor.forEach_keepColor ( input , output );
        output.copyTo ( bmp ) ;
        input.destroy () ;
        output.destroy () ;
        keepColor.destroy () ;
        rs.destroy () ;
    }
}
