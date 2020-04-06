package fr.ubordeaux.pimp.fragments;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;

import fr.ubordeaux.pimp.R;
import fr.ubordeaux.pimp.activity.MainActivity;
import fr.ubordeaux.pimp.filters.Retouching;
import fr.ubordeaux.pimp.image.ImageEffect;
import fr.ubordeaux.pimp.image.ImagePack;
import fr.ubordeaux.pimp.util.Effects;

import static fr.ubordeaux.pimp.util.Effects.*;

public class EffectsFragment extends Fragment {

    private Button bBrightness;
    private Button bSaturation;
    private Button bContrast;
    private Button bEnhance;
    private Button bChangeHue;
    private Button bKeepHue;
    private Button bBlur;
    private Button bSharpen;
    private Button bNeon;
    private Button bToGray;
    private Button bInvert;

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

    }

    /**
     * Update previews view. lol
     *
     * @param pack The ImagePack
     */
    public void showPreviews(ImagePack pack) {
        ArrayList<ImagePack.Preview> list = pack.getPreviewsList();

        bBrightness.setBackground(new BitmapDrawable(getResources(), list.get(0).image.getBitmap()));
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

        bBrightness = view.findViewById(R.id.bBrightness);
        bSaturation = view.findViewById(R.id.bSaturation);
        bContrast = view.findViewById(R.id.bContrast);
        bEnhance = view.findViewById(R.id.bEnhance);
        bChangeHue = view.findViewById(R.id.bChangeHue);
        bKeepHue = view.findViewById(R.id.bKeepHue);
        bBlur = view.findViewById(R.id.bBlur);
        bSharpen = view.findViewById(R.id.bSharpen);
        bNeon = view.findViewById(R.id.bNeon);
        bToGray = view.findViewById(R.id.bToGray);
        bInvert = view.findViewById(R.id.bInvert);


        bToGray.setOnClickListener(v -> {
            assert main != null;
            main.getImage().quickSave();
            main.inflateEffectSettings(TO_GRAY);
        });
        bInvert.setOnClickListener(v -> {
            assert main != null;
            main.getImage().quickSave();
            main.inflateEffectSettings(INVERT);
        });
        bBrightness.setOnClickListener(v -> {
            assert main != null;
            main.getImage().quickSave();
            main.inflateEffectSettings(Effects.BRIGHTNESS);
        });
        bSaturation.setOnClickListener(v -> {
            assert main != null;
            main.getImage().quickSave();
            main.inflateEffectSettings(SATURATION);
        });

        bContrast.setOnClickListener(v -> {
            assert main != null;
            main.getImage().quickSave();
            main.inflateEffectSettings(CONTRAST);
        });
        bEnhance.setOnClickListener(v -> {
            assert main != null;
            main.getImage().quickSave();
            main.inflateEffectSettings(ENHANCE);
        });
        bChangeHue.setOnClickListener(v -> {
            assert main != null;
            main.getImage().quickSave();
            main.inflateEffectSettings(CHANGE_HUE);
        });
        bKeepHue.setOnClickListener(v -> {
            assert main != null;
            main.getImage().quickSave();
            main.inflateEffectSettings(KEEP_HUE);
        });
        bBlur.setOnClickListener(v -> {
            assert main != null;
            main.getImage().quickSave();
            main.inflateEffectSettings(BLUR);
        });
        bSharpen.setOnClickListener(v -> {
            assert main != null;
            main.getImage().quickSave();
            main.inflateEffectSettings(SHARPEN);
        });
        bNeon.setOnClickListener(v -> {
            assert main != null;
            main.getImage().quickSave();
            main.inflateEffectSettings(NEON);
        });
    }
}
