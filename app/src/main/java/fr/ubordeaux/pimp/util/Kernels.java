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

    //Sobel Filter
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


    //Separated kernel (to use for both x and y kernels)
    public static final int[] LAPLACIAN = {
            1, -2, 1,
    };

    public static final int[] CLEARNESS = {
            0, -1, 0,
    };






    //-------------------------------------


    //Separated kernel (to use for both x and y kernels)
    public static int[] gauss(int size, float sigma) {
        //Test if size is even
        int[] kernel = new int[size];
        for(int x = 0 ; x < size ; ++x) {
            kernel[x] = (int) ((1 / (Math.sqrt(2 * (Math.PI) * sigma * sigma))) * Math.exp(-((x - size / 2) * (x - size / 2) / (2 * sigma * sigma))));
        }
        return kernel;
    }

    //Separated kernel (to use for both x and y kernels)
    public static int[] mean(int size) {
        //Test if size is even
        int[] kernel = new int[size];
        for(int x = 0 ; x < size ; ++x)
            kernel[x] = 1;

        return kernel;
    }




}
