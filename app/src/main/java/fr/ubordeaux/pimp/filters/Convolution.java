package fr.ubordeaux.pimp.filters;

import android.content.Context;
import android.graphics.Bitmap;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;

import fr.ubordeaux.pimp.ScriptC_convolution;
import fr.ubordeaux.pimp.util.Kernels;

/**
 * All convolution-related effects.
 */
public class Convolution {

    /**
     * Convolve an image with a kernel of width (kWidth) and height (kHeight) with a normalization boolean. The kernel must have odd dimensions.
     * @param bmp Bitmap to convolve
     * @param kernel Kernel to use
     * @param kWidth Width of Kernel
     * @param kHeight Height of kernel
     * @param normalize Normalize or not the output (Most of the cases must be set to true)
     * @param context MainActivity context.
     */
    public static void convolve2d(Bitmap bmp, float [] kernel, int kWidth, int kHeight, boolean normalize, Context context){
        if (kWidth % 2 == 0 || kHeight % 2 == 0) return; //Only odd kernels are allowed.
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
     * Convolve an image with 2 separated 1D-kernels (X and Y). This is faster than the convolve2d method. The kernel must have odd dimensions.
     * @param bmp Bitmap to convolve
     * @param kernelX 1D horizontal kernel
     * @param kernelY 1D vertical kernel
     * @param normalize Normalize or not the output (Most of the cases must be set to true)
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
        for (float y : kernelY) {
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
     * 2D convolution done twice, with kernelX and kernelY, in order to detect edges. Used by convolutions with Sobel, Prewitt, Robertson, etc. The kernel must have odd dimensions.
     * @param bmp Image to convolve
     * @param kernelX kernelX operator
     * @param kernelY kernelY operator
     * @param size the size of the kernel
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

        //Launch script
        sConvolution.invoke_edgeDetection(input, output);

        output.copyTo(bmp);
        //Free memory
        rs.destroy();
        sConvolution.destroy();
        input.destroy();
        output.destroy();
        kernelXAlloc.destroy();
        kernelYAlloc.destroy();

    }

    /**
     * The intrinsic renderscript blur.
     * @param bmp the bitmap to modify
     * @param progress the seekbar position, converted into a blur intensity afterwards
     * @param context
     */
    public static void intrinsicBlur(Bitmap bmp, int progress, Context context){
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

    /**
     * Applies gaussian blur with progress strength (Clamped to 0 - 25 range)
     * @param Bitmap to apply filter
     * @param progress Blur strength
     * @param context Main activity context
     */
    public static void gaussianBlur(Bitmap bmp, int progress, Context context){
        int size = progress /10;
        float[] kernel = Kernels.gauss(size);
        convolve2dSeparable(bmp, kernel, kernel, true, context);

        //De-comment this to compare with our blur :
        //intrinsicBlur(bmp, size, context);
    }

    /**
     * Applies mean filter with progress strength (Clamped to 0 - 25 range)
     * @param Bitmap to apply filter
     * @param progress Blur strength
     * @param context Main activity context
     */
    public static void meanBlur(Bitmap bmp, int progress, Context context){
        int size = progress/10;
        float[] kernel = Kernels.mean(size);
        convolve2dSeparable(bmp, kernel, kernel, true, context);
    }

    /**
     * Applies sharpen filter with progress value (Clamped to 0 - 13 range)
     * @param bmp Bitmap to apply effect
     * @param progress sharpen strength
     * @param context MainActivity Context
     */
    public static void sharpen(Bitmap bmp, int progress,  Context context){
        int size = progress/20; //Limit size of kernels
        if (size < 3) size = 3; //3 minimal allowed
        else if (size % 2 == 0) size++;
        float[] kernel = Kernels.laplacianOfGaussian(size);
        convolve2d(bmp, kernel,size,size, true, context);
    }

    /**
     * Computes edge detection using Prewitt operator
     * @param bmp
     * @param context
     */
    public static void neonSobel(Bitmap bmp, Context context){
        edgeDetectionConvolution(bmp, Kernels.SOBEL_X, Kernels.SOBEL_Y,3, context);
    }
    /**
     * Computes edge detection using Prewitt operator
     * @param bmp
     * @param context
     */
    public static void neonPrewitt(Bitmap bmp, Context context){
        edgeDetectionConvolution(bmp, Kernels.PREWITT_X, Kernels.PREWITT_Y,3, context);
    }

    /**
     * Computes edge detection using laplace operator
     * @param bmp
     * @param context
     */
    public static void laplace(Bitmap bmp, Context context){
        convolve2d(bmp, Kernels.LAPLACIAN3x3,3,3, true, context);
    }



}
