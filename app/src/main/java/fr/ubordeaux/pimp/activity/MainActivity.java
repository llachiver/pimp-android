package fr.ubordeaux.pimp.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.github.chrisbanes.photoview.PhotoView;

import java.io.File;
import java.util.Queue;

import fr.ubordeaux.pimp.R;
import fr.ubordeaux.pimp.fragments.EffectSettingsFragment;
import fr.ubordeaux.pimp.fragments.EffectsFragment;
import fr.ubordeaux.pimp.fragments.InfosFragment;
import fr.ubordeaux.pimp.fragments.MacrosFragment;
import fr.ubordeaux.pimp.image.Image;
import fr.ubordeaux.pimp.image.ImageEffect;
import fr.ubordeaux.pimp.image.ImagePack;
import fr.ubordeaux.pimp.task.ExportImageTask;
import fr.ubordeaux.pimp.task.LoadImageUriTask;
import fr.ubordeaux.pimp.util.Effects;
import fr.ubordeaux.pimp.util.Utils;

public class MainActivity extends AppCompatActivity {

    public final static int PREVIEWS_WIDTH = 200; //TODO
    public final static int PREVIEWS_HEIGHT = 200;

    private EffectsFragment effectsListFragment;
    private EffectSettingsFragment effectSettingsFragment;
    private MacrosFragment macrosFragment;
    private InfosFragment infosFragment;
    private FragmentManager fragmentManager;
    private AsyncTask currentTask; //Current asyncTask


    //Image currently modified.
    private ImagePack editionPack;

    private PhotoView iv;

    private Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        iv = findViewById(R.id.photoView);


        //Allow more zooming
        iv.setMaximumScale(10);

        //Init fragments
        effectsListFragment = new EffectsFragment();
        macrosFragment = new MacrosFragment(); //only one instanciation of fragments

        //Used for fragment transactions
        fragmentManager = getSupportFragmentManager();

        inflateEffectsList();

        try {
            new LoadImageUriTask(this, getIntent().getData(), true).execute();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Can't load first picture", Toast.LENGTH_LONG).show();
            // Rare situation, then go back to FirstActivity :
            startActivity(new Intent(this, FirstActivity.class));
        }


    }


    /**
     * Update previews UI
     */
    public void showPreviews() {
        effectsListFragment.showPreviews(editionPack);
    }


    /**
     * Refresh bitmap inside the PhotoView
     */
    public void updateIv() {
        iv.setImageBitmap(editionPack.getMainImage().getBitmap());
    }

    /**
     * @return Instance of the main image, the image in the PhotoView at the center of the screen
     */
    public Image getImage() {
        return editionPack.getMainImage();
    }

    /**
     * @return Instance of the image pacj currently edited.
     */
    public ImagePack getImagePack() {
        return editionPack;
    }

    /**
     * Set currently edite {@link ImagePack}.
     *
     * @param imagePack the pack
     */
    public void setImagePack(ImagePack imagePack) {
        this.editionPack = imagePack;
        macrosFragment.resetCounter();
    }

    /**
     * Apply the given effect on all previews
     *
     * @param effect the effect
     */
    public void effectOnPreviews(ImageEffect effect) {
        editionPack.applyEffect(effect, false); // Not on the main Image because EffectSettingsFragment already done it.
    }

    /**
     * Apply the given effects on all previews
     *
     * @param macro the effects
     */
    public void effectOnPreviews(Queue<ImageEffect> macro) {
        editionPack.applyEffect(macro, false); // Not on the main Image because MacroConfirmationFragment already done it.
    }


    public void setCurrentTask(AsyncTask currentTask) {
        this.currentTask = currentTask;
    }

    public void cancelCurrentTask() {
        if (this.currentTask != null)
            this.currentTask.cancel(true);
    }

    /**
     * @param item Item chosen by user.
     * @return true if user click on an item.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            //Load photo from gallery
            case R.id.loadFromGallery:
                ActivityIO.startGalleryActivityWithPermissions(this);
                return true;
            case R.id.loadFromCamera:
                ActivityIO.startCameraActivityWithPermissions(this);
                return true;
            case R.id.restoreChanges:
                editionPack.reset();
                updateIv(); //Update imageview
                macrosFragment.resetCounter();
                return true;
            case R.id.exportToGallery: //this operation need a permission :
                if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED) {
                    exportImage();

                } else {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        Toast.makeText(this, "Permission is needed to save image", Toast.LENGTH_LONG).show();
                        // Show an explanation to the user *asynchronously* -- don't block
                        // this thread waiting for the user's response! After the user
                        // sees the explanation, try again to request the permission.
                    }

                    // No explanation needed, we can request the permission.
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            ActivityIO.REQUEST_WRITE_EXTERNAL_STORAGE);

                }


                return true;

            case R.id.imageInfo:
                //init info fragment :
                if (infosFragment == null) {
                    infosFragment = new InfosFragment();
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("info", getImage().getInfo()); //send image info to fragment
                    if (editionPack.getPreviewsList().size() > 0) {
                        bundle.putInt("prW", editionPack.getPreviewsList().get(0).image.getWidth());
                        bundle.putInt("prH", editionPack.getPreviewsList().get(0).image.getHeight());
                    }
                    infosFragment.setArguments(bundle);
                }
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.infosFragment, infosFragment);
                ft.addToBackStack("info_fragment");
                ft.commit();
                return true;

            case R.id.macroMenu:
                FragmentTransaction ft2 = getSupportFragmentManager().beginTransaction();
                ft2.replace(R.id.macrosFragment, macrosFragment);
                ft2.addToBackStack("macro_fragment");
                ft2.commit();
                return true;
            default:
                super.onOptionsItemSelected(item);
        }
        return false;
    }

    /**
     * Inflate upper menu
     *
     * @param menu to inflate
     * @return true if menu inflated with success
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        hideMenu();
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.app_name);
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }
        showMenu();
        return true;
    }

    /**
     * Hide items from menu
     */
    public void hideMenu() {
        menu.clear();
    }

    /**
     * Show items from menu
     */
    public void showMenu() {
        getMenuInflater().inflate(R.menu.activity_main, menu);
    }


    @Override
    public void onBackPressed() {
        if (fragmentManager.getBackStackEntryCount() > 0) {
            fragmentManager.popBackStack();
        } else if (effectSettingsFragment != null && effectSettingsFragment.isVisible()) {
            if (currentTask != null) currentTask.cancel(true); //Cancel task if running
            getImage().discard();
            deflateEffectSettings();
        } else {
            moveTaskToBack(true);
        }
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
        if (reqCode == ActivityIO.REQUEST_GET_SINGLE_FILE) { // Intent from gallery, containing Uri of a the picture selected.
            if (resultCode == RESULT_OK) {
                try {
                    new LoadImageUriTask(this, data.getData()).execute(); // Load and instantiate Image from Uri, see fr.ubordeaux.pimp.task.LoadImageUriTask
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(this, "You did not pick an image", Toast.LENGTH_LONG).show();
            }
        }

        if (reqCode == ActivityIO.REQUEST_TAKE_PHOTO) {//Intent from camera.
            if (resultCode == RESULT_OK) {
                try {
                    new LoadImageUriTask(this, Uri.fromFile(new File(Utils.CAMERA_LAST_BITMAP_PATH))).execute(); // see fr.ubordeaux.pimp.util.Utils.CAMERA_LAST_BITMAP_PATH
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();
                }

            } else {
                Toast.makeText(this, "You did not take a picture", Toast.LENGTH_LONG).show();

            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case ActivityIO.REQUEST_WRITE_EXTERNAL_STORAGE: {
                if (ActivityIO.writePermissionResult(this, permissions, grantResults)) {
                    exportImage();
                }
                return;
            }
            case ActivityIO.REQUEST_READ_EXTERNAL_STORAGE: {
                if (ActivityIO.readPermissionResult(this, permissions, grantResults)) {
                    ActivityIO.startGalleryActivityWithPermissions(this);
                }
                return;
            }

            case ActivityIO.REQUEST_CAMERA: {
                if (ActivityIO.cameraPermissionResult(this, permissions, grantResults)) {
                    ActivityIO.startCameraActivityWithPermissions(this);
                }
                return;
            }

            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);    // other 'case' lines to check for other
                // permissions this app might request.
        }
    }

    /**
     * Inflates the list of effects at the bottom of the screen w/ listeners.
     */
    public void inflateEffectsList() {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_effects_container, effectsListFragment);
        fragmentTransaction.commit();
    }

    public void hideEffectsList() {
        findViewById(R.id.fragment_effects_container).setVisibility(View.GONE);
    }

    public void showEffectsList() {
        findViewById(R.id.fragment_effects_container).setVisibility(View.VISIBLE);
    }

    /**
     * Inflates the settings (seekbars, buttons...) of a specific effect w/ listeners.
     *
     * @param effect the enum of the effect
     */
    public void inflateEffectSettings(Effects effect) {
        inflateEffectSettings(effect, null);
    }

    /**
     * Inflates the settings (seekbars, buttons...) of a specific effect w/ listeners.
     *
     * @param effect the enum of the effect
     * @param macro  additional paramter for MACRO option
     */
    public void inflateEffectSettings(Effects effect, Queue<ImageEffect> macro) {
        hideEffectsList();
        effectSettingsFragment = new EffectSettingsFragment(macro);

        Bundle args = new Bundle();
        args.putSerializable("effect", effect);
        effectSettingsFragment.setArguments(args);


        //resize image view :
        ConstraintSet constraintSet = new ConstraintSet();
        ConstraintLayout constraintLayout = findViewById(R.id.mainLayout);
        constraintSet.clone(constraintLayout);
        constraintSet.connect(R.id.photoView, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 0);
        constraintSet.applyTo(constraintLayout);


        //fragment switch :
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_settings_container, effectSettingsFragment);
        fragmentTransaction.commit();
    }

    public void deflateEffectSettings() {
        //resize image view :
        ConstraintSet constraintSet = new ConstraintSet();
        ConstraintLayout constraintLayout = findViewById(R.id.mainLayout);
        constraintSet.clone(constraintLayout);
        constraintSet.connect(R.id.photoView, ConstraintSet.BOTTOM, R.id.guideline3, ConstraintSet.BOTTOM, 0);
        constraintSet.applyTo(constraintLayout);

        //fragment switch :
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.remove(effectSettingsFragment);
        fragmentTransaction.commit();
        currentTask = null;
        effectSettingsFragment = null;
        showEffectsList();
    }

    private void exportImage() {
        try {
            new ExportImageTask(this, getImage()).execute();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Save cannot be performed", Toast.LENGTH_LONG).show();
        }
    }

}
