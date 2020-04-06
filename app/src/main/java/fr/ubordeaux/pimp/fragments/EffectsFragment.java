package fr.ubordeaux.pimp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

import fr.ubordeaux.pimp.R;
import fr.ubordeaux.pimp.activity.MainActivity;
import fr.ubordeaux.pimp.image.Image;
import fr.ubordeaux.pimp.image.ImageEffect;
import fr.ubordeaux.pimp.util.Effects;

import static fr.ubordeaux.pimp.util.Effects.*;

public class EffectsFragment extends Fragment {


    Button bBrightness;
    Button bSaturation;
    Button bContrast;
    Button bEnhance;
    Button bChangeHue;
    Button bKeepHue;
    Button bBlur;
    Button bSharpen;
    Button bNeon;
    Button bToGray;
    Button bInvert;

    public void createPreviews(Image image) {
//TODO
    }

    public void updatePreviews(ImageEffect effect) {
//TODO
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
