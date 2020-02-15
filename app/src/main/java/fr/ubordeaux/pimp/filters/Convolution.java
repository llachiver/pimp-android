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
        if (kWidth % 2 == 0 || kHeight % 2 == 0) return; //Pair kernels not allowed
        RenderScript rs = RenderScript.create(context); //Create rs context
        Allocation input = Allocation.createFromBitmap(rs, bmp); //Getting input
        ScriptC_convolution sConvolution = new ScriptC_convolution(rs); //Create script

        //int squareSize = (int) Math.sqrt(kernel.length); //Getting square size of kernel

        //sConvolution.set_square_ksize(squareSize);

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

    public static void convolve2dSeparable(Bitmap bmp, float[] kernelX, float[] kernelY, boolean normalize, Context context){
        int kXsize = kernelX.length; int kYsize = kernelY.length;
        if(kXsize != kYsize) return;
        RenderScript rs = RenderScript.create(context); //Create rs context
        Allocation input = Allocation.createFromBitmap(rs, bmp); //Getting input
        ScriptC_convolution sConvolution = new ScriptC_convolution(rs); //Create script

        //int squareSize = (int) Math.sqrt(kernel.length); //Getting square size of kernel

        //sConvolution.set_square_ksize(squareSize);

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


    //To replace by an edge detector method ?
    public static void edgeDetection(Bitmap bmp, float[] kernelX, float[] kernelY, Context context){
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
        sConvolution.set_kWidth(3);
        sConvolution.set_kHeight(3);
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



}
