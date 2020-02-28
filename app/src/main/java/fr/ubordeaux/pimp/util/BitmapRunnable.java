package fr.ubordeaux.pimp.util;

import android.graphics.Bitmap;

import fr.ubordeaux.pimp.image.Image;

/**
 * This class stores a runnable call and a bitmap reference to be changed in {@link fr.ubordeaux.pimp.task.ApplyFilterQueueTask (Image, Context)}
 */

public abstract class BitmapRunnable implements Runnable {
    private Bitmap bmp;

    public Bitmap getBmp() {
        return bmp;
    }

    /**
     * Change bitmap reference in BitmapRunnable object
     * @param bmp Bmp to modify
     */
    public void setBmp(Bitmap bmp) {
        this.bmp = bmp;
    }

    public BitmapRunnable(Bitmap bmp){
        this.bmp = bmp;
    }
}
