package fr.ubordeaux.pimp.filters;

import android.content.Context;
import android.graphics.Bitmap;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;

import fr.ubordeaux.pimp.ScriptC_convolution;

public class Convolution {

    public static void convolve2d(Bitmap bmp, float [] kernel, int kWidth, int kHeight, boolean normalize, Context context){
        RenderScript rs = RenderScript.create(context); //Create rs context
        Allocation input = Allocation.createFromBitmap(rs, bmp); //Getting input
        ScriptC_convolution sConvolution = new ScriptC_convolution(rs); //Create script

        //int squareSize = (int) Math.sqrt(kernel.length); //Getting square size of kernel

        //sConvolution.set_square_ksize(squareSize);

        Allocation kAlloc = Allocation.createSized(rs, Element.F32(rs),kernel.length); //Allocate memory for kernel
        kAlloc.copyFrom(kernel); //Copy data from kernel

        sConvolution.bind_kernel(kAlloc);
        float totalNormalize = 0.f;
        for (float f : kernel) totalNormalize += f; //Compute normalizing coefficient

        //Initialize global variables
        sConvolution.set_kdiv(totalNormalize);
        sConvolution.set_normal(normalize);
        sConvolution.set_height(bmp.getHeight());
        sConvolution.set_width(bmp.getWidth());
        sConvolution.set_kWidth(kWidth);
        sConvolution.set_kHeight(kHeight);
        sConvolution.invoke_setup(); //Initialize kCenters

        //Allocate output
        Allocation output = Allocation.createTyped(rs, input.getType());

        //Launch script
        sConvolution.forEach_conv2d(input,output);
        //Copy to bmp
        output.copyTo(bmp);
        //Free memory
        rs.destroy();
        sConvolution.destroy();
        input.destroy();
        output.destroy();
        kAlloc.destroy();


    }
}
