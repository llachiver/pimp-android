package fr.ubordeaux.pimp.image;

import java.util.ArrayList;
import java.util.Queue;

/**
 * This class is used to store an {@link Image} and a list of "previews" of this {@link Image}.
 * This {@link Preview} are also Images but with an associated effect wich is permananlty applyed on the image.
 * Note that this class is usefull with a particular utilisation of {@link Image#quickSave()} and  {@link Image#discard()}
 */
public class ImagePack {
    /**
     * Simple preview object, containing an {@link Image} and an {@link ImageEffect}.
     */
    public class Preview {
        public Image image;
        public ImageEffect effect;

        /**
         * Create a preview.
         *
         * @param origin         Source {@link Image}.
         * @param effect         Effect to apply to the preview
         * @param requiredWidth  Desired width of the preview
         * @param requiredHeight Desired height of the preview
         */
        public Preview(Image origin, ImageEffect effect, int requiredWidth, int requiredHeight) {
            this.effect = effect;

            //Load Image :
            image = new Image(origin, requiredWidth, requiredHeight);

            //apply effect
            image.quickSave();
            effect.apply(image.getBitmap());
        }
    }


    private Image mainImage;

    private ArrayList<Preview> previews;

    private int previewWidth;
    private int previewHeight;

    /**
     * Create a pack, note that the mainImage will not be duplicated.
     *
     * @param mainImage             Main {@link Image} of the pack
     * @param requiredPreviewWidth  Desired width for previews
     * @param requiredPreviewHeight Desired height for previews
     */
    public ImagePack(Image mainImage, int requiredPreviewWidth, int requiredPreviewHeight) {
        this.mainImage = mainImage;
        this.previewWidth = requiredPreviewWidth;
        this.previewHeight = requiredPreviewHeight;
        previews = new ArrayList<>();
    }

    /**
     * Generate and add a {@link Preview} to the pack.
     *
     * @param effect The effect associated to the preview.
     */
    public void createNewPreview(ImageEffect effect) {
        previews.add(new Preview(mainImage, effect, previewWidth, previewHeight));
    }

    /**
     * Simply get the list of previews, you can get its size,remove elements or add elements already instanciated.
     * For create a new {@link Preview}, prefer {@link #createNewPreview(ImageEffect)}
     *
     * @return List of previews
     */
    public ArrayList<Preview> getPreviewsList() {
        return previews;
    }

    /**
     * Will apply an effect on ALL images, the main and all previews (they will then re-apply their own effects)
     *
     * @param effect The effect.
     * @param onMain Set as false if you want apply your effect only on previews (if for example the mainImage already has the effect)
     */
    public void applyEffect(ImageEffect effect, boolean onMain) {
        //Apply on Image:
        if (onMain)
            effect.apply(mainImage.getBitmap());

        //Apply on all effects :
        for (Preview preview : previews) {
            preview.image.discard();
            effect.apply(preview.image.getBitmap());
            preview.image.quickSave();
            preview.effect.apply(preview.image.getBitmap());
        }
    }

    /**
     * Will apply all effects on ALL images, the main and all previews (they will then re-apply their own effects)
     *
     * @param effects Effect queue.
     * @param onMain  Set as false if you want apply your effect only on previews (if for example the mainImage already has the effect)
     */
    public void applyEffect(Queue<ImageEffect> effects, boolean onMain) {

        for (ImageEffect effect : effects) {
            //Apply on Image:
            if (onMain)
                effect.apply(mainImage.getBitmap());

            //Apply on all effects :
            for (Preview preview : previews) {
                preview.image.discard();
                effect.apply(preview.image.getBitmap());
                preview.image.quickSave();
                preview.effect.apply(preview.image.getBitmap());
            }
        }
    }

    /**
     * Reset main Image and all previews.
     */
    public void reset() {
        mainImage.reset();
        for (Preview preview : previews) {
            preview.image.reset();
            preview.image.quickSave();
            preview.effect.apply(preview.image.getBitmap());
        }
    }

    /**
     * @return Main image of the pack
     */
    public Image getMainImage() {
        return mainImage;
    }


}
