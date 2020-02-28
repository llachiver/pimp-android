package fr.ubordeaux.pimp.util;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Point;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Display;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;


/**
 * Class with static methods, useful calculations for several things.
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
     * @param context Execution context
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
     * @param context Execution context
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

    /**
     * Rotates a bitmap with degrees passed as parameter and return a new one
     *
     * @param bitmap  bitmap to rotate
     * @param degrees degrees to rotates
     * @return new rotated bitmap
     */
    public static Bitmap rotateBitmap(Bitmap bitmap, float degrees) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degrees);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    /**
     * Rotates a bitmap with boolean orientation
     *
     * @param bitmap     bitmap to rotate
     * @param horizontal rotate horizontal sens
     * @param vertical   rotate in vertical sens
     * @return rotated bitmap
     */
    public static Bitmap flipBitmap(Bitmap bitmap, boolean horizontal, boolean vertical) {
        Matrix matrix = new Matrix();
        matrix.preScale(horizontal ? -1 : 1, vertical ? -1 : 1);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }


    /**
     * Checks if a bitmap with uri must be rotated checking context orientation, if it must to be rotated, return a new rotated bitmap.
     *
     * @param selectedImage image to check and rotate
     * @param context       Execution context
     * @param imageUri      uri from image
     * @return a new bitmap rotated
     * @throws IOException
     * @see <a href="https://teamtreehouse.com/community/how-to-rotate-images-to-the-correct-orientation-portrait-by-editing-the-exif-data-once-photo-has-been-taken</a>
     */
    public static Bitmap rotateImageIfRequired(Bitmap selectedImage, Context context, Uri imageUri) throws IOException {

        if (Objects.equals(imageUri.getScheme(), "content")) {
            String[] projection = {MediaStore.Images.ImageColumns.ORIENTATION};
            Cursor c = context.getContentResolver().query(imageUri, projection, null, null, null);
            assert c != null;
            if (c.moveToFirst()) {
                final int rotation = c.getInt(0);
                c.close();
                return rotateBitmap(selectedImage, rotation);
            }
            return selectedImage;
        } else {
            ExifInterface ei = new ExifInterface(Objects.requireNonNull(imageUri.getPath()));
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    return Utils.rotateBitmap(selectedImage, 90);

                case ExifInterface.ORIENTATION_ROTATE_180:
                    return Utils.rotateBitmap(selectedImage, 180);

                case ExifInterface.ORIENTATION_ROTATE_270:
                    return Utils.rotateBitmap(selectedImage, 270);

                case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                    return Utils.flipBitmap(selectedImage, true, false);

                case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                    return Utils.flipBitmap(selectedImage, false, true);

                default:
                    return selectedImage;
            }
        }
    }


    // And to convert the image URI to the direct file system path of the image file
    public static String getRealPathFromURI(Uri contentUri, Context context) {
        // can post image
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(contentUri,
                proj, // Which columns to return
                null,       // WHERE clause; which rows to return (all rows)
                null,       // WHERE clause selection arguments (none)
                null); // Order-by clause (ascending by name)
        if (cursor == null) {
            return contentUri.getPath(); //For camera path
        }
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String path = cursor.getString(column_index);
        cursor.close();
        return path;
    }
}
