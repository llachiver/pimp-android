package fr.ubordeaux.pimp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import fr.ubordeaux.pimp.R;
import fr.ubordeaux.pimp.activity.MainActivity;
import fr.ubordeaux.pimp.util.Effects;

public class EffectSettingsFragment extends Fragment {

    RelativeLayout settingsLayout;
    LinearLayout settingsList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle args = getArguments();

        //Get the desired effect
        Effects effect = (Effects) args.getSerializable("effect");

        settingsLayout = (RelativeLayout) inflater.inflate(R.layout.fragment_effect_settings,null);

        RelativeLayout.LayoutParams rlp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);

        settingsList = new LinearLayout(super.getContext());
        settingsList.setOrientation(LinearLayout.VERTICAL);
        //;
        settingsList.setLayoutParams(rlp);

        //Generate the view and set the listeners for the desired effect
        switch(effect){
            case CONTRAST:
                contrastView();
                break;
            case CHANGE_HUE:
                hueView();
                break;
            case KEEP_HUE:
                keepHueView();
                break;
            default :
                simpleEffectView(effect.getName());
                break;
        }
        settingsLayout.addView(settingsList);

        return settingsLayout;
    }

    public void cancelConfirmListeners(){
        final MainActivity mainActivity = (MainActivity) getActivity();

        Button bCancel = settingsLayout.findViewById(R.id.bCancel);
        Button bConfirm = settingsLayout.findViewById(R.id.bCancel);

        bCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.getImage().discard();
            }
        });

        bConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

    }

    /**
     * Generate a basic layout with the name of the effect and a seekbar.
     * @param effect the name of the effect.
     * @return the view of the layout
     */
    public void simpleEffectView(final String effect){
        //Set the settings layout
        TextView tv = new TextView(super.getContext());
        tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        tv.setText(effect);

        SeekBar sb = new SeekBar(super.getContext());
        sb.setMax(255);
        sb.setProgress(127);

        sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                switch(effect){
                    //TODO method call
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        settingsList.addView(tv);
        settingsList.addView(sb);
    }


    public void contrastView(){
        TextView tv = new TextView(super.getContext());
        tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        tv.setText("Contrast");

        SeekBar sb = new SeekBar(super.getContext());
        sb.setMax(255);

        sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //Contrast call
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        Button bEqualization = new Button(super.getContext());
        bEqualization.setText("EQUALIZATION");

        bEqualization.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Equalization call
            }
        });

        settingsList.addView(tv);
        settingsList.addView(sb);
        settingsList.addView(bEqualization);
    }


    public void hueView(){
        TextView tvUniform = new TextView(super.getContext());
        tvUniform.setText("Uniform hue");
        CheckBox cbUniform = new CheckBox(super.getContext());

        TextView tvHue = new TextView(super.getContext());
        tvHue.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        tvHue.setText("Hue");

        SeekBar sbChangeHue = new SeekBar(super.getContext());
        sbChangeHue.setMax(255);
        sbChangeHue.setProgress(127);

        sbChangeHue.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //Change hue call
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        settingsList.addView(tvUniform);
        settingsList.addView(cbUniform);
        settingsList.addView(tvHue);
        settingsList.addView(sbChangeHue);
    }



    public void keepHueView(){
        TextView tvHue = new TextView(super.getContext());
        tvHue.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        tvHue.setText("Hue");

        SeekBar sbSelectedHue = new SeekBar(super.getContext());
        sbSelectedHue.setMax(255);
        sbSelectedHue.setProgress(127);

        TextView tvTolerance = new TextView(super.getContext());
        tvTolerance.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        tvTolerance.setText("Tolerance");

        SeekBar sbTolerance = new SeekBar(super.getContext());
        sbTolerance.setMax(255);
        sbTolerance.setProgress(127);

        sbSelectedHue.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //Keep hue call
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        settingsList.addView(tvHue);
        settingsList.addView(sbSelectedHue);
        settingsList.addView(tvTolerance);
        settingsList.addView(sbTolerance);
    }

}