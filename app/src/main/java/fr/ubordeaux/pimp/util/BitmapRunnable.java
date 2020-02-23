package fr.ubordeaux.pimp.util;

import android.graphics.Bitmap;

import fr.ubordeaux.pimp.io.BitmapIO;

public abstract class BitmapRunnable implements Runnable {
    private Bitmap bmp;

    public Bitmap getBmp() {
        return bmp;
    }

    public void setBmp(Bitmap bmp) {
        this.bmp = bmp;
    }

    public BitmapRunnable(Bitmap bmp){
        this.bmp = bmp;
    }
}
