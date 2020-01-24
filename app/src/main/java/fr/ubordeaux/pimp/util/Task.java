package fr.ubordeaux.pimp.util;

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
public class Task extends AsyncTask<Bitmap, Void, Bitmap> {
    private WeakReference<MainActivity> activityWeakReference;
    private BitmapAsync callback;

    public Task(BitmapAsync callback, MainActivity activity) {
        this.callback = callback;
        this.activityWeakReference = new WeakReference<MainActivity>(activity);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        MainActivity activity = activityWeakReference.get();
        if (activity == null || activity.isFinishing()){
            return;
        }
        activity.findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
    }

    @Override
    protected Bitmap doInBackground(Bitmap ... bitmaps) {
        MainActivity activity = activityWeakReference.get();
        Bitmap bmp;
        while (!isCancelled()) {
            if (activity == null || activity.isFinishing()) {
                return bitmaps[0];
            }

            bmp = callback.process(bitmaps[0]);
            return bmp;
        }
        return bitmaps[0];

    }



    @Override
    protected void onPostExecute(Bitmap img) {
        super.onPostExecute(img);
        MainActivity activity = activityWeakReference.get();
        if (activity == null || activity.isFinishing()){
            return;
        }
        activity.findViewById(R.id.progressBar).setVisibility(View.INVISIBLE);
        Image ima = new Image(img);
        activity.setImage(ima);
        activity.getIv().setImageBitmap(ima.getBmpCurrent());
        //ctivity.getHistogramBarHelper().updateHistogram(true);
    }
}