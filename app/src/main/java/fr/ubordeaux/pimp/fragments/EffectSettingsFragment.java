package fr.ubordeaux.pimp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import fr.ubordeaux.pimp.R;
import fr.ubordeaux.pimp.activity.MainActivity;
import fr.ubordeaux.pimp.filters.Convolution;
import fr.ubordeaux.pimp.filters.Retouching;
import fr.ubordeaux.pimp.image.Image;
import fr.ubordeaux.pimp.util.ApplyEffectTask;
import fr.ubordeaux.pimp.util.Effects;

public class EffectSettingsFragment extends Fragment {

    //Contains the cancel/confirm buttons + the settings list.
    RelativeLayout settingsLayout;
    //The settings list only (containing buttons, seekbars etc).
    LinearLayout settingsList;

    MainActivity mainActivity;
    //The image we modify.
    Image image;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mainActivity = (MainActivity) getActivity();
        setHasOptionsMenu(true); //change toolbar
        image = mainActivity.getImage();

        Bundle args = getArguments();

        //Get the desired effect
        Effects effect = (Effects) args.getSerializable("effect");

        settingsLayout = (RelativeLayout) inflater.inflate(R.layout.fragment_effect_settings,null);

        //Those params are used to align the settings widgets to the bottom.
        RelativeLayout.LayoutParams rlp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        rlp.bottomMargin = 200;
        rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);

        //Create the layout containing the widgets.
        settingsList = new LinearLayout(super.getContext());
        settingsList.setOrientation(LinearLayout.VERTICAL);
        settingsList.setLayoutParams(rlp);

        //Generate the view and set the listeners for the desired effect
        switch(effect){
            case BRIGHTNESS:
            case SATURATION:
            case BLUR:
            case CONTRAST:
                simpleEffectView(effect);
                break;
            case CHANGE_HUE:
                hueView();
                break;
            case KEEP_HUE:
                keepHueView();
                break;
            //Effects without layout are directly applied :
            case ENHANCE:
                Retouching.histogramEqualization(image.getBitmap(),mainActivity);
                break;
            case TOGRAY:
                Retouching.toGray(image.getBitmap(), mainActivity);
                break;
            case INVERT:
                Retouching.invert(image.getBitmap(), mainActivity);
                break;
            case SHARPEN:
                Convolution.sharpen(image.getBitmap(),mainActivity);
                break;
            case NEON:
                Convolution.neon(image.getBitmap(),mainActivity);
                break;
            default :
                break;
        }
        //We add the settings list to the existing layout.
        settingsLayout.addView(settingsList);

        cancelConfirmListeners();

        return settingsLayout;
    }

    public void cancelConfirmListeners(){


        ImageButton bCancel = (ImageButton) settingsLayout.findViewById(R.id.bCancel);
        ImageButton bConfirm = (ImageButton) settingsLayout.findViewById(R.id.bConfirm);
        System.out.println(bCancel);
        bCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.onBackPressed();
                if (mainActivity.getCurrentTask() != null) mainActivity.getCurrentTask().cancel(true); //Interrupt async task if it exists.
                image.discard();
            }
        });

        bConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO save applied effect into the queue
                mainActivity.deflateEffectSettings();
            }
        });

    }

    /**
     * Generate a basic layout with the name of the effect and a seekbar.
     * @param effect the name of the effect.
     * @return the view of the layout
     */
    public void simpleEffectView(final Effects effect){
        //Set the settings layout
        TextView tv = new TextView(super.getContext());
        tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        tv.setText(effect.getName());

        SeekBar sb = new SeekBar(super.getContext());
        sb.setMax(255);
        sb.setProgress(127);

        sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                switch(effect){
                    case BRIGHTNESS:
                        image.discard();
                        Retouching.setBrightness(image.getBitmap(), progress, mainActivity);
                        break;
                    case SATURATION:
                        image.discard();
                        Retouching.setSaturation(image.getBitmap(), progress, mainActivity);
                        break;
                    case BLUR:
                        image.discard();
                        Convolution.gaussianBlur(image.getBitmap(),progress,mainActivity);
                        break;
                    case CONTRAST:
                        image.discard();
                        Retouching.dynamicExtensionRGB(image.getBitmap(),progress,mainActivity);
                        break;

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


    /**
     * Change ToolBar for this fragment.
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear(); //hide main ToolBar
        assert (getActivity() != null); //avoid warnings
    }

}