package fr.ubordeaux.pimp.task;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.view.View;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.util.LinkedList;
import java.util.Queue;

import fr.ubordeaux.pimp.R;
import fr.ubordeaux.pimp.activity.MainActivity;
import fr.ubordeaux.pimp.image.Image;
import fr.ubordeaux.pimp.io.BitmapIO;
import fr.ubordeaux.pimp.util.BitmapRunnable;

/**
 * Apply queue of filters
 */
public class ApplyFilterQueueTask extends AsyncTask<Void, Integer, Void> {
    private WeakReference<MainActivity> activityWeakReference; //MainActivity reference
    private Image image;
    private Bitmap bmp;

    public ApplyFilterQueueTask(MainActivity activity, Image image) {
        this.activityWeakReference = new WeakReference<>(activity);
        this.image = image;
    }

    //Work to do before heavy task
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        MainActivity activity = activityWeakReference.get();
        if (activity == null || activity.isFinishing()) { //Prevent memory leaks
            return;
        }
        Toast.makeText(activity,"Preparing picture for treatment...",Toast.LENGTH_SHORT).show();
        activity.hideMenu();
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
            try {
                bmp = BitmapIO.decodeAndScaleBitmapFromUri(image.getUri(), 5000, 5000, activity);
            }catch (Exception e){
                e.printStackTrace();
                return null;
            }
            Queue<BitmapRunnable> effectQueue = new LinkedList<>(image.getEffectQueue()); //Get copy of queue
            BitmapRunnable effect;
            int totalEffects = effectQueue.size();
            int currentEffect = 1;
            effect = effectQueue.poll(); // Get first effect
            while(effect != null){
                publishProgress(currentEffect++, totalEffects);
                effect.setBmp(bmp); //Set new bitmap to run
                effect.run();
                effect = effectQueue.poll();
            }
            return null;
        }

        return null;
    }

    @Override
    protected void onProgressUpdate(Integer... values){
        super.onProgressUpdate(values);
        MainActivity activity = activityWeakReference.get();
        if (activity == null || activity.isFinishing()) {
            return;
        }
        Toast.makeText(activity,"Applying effect " + values[0] + " of " + values[1],Toast.LENGTH_SHORT).show();

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
        try {
            BitmapIO.saveBitmap(bmp, "pimp", activity);  // Save edited bitmap if saveBitmap returned false then get bitmap from image View
        }catch (Exception e){
            e.printStackTrace();
            if (!BitmapIO.saveBitmap(image.getBitmap(), "pimp", activity)) { //Save downscaled bitmap instead | should not happen If this call returns false then we finish activity
                Toast.makeText(activity, "Save cannot be performed", Toast.LENGTH_LONG).show();
                activity.finish();
            }
        }
        Toast.makeText(activity, "Save success", Toast.LENGTH_SHORT).show();
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