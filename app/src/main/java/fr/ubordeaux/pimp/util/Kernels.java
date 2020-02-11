package fr.ubordeaux.pimp.util;

import android.util.Log;

public class Kernels {

    //Sobel Filter
    public static final float[] SOBEL_X = {
            1.0f, 0.0f, -1.0f,
            2.0f, 0.0f, -2.0f,
            1.0f, 0.0f, -1.0f,
    };
    public static final float[] SOBEL_Y = {
            1.0f, 2.0f, 1.0f,
            0.0f, 0.0f, 0.0f,
            -1.0f, -2.0f, -1.0f,
    };
    //-------------------------------------

    //Prewitt Filter
    public static final float[] PREWITT_X = {
            1.0f, 0.0f, -1.0f,
            1.0f, 0.0f, -1.0f,
            1.0f, 0.0f, -1.0f,
    };
    public static final float[] PREWITT_Y = {
            1.0f, 1.0f, 1.0f,
            0.0f, 0.0f, 0.0f,
            -1.0f, -1.0f, -1.0f,
    };
    //-------------------------------------

    //Roberts Filter
    public static final float[] ROBERTS_X = {
            0.0f, -1.0f,
            1.0f, 0.0f
    };
    public static final float[] ROBERTS_Y = {
            -1.0f, 0.0f,
            0.0f, 1.0f
    };
    //-------------------------------------

    //Separated kernel (to use for both x and y kernels)
    public static final float[] LAPLACIAN = {
            1.0f, -2.0f, 1.0f,
    };

    public static final float[] CLEARNESS = {
            0.0f, -1.0f, 0.0f,
    };



    //-------------------------------------


    //Separated kernel (to use for both x and y kernels)
    public static float[] gauss(int size, float sigma) {
        //Test if size is even
        float[] kernel = new float[size];
        for(int x = 0 ; x < size ; ++x) {
            kernel[x] = (float) ((1 / (Math.sqrt(2 * (Math.PI) * sigma * sigma))) * Math.exp(-((x - size / 2) * (x - size / 2) / (2 * sigma * sigma))));
            Log.e("VALUES " + String.valueOf(x), String.valueOf(kernel[x]));
        }
        return kernel;
    }

    //Separated kernel (to use for both x and y kernels)
    public static final float[] mean(int size) {
        //Test if size is even
        float[] kernel = new float[size];
        for(int x = 0 ; x < size ; ++x)
            kernel[x] = 1f;

        return kernel;
    }




}
