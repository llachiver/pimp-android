package fr.ubordeaux.pimp.filters;

import android.content.Context;
import android.graphics.Bitmap;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;

import fr.ubordeaux.pimp.ScriptC_convolution;
import fr.ubordeaux.pimp.util.Kernels;

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
        for (float f : kernel) totalNormalize += Math.abs(f); //Compute normalizing coefficient

        //Initialize global variables
        sConvolution.set_kdiv(totalNormalize);
        sConvolution.set_normal(normalize);
        sConvolution.set_height(bmp.getHeight());
        sConvolution.set_width(bmp.getWidth());
        sConvolution.set_kWidth(kWidth);
        sConvolution.set_kHeight(kHeight);
        sConvolution.set_pIn(input);
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

    public static void convolve2dSeparable(Bitmap bmp, float[] kernelX, float[] kernelY, boolean normalize, Context context){
        RenderScript rs = RenderScript.create(context); //Create rs context
        Allocation input = Allocation.createFromBitmap(rs, bmp); //Getting input
        ScriptC_convolution sConvolution = new ScriptC_convolution(rs); //Create script

        //int squareSize = (int) Math.sqrt(kernel.length); //Getting square size of kernel

        //sConvolution.set_square_ksize(squareSize);

        Allocation kAllocX = Allocation.createSized(rs, Element.F32(rs),kernelX.length); //Allocate memory for kernel
        kAllocX.copyFrom(kernelX); //Copy data from kernel
        Allocation kAllocY = Allocation.createSized(rs, Element.F32(rs),kernelY.length); //Allocate memory for kernel
        kAllocY.copyFrom(kernelY); //Copy data from kernel

        sConvolution.bind_kernelX(kAllocX);
        sConvolution.bind_kernelY(kAllocY);

        float totalNormalize = 0.f;
        for (float x : kernelX){
            for (float y : kernelY){
                totalNormalize += Math.abs(x * y); //Compute normalizing coefficient
            }
        }

        //Initialize global variables
        sConvolution.set_kdiv(totalNormalize);
        sConvolution.set_normal(normalize);
        sConvolution.set_height(bmp.getHeight());
        sConvolution.set_width(bmp.getWidth());
        sConvolution.set_kWidth(kernelX.length);
        sConvolution.set_kHeight(kernelY.length);
        sConvolution.set_pIn(input);
        sConvolution.invoke_setup(); //Initialize kCenters

        //Allocate output
        Allocation output = Allocation.createTyped(rs, input.getType());

        //Launch script
        sConvolution.invoke_convolutionSeparable(input,output);
        //Copy to bmp
        output.copyTo(bmp);
        //Free memory
        rs.destroy();
        sConvolution.destroy();
        input.destroy();
        output.destroy();
        kAllocX.destroy();
        kAllocY.destroy();
    }

    public static void averageBlur3x3(Bitmap bmp, Context context){
        float [] kernel = new float[9];
        for(int i = 0; i < kernel.length ; i++) kernel[i] = 1.0f;
        convolve2d(bmp, kernel, 3, 3, true, context);
    }
    public static void averageBlur5x5(Bitmap bmp, Context context){
        float [] kernel = new float[25];
        for(int i = 0; i < kernel.length ; i++) kernel[i] = 1.0f;
        convolve2d(bmp, kernel, 5, 5, true, context);
    }


    //To replace by an edge detector method ?
    public static void sobelOperator(Bitmap bmp, Context context){
        //Create context
        RenderScript rs = RenderScript.create(context); //Create rs context
        Allocation input = Allocation.createFromBitmap(rs, bmp); //Getting input
        Allocation output = Allocation.createTyped(rs, input.getType());
        ScriptC_convolution sConvolution = new ScriptC_convolution(rs); //Create script

        //Declare sobel X and Y operators
        float[] sobelX = Kernels.SOBEL_X;

        float[] sobelY = Kernels.SOBEL_Y;

        //Allocating sobel operators for RS
        Allocation sobelXAlloc = Allocation.createSized(rs, Element.F32(rs),sobelX.length); //Allocate memory for kernel
        sobelXAlloc.copyFrom(sobelX); //Copy data from kernel
        Allocation sobelYAlloc = Allocation.createSized(rs, Element.F32(rs),sobelY.length); //Allocate memory for kernel
        sobelYAlloc.copyFrom(sobelY); //Copy data from kernel

        //Bind global variables
        sConvolution.bind_kernelX(sobelXAlloc);
        sConvolution.bind_kernelY(sobelYAlloc);
        sConvolution.set_height(bmp.getHeight());
        sConvolution.set_width(bmp.getWidth());
        sConvolution.set_kWidth(3);
        sConvolution.set_kHeight(3);
        sConvolution.set_pIn(input);
        sConvolution.set_pOut(output);
        //Allocate output




        sConvolution.invoke_sobelOperator(input, output);
        input.copyTo(bmp); //Input because it is recycled
        //Free memory
        rs.destroy();
        sConvolution.destroy();
        input.destroy();
        output.destroy();
        sobelXAlloc.destroy();
        sobelYAlloc.destroy();

    }



}
