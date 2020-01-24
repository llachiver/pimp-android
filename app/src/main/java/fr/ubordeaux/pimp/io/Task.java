package fr.ubordeaux.pimp.io;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.view.View;

import java.lang.ref.WeakReference;

import fr.ubordeaux.pimp.R;
import fr.ubordeaux.pimp.activity.MainActivity;
import fr.ubordeaux.pimp.image.Image;

/**
 * General Async task any filter function from this project as an async Task
 */
public class Task extends AsyncTask<Void, Void, Bitmap> {
    private WeakReference<MainActivity> activityWeakReference; //MainActivity reference
    private BitmapAsync callback; //Callback function to Override

    public Task(BitmapAsync callback, MainActivity activity) {
        this.callback = callback;
        this.activityWeakReference = new WeakReference<MainActivity>(activity);
    }

    //Work to do before heavy task
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        MainActivity activity = activityWeakReference.get();
        if (activity == null || activity.isFinishing()){ //Prevent memory leaks
            return;
        }
        activity.findViewById(R.id.progressBar).setVisibility(View.VISIBLE); //Show progressBar
    }

    //Heavy task to do
    @Override
    protected Bitmap doInBackground(Void ... voids) {
        MainActivity activity = activityWeakReference.get();
        while (!isCancelled()) { //Prevent cancelled task by task.cancel()
            if (activity == null || activity.isFinishing()) {
                return null;
            }

            return callback.process(); //Call overrode method

        }
        return null;

    }


    //Update image when heavy task is finished
    @Override
    protected void onPostExecute(Bitmap img) {
        super.onPostExecute(img);
        MainActivity activity = activityWeakReference.get();
        if (activity == null || activity.isFinishing()){
            return;
        }

        //***Linked to main activity ***/
        activity.findViewById(R.id.progressBar).setVisibility(View.INVISIBLE);
        Image image = new Image(img);
        activity.setImage(image);
        activity.updateIv();
    }
}