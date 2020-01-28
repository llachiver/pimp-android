package fr.ubordeaux.pimp.io;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Display;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import androidx.core.content.FileProvider;

import fr.ubordeaux.pimp.activity.MainActivity;
import fr.ubordeaux.pimp.util.BitmapAsync;
import fr.ubordeaux.pimp.util.Task;
import fr.ubordeaux.pimp.util.Utils;

/**
 * Class containing several static methods to write and read Bitmap obejcts.
 */
public class BitmapIO {


    /**
     * @param id        int id from resource to load
     * @param reqWidth  The desired width for the image.
     * @param reqHeight The desired height for the image.
     * @return returns scaled bitmap, see {@link fr.ubordeaux.pimp.util.Utils#calculateInSampleSize(int, int, int, int)}
     */
    public static Bitmap decodeAndScaleBitmapFromResource(int id, int reqWidth, int reqHeight, Context context) {
        //Loads the image
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inJustDecodeBounds = true;
        //InScaled set to false because it matches in target density
        opt.inScaled = false;
        //Firstly, we don't load the image, we just get the dimensions to be able to re-scale it.
        BitmapFactory.decodeResource(context.getResources(), id, opt);

        opt.inMutable = true;
        opt.inJustDecodeBounds = false;
        //Rescaling
        opt.inSampleSize = Utils.calculateInSampleSize(opt.outWidth, opt.outHeight, reqWidth, reqHeight);
        return BitmapFactory.decodeResource(context.getResources(), id, opt);

    }



    /**
     *
     * @param imageUri path to bitmap to load
     * @return bitmap loaded and reescaled from uri
     */

    private static Bitmap decodeAndScaleBitmapFromUri(Uri imageUri, Context context) {
        //Initialize Bitmap to null
        Bitmap selectedImage = null;
        try {
            //Get inputStream from gallery activity
            InputStream imageStream = context.getContentResolver().openInputStream(imageUri);

            //Instantiate options
            BitmapFactory.Options opt = new BitmapFactory.Options();

            //Avoid dpi troubles setting this to false
            opt.inScaled = false;

            //Get only out sizes through options
            opt.inJustDecodeBounds = true;

            //Getting width and height
            BitmapFactory.decodeStream(imageStream, null, opt);

            //Close stream

            assert imageStream != null;
            imageStream.close();

            //Getting screen size to downscale size.x == screenWidth, size.y == screenHeight
            Point size = Utils.getScreenSize((Activity)context);

            //Downscale
            opt.inSampleSize = Utils.calculateInSampleSize(opt.outWidth, opt.outHeight, size.x, size.y);


            opt.inJustDecodeBounds = false;
            opt.inMutable = true;
            InputStream imageStream2 = context.getContentResolver().openInputStream(imageUri);
            //Get image
            selectedImage = BitmapFactory.decodeStream(imageStream2, null, opt);

            assert imageStream2 != null;
            imageStream2.close();


            return selectedImage;


        } catch (IOException e) {
            e.printStackTrace();
            return selectedImage;
        }
    }



    /**Async Task LoadImage**/

    public static void LoadImageTask(final Uri uri, final MainActivity activity) {
        try {
            BitmapAsync callback = new BitmapAsync() {
                @Override
                public Bitmap process() {
                    return decodeAndScaleBitmapFromUri(uri, activity);
                }
            };
            new Task(callback, activity).execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**** SAVE METHODS ******/

    private static String currentPhotoPath;

    public static Uri getUriFromCameraFile() {
        File f = new File(currentPhotoPath);
        return Uri.fromFile(f);
    }



    public static File createImageFile(Context context) throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.FRANCE).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }








}
