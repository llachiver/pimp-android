package fr.ubordeaux.pimp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import fr.ubordeaux.pimp.R;
import fr.ubordeaux.pimp.activity.MainActivity;
import fr.ubordeaux.pimp.filters.Convolution;
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
        Button bChangeHue = (Button) view.findViewById(R.id.bChangeHue);
        Button bKeepHue = (Button) view.findViewById(R.id.bKeepHue);
        Button bNeon = (Button) view.findViewById(R.id.bNeon);
        //TODO ...

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
        bNeon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO the convolution is applied HERE
                main.inflateEffectSettings(Effects.NEON);
            }
        });



    }
}
