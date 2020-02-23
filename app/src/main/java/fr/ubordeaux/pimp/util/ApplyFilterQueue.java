package fr.ubordeaux.pimp.util;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.view.View;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.util.Queue;

import fr.ubordeaux.pimp.R;
import fr.ubordeaux.pimp.activity.MainActivity;
import fr.ubordeaux.pimp.image.Image;
import fr.ubordeaux.pimp.io.BitmapIO;

/**
 * Apply queue of filters
 */
public class ApplyFilterQueue extends AsyncTask<Void, Void, Void> {
    private WeakReference<MainActivity> activityWeakReference; //MainActivity reference
    private Queue <BitmapRunnable> effectQueue;
    private Bitmap bmp;

    public ApplyFilterQueue (MainActivity activity, Queue <BitmapRunnable> effectQueue, Bitmap bmp) {
        this.activityWeakReference = new WeakReference<>(activity);
        this.effectQueue = effectQueue;
        this.bmp = bmp;
    }

    //Work to do before heavy task
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        MainActivity activity = activityWeakReference.get();
        if (activity == null || activity.isFinishing()) { //Prevent memory leaks
            return;
        }
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
            BitmapRunnable effect;
            effect = effectQueue.poll(); // Get first effect
            while(effect != null){
                effect.setBmp(bmp); //Set new bitmap to run
                effect.run();
                effect = effectQueue.poll();
            }
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

        //***Linked to main activity ***/
        activity.findViewById(R.id.progressBar).setVisibility(View.INVISIBLE);
        BitmapIO.saveBitmap(bmp, "pimp", activity); // Save edited bitmap
        Toast.makeText(activity, "Save success", Toast.LENGTH_SHORT).show();
        //activity.updateIv();
    }

    //User cancelled effect
    @Override
    protected void onCancelled() {
        super.onCancelled();
        MainActivity activity = activityWeakReference.get();
        if (activity == null || activity.isFinishing()) {
            return;
        }
        activity.getImage().discard(); //Reset image
        activity.findViewById(R.id.progressBar).setVisibility(View.INVISIBLE); //Hide progressbar

    }
}