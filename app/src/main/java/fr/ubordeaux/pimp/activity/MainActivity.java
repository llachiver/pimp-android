package fr.ubordeaux.pimp.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.github.chrisbanes.photoview.PhotoView;

import fr.ubordeaux.pimp.R;
import fr.ubordeaux.pimp.image.Image;
import fr.ubordeaux.pimp.io.BitmapIO;
import fr.ubordeaux.pimp.util.MainSingleton;

public class MainActivity extends AppCompatActivity {
    private Image image;

    private PhotoView iv;

    private static InfosFragment infosFragment;
    private static MainFragment mainFragment;

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public void updateIv(){
        iv.setImageBitmap(image.getBmpCurrent());
    }


    public PhotoView getIv() {
        return iv;
    }

    public void setIv(PhotoView iv) {
        this.iv = iv;
    }

    public static final int REQUEST_GET_SINGLE_FILE = 202;
    public static final int REQUEST_TAKE_PHOTO = 12;


    /**
     * Inflate upper menu
     * @param menu to inflate
     * @return true if menu inflated with success
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }



    /**
     * Load image from internal storage
     * @param reqCode request code to identify user's choice
     * @param resultCode result to load image
     * @param data Event given by user to display something
     */
    @Override
    protected void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);
        if (reqCode == REQUEST_GET_SINGLE_FILE) {
            if (resultCode == RESULT_OK) {
                try {
                    BitmapIO.LoadImageTask(data.getData(), this);
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(this, "You haven't picked Image", Toast.LENGTH_LONG).show();
            }
        }

        if (reqCode == REQUEST_TAKE_PHOTO) {
            if (resultCode == RESULT_OK) {
                try {
                    Uri imageUri = BitmapIO.getUriFromCameraFile();
                    BitmapIO.LoadImageTask(imageUri, this);
                }catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();
                }

            } else {
                Toast.makeText(this, "You haven't took picture", Toast.LENGTH_LONG).show();

            }
        }
    }

    /**
     *
     * @param item Item chosen by user.
     * @return true user click on an item.
     */


    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()) {
            //Load photo from gallery
            case R.id.loadFromGallery:

                BitmapIO.startGalleryActivity();

                return true;
            case R.id.loadFromCamera:
                BitmapIO.dispatchTakePictureIntent();
                //Display width and height from bitmap
                return true;

            case R.id.restoreChanges:
                image.restoreBmp();
                updateIv(); //Update imageview
                return true;

            case R.id.imageInfo:
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.contentFragment, infosFragment).commit(); // Show infos fragment
                //TODO getSupportFragmentManager().popBackStackImmediate();
                return true;


            default:
                super.onOptionsItemSelected(item);
        }
        return false;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainFragment = new MainFragment();
        infosFragment = new InfosFragment();
        if (findViewById(R.id.contentFragment) != null) {//Recommended verifications.
            if (savedInstanceState != null)
                return;
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.contentFragment, mainFragment).commit(); // Insert main fragment into activity.
        }

    }

    //TODO move the followging code in onActivityCreated() methods in Fragments class
    @Override
    protected void onStart() {
        super.onStart();
        iv = findViewById(R.id.imageView);
        //Initialize MainSingleton
        MainSingleton.INSTANCE.setContext(this);

        //Loading default image from resources
        Bitmap bmp = BitmapIO.decodeAndScaleBitmapFromResource(R.drawable.starwars);

        //Create image object
        image = new Image(bmp);

        //Allow more zooming
        iv.setMaximumScale(10);
        //Set imageview bitmap
        iv.setImageBitmap(image.getBmpCurrent());
    }

    //BugFix loadImage
    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }


}
