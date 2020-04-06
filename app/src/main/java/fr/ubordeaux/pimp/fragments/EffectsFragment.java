package fr.ubordeaux.pimp.fragments;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;

import fr.ubordeaux.pimp.R;
import fr.ubordeaux.pimp.activity.MainActivity;
import fr.ubordeaux.pimp.filters.Color;
import fr.ubordeaux.pimp.filters.Convolution;
import fr.ubordeaux.pimp.filters.Retouching;
import fr.ubordeaux.pimp.image.ImageEffect;
import fr.ubordeaux.pimp.image.ImagePack;
import fr.ubordeaux.pimp.util.Effects;

import static fr.ubordeaux.pimp.util.Effects.*;

public class EffectsFragment extends Fragment {

    private ImageView imgBrightness;
    private ImageView imgContrast;
    private ImageView imgSaturation;
    private ImageView imgEnhance;
    private ImageView imgToGray;
    private ImageView imgInvert;
    private ImageView imgChangeHue;
    private ImageView imgKeepHue;
    private ImageView imgBlur;
    private ImageView imgSharpen;
    private ImageView imgNeon;


    /**
     * Tools method to create our effects preview in an {@link ImagePack}.
     *
     * @param pack    The ImagePack
     * @param context activity context
     */
    public static void createPreviews(ImagePack pack, Activity context) {

        //Brightness effect :
        pack.createNewPreview(new ImageEffect("Brightness preview", new String[]{String.valueOf(200)}, (Bitmap target) ->
                Retouching.setBrightness(target, 200, context)));

        //Contrast effect :
        pack.createNewPreview(new ImageEffect("Contrast preview", new String[]{String.valueOf(200)}, (Bitmap target) ->
                Retouching.dynamicExtensionRGB(target, 200, context)));

        //Saturation effect :
        pack.createNewPreview(new ImageEffect("Saturation preview", new String[]{String.valueOf(240)}, (Bitmap target) ->
                Retouching.setSaturation(target, 240, context)));

        //Enhance effect :
        pack.createNewPreview(new ImageEffect("Enhance preview", new String[]{}, (Bitmap target) ->
                Retouching.histogramEqualization(target, context)));

        //Gray effect :
        pack.createNewPreview(new ImageEffect("Gray preview", new String[]{}, (Bitmap target) ->
                Color.toGray(target, context)));

        //Invert effect :
        pack.createNewPreview(new ImageEffect("Invert preview", new String[]{}, (Bitmap target) ->
                Color.invert(target, context)));

        //Hue effect :
        pack.createNewPreview(new ImageEffect("Hue preview", new String[]{String.valueOf(80), String.valueOf(true)}, (Bitmap target) ->
                Color.colorize(target, 80, context, true)));

        //Keep hue effect :
        pack.createNewPreview(new ImageEffect("Keep Hue preview", new String[]{String.valueOf(0), String.valueOf(70)}, (Bitmap target) ->
                Color.keepColor(target, 0, 70, context)));

        //Blur effect :
        pack.createNewPreview(new ImageEffect("Gauss Blur preview", new String[]{String.valueOf(2)}, (Bitmap target) ->
                Convolution.gaussianBlur(target, 2, context)));

        //Sharpen effect :
        pack.createNewPreview(new ImageEffect("Sharpen preview", new String[]{String.valueOf(2)}, (Bitmap target) ->
                Convolution.sharpen(target, 2, context)));

        //Neon effect :
        pack.createNewPreview(new ImageEffect("Sobel neon preview", new String[]{}, (Bitmap target) ->
                Convolution.neonSobel(target, context)));
    }

    /**
     * Update previews view. lol
     *
     * @param pack The ImagePack
     */
    public void showPreviews(ImagePack pack) {
        ArrayList<ImagePack.Preview> list = pack.getPreviewsList();

        imgBrightness.setImageBitmap(list.get(0).image.getBitmap());
        imgContrast.setImageBitmap(list.get(1).image.getBitmap());
        imgSaturation.setImageBitmap(list.get(2).image.getBitmap());
        imgEnhance.setImageBitmap(list.get(3).image.getBitmap());
        imgToGray.setImageBitmap(list.get(4).image.getBitmap());
        imgInvert.setImageBitmap(list.get(5).image.getBitmap());
        imgChangeHue.setImageBitmap(list.get(6).image.getBitmap());
        imgKeepHue.setImageBitmap(list.get(7).image.getBitmap());
        imgBlur.setImageBitmap(list.get(8).image.getBitmap());
        imgSharpen.setImageBitmap(list.get(9).image.getBitmap());
        imgNeon.setImageBitmap(list.get(9).image.getBitmap());
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_effects_list, null);
        listeners(view);
        return view;

    }


    private void listeners(View view) {

        final MainActivity main = (MainActivity) getActivity();

        RelativeLayout effectBrightness = view.findViewById(R.id.effectBrightness);
        RelativeLayout effectContrast = view.findViewById(R.id.effectContrast);
        RelativeLayout effectSaturation = view.findViewById(R.id.effectSaturation);
        RelativeLayout effectEnhance = view.findViewById(R.id.effectEnhance);
        RelativeLayout effectToGray = view.findViewById(R.id.effectToGray);
        RelativeLayout effectInvert = view.findViewById(R.id.effectInvert);
        RelativeLayout effectChangeHue = view.findViewById(R.id.effectChangeHue);
        RelativeLayout effectKeepHue = view.findViewById(R.id.effectKeepHue);
        RelativeLayout effectBlur = view.findViewById(R.id.effectBlur);
        RelativeLayout effectSharpen = view.findViewById(R.id.effectSharpen);
        RelativeLayout effectNeon = view.findViewById(R.id.effectNeon);


        imgBrightness = view.findViewById(R.id.imgBrightness);
        imgContrast = view.findViewById(R.id.imgContrast);
        imgSaturation = view.findViewById(R.id.imgSaturation);
        imgEnhance = view.findViewById(R.id.imgEnhance);
        imgToGray = view.findViewById(R.id.imgToGray);
        imgInvert = view.findViewById(R.id.imgInvert);
        imgChangeHue = view.findViewById(R.id.imgChangeHue);
        imgKeepHue = view.findViewById(R.id.imgKeepHue);
        imgBlur = view.findViewById(R.id.imgBlur);
        imgSharpen = view.findViewById(R.id.imgSharpen);
        imgNeon = view.findViewById(R.id.imgNeon);


        effectToGray.setOnClickListener(v -> {
            assert main != null;
            main.getImage().quickSave();
            main.inflateEffectSettings(TO_GRAY);
        });
        effectInvert.setOnClickListener(v -> {
            assert main != null;
            main.getImage().quickSave();
            main.inflateEffectSettings(INVERT);
        });
        effectBrightness.setOnClickListener(v -> {
            assert main != null;
            main.getImage().quickSave();
            main.inflateEffectSettings(Effects.BRIGHTNESS);
        });
        effectSaturation.setOnClickListener(v -> {
            assert main != null;
            main.getImage().quickSave();
            main.inflateEffectSettings(SATURATION);
        });

        effectContrast.setOnClickListener(v -> {
            assert main != null;
            main.getImage().quickSave();
            main.inflateEffectSettings(CONTRAST);
        });
        effectEnhance.setOnClickListener(v -> {
            assert main != null;
            main.getImage().quickSave();
            main.inflateEffectSettings(ENHANCE);
        });
        effectChangeHue.setOnClickListener(v -> {
            assert main != null;
            main.getImage().quickSave();
            main.inflateEffectSettings(CHANGE_HUE);
        });
        effectKeepHue.setOnClickListener(v -> {
            assert main != null;
            main.getImage().quickSave();
            main.inflateEffectSettings(KEEP_HUE);
        });
        effectBlur.setOnClickListener(v -> {
            assert main != null;
            main.getImage().quickSave();
            main.inflateEffectSettings(BLUR);
        });
        effectSharpen.setOnClickListener(v -> {
            assert main != null;
            main.getImage().quickSave();
            main.inflateEffectSettings(SHARPEN);
        });
        effectNeon.setOnClickListener(v -> {
            assert main != null;
            main.getImage().quickSave();
            main.inflateEffectSettings(NEON);
        });
    }
}
