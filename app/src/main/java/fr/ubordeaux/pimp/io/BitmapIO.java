package fr.ubordeaux.pimp.io;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.view.Display;

import fr.ubordeaux.pimp.activity.MainActivity;
import fr.ubordeaux.pimp.util.MainSingleton;


public class BitmapIO {

    /**
     *
     * @param id int id from resource to load
     * @return returns scaled bitmap from phone screen
     */
    public static Bitmap decodeBitmapFromResource(int id){
        MainActivity context = MainSingleton.getContext();
        //Get screen dimensions
        Display display = context.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int screenWidth = size.x;
        int screenHeight = size.y;


        //Loads the image
        Bitmap bmp;
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inJustDecodeBounds = true;
        //InScaled set to false because it matches in target density
        opt.inScaled = false;

        //Firstly, we don't load the image, we just get the dimensions to be able to re-scale it.
        BitmapFactory.decodeResource(context.getResources(), id, opt);


        opt.inMutable = true;
        opt.inJustDecodeBounds = false;
        //Rescaling
        opt.inSampleSize = BitmapIO.calculateInSampleSize(opt, screenWidth, screenHeight);
        return BitmapFactory.decodeResource(context.getResources(), id, opt);

    }

    /**
     * Calculates sample size of BitmapFactory.Options options with reqWidth and reqHeight
     * @param options options from bitmap to downscale.
     * @param reqWidth required bitmap width.
     * @param reqHeight required bitmap height.
     * @return scaled sample size to assign in options.inSampleSize.
     */

    private static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

}
