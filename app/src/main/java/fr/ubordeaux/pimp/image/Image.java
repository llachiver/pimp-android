package fr.ubordeaux.pimp.image;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.view.View;

import fr.ubordeaux.pimp.activity.MainActivity;
import fr.ubordeaux.pimp.io.BitmapIO;
import fr.ubordeaux.pimp.util.MainSingleton;
import fr.ubordeaux.pimp.util.Utils;

public class Image {

    //TODO To remove !!!!!
    private static MainActivity context = MainSingleton.INSTANCE.getContext();

    private int width;
    private int height;

    //Original version of the image at its creation
    private int[] imgBase;

    //Core of the Image, Bitmap representing its pixels.
    private Bitmap bitmap;

    /**
     * Load an image from resources, size is automatically limited depending the screen size.
     *
     * @param id int value of the resource
     * @param context An Activity launched in the device where you want to adapt your Image.
     */
    public Image(int id, Activity context) {
        this(id, Utils.getScreenSize(context));
    }

    /**
     * See {@link #Image(int, int, int)}
     *
     * @param id   int value of the resource
     * @param size Point where x is width and y is height.
     */
    public Image(int id, Point size) {
        this(id, size.x, size.y);
    }

    /**
     * Load an image from resources with size limitation.
     * See {@link fr.ubordeaux.pimp.util.Utils#calculateInSampleSize(int, int, int, int)}
     *
     * @param id             int value of the resource
     * @param requiredWidth  The desired width for the image.
     * @param requiredHeight The desired height for the image.
     */
    public Image(int id, int requiredWidth, int requiredHeight) {
        this(BitmapIO.decodeAndScaleBitmapFromResource(id, requiredWidth, requiredHeight));
    }


    /**
     * Special constructor to convert a Bitmap already created to an Image.
     * Note that the Bitmap instance is included in the Image and its not a copy of it.
     * A modification on the Image will modify the Bitmap and vice versa.
     *
     * @param bmp Bitmap to include in the Image.
     */
    public Image(Bitmap bmp) {
        bitmap = bmp;
        width = bmp.getWidth();
        height = bmp.getHeight();
        imgBase = new int[width * height];
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
