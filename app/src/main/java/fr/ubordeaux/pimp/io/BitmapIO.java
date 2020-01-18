package fr.ubordeaux.pimp.io;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.net.Uri;
import android.os.AsyncTask;
import android.view.Display;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.Objects;

import fr.ubordeaux.pimp.R;
import fr.ubordeaux.pimp.activity.MainActivity;
import fr.ubordeaux.pimp.util.MainSingleton;


public class BitmapIO {

    private static MainActivity context = MainSingleton.getContext();

    /**
     *
     * @return Point object size, where size.x == screenWidth and size.y == screenHeight
     */
    private static Point getScreenSize(){
        //Get screen dimensions
        Display display = context.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size;

    }

    /**
     *
     * @param id int id from resource to load
     * @return returns scaled bitmap from phone screen
     */
    public static Bitmap decodeAndScaleBitmapFromResource(int id){

        //size.x == screenWidth, size.y == screenHeight
        Point size = getScreenSize();

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
        opt.inSampleSize = BitmapIO.calculateInSampleSize(opt, size.x, size.y);
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

    /**
     *
     * @param imageUri path to bitmap to load
     * @return bitmap loaded and reescaled from uri
     */

    private static Bitmap decodeAndScaleBitmapFromUri(Uri imageUri) {
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
            Point size = getScreenSize();

            //Downscale
            opt.inSampleSize = BitmapIO.calculateInSampleSize(opt, size.x, size.y);


            opt.inJustDecodeBounds = false;
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

    /**
     * Starts intent to pick an image from gallery
     */
    public static void startGalleryActivity(){
        //Photo intent
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);

        photoPickerIntent.setType("image/*");
        //Start activity and wait for result
        context.startActivityForResult(photoPickerIntent, MainActivity.REQUEST_GET_SINGLE_FILE);
    }

    /**Async Task LoadImage**/

    public static void LoadImageTask(Uri uri, MainActivity activity) {
        LoadImageTask task = new LoadImageTask(activity);
        task.execute(uri);
    }

    private static class LoadImageTask extends AsyncTask<Uri, Void, Bitmap> {
        private WeakReference<MainActivity> activityWeakReference;

        private LoadImageTask(MainActivity activity) {
            this.activityWeakReference = new WeakReference<MainActivity>(activity);
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            MainActivity activity = activityWeakReference.get();
            if (activity == null || activity.isFinishing()){
                return;
            }
            activity.findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
        }

        @Override
        protected Bitmap doInBackground(Uri... uris) {
            MainActivity activity = activityWeakReference.get();
            if (activity == null || activity.isFinishing()){
                return null;
            }

            return decodeAndScaleBitmapFromUri(uris[0]);


        }

        @Override
        protected void onPostExecute(Bitmap bmp) {
            super.onPostExecute(bmp);
            //Avoid memory leaks if activity has been finished
            MainActivity activity = activityWeakReference.get();
            if (activity == null || activity.isFinishing()){
                return;
            }

            activity.findViewById(R.id.progressBar).setVisibility(View.INVISIBLE);

            ImageView iv = activity.findViewById(R.id.imageView);
            iv.setImageBitmap(bmp);


        }

    }
    /**End of Async Task declaration **/




}
