package fr.ubordeaux.pimp.util;

import java.lang.reflect.Method;

public enum Effects {
    BRIGHTNESS("Brightness"),
    SATURATION("Saturation"),
    CONTRAST("Contrast"),
    CHANGE_HUE("Change hue"),
    KEEP_HUE("Keep hue"),
    BLUR("Blur"),
    SHARPEN("Sharpen"),
    NEON("Neon");

    private String name = "";

    //Constructeur
    Effects(String name){
        this.name = name;
    }

    public String getName(){
        return name;
    }
}
