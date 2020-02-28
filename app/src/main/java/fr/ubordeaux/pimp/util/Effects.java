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
    NEON("Neon"),
    //used for benchmarking :
    GAUSS_MIN("Gaussian blur 3x3"),
    GAUSS_MAX("Gaussian blur 25x25"),
    MEAN_MIN("Mean blur 3x3"),
    MEAN_MAX("Mean blur 25x25"),
    SHARPEN_MIN("Sharpen 3x3"),
    SHARPEN_MAX("Sharpen 13x13"),
    NEON_SOBEL("Sobel filter"),
    NEON_PREWITT("Prewitt filter"),
    LAPLACE("Laplacian filter");


    private String name = "";

    Effects(String name){
        this.name = name;
    }

    public String getName(){
        return name;
    }
}
