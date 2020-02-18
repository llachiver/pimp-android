package fr.ubordeaux.pimp.io;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import fr.ubordeaux.pimp.image.ImageInfos;
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
     * @param imageUri  path to bitmap to load
     * @param reqWidth  The desired width for the image.
     * @param reqHeight The desired height for the image.
     * @return bitmap loaded and scaled from uri, see {@link fr.ubordeaux.pimp.util.Utils#calculateInSampleSize(int, int, int, int)}
     */

    public static Bitmap decodeAndScaleBitmapFromUri(Uri imageUri, int reqWidth, int reqHeight, Context context) {
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

            //Downscale
            opt.inSampleSize = Utils.calculateInSampleSize(opt.outWidth, opt.outHeight, reqWidth, reqHeight);


            opt.inJustDecodeBounds = false;
            opt.inMutable = true;
            InputStream imageStream2 = context.getContentResolver().openInputStream(imageUri);
            //Get image
            selectedImage = BitmapFactory.decodeStream(imageStream2, null, opt);

            assert imageStream2 != null;

            //Get image orientation

            imageStream2.close();

            return Utils.rotateImageIfRequired(selectedImage, context, imageUri);



        } catch (IOException e) {
            e.printStackTrace();
            return selectedImage;
        }
    }


    /**** SAVE METHODS ******/

    public static boolean saveBitmap(Bitmap imgBitmap, String fileNameOpening, Context context){

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.US);
        Date now = new Date();
        String fileName = fileNameOpening + "_" + formatter.format(now) + ".jpeg";

        FileOutputStream outStream;

            File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

            File saveDir =  new File(path + "/PimpImages/");

            if(!saveDir.exists()) {
                saveDir.mkdirs();
            }
            File fileDir =  new File(saveDir, fileName);

        try {
            outStream = new FileOutputStream(fileDir);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }
        if (imgBitmap == null)
                Toast.makeText(context, "Null pointer exception", Toast.LENGTH_LONG).show();
            assert imgBitmap != null;
            imgBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);

            MediaScannerConnection.scanFile(context.getApplicationContext(),
                    new String[] { fileDir.toString() }, null,
                    new MediaScannerConnection.OnScanCompletedListener() {
                        public void onScanCompleted(String path, Uri uri) {
                        }
                    });
        try {
            outStream.flush();
            outStream.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

}
