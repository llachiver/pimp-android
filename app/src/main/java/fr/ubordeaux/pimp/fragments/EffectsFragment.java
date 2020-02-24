package fr.ubordeaux.pimp.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import fr.ubordeaux.pimp.R;
import fr.ubordeaux.pimp.activity.MainActivity;
import fr.ubordeaux.pimp.filters.Convolution;
import fr.ubordeaux.pimp.filters.Retouching;
import fr.ubordeaux.pimp.image.Image;
import fr.ubordeaux.pimp.util.ApplyEffectTask;
import fr.ubordeaux.pimp.util.Effects;
import fr.ubordeaux.pimp.util.Kernels;

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

        Button bBrightness = (Button) view.findViewById(R.id.bBrightness);
        Button bSaturation = (Button) view.findViewById(R.id.bSaturation);
        Button bContrast = (Button) view.findViewById(R.id.bContrast);
        Button bEnhance = (Button) view.findViewById(R.id.bEnhance);
        Button bChangeHue = (Button) view.findViewById(R.id.bChangeHue);
        Button bKeepHue = (Button) view.findViewById(R.id.bKeepHue);
        Button bBlur = (Button) view.findViewById(R.id.bBlur);
        Button bSharpen = (Button) view.findViewById(R.id.bSharpen);
        Button bNeon = (Button) view.findViewById(R.id.bNeon);
        Button bToGray = (Button) view.findViewById(R.id.bToGray);
        Button bInvert = (Button) view.findViewById(R.id.bInvert);


        bToGray.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                main.getImage().quickSave();
                main.inflateEffectSettings(Effects.TOGRAY);
            }
        });
        bInvert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                main.getImage().quickSave();
                main.inflateEffectSettings(Effects.INVERT);
            }
        });
        bBrightness.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                main.getImage().quickSave();
                main.inflateEffectSettings(Effects.BRIGHTNESS);
            }
        });
        bSaturation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                main.getImage().quickSave();
                main.inflateEffectSettings(Effects.SATURATION);
            }
        });

        bContrast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                main.getImage().quickSave();
                main.inflateEffectSettings(Effects.CONTRAST);
            }
        });
        bEnhance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                main.getImage().quickSave();
                main.inflateEffectSettings(Effects.ENHANCE);
            }
        });
        bChangeHue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                main.getImage().quickSave();
                main.inflateEffectSettings(Effects.CHANGE_HUE);
            }
        });
        bKeepHue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                main.getImage().quickSave();
                main.inflateEffectSettings(Effects.KEEP_HUE);
            }
        });
        bBlur.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                main.getImage().quickSave();
                main.inflateEffectSettings(Effects.BLUR);
            }
        });
        bSharpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                main.getImage().quickSave();
                main.inflateEffectSettings(Effects.SHARPEN);
            }
        });
        bNeon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                main.getImage().quickSave();
                main.inflateEffectSettings(Effects.NEON);
            }
        });
    }
}
