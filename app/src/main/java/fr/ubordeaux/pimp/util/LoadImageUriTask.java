package fr.ubordeaux.pimp.util;

import android.net.Uri;
import android.os.AsyncTask;
import android.view.View;

import java.lang.ref.WeakReference;

import fr.ubordeaux.pimp.R;
import fr.ubordeaux.pimp.activity.MainActivity;
import fr.ubordeaux.pimp.image.Image;

/**
 * Load asynchronously a new Image from Uri with async task
 */
public class LoadImageUriTask extends AsyncTask<Void, Void, Void> {
    private WeakReference<MainActivity> activityWeakReference; //MainActivity reference
    private Image image;
    private Uri source;

    public LoadImageUriTask(MainActivity activity, Uri source) {
        this.activityWeakReference = new WeakReference<>(activity);
        this.source = source;
    }

    //Work to do before heavy task
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        MainActivity activity = activityWeakReference.get();
        if (activity == null || activity.isFinishing()) { //Prevent memory leaks
            return;
        }

        activity.hideMenu(); //Hide menu
        activity.hideEffectsList();
        activity.findViewById(R.id.progressBar).setVisibility(View.VISIBLE); //Show progressBar
    }

    //Heavy task to do
    @Override
    protected Void doInBackground(Void... voids) {
        MainActivity activity = activityWeakReference.get();
        while (!isCancelled()) { //Prevent cancelled task by task.cancel()
            if (activity == null || activity.isFinishing()) {
                return null;
            }
            image = new Image(source, activity); //load and create Image
            return null;
        }

        return null;
    }


    //Update image when heavy task is finished
    @Override
    protected void onPostExecute(Void voids) {
        super.onPostExecute(voids);
        MainActivity activity = activityWeakReference.get();
        if (activity == null || activity.isFinishing()) {
            return;
        }


        activity.showMenu();
        activity.showEffectsList();
        //***Linked to main activity ***/
        activity.findViewById(R.id.progressBar).setVisibility(View.INVISIBLE);
        activity.setImage(image);
        activity.updateIv();
    }
}