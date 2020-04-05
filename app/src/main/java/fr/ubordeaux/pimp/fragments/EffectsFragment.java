package fr.ubordeaux.pimp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.fragment.app.Fragment;

import fr.ubordeaux.pimp.R;
import fr.ubordeaux.pimp.activity.MainActivity;
import fr.ubordeaux.pimp.util.Effects;

import static fr.ubordeaux.pimp.util.Effects.*;

public class EffectsFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_effects_list, null);
        listeners(view);
        return view;

    }


    private void listeners(View view){

        final MainActivity main = (MainActivity) getActivity();

        Button bBrightness = view.findViewById(R.id.bBrightness);
        Button bSaturation = view.findViewById(R.id.bSaturation);
        Button bContrast = view.findViewById(R.id.bContrast);
        Button bEnhance = view.findViewById(R.id.bEnhance);
        Button bChangeHue = view.findViewById(R.id.bChangeHue);
        Button bKeepHue = view.findViewById(R.id.bKeepHue);
        Button bBlur = view.findViewById(R.id.bBlur);
        Button bSharpen = view.findViewById(R.id.bSharpen);
        Button bNeon = view.findViewById(R.id.bNeon);
        Button bToGray = view.findViewById(R.id.bToGray);
        Button bInvert = view.findViewById(R.id.bInvert);


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
