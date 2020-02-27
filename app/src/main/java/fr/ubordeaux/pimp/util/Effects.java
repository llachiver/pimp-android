package fr.ubordeaux.pimp.util;

/**
 * This enum is used for generating the layout (see EffectSettingsFragment.java).
 */
public enum Effects {
    BRIGHTNESS("Brightness"),
    CONTRAST("Contrast"),
    SATURATION("Saturation"),
    ENHANCE("Enhance"),
    TO_GRAY("To gray"),
    INVERT("Invert"),
    CHANGE_HUE("Change hue"),
    KEEP_HUE("Keep hue"),
    BLUR("Blur"),
    SHARPEN("Sharpen"),
    NEON("Neon");

    private String name = "";

    Effects(String name){
        this.name = name;
    }

    public String getName(){
        return name;
    }
}
