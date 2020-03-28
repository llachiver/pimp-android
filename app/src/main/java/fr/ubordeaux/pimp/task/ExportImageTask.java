package fr.ubordeaux.pimp.task;

import android.os.AsyncTask;
import android.view.View;
import android.widget.Toast;

import java.lang.ref.WeakReference;

import fr.ubordeaux.pimp.R;
import fr.ubordeaux.pimp.activity.MainActivity;
import fr.ubordeaux.pimp.image.Image;

/**
 * Apply queue of filters
 */
public class ExportImageTask extends AsyncTask<Void, Integer, Void> {
    private WeakReference<MainActivity> activityWeakReference; //MainActivity reference
    private Image image;
    private boolean done;

    public ExportImageTask(MainActivity activity, Image image) {
        this.activityWeakReference = new WeakReference<>(activity);
        this.image = image;
        done = true;
    }

    //Work to do before heavy task
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        MainActivity activity = activityWeakReference.get();
        if (activity == null || activity.isFinishing()) { //Prevent memory leaks
            return;
        }
        Toast.makeText(activity, "Preparing picture for treatment...", Toast.LENGTH_SHORT).show();
        activity.hideMenu();
        activity.hideEffectsList();
        activity.findViewById(R.id.progressBar).setVisibility(View.VISIBLE); //Show progressBar

    }

    //Heavy task to do
    @Override
    protected Void doInBackground(Void... voids) {
        MainActivity activity = activityWeakReference.get();
        while (!isCancelled()) { //Prevent cancelled task by task.cancel()

            if (!image.exportOriginalToGallery(activity, false)) {//export original Image

                //something went wrong :
                Toast.makeText(activity, "Can't export original, exporting edited picture...", Toast.LENGTH_LONG).show();
                if (!image.exportToGallery(activity)) {
                    Toast.makeText(activity, "Can't export original, exporting edited picture...", Toast.LENGTH_LONG).show();
                    done = false;
                    return null;
                }

            }

            return null;
        }

        return null;
    }

    //Save big bitmap
    @Override
    protected void onPostExecute(Void voids) {
        super.onPostExecute(voids);
        MainActivity activity = activityWeakReference.get();
        if (activity == null || activity.isFinishing()) {
            return;
        }

        //***Linked to main activity ***/
        activity.showMenu();
        activity.showEffectsList();
        activity.findViewById(R.id.progressBar).setVisibility(View.INVISIBLE);
        Toast.makeText(activity, done ? "Save success" : "Can't export anything", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        MainActivity activity = activityWeakReference.get();
        if (activity == null || activity.isFinishing()) {
            return;
        }
        activity.showMenu();
        activity.showEffectsList();
        activity.findViewById(R.id.progressBar).setVisibility(View.INVISIBLE); //Hide progressbar

    }
}