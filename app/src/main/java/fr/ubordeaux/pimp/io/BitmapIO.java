package fr.ubordeaux.pimp.io;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Display;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import androidx.core.content.FileProvider;
import fr.ubordeaux.pimp.R;
import fr.ubordeaux.pimp.activity.MainActivity;
import fr.ubordeaux.pimp.image.Image;
import fr.ubordeaux.pimp.util.MainSingleton;


public class BitmapIO {

    private static MainActivity context = MainSingleton.INSTANCE.getContext();

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

            activity.getIv().setImageBitmap(bmp);
            activity.setImage(new Image(bmp));


        }

    }
    /**End of Async Task declaration **/

    /**** SAVE METHODS ******/

    private static String currentPhotoPath;

    public static Uri getUriFromCameraFile() {
        File f = new File(currentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        return contentUri;
    }



    private static File createImageFile() throws IOException {
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

    public static void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(context.getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                ex.printStackTrace();
                Toast.makeText(context, "Something went wrong", Toast.LENGTH_LONG).show();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(context,
                        "com.example.android.fileprovider",
                        photoFile);
                //context.grantUriPermission("fr.ubordeaux.pimp", photoURI, Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                context.startActivityForResult(takePictureIntent, MainActivity.REQUEST_TAKE_PHOTO);
            }
        }
    }






}
