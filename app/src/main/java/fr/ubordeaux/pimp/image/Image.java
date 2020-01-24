package fr.ubordeaux.pimp.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.renderscript.RenderScript;

public class Image {

    private int width;
    private int height;

    //Base image when it is loaded
    private Bitmap bmpBase;

    //Current visible image with applied effects
    private Bitmap bmpCurrent;

    //Passed in arguments of effects methods
    private RenderScript rs;

    public Image(Bitmap bmp){
        this.bmpBase = bmp;
        this.bmpCurrent = bmp.copy(Bitmap.Config.ARGB_8888, true);
        width = bmp.getWidth();
        height = bmp.getHeight();
    }

    //Discard all effects
    public void restoreBmp() {
        bmpCurrent = bmpBase.copy(Bitmap.Config.ARGB_8888, true);
    }

    public Bitmap getBmpBase() {
        return bmpBase;
    }

    public void setBmpBase(Bitmap bmpBase) {
        this.bmpBase = bmpBase;
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


    public RenderScript getRs() {
        return rs;
    }

    public void initRs(Context ctx){
        this.rs.create(ctx);
    }




}
