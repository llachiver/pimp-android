package fr.ubordeaux.pimp.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.os.Environment;
import android.view.Display;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Class with static methods, usefull calculations for several things.
 */
public class Utils {


    /**
     * Function to calculate integer to load sample of an image.
     * If required width or height is set to zero, the value returned will be 1.
     *
     * @param originalWidth  Width of the orignal picture.
     * @param originalHeight Height of the orignal picture.
     * @param reqWidth       The desired width for the sample.
     * @param reqHeight      The desired height for the sample.
     * @return An integer X corresponding to the smallest power of 2 keeping size inferior or equal to the required size, where it takes X*X pixels to make 1 pixel in the sample.
     */
    public static int calculateInSampleSize(int originalWidth, int originalHeight,
                                            int reqWidth, int reqHeight) {
        if (reqWidth <= 0 || reqHeight <= 0)
            return 1;
        int inSampleSize = 1;
        // Found smallest SampleSize value which keep both with and height inferior of required size.
        while ((originalHeight / inSampleSize) > reqHeight
                || (originalWidth / inSampleSize) > reqWidth) {
            inSampleSize *= 2;
        }

        return inSampleSize;
    }


    /**
     * Get size of the screen where your activity is running.
     *
     * @param context An Activity launched in the device whose screen size you want to know.
     * @return Point object , where size.x = screen width and size.y = screen height
     */
    public static Point getScreenSize(Activity context) {
        //Get screen dimensions
        Display display = context.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size;

    }

    /**
     * Needed for use {@link #createJPGFile(Context)} }
     */
    public static String CAMERA_LAST_BITMAP_PATH;

    /**
     * This method create and return a .jpg file, its name will be the date and hour of the capture.
     * Also set the String {@link #CAMERA_LAST_BITMAP_PATH} }
     *
     * @param context Activity which requested the creation of the file.
     * @return File object.
     * @throws IOException IOException can appear because of File creation.
     */
    public static File createJPGFile(Context context) throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.FRANCE).format(new Date()); //TODO manage default locale ?
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        CAMERA_LAST_BITMAP_PATH = image.getAbsolutePath();
        return image;
    }


}
