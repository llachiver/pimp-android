package fr.ubordeaux.pimp.image;

import android.graphics.Bitmap;

/**
 * This class stores a runnable call and a bitmap reference to be changed in {@link fr.ubordeaux.pimp.task.ApplyFilterQueueTask (Image, Context)}
 * This is essentially used to "store" a particular effect method and its arguments, the {@link Runnable#run()} function must be defined to call an effect method.
 */

public abstract class BitmapRunnable implements Runnable {
    private Bitmap bmp;

    /**
     * @return Bitmap target of the effect.
     */
    public Bitmap getBmp() {
        return bmp;
    }

    /**
     * Change bitmap reference in BitmapRunnable object
     *
     * @param bmp Bmp to modify
     */
    public void setBmp(Bitmap bmp) {
        this.bmp = bmp;
    }

    /**
     * Construct an effect Runnable.
     *
     * @param bmp  Bitmap, target of the effect
     */
    public BitmapRunnable(Bitmap bmp) {
        this.bmp = bmp;
    }
}
