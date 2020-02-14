package fr.ubordeaux.pimp.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.github.chrisbanes.photoview.PhotoView;

import java.io.File;
import java.io.IOException;

import fr.ubordeaux.pimp.R;
import fr.ubordeaux.pimp.filters.Convolution;
import fr.ubordeaux.pimp.filters.Retouching;
import fr.ubordeaux.pimp.image.Image;
import fr.ubordeaux.pimp.util.Kernels;
import fr.ubordeaux.pimp.util.LoadImageUriTask;
import fr.ubordeaux.pimp.util.Utils;

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
    public static final int REQUEST_WRITE_EXTERNAL_STORAGE = 69;


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

        if (reqCode == REQUEST_TAKE_PHOTO) {//Intent from camera.
            if (resultCode == RESULT_OK) {
                try {
                    new LoadImageUriTask(this, Uri.fromFile(new File(Utils.CAMERA_LAST_BITMAP_PATH))).execute(); // see fr.ubordeaux.pimp.util.Utils.CAMERA_LAST_BITMAP_PATH
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
                return true;
            case R.id.restoreChanges:
                image.reset();
                updateIv(); //Update imageview
                return true;
            case R.id.exportToGallery:
                image.exportToGallery(this);
                return true;
            case R.id.imageInfo:
                Toast.makeText(this, "Width = "+ image.getWidth() + " Heigth = " + image.getHeight(), Toast.LENGTH_SHORT).show();

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

        updateIv();

        listeners();
    }



    //BugFix loadImage
    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    public void listeners(){
        SeekBar sbBrightness = this.findViewById(R.id.sbBrightness);
        SeekBar sbSaturation = this.findViewById(R.id.sbSaturation);
        SeekBar sbContrast = this.findViewById(R.id.sbContrast);
        Button bEqualization = this.findViewById(R.id.bEqualization);
        Button bGray = this.findViewById(R.id.bGray);
        Button bConvolution = this.findViewById(R.id.bConvolution);
        Button bContrast = this.findViewById(R.id.bContrast);
        Button bSelectHue = this.findViewById(R.id.bSelectHue);

        sbBrightness.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                image.reset();
                Retouching.setBrightness(image.getBitmap(), progress-127, MainActivity.this);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }

        });

        sbSaturation.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                image.reset();
                Retouching.setSaturation(image.getBitmap(), progress-127, MainActivity.this);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }

        });

        sbContrast.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                image.reset();
                Retouching.dynamicExtensionRGB(image.getBitmap(), progress, MainActivity.this);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }

        });

        bGray.setOnClickListener(new Button.OnClickListener(){

            @Override
            public void onClick(View v) {
                Retouching.toGray(image.getBitmap(), MainActivity.this);
                updateIv();
            }
        });


        bEqualization.setOnClickListener(new Button.OnClickListener(){

            @Override
            public void onClick(View v) {
                Retouching.histogramEqualization(image.getBitmap(), MainActivity.this);
                updateIv();
            }
        });

        bConvolution.setOnClickListener(new Button.OnClickListener(){

            @Override
            public void onClick(View v) {
                Convolution.convolve2d(image.getBitmap(), Kernels.laplaceOfGauss(9,9,1.8f), 9, 9 , true, MainActivity.this );
                updateIv();
            }
        });

        bContrast.setOnClickListener(new Button.OnClickListener(){

            @Override
            public void onClick(View v) {

                Convolution.edgeDetection(image.getBitmap(), Kernels.SOBEL_X, Kernels.SOBEL_Y, MainActivity.this);
                updateIv();
            }
        });

        bSelectHue.setOnClickListener(new Button.OnClickListener(){

            @Override
            public void onClick(View v) {
                float [] gauss = Kernels.gauss(9,1.2f);
                Convolution.convolve2dSeparable(image.getBitmap(), gauss, gauss, true, MainActivity.this);
                updateIv();
            }
        });

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
                photoFile = Utils.createJPGFile(this);
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
                //this.grantUriPermission("fr.ubordeaux.pimp", photoURI, Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                this.startActivityForResult(takePictureIntent, MainActivity.REQUEST_TAKE_PHOTO);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_WRITE_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    image.exportToGallery(this);
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    Toast.makeText(this, "Save success", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;

            }


            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);    // other 'case' lines to check for other
                // permissions this app might request.
        }
    }



}
