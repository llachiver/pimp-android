package fr.ubordeaux.pimp.util;

/**
 * This enum is used for generating the layout (see EffectSettingsFragment.java).
 */
public enum Effects {
    BRIGHTNESS("Brightness"),
    SATURATION("Saturation"),
    CONTRAST("Contrast"),
    CHANGE_HUE("Change hue"),
    KEEP_HUE("Keep hue"),
    BLUR("Blur"),
    //Generic is used when an effect doesn't have settings, and therefore no layout.
    GENERIC("");

    private String name = "";

    Effects(String name){
        this.name = name;
    }

    public String getName(){
        return name;
    }
}
