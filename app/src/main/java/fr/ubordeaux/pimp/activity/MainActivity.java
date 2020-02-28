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
import fr.ubordeaux.pimp.filters.Color;
import fr.ubordeaux.pimp.filters.Convolution;
import fr.ubordeaux.pimp.filters.Retouching;
import fr.ubordeaux.pimp.fragments.EffectSettingsFragment;
import fr.ubordeaux.pimp.fragments.EffectsFragment;
import fr.ubordeaux.pimp.fragments.InfosFragment;
import fr.ubordeaux.pimp.image.Image;
import fr.ubordeaux.pimp.util.Effects;
import fr.ubordeaux.pimp.task.LoadImageUriTask;
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
                ft.replace(R.id.infosFragment, fragment);
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
            new LoadImageUriTask(this, getIntent().getData(), true).execute();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Can't load first picture", Toast.LENGTH_LONG).show();
            // Rare situation, then go back to FirstActivity :
            startActivity(new Intent(this, FirstActivity.class));
        }

    }


    /**
     * Used to benchmark an effect, linked to the BRIGHTNESS button for the moment
     */
    public void timeBenchmark(Effects effect){
        float times[] = new float[10];

        long startTime = 0;
        long elapsedTime = 0;

        float min = Float.MAX_VALUE, max = 0;
        float sum = 0;
        float stdDeviation;

        float mean;

        for(int i = 0 ; i < 10 ; i++){
            startTime = System.currentTimeMillis();

            //HERE, effect call :
            image.reset();

            switch(effect){
                case BRIGHTNESS:
                    Retouching.setBrightness(image.getBitmap(),255, this);
                    break;
                case CONTRAST:
                    Retouching.dynamicExtensionRGB(image.getBitmap(), 255, this);
                    break;
                case SATURATION:
                    Retouching.setSaturation(image.getBitmap(), 255, this);
                    break;
                case ENHANCE:
                    Retouching.histogramEqualization(image.getBitmap(), this);
                    break;
                case TO_GRAY:
                    Color.toGray(image.getBitmap(), this);
                    break;
                case INVERT:
                    Color.invert(image.getBitmap(), this);
                    break;
                case CHANGE_HUE:
                    Color.colorize(image.getBitmap(), 255, this, false);
                    break;
                case KEEP_HUE:
                    Color.keepColor(image.getBitmap(), 255, 20, this);
                    break;
                case GAUSS_MIN:
                    Convolution.gaussianBlur(image.getBitmap(), 0, this);
                    break;
                case GAUSS_MAX:
                    Convolution.gaussianBlur(image.getBitmap(), 255, this);
                    break;
                case MEAN_MIN:
                    Convolution.meanBlur(image.getBitmap(), 0, this);
                    break;
                case MEAN_MAX:
                    Convolution.meanBlur(image.getBitmap(), 255, this);
                    break;
                case SHARPEN_MIN:
                    Convolution.sharpen(image.getBitmap(), 0, this);
                    break;
                case SHARPEN_MAX:
                    Convolution.sharpen(image.getBitmap(), 255, this);
                    break;
                case NEON_SOBEL:
                    Convolution.neonSobel(image.getBitmap(),this);
                    break;
                case NEON_PREWITT:
                    Convolution.neonPrewitt(image.getBitmap(),this);
                    break;
                case LAPLACE:
                    Convolution.laplace(image.getBitmap(),this);
                    break;

            }
            elapsedTime = System.currentTimeMillis() - startTime;
            if(elapsedTime < min) min = elapsedTime;
            if(elapsedTime > max) max = elapsedTime;
            times[i] = elapsedTime;
            sum += elapsedTime;
        }
        mean = sum/10;

        sum = 0;
        for(int i = 0 ; i < 10 ; i++){
            sum += (times[i] - mean)*(times[i] - mean);
        }
        stdDeviation = (float) Math.sqrt(sum / 10f);

        Log.i("benchmark",effect.getName() + " & " + min + " | " + max + " | " + mean + " | " + stdDeviation + "\\\\");
        Log.i("benchmark","\\hline");
    }



    @Override
    public void onBackPressed() {
        if (fragmentManager.getBackStackEntryCount() > 0) {
            fragmentManager.popBackStack();
        } else if (effectSettingsFragment != null && effectSettingsFragment.isVisible()) {
            if(currentTask != null) currentTask.cancel(true); //Cancel task if running
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
