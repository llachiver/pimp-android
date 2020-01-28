package fr.ubordeaux.pimp.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.github.chrisbanes.photoview.PhotoView;

import java.io.File;
import java.io.IOException;

import fr.ubordeaux.pimp.R;
import fr.ubordeaux.pimp.image.Image;
import fr.ubordeaux.pimp.io.BitmapIO;
import fr.ubordeaux.pimp.util.LoadImageUriTask;

public class MainActivity extends AppCompatActivity {
    /////////////////////////////////////////////////////////////////////////////////////
    // Settings :
    /////////////////////////////////////////////////////////////////////////////////////
    private static int DEFAULT_IMAGE = R.drawable.starwars;


    //Image currently modified.
    private Image image;

    private PhotoView iv;

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public void updateIv() {
        iv.setImageBitmap(image.getBitmap());
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
     *
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
     *
     * @param reqCode    request code to identify user's choice
     * @param resultCode result to load image
     * @param data       Event given by user to display something
     */
    @Override
    protected void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);
        if (reqCode == REQUEST_GET_SINGLE_FILE) { // Intent from gallery, containing Uri of a the picture selected.
            if (resultCode == RESULT_OK) {
                try {
                    new LoadImageUriTask(this, data.getData()).execute(); // Load and instantiate Image from Uri, see fr.ubordeaux.pimp.util.LoadImageUriTask
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
                    Uri imageUri = BitmapIO.getUriFromCameraFile(); //TODO
                    // BitmapIO.loadImageTask(imageUri, this); TODO
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();
                }

            } else {
                Toast.makeText(this, "You haven't took picture", Toast.LENGTH_LONG).show();

            }
        }
    }

    /**
     * @param item Item chosen by user.
     * @return true user click on an item.
     */


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            //Load photo from gallery
            case R.id.loadFromGallery:

                startGalleryActivity();

                return true;
            case R.id.loadFromCamera:
                startCameraActivity();
                //Display width and height from bitmap
                return true;

            case R.id.restoreChanges:
                image.reset();
                updateIv(); //Update imageview
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
        iv = findViewById(R.id.imageView);
        //Initialize MainSingleton

        //Loading default image from resources
        setImage(new Image(DEFAULT_IMAGE, this));

        //Allow more zooming
        iv.setMaximumScale(10);
        //Set imageview bitmap
        updateIv();
    }

    //BugFix loadImage
    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }


    /**
     * Starts intent to pick an image from gallery
     */
    public void startGalleryActivity() {
        //Photo intent
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);

        photoPickerIntent.setType("image/*");
        //Start activity and wait for result
        this.startActivityForResult(photoPickerIntent, MainActivity.REQUEST_GET_SINGLE_FILE);
    }

    public void startCameraActivity() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(this.getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = BitmapIO.createImageFile(this);
            } catch (IOException ex) {
                // Error occurred while creating the File
                ex.printStackTrace();
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                //context.grantUriPermission("fr.ubordeaux.pimp", photoURI, Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                this.startActivityForResult(takePictureIntent, MainActivity.REQUEST_TAKE_PHOTO);
            }
        }
    }
}
