package fr.ubordeaux.pimp.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.IOException;

import fr.ubordeaux.pimp.util.Utils;

/**
 * This class offers useful methods to allow activities to communicate with each other.
 */
public class ActivityIO {

    public static final int REQUEST_GET_SINGLE_FILE = 202;
    public static final int REQUEST_TAKE_PHOTO = 12;
    public static final int REQUEST_WRITE_EXTERNAL_STORAGE = 69;
    public static final int REQUEST_READ_EXTERNAL_STORAGE = 68;
    public static final int REQUEST_CAMERA = 67;

    /**
     * Same as {@link #startGalleryActivity(Activity)} but needs permissions.
     *
     * @param activity Context
     */
    static void startGalleryActivityWithPermissions(Activity activity) {
        if (ContextCompat.checkSelfPermission(activity,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            startGalleryActivity(activity);

        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Toast.makeText(activity, "Permission is needed to load image from gallery", Toast.LENGTH_LONG).show();
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waits for the user's response! After the user
                // sees the explanation, try again to request the permission.
            }

            // No explanation needed, we can request the permission.

            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_READ_EXTERNAL_STORAGE);


        }
    }

    /**
     * Same as {@link #startCameraActivity(Activity)} but needs permissions.
     *
     * @param activity Context
     */
    static void startCameraActivityWithPermissions(Activity activity) {
        if (ContextCompat.checkSelfPermission(activity,
                Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            startCameraActivity(activity);

        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                    Manifest.permission.CAMERA)) {
                Toast.makeText(activity, "Permission is needed to load image from camera", Toast.LENGTH_LONG).show();
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waits for the user's response! After the user
                // sees the explanation, try again to request the permission.
            }

            // No explanation needed, we can request the permission.

            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.CAMERA},
                    REQUEST_CAMERA);

        }
    }

    /**
     * Launch local gallery app with Intent
     *
     * @param activity Context
     */
    private static void startGalleryActivity(Activity activity) {
        //Photo intent
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        photoPickerIntent.setType("image/*");
        //Start activity and wait for result
        activity.startActivityForResult(photoPickerIntent, REQUEST_GET_SINGLE_FILE);
    }

    /**
     * Launch local camera app with Intent
     *
     * @param activity Context
     */
    private static void startCameraActivity(Activity activity) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(activity.getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = Utils.createJPGFile(activity);
            } catch (IOException ex) {
                // Error occurred while creating the File
                ex.printStackTrace();
                Toast.makeText(activity, "Something went wrong loading from camera", Toast.LENGTH_LONG).show();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(activity,
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                activity.startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    /**
     * Macro to check if permission is granted or not, in the second case, displays a Toast to inform the user that the permission to use device's camera was not granted.
     *
     * @param activity     Context
     * @param permissions  see {@link Activity#onRequestPermissionsResult(int, String[], int[])}
     * @param grantResults see {@link Activity#onRequestPermissionsResult(int, String[], int[])}
     * @return True if permission is granted, otherwise false
     */
    static boolean cameraPermissionResult(Activity activity, String[] permissions, int[] grantResults) {
        if (grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            return true;
            // permission was granted, yay! Do the
            // contacts-related task you need to do.
        }
        Toast.makeText(activity, "Camera permission denied", Toast.LENGTH_SHORT).show();
        // permission denied, boo! Disable the
        // functionality that depends on this permission.

        return false;
    }

    /**
     * Macro to check if permission is granted or not, in the second case, displays a Toast to inform the user that the permission to read local files was not granted.
     *
     * @param activity     Context
     * @param permissions  see {@link Activity#onRequestPermissionsResult(int, String[], int[])}
     * @param grantResults see {@link Activity#onRequestPermissionsResult(int, String[], int[])}
     * @return True if permission is granted, otherwise false
     */
    static boolean readPermissionResult(Activity activity, String[] permissions, int[] grantResults) {
        // If request is cancelled, the result arrays is empty.
        if (grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            return true;
            // permission was granted, yay! Do the
            // contacts-related task you need to do.
        }
        Toast.makeText(activity, "Read permission denied", Toast.LENGTH_SHORT).show();
        // permission denied, boo! Disable the
        // functionality that depends on this permission.

        return false;
    }

    /**
     * Macro to check if permission is granted or not, in the second case, displays a Toast to inform the user that the permission to write in local folders was not granted.
     *
     * @param activity     Context
     * @param permissions  see {@link Activity#onRequestPermissionsResult(int, String[], int[])}
     * @param grantResults see {@link Activity#onRequestPermissionsResult(int, String[], int[])}
     * @return True if permission is granted, otherwise false
     */
    static boolean writePermissionResult(Activity activity, String[] permissions, int[] grantResults) {
        // If request is cancelled, the result arrays is empty.
        if (grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            return true;
            // permission was granted, yay! Do the
            // contacts-related task you need to do.
        }
        Toast.makeText(activity, "Write permission denied", Toast.LENGTH_SHORT).show();
        // permission denied, boo! Disable the
        // functionality that depends on this permission.
        return false;
    }

}
