package fr.ubordeaux.pimp.util;

/**
 * Class that stores all the predefined Kernels and some methods to generate new kernels
 */
public class Kernels {


    public static final float[] LAPLACIAN3x3 = {
            1, 1, 1,
            1, -8, 1,
            1, 1,  1,
    };

    public static final float[] SHARPEN3X3 = {
            0, -1, 0,
            -1, 5, -1,
            0, -1, 0,
    };

    //-------------------------------------
    //SOBEL Filter
    public static final float[] SOBEL_X = {
            -1,  0,  1,
            -2,  0,  2,
            -1,  0,  1,
    };
    public static final float[] SOBEL_Y = {
             1,  2,  1,
             0,  0,  0,
            -1, -2, -1,
    };

    //-----------------------------------------
    //Prewitt Filter
    public static final float[] PREWITT_X = {
            1, 0, -1,
            1, 0, -1,
            1, 0, -1,
    };
    public static final float[] PREWITT_Y = {
            1, 1, 1,
            0, 0, 0,
            -1, -1, -1,
    };
    //-------------------------------------



    //Separated kernel (to use for both x and y kernels)
    public static float[] gauss(int size) {
        //Test if size is even
        if( size%2 == 0) size++;
        float sigma = size/2f;
        float[] kernel = new float[size];
        for(int x = 0 ; x < size ; ++x) {
            kernel[x] = (float) ((1 / (Math.sqrt(2 * (Math.PI) * sigma * sigma))) * Math.exp(-((x - (size >> 1)) * (x - (size >> 1)) / (2 * sigma * sigma))));

        }
        return kernel;
    }

    //Separated kernel (to use for both x and y kernels)
    public static float[] mean(int size) {
        //Test if size is even
        if( size%2 == 0) size++;
        float[] kernel = new float[size];
        for(int x = 0 ; x < size ; ++x)
            kernel[x] = 1;

        return kernel;
    }

    public static float[] identity (int width, int height){
        if (width % 2 == 0 || height % 2 == 0) return null;
        float [] kernel = new float[width * height];
        int xCenter = width / 2;
        int yCenter = height / 2;
        kernel [xCenter + (yCenter * width)] = 1;
        return kernel;
    }

    //Full sobel-x kernel
    public static float[] sobelX(int size) {
        if (size % 2 == 0) size++;
        float [] kernel = new float[size * size];
        int xCenter = size / 2;
        int yCenter = size / 2;
        for(int y = 0 ; y < size ; y++){
            for(int x = 0 ; x < size ; x++){
                int idx = y*size + x;
                float value = 0;
                if(y < yCenter || y > yCenter)
                    value = 1;
                else if(y == yCenter)
                    value = 2;
                if(x > xCenter)
                    value *= -1;
                else if(x == xCenter)
                    value = 0;
                kernel[idx] = value;
            }
        }
        return kernel;
    }


    //Full sobel-y kernel
    public static float[] sobelY(int size) {
        if (size % 2 == 0) size++;
        float [] kernel = new float[size * size];
        int xCenter = size / 2;
        int yCenter = size / 2;
        for(int y = 0 ; y < size ; y++){
            for(int x = 0 ; x < size ; x++){
                int idx = y*size + x;
                float value = 0;
                if(x < xCenter || x > xCenter)
                    value = 1;
                else if(x == xCenter)
                    value = 2;
                if(y > yCenter)
                    value *= -1;
                else if(y == yCenter)
                    value = 0;
                kernel[idx] = value;
            }
        }
        return kernel;
    }

    /**
     * Generate sharpenFilter in function of edgeDetectionConvolution filter. Calculates the difference between identity matrix and edgeDetector filter.
     * @param edgeDetectionKernel
     * @return sharpenKernel of size of edgeDetectionKernel
     */
    public static float[] sharpenFilter(float [] edgeDetectionKernel, int width, int height){
        if (width % 2 == 0 || height % 2 == 0) return null;
        float[] filter = identity(width, height);
        for (int i = 0; i < width*height; ++i){
            filter[i] -= edgeDetectionKernel[i];
        }
        return filter;
    }


    /**
     * Generates a 2D kernel of size dimensions using the Laplacian Of Gaussiian formula @Link https://homepages.inf.ed.ac.uk/rbf/HIPR2/log.htm
     * @return
     */
    public static float[] laplacianOfGaussian(int size) {
        if (size%2 == 0) size++;
        float sigma =  size / 5f;
        float [] kernel = new float[size * size];


        for(int y = 0; y < size; y++){
            for (int x = 0; x < size; x++){
                int index = x + (y * size);

                float res =  ((((x - (size >> 1)) * (x - (size >> 1)) ) + ((y - (size >> 1)) * (y - (size >> 1)) )) / (2*(sigma*sigma))) ;
                kernel[index] = (float) ( - ( 1/ (Math.PI * Math.pow(sigma, 4))) * (1 - res) * Math.exp((-res)));
            }
        }
        return kernel;

    }






}
