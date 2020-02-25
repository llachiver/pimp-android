package fr.ubordeaux.pimp.image;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.LinkedList;
import java.util.Queue;

import fr.ubordeaux.pimp.activity.MainActivity;
import fr.ubordeaux.pimp.io.BitmapIO;
import fr.ubordeaux.pimp.util.ApplyFilterQueueTask;
import fr.ubordeaux.pimp.util.BitmapRunnable;
import fr.ubordeaux.pimp.util.Utils;

import static fr.ubordeaux.pimp.activity.MainActivity.REQUEST_WRITE_EXTERNAL_STORAGE;

public class Image {

    private int width;
    private int height;

    private Uri uri;
    private ImageInfo infos; //embeds all available information

    //Original version of the image at its creation
    private int[] imgBase;

    //Quick save of the image done when opening an effect, in order to discard its modifications later
    private int[] imgQuickSave;

    
    private Queue <BitmapRunnable> effectQueue;

    //Core of the Image, Bitmap representing its pixels.
    private Bitmap bitmap;



    /**
     * Load an image from resources, size is automatically limited depending the screen size.
     *
     * @param id      int value of the resource
     * @param context An Activity launched in the device where you want to adapt your Image.
     */
    public Image(int id, Activity context) {
        this(id, Utils.getScreenSize(context), context);
    }

    /**
     * See {@link #Image(int, int, int, Activity)}
     *
     * @param id      int value of the resource
     * @param size    Point where x is width and y is height.
     * @param context An Activity launched in the device where you want to adapt your Image.
     */
    public Image(int id, Point size, Activity context) {
        this(id, size.x, size.y, context);
    }

    /**
     * Load an image from resources with size limitation.
     * See {@link fr.ubordeaux.pimp.util.Utils#calculateInSampleSize(int, int, int, int)}
     * Note that information are not yet managed with this constructor.
     *
     * @param id             int value of the resource
     * @param requiredWidth  The desired width for the image.
     * @param requiredHeight The desired height for the image.
     * @param context        An Activity launched in the device where you want to adapt your Image.
     */
    public Image(int id, int requiredWidth, int requiredHeight, Activity context) {
        this(BitmapIO.decodeAndScaleBitmapFromResource(id, requiredWidth, requiredHeight, context));
        Resources resources = context.getResources();
        this.uri = new Uri.Builder()
                .scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
                .authority(resources.getResourcePackageName(id))
                .appendPath(resources.getResourceTypeName(id))
                .appendPath(resources.getResourceEntryName(id))
                .build(); //TO TEST !!!!!!
        infos = new ImageInfo(null, context); // todo ?
        infos.setLoadedHeight(height);//set values n info pack
        infos.setLoadedWidth(width);
    }

    /**
     * Load an image from folders, size is automatically limited depending the screen size.
     *
     * @param uri     Path of the picture to laod.
     * @param context An Activity launched in the device where you want to adapt your Image.
     */
    public Image(Uri uri, Activity context) {
        this(uri, Utils.getScreenSize(context), context);
    }

    /**
     * See {@link #Image(Uri, int, int, Activity)}
     *
     * @param uri     Path of the picture to load.
     * @param size    Point where x is width and y is height.
     * @param context An Activity launched in the device where you want to adapt your Image.
     */
    public Image(Uri uri, Point size, Activity context) {
        this(uri, size.x, size.y, context);
    }

    /**
     * Load an image from folders with size limitation.
     * See {@link fr.ubordeaux.pimp.util.Utils#calculateInSampleSize(int, int, int, int)}
     *
     * @param uri            Path of the picture to laod.
     * @param requiredWidth  The desired width for the image.
     * @param requiredHeight The desired height for the image.
     * @param context        An Activity launched in the device where you want to adapt your Image.
     */
    public Image(Uri uri, int requiredWidth, int requiredHeight, Activity context) {
        this(BitmapIO.decodeAndScaleBitmapFromUri(uri, requiredWidth, requiredHeight, context));
        this.uri = uri;
        infos = new ImageInfo(this.uri, context);
        infos.setLoadedHeight(height);//set values n info pack
        infos.setLoadedWidth(width);
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
        imgQuickSave = new int[width * height];
        bitmap.getPixels(imgBase, 0, width, 0, 0, width, height);
        bitmap.getPixels(imgQuickSave, 0, width, 0, 0, width, height);
        effectQueue = new LinkedList<>();
    }

    /**
     * See {@link Image(Image, int, int)}
     * Differs from {@link Image(Bitmap)} because this does not pack the bitmap in the Image, but create another Bitmap.
     *
     * @param bmp               Source Bitmap
     * @param newRequiredWidth  The desired width for the image. (must be less than or equal to the original)
     * @param newRequiredHeight The desired height for the image. (must be less than or equal to the original)
     */
    public Image(Bitmap bmp, int newRequiredWidth, int newRequiredHeight) {
        newRequiredWidth = newRequiredWidth == 0 || newRequiredWidth > bmp.getWidth() ? bmp.getWidth() : newRequiredWidth;
        newRequiredHeight = newRequiredHeight == 0 || newRequiredHeight > bmp.getHeight() ? bmp.getHeight() : newRequiredHeight;
        int ratio = Utils.calculateInSampleSize(bmp.getWidth(), bmp.getHeight(), newRequiredWidth, newRequiredHeight);
        Bitmap newBitmap = Bitmap.createScaledBitmap(bmp, bmp.getWidth() / ratio,
                bmp.getHeight() / ratio, true); //true for bilinear filtering
        bitmap = newBitmap;
        width = newBitmap.getWidth();
        height = newBitmap.getHeight();
        imgBase = new int[width * height];
        imgQuickSave = new int[width * height];
        bitmap.getPixels(imgBase, 0, width, 0, 0, width, height);
        bitmap.getPixels(imgQuickSave, 0, width, 0, 0, width, height);
        infos = new ImageInfo(null, null);
        infos.setLoadedHeight(height);//set values n info pack
        infos.setLoadedWidth(width);
        effectQueue = new LinkedList<>();
    }

    /**
     * Use this constructor to duplicate an Image.
     *
     * @param source Image to copy.
     */
    public Image(Image source) {
        this(source, source.getWidth(), source.getHeight());
    }

    /**
     * Constructor to create an Image from a rescaled other Image.
     * See {@link fr.ubordeaux.pimp.util.Utils#calculateInSampleSize(int, int, int, int)}
     * Note that the rescaling is using bilinear filtering, see {@link android.graphics.Bitmap#createScaledBitmap(Bitmap, int, int, boolean)}.
     * Note that infos will be duplicated.
     *
     * @param source            Source Image
     * @param newRequiredWidth  The desired width for the image. (must be less than or equal to the original)
     * @param newRequiredHeight The desired height for the image. (must be less than or equal to the original)
     */
    public Image(Image source, int newRequiredWidth, int newRequiredHeight) {
        this(source.getBitmap(), newRequiredWidth, newRequiredHeight);
        this.uri = source.uri;
        infos = new ImageInfo(source.getInfo());
        infos.setLoadedHeight(height);//set values n info pack
        infos.setLoadedWidth(width);
    }

    /**
     * Reset all pixels of the Image to the original version (when the Image was created or loaded).
     */
    public void reset() {
        bitmap.setPixels(imgBase, 0, width, 0, 0, width, height);
        effectQueue.clear();
    }

    public void quickSave() {
        bitmap.getPixels(imgQuickSave, 0, width, 0, 0, width, height);
    }

    public void discard() {
        bitmap.setPixels(imgQuickSave, 0, width, 0, 0, width, height);
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

    /**
     * @return Get a pack of information about this image: see {@link ImageInfo}. Note that depending if the image is loaded or if it was created from a Bitmap, some fiels can be null.
     */
    public ImageInfo getInfo() {
        return infos;
    }

    public Queue<BitmapRunnable> getEffectQueue() {
        return effectQueue;
    }

    /**
     *
     * @return Get uri from image
     */
    public Uri getUri() {
        return uri;
    }

    /**
     * Export the current image to the devices gallery
     * Ask for the user's permission if not yet given to store the current
     * image in the gallery before calling the function that saves it to the gallery.
     *
     * @param context n Activity launched in the device where you want to save your Image.
     */
    public void exportToGallery(Activity context) {
        if (ContextCompat.checkSelfPermission(context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            //Load new Bitmap and apply with async task

            new ApplyFilterQueueTask((MainActivity) context,this).execute(); //Apply effectQueue
            //BitmapIO.saveBitmap(this.getBitmap(), "pimp", context);

        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(context,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                Toast.makeText(context, "Permission is needed to save image", Toast.LENGTH_LONG).show();
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            }


            // No explanation needed, we can request the permission.

            ActivityCompat.requestPermissions(context,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_WRITE_EXTERNAL_STORAGE);

            // MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE is an
            // app-defined int constant. The callback method gets the
            // result of the request.

        }




    }
}
