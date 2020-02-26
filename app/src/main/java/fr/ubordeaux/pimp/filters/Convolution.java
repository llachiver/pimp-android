package fr.ubordeaux.pimp.filters;

import android.content.Context;
import android.graphics.Bitmap;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.util.Log;

import fr.ubordeaux.pimp.ScriptC_convolution;
import fr.ubordeaux.pimp.util.Kernels;

/**
 * Class refereed to all convolution methods.
 */

public class Convolution {

    /**
     * Convolve an image with a kernel of width (kWidth) and height (kHeight) odds both exclusively, with a normalization boolean.
     * @param bmp Image to convolve
     * @param kernel Kernel to use
     * @param kWidth Width of Kernel
     * @param kHeight Height of kernel
     * @param normalize Normalize or not the output (Most of the cases must be set to true)
     * @param context MainActivity context.
     */
    public static void convolve2d(Bitmap bmp, float [] kernel, int kWidth, int kHeight, boolean normalize, Context context){
        if (kWidth % 2 == 0 || kHeight % 2 == 0) return; //Pair kernels not allowed
        RenderScript rs = RenderScript.create(context); //Create rs context
        Allocation input = Allocation.createFromBitmap(rs, bmp); //Getting input
        ScriptC_convolution sConvolution = new ScriptC_convolution(rs); //Create script


        Allocation kAlloc = Allocation.createSized(rs, Element.F32(rs),kWidth * kHeight); //Allocate memory for kernel
        kAlloc.copyFrom(kernel); //Copy data from kernel

        sConvolution.bind_kernel(kAlloc);
        float totalNormalize = 0;
        for (float f : kernel) totalNormalize += f; //Compute normalizing coefficient
        if (totalNormalize == 0) normalize = false; //Do not normalize if totalNormalize equals to zero

        //Initialize global variables
        sConvolution.set_kdiv(totalNormalize);
        sConvolution.set_normal(normalize);
        sConvolution.set_height(bmp.getHeight());
        sConvolution.set_width(bmp.getWidth());
        sConvolution.set_kWidth(kWidth);
        sConvolution.set_kHeight(kHeight);
        sConvolution.set_pIn(input);

        //Allocate output
        Allocation output = Allocation.createTyped(rs, input.getType());
        sConvolution.set_pOut(output);

        //Launch script
        sConvolution.invoke_convolve2d(input,output);
        //Copy to bmp
        output.copyTo(bmp);
        //Free memory
        rs.destroy();
        sConvolution.destroy();
        input.destroy();
        output.destroy();
        kAlloc.destroy();


    }

    /**
     * Computes a 1D Convolution twice (Once vertically and once horizontally) using a kernelX and a kernelY, this application is more faster than use a classic 2D Convolution.
     * @param bmp Image to convolve
     * @param kernelX kernel to convolve horizontally
     * @param kernelY kernel to convolve vertically
     * @param normalize normalize output
     * @param context MainActivity context
     */
    public static void convolve2dSeparable(Bitmap bmp, float[] kernelX, float[] kernelY, boolean normalize, Context context){
        int kXsize = kernelX.length; int kYsize = kernelY.length;
        if(kXsize != kYsize) return;
        RenderScript rs = RenderScript.create(context); //Create rs context
        Allocation input = Allocation.createFromBitmap(rs, bmp); //Getting input
        ScriptC_convolution sConvolution = new ScriptC_convolution(rs); //Create script

        Allocation kAllocX = Allocation.createSized(rs, Element.F32(rs),kXsize); //Allocate memory for kernel
        kAllocX.copyFrom(kernelX); //Copy data from kernel
        Allocation kAllocY = Allocation.createSized(rs, Element.F32(rs),kYsize); //Allocate memory for kernel
        kAllocY.copyFrom(kernelY); //Copy data from kernel

        sConvolution.bind_kernelX(kAllocX);
        sConvolution.bind_kernelY(kAllocY);

        float normalizeX = 0;
        float normalizeY = 0;
        for (float x : kernelX){
            normalizeX += x;
        }
        for (float y : kernelX) {
            normalizeY += y;
        }

        if (normalizeX == 0 || normalizeY == 0) normalize = false; //Do not normalize if totalNormalize equals to zero



        //Initialize global variables
        sConvolution.set_kdivX(normalizeX);
        sConvolution.set_kdivY(normalizeY);
        sConvolution.set_normal(normalize);
        sConvolution.set_height(bmp.getHeight());
        sConvolution.set_width(bmp.getWidth());
        sConvolution.set_kWidth(kXsize);
        sConvolution.set_kHeight(kYsize);
        sConvolution.set_pIn(input);

        sConvolution.invoke_setup(); //Initialize kCenters

        //Allocate output
        Allocation output = Allocation.createTyped(rs, input.getType());
        sConvolution.set_pOut(output);

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


    /**
     * 2D convolution adding the operator X and Y of kernelX and kernelY, used by operators as Sobel, Prewitt, Robertson, etc.
     * @param bmp Image to convolve
     * @param kernelX kernelX operator
     * @param kernelY kernelY operator
     * @param context MainActivity Context
     */
    public static void edgeDetectionConvolution(Bitmap bmp, float[] kernelX, float[] kernelY, int size, Context context){
        //Create context
        int kXsize = kernelX.length; int kYsize = kernelY.length;
        if (kXsize != kYsize) return;
        RenderScript rs = RenderScript.create(context); //Create rs context
        Allocation input = Allocation.createFromBitmap(rs, bmp); //Getting input
        Allocation output = Allocation.createTyped(rs, input.getType());
        ScriptC_convolution sConvolution = new ScriptC_convolution(rs); //Create script

        //Allocating sobel operators for RS
        Allocation kernelXAlloc = Allocation.createSized(rs, Element.F32(rs),kXsize); //Allocate memory for kernel
        kernelXAlloc.copyFrom(kernelX); //Copy data from kernel
        Allocation kernelYAlloc = Allocation.createSized(rs, Element.F32(rs),kYsize); //Allocate memory for kernel
        kernelYAlloc.copyFrom(kernelY); //Copy data from kernel

        //Bind global variables
        sConvolution.bind_kernelX(kernelXAlloc);
        sConvolution.bind_kernelY(kernelYAlloc);
        sConvolution.set_height(bmp.getHeight());
        sConvolution.set_width(bmp.getWidth());
        sConvolution.set_kWidth(size);
        sConvolution.set_kHeight(size);
        sConvolution.set_pIn(input);
        sConvolution.set_pOut(output);
        //Allocate output




        sConvolution.invoke_edgeDetection(input, output);

        output.copyTo(bmp); //Input because it is recycled
        //Free memory
        rs.destroy();
        sConvolution.destroy();
        input.destroy();
        output.destroy();
        kernelXAlloc.destroy();
        kernelYAlloc.destroy();

    }

    public static void intrinsecBlur(Bitmap bmp, int progress, Context context){
        progress = progress < 1 ? 1 : (progress > 24 ? 24 : progress);
        RenderScript rs = RenderScript.create(context); //Create rs context
        Allocation input = Allocation.createFromBitmap(rs, bmp); //Getting input
        Allocation output = Allocation.createTyped(rs, input.getType());


        ScriptIntrinsicBlur sBlur = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));

        sBlur.setInput(input);
        sBlur.setRadius(progress);
        sBlur.forEach(output);
        output.copyTo(bmp);
        rs.destroy();
        sBlur.destroy();
        input.destroy();
        output.destroy();




    }

    //-------------------------------------
    // Called effects
    //-------------------------------------

    public static void gaussianBlur(Bitmap bmp, int progress, Context context){
        int size = progress /10;
        //float[] kernel = Kernels.gauss(size);
        //convolve2dSeparable(bmp, kernel, kernel, true, context);
        intrinsecBlur(bmp, size, context);
    }

    public static void meanBlur(Bitmap bmp, int progress, Context context){
        int size = progress/10;
        float[] kernel = Kernels.mean(size);
        convolve2dSeparable(bmp, kernel, kernel, true, context);
    }

    public static void sharpen(Bitmap bmp, Context context){
        convolve2d(bmp, Kernels.laplacianOfGaussian(9,9,1.8f),9,9, true, context);
    }

    public static void neon(Bitmap bmp, Context context){
        edgeDetectionConvolution(bmp, Kernels.sobelX(3), Kernels.sobelY(3),3, context);
    }

    public static void laplace (Bitmap bmp, Context context){
        Retouching.toGray(bmp,context);
        convolve2d(bmp, Kernels.LAPLACIAN3x3,3,3, true, context);
    }



}
