package fr.ubordeaux.pimp.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.renderscript.RenderScript;

public class Image {

    private int width;
    private int height;

    //Base image when it is loaded
    private int[] imgBase;

    //Current visible image with applied effects
    private Bitmap bmpCurrent;

    //Passed in arguments of effects methods

    public Image(Bitmap bmp){
        width = bmp.getWidth();
        height = bmp.getHeight();
        imgBase = new int [width * height];
        bmpCurrent = bmp;
        bmpCurrent.getPixels(imgBase, 0, width, 0, 0, width, height);
    }

    //Discard all effects
    public void restoreBmp() {
        bmpCurrent.setPixels(imgBase, 0, width, 0, 0, width, height);
    }

    public Bitmap getBmpCurrent() {
        return bmpCurrent;
    }

    public void setBmpCurrent(Bitmap bmpCurrent) {
        this.bmpCurrent = bmpCurrent;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }






}
