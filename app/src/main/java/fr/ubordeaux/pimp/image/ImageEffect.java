package fr.ubordeaux.pimp.image;

import android.graphics.Bitmap;

/**
 * This class can pack an Effect for pictures:
 * - its method call
 * - todo
 * <p>
 * See {@link #ImageEffect(ImageEffectCommand)} to see how to construct it.
 */
public class ImageEffect {

    /**
     * Because we are using command patern, this interface allows to store lines of codes as an Object, and allows to simplified syntax with lambda expressions.
     * See {@link ImageEffect}
     */
    public interface ImageEffectCommand {
        void run(Bitmap bitmap);
    }

    private ImageEffectCommand effect;

    /**
     * Construct a kind of effect pack, you have to pass to its function your(s) line(s) of code to apply your effect.
     * Example :
     * <pre>
     * {@code
     * new ImageEffect((Bitmap target) -> myEffect(target, arg1, arg2, ...));
     * }
     * </pre>
     * Or :
     * <pre>
     * {@code
     * new ImageEffect((Bitmap target) -> {
     *          foo(target);
     *          bar(target);
     * });
     * }
     * </pre>
     * <p>
     * Or without lambda expression:
     * <pre>
     * {@code
     * new ImageEffect(new ImageEffect.ImageEffectCommand() {
     *                     public void run(Bitmap bitmap) {
     *                          myEffect(target, arg1, arg2, ...);
     *                     }
     *                 });
     *  }
     *  </pre>
     * <p>
     * <p>
     * Because your effects librarie is using Android {@link Bitmap} and not our {@link Image} librarie, the argument in the lambda is a Bitmap, and that's why this class manipulates direclty Bitmaps.
     *
     * @param effect Effect method or lines of code.
     */
    public ImageEffect(ImageEffectCommand effect) {
        this.effect = effect;
    }


    /**
     * Will apply the {@link ImageEffectCommand} (your effect) on the specified Bitmap.
     *
     * @param bitmap target Bitmap
     */
    void apply(Bitmap bitmap) {
        effect.run(bitmap);
    }
}

