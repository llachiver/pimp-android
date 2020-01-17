package fr.ubordeaux.pimp;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Get screen dimensions
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int screenWidth = size.x;
        int screenHeight = size.y;

        //Loads the image
        Bitmap bmp;
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inJustDecodeBounds = true;

        //Firstly, we don't load the image, we just get the dimensions to be able to re-scale it.
        bmp = BitmapFactory.decodeResource(getResources(), R.drawable.colline, opt);
        int imgWidth = opt.outWidth;
        int imgHeight = opt.outHeight;

        opt.inMutable = true;
        opt.inJustDecodeBounds = false;
        //Rescaling
        opt.inScaled = true;
        opt.inSampleSize = calculateInSampleSize(opt, screenWidth, screenHeight);
        bmp = BitmapFactory.decodeResource(getResources(), R.drawable.colline, opt);


        ImageView iv = (ImageView) findViewById(R.id.imageView);
        iv.setImageBitmap(bmp);
    }



    public static int calculateInSampleSize(
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
