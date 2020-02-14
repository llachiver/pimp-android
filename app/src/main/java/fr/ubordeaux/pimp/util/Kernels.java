package fr.ubordeaux.pimp.util;

import android.util.Log;

public class Kernels {

    //Sobel Filter
    public static final int[] SOBEL_X = {
            1, 0, -1,
            2, 0, -2,
            1, 0, -1,
    };
    public static final int[] SOBEL_Y = {
            1, 2, 1,
            0, 0, 0,
            -1, -2, -1,
    };
    //-------------------------------------

    public static final float[] LAPLACIAN3x3 = {
            -1, -1, -1,
            -1, 8, -1,
            -1, -1, -1,
    };



    //KIRSCH Filter
    public static final int[] KIRSCH_X = {
              5,  5,  5,
             -3,  0, -3,
             -3, -3, -3,
    };
    public static final int[] KIRSCH_Y = {
            5, -3, -3,
            5,  0, -3,
            5, -3, -3,
    };
    //-------------------------------------


    //Prewitt Filter
    public static final int[] PREWITT_X = {
            1, 0, -1,
            1, 0, -1,
            1, 0, -1,
    };
    public static final int[] PREWITT_Y = {
            1, 1, 1,
            0, 0, 0,
            -1, -1, -1,
    };
    //-------------------------------------





    //-------------------------------------


    //Separated kernel (to use for both x and y kernels)
    public static float[] gauss(int size, float sigma) {
        //Test if size is even
        float[] kernel = new float[size];
        for(int x = 0 ; x < size ; ++x) {
            kernel[x] = (float) ((1 / (Math.sqrt(2 * (Math.PI) * sigma * sigma))) * Math.exp(-((x - (size >> 1)) * (x - (size >> 1)) / (2 * sigma * sigma))));

        }
        return kernel;
    }

    //Separated kernel (to use for both x and y kernels)
    public static float[] mean(int size) {
        //Test if size is even
        float[] kernel = new float[size];
        for(int x = 0 ; x < size ; ++x)
            kernel[x] = 1;

        return kernel;
    }

    public static float[] laplaceOfGauss(int width, int height, float sigma) {

        float [] kernel = new float[width * height];


        for(int y = 0; y < height; y++){
            for (int x = 0; x < width; x++){
                int index = x + (y * width);

                float res =  ((((x - (width >> 1)) * (x - (width >> 1)) ) + ((y - (height >> 1)) * (y - (height >> 1)) )) / (2*(sigma*sigma))) ;
                kernel[index] = (float) ( - ( 1/ (Math.PI * Math.pow(sigma, 4))) * (1 - res) * Math.exp((-res)));
                //kernel[index] *= 1000;
                Log.e("VALUES", String.valueOf(x) + " " + String.valueOf(y) + "   " + String.valueOf(kernel[index]));

            }
        }
        return kernel;

    }






}
