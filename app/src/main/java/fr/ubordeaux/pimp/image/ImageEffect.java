package fr.ubordeaux.pimp.image;

import android.graphics.Bitmap;

import java.io.Serializable;


/**
 * This class can pack an Effect for pictures:
 * - Its method call
 * - A name to describe the effect
 * - An array of arguments, to describe which settings were applied on the effect.
 * <p>
 * See {@link #ImageEffect(String, String[], ImageEffectCommand)} to see how to construct it.
 */
public class ImageEffect implements Serializable {

    private ImageEffectCommand effect;
    private String name;
    private String[] args;

    /**
     * Construct a kind of effect pack, you have to pass to its function your(s) line(s) of code to apply your effect.
     * Example :
     * <pre>
     * {@code
     * new ImageEffect("My Effect A", new String{}, (Bitmap target) -> myEffect(target, arg1, arg2, ...));
     * }
     * </pre>
     * Or :
     * <pre>
     * {@code
     * new ImageEffect("My Effect B", new String{"2", "activated"}, (Bitmap target) -> {
     *          foo(target, 2, true);
     *          bar(target);
     * });
     * }
     * </pre>
     * <p>
     * Or without lambda expression:
     * <pre>
     * {@code
     * new ImageEffect("My Effect C", new String{}, new ImageEffect.ImageEffectCommand() {
     *                     public void run(Bitmap bitmap) {
     *                          myEffect(target, arg1, arg2, ...);
     *                     }
     *                 });
     *  }
     *  </pre>
     * <p>
     * <p>
     * Because your effects library is using Android {@link Bitmap} and not our {@link Image} library, the argument in the lambda is a Bitmap, and that's why this class manipulates directly Bitmaps.
     *
     * @param name   A name to describe the effect.
     * @param args   Effect method or lines of code.
     * @param effect An array of arguments, generally the sames as in the effect method.
     */
    public ImageEffect(String name, String[] args, ImageEffectCommand effect) {
        this.effect = effect;
        this.name = name;
        this.args = args;
    }


    /**
     * Will apply the {@link ImageEffectCommand} (your effect) on the specified Bitmap.
     *
     * @param bitmap target Bitmap
     */
    void apply(Bitmap bitmap) {
        effect.run(bitmap);
    }


    /**
     * Because we are using command pattern, this interface allows to store lines of codes as an Object, and allows to simplified syntax with lambda expressions.
     * See {@link ImageEffect}
     */
    public interface ImageEffectCommand{
        void run(Bitmap bitmap);
    }

    /**
     * @return Name description of the effect
     */
    public String getName() {
        return name;
    }

    /**
     * @return Array of args, settings of the effect. (like intensity, hue, ...)
     */
    public String[] getArgs() {
        return args;
    }
}

