package fr.ubordeaux.pimp.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.github.chrisbanes.photoview.PhotoView;

import java.io.File;

import fr.ubordeaux.pimp.R;
import fr.ubordeaux.pimp.fragments.EffectSettingsFragment;
import fr.ubordeaux.pimp.fragments.EffectsFragment;
import fr.ubordeaux.pimp.fragments.InfosFragment;
import fr.ubordeaux.pimp.image.Image;
import fr.ubordeaux.pimp.util.Effects;
import fr.ubordeaux.pimp.util.LoadImageUriTask;
import fr.ubordeaux.pimp.util.Utils;

public class MainActivity extends AppCompatActivity {

    private EffectsFragment effectsListFragment;
    private EffectSettingsFragment effectSettingsFragment;
    private FragmentManager fragmentManager;
    private AsyncTask currentTask; //Current asyncTask


    //Image currently modified.
    private Image image;

    private PhotoView iv;

    private Menu menu;

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

    public AsyncTask getCurrentTask() {
        return currentTask;
    }

    public void setCurrentTask(AsyncTask currentTask) {
        this.currentTask = currentTask;
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
                ActivityIO.startGalleryActivityWithPermissions(this);
                return true;
            case R.id.loadFromCamera:
                ActivityIO.startCameraActivityWithPermissions(this);
                return true;
            case R.id.restoreChanges:
                image.reset();
                updateIv(); //Update imageview
                return true;
            case R.id.exportToGallery:
                image.exportToGallery(this);
                return true;

            case R.id.imageInfo:
                InfosFragment fragment = new InfosFragment();
                Bundle bundle = new Bundle();
                bundle.putParcelable("info", getImage().getInfo()); //send image info to fragment
                fragment.setArguments(bundle);
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.contentFragment, fragment);
                ft.addToBackStack("info_fragment");
                ft.commit();
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        iv = findViewById(R.id.photoView);
        //Allow more zooming
        iv.setMaximumScale(10);

        //Init the fragments
        effectsListFragment = new EffectsFragment();
        //Used for fragment transactions
        fragmentManager = getSupportFragmentManager();

        inflateEffectsList();


        try {
            setImage(new Image(getIntent().getData(), this));
            updateIv();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Can't load first picture", Toast.LENGTH_LONG).show();
            // Rare situation, then go back to FirstActivity :
            startActivity(new Intent(this, FirstActivity.class));
        }
    }

    @Override
    public void onBackPressed() {
        if (fragmentManager.getBackStackEntryCount() > 0) {
            fragmentManager.popBackStack();
        } else if (effectSettingsFragment != null && effectSettingsFragment.isVisible()) {
            image.discard();
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
                    new LoadImageUriTask(this, data.getData()).execute(); // Load and instantiate Image from Uri, see fr.ubordeaux.pimp.util.LoadImageUriTask
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(this, "You haven't picked Image", Toast.LENGTH_LONG).show();
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
                Toast.makeText(this, "You haven't took picture", Toast.LENGTH_LONG).show();

            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case ActivityIO.REQUEST_WRITE_EXTERNAL_STORAGE: {
                if (ActivityIO.writePermissionResult(this, permissions, grantResults)) {
                    image.exportToGallery(this);
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
        hideEffectsList();
        effectSettingsFragment = new EffectSettingsFragment();

        Bundle args = new Bundle();
        args.putSerializable("effect", effect);
        effectSettingsFragment.setArguments(args);

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_settings_container, effectSettingsFragment);
        fragmentTransaction.commit();
    }

    public void deflateEffectSettings() {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.remove(effectSettingsFragment);
        fragmentTransaction.commit();
        showEffectsList();
    }


}
