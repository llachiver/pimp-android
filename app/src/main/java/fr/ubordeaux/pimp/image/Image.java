package fr.ubordeaux.pimp.image;

import android.graphics.Bitmap;

public class Image {

    private int width;
    private int height;

    //Original version of the image at its creation
    private int[] imgBase;

    //Core of the Image, Bitmap representing its pixels.
    private Bitmap bitmap;

    /**
     * Special constructor to convert a Bitmap already created to an Image.
     * Note that the Bitmap instance is included in the Image and its not a copy of it.
     * A modification on the Image will modify the Bitmap and vice versa.
     *
     * @param bmp Bitmap to include in the Image.
     */
    public Image(Bitmap bmp) {
        width = bmp.getWidth();
        height = bmp.getHeight();
        imgBase = new int[width * height];
        bitmap = bmp;
        bitmap.getPixels(imgBase, 0, width, 0, 0, width, height);
    }

    /**
     * Reset all pixels of the Image to the original version (when the Image was created or loaded).
     */
    public void reset() {
        bitmap.setPixels(imgBase, 0, width, 0, 0, width, height);
    }

    /**
     * Getter of the Bitmap included in the Image, use it to convert this Image to a Bitmap.
     *
     * @return Bitmap object of this Image
     */
    public Bitmap getBitmap() {
        return bitmap;
    }

    /**
     * @return Number of pixels in the width of this Image
     */
    public int getWidth() {
        return width;
    }

    /**
     * @return Number of pixels in the height of this Image
     */
    public int getHeight() {
        return height;
    }


}
