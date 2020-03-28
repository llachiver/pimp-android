package fr.ubordeaux.pimp.fragments;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import fr.ubordeaux.pimp.R;
import fr.ubordeaux.pimp.activity.MainActivity;
import fr.ubordeaux.pimp.filters.Color;
import fr.ubordeaux.pimp.filters.Convolution;
import fr.ubordeaux.pimp.filters.Retouching;
import fr.ubordeaux.pimp.image.Image;
import fr.ubordeaux.pimp.image.ImageEffectRunnable;
import fr.ubordeaux.pimp.task.ApplyEffectTask;
import fr.ubordeaux.pimp.util.Effects;

/**
 * Effect settings class handle the execution and generation of fragments seekbars
 */
public class EffectSettingsFragment extends Fragment {

    //Contains the cancel/confirm buttons + the settings list.
    RelativeLayout settingsLayout;
    //The effect settings list only (containing buttons, seekbars etc).
    LinearLayout settingsList;

    //The context of the main activity.
    MainActivity mainActivity;
    //The image we modify.
    Image image;

    ImageEffectRunnable currentEffect;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mainActivity = (MainActivity) getActivity();
        setHasOptionsMenu(true); //change toolbar
        image = mainActivity.getImage();

        Bundle args = getArguments();

        //Get the desired effect
        Effects effect = (Effects) args.getSerializable("effect");

        settingsLayout = (RelativeLayout) inflater.inflate(R.layout.fragment_effect_settings, null);

        //Those params are used to align the settings widgets to the bottom of the screen.
        RelativeLayout.LayoutParams rlp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        rlp.bottomMargin = 200;
        rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);

        //Create the layout containing the widgets.
        settingsList = new LinearLayout(super.getContext());
        settingsList.setOrientation(LinearLayout.VERTICAL);
        settingsList.setLayoutParams(rlp);

        //Generate the view and set the listeners for the desired effect
        switch (effect) {
            case BRIGHTNESS:
            case SATURATION:
            case SHARPEN:
            case CONTRAST:
                simpleEffectView(effect);
                break;
            case CHANGE_HUE:
                hueView();
                break;
            case KEEP_HUE:
                keepHueView();
                break;
            case BLUR:
                blurView();
                break;
            //Effects without layout are directly applied :
            case ENHANCE:
                image.discard();
                currentEffect = new ImageEffectRunnable(image.getBitmap()) {
                    @Override
                    public void run() {
                        Retouching.histogramEqualization(this.getBmp(), mainActivity);
                    }
                };
                mainActivity.setCurrentTask(new ApplyEffectTask(mainActivity, currentEffect, image).execute());
                break;
            case TO_GRAY:
                image.discard();
                currentEffect = new ImageEffectRunnable(image.getBitmap()) {
                    @Override
                    public void run() {
                        Color.toGray(this.getBmp(), mainActivity);
                    }
                };
                mainActivity.setCurrentTask(new ApplyEffectTask(mainActivity, currentEffect, image).execute());
                break;
            case INVERT:
                image.discard();
                currentEffect = new ImageEffectRunnable(image.getBitmap()) {
                    @Override
                    public void run() {
                        Color.invert(this.getBmp(), mainActivity);
                    }
                };
                mainActivity.setCurrentTask(new ApplyEffectTask(mainActivity, currentEffect, image).execute());
                break;
            case NEON:
                neonView();
                break;
            default:
                break;
        }
        //We add the settings list to the existing layout.
        settingsLayout.addView(settingsList);

        cancelConfirmListeners();

        return settingsLayout;
    }

    /**
     * Button cancel changes and confirm Listeners
     */
    public void cancelConfirmListeners() {


        ImageButton bCancel = settingsLayout.findViewById(R.id.bCancel);
        ImageButton bConfirm = settingsLayout.findViewById(R.id.bConfirm);
        System.out.println(bCancel);
        bCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.onBackPressed();
                mainActivity.cancelCurrentTask();
                currentEffect = null;
                image.discard();
            }
        });

        bConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentEffect = null;
                mainActivity.deflateEffectSettings();

            }
        });

    }

    /**
     * Generate a basic layout with the name of the effect and a seekbar.
     *
     * @param effect the name of the effect.
     */
    public void simpleEffectView(final Effects effect) {
        //Set the settings layout
        TextView tv = new TextView(super.getContext());
        tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        tv.setText(effect.getName());

        SeekBar sb = new SeekBar(super.getContext());
        sb.setMax(255);
        sb.setProgress(127);

        sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, final int progress, boolean fromUser) {
                switch (effect) {
                    case BRIGHTNESS:
                        image.discard();
                        currentEffect = new ImageEffectRunnable(image.getBitmap()) {
                            @Override
                            public void run() {
                                Retouching.setBrightness(this.getBmp(), progress, mainActivity);
                            }
                        };
                        image.applyEffect(currentEffect);
                        break;
                    case SATURATION:
                        image.discard();
                        currentEffect = new ImageEffectRunnable(image.getBitmap()) {
                            @Override
                            public void run() {
                                Retouching.setSaturation(this.getBmp(), progress, mainActivity);
                            }
                        };
                        image.applyEffect(currentEffect);

                        break;
                    case CONTRAST:
                        image.discard();
                        currentEffect = new ImageEffectRunnable(image.getBitmap()) {
                            @Override
                            public void run() {
                                Retouching.dynamicExtensionRGB(this.getBmp(), progress, mainActivity);
                            }
                        };
                        image.applyEffect(currentEffect);

                        break;

                    case SHARPEN:

                        image.discard();
                        currentEffect = new ImageEffectRunnable(image.getBitmap()) {
                            @Override
                            public void run() {
                                Convolution.sharpen(this.getBmp(), progress, mainActivity);
                            }
                        };
                        image.applyEffect(currentEffect);
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


    /**
     * View generator for hue effects
     */
    public void hueView() {
        TextView tvUniform = new TextView(super.getContext());
        tvUniform.setText("Uniform hue");
        final CheckBox cbUniform = new CheckBox(super.getContext());

        TextView tvHue = new TextView(super.getContext());
        tvHue.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        tvHue.setText("Hue");

        SeekBar sbChangeHue = new SeekBar(super.getContext());
        sbChangeHue.setMax(360);
        sbChangeHue.setProgress(0);

        sbChangeHue.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, final int progress, boolean fromUser) {
                //Change hue call
                image.discard();
                currentEffect = new ImageEffectRunnable(image.getBitmap()) {
                    @Override
                    public void run() {
                        Color.colorize(this.getBmp(), progress, mainActivity, cbUniform.isChecked());
                    }
                };
                image.applyEffect(currentEffect);
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


    /**
     * View generator for keep color effects
     */
    public void keepHueView() {
        TextView tvHue = new TextView(super.getContext());
        tvHue.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        tvHue.setText("Hue");

        SeekBar sbSelectedHue = new SeekBar(super.getContext());
        sbSelectedHue.setMax(360);
        sbSelectedHue.setProgress(0);

        TextView tvTolerance = new TextView(super.getContext());
        tvTolerance.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        tvTolerance.setText("Tolerance");

        SeekBar sbTolerance = new SeekBar(super.getContext());
        sbTolerance.setMax(360);
        sbTolerance.setProgress(180);
        final int[] args = new int[2];
        args[0] = 127;
        args[1] = 127;
        sbTolerance.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, final int progress, boolean fromUser) {


                //Keep hue call
                image.discard();
                args[1] = progress;

                currentEffect = new ImageEffectRunnable(image.getBitmap()) {
                    @Override
                    public void run() {
                        Color.keepColor(this.getBmp(), args[0], progress, mainActivity);
                    }
                };
                image.applyEffect(currentEffect);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        sbSelectedHue.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, final int progress, boolean fromUser) {
                //Keep hue call
                image.discard();
                args[0] = progress;
                currentEffect = new ImageEffectRunnable(image.getBitmap()) {
                    @Override
                    public void run() {
                        Color.keepColor(this.getBmp(), progress, args[1], mainActivity);
                    }
                };
                image.applyEffect(currentEffect);
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
     * View generator for blur effects
     */
    public void blurView() {
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.gravity = Gravity.CENTER_HORIZONTAL;

        RadioGroup rbGroup = new RadioGroup(mainActivity);
        rbGroup.setOrientation(LinearLayout.HORIZONTAL);
        rbGroup.setLayoutParams(lp);

        final RadioButton rbMean = new RadioButton(mainActivity);
        rbMean.setText("Mean");

        final RadioButton rbGauss = new RadioButton(mainActivity);
        rbGauss.setText("Gauss      ");

        rbGroup.addView(rbGauss);
        rbGroup.addView(rbMean);

        rbGroup.check(rbGauss.getId());

        SeekBar sbBlur = new SeekBar(mainActivity);
        sbBlur.setMax(25);
        sbBlur.setProgress(0);

        sbBlur.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, final int progress, boolean fromUser) {
                if (rbGauss.isChecked()) {
                    image.discard();
                    currentEffect = new ImageEffectRunnable(image.getBitmap()) {
                        @Override
                        public void run() {
                            Convolution.gaussianBlur(this.getBmp(), progress, mainActivity);
                        }
                    };
                    image.applyEffect(currentEffect);

                }
                if (rbMean.isChecked()) {
                    image.discard();
                    currentEffect = new ImageEffectRunnable(image.getBitmap()) {
                        @Override
                        public void run() {
                            Convolution.meanBlur(this.getBmp(), progress, mainActivity);
                        }
                    };
                    image.applyEffect(currentEffect);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        settingsList.addView(sbBlur);
        settingsList.addView(rbGroup);

    }


    /**
     * View generator for neon effects
     */
    public void neonView() {
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.gravity = Gravity.CENTER_HORIZONTAL;

        RadioGroup rbGroup = new RadioGroup(mainActivity);
        rbGroup.setOrientation(LinearLayout.HORIZONTAL);
        rbGroup.setLayoutParams(lp);

        final RadioButton rbSobel = new RadioButton(mainActivity);
        rbSobel.setText("Sobel");

        final RadioButton rbPrewitt = new RadioButton(mainActivity);
        rbPrewitt.setText("Prewitt");

        final RadioButton rbLaplace = new RadioButton(mainActivity);
        rbLaplace.setText("Laplace");

        rbGroup.addView(rbSobel);
        rbGroup.addView(rbPrewitt);
        rbGroup.addView(rbLaplace);
        rbGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (rbSobel.isChecked()) {
                    mainActivity.cancelCurrentTask(); //Avoid multiple checks
                    image.discard();
                    currentEffect = new ImageEffectRunnable(image.getBitmap()) {
                        @Override
                        public void run() {
                            Convolution.neonSobel(this.getBmp(), mainActivity);
                        }
                    };
                    mainActivity.setCurrentTask(new ApplyEffectTask(mainActivity, currentEffect, image).execute());
                } else if (rbPrewitt.isChecked()) {
                    mainActivity.cancelCurrentTask(); //Avoid multiple checks
                    image.discard();
                    currentEffect = new ImageEffectRunnable(image.getBitmap()) {
                        @Override
                        public void run() {
                            Convolution.neonPrewitt(this.getBmp(), mainActivity);
                        }
                    };
                    mainActivity.setCurrentTask(new ApplyEffectTask(mainActivity, currentEffect, image).execute());
                } else if (rbLaplace.isChecked()) {
                    mainActivity.cancelCurrentTask(); //Avoid multiple checks
                    image.discard();
                    currentEffect = new ImageEffectRunnable(image.getBitmap()) {
                        @Override
                        public void run() {
                            Convolution.laplace(this.getBmp(), mainActivity);
                        }
                    };
                    mainActivity.setCurrentTask(new ApplyEffectTask(mainActivity, currentEffect, image).execute());

                }
            }
        });

        settingsList.addView(rbGroup);
    }


    /**
     * Empty the actionBar when opening an effect.
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear(); //hide main ToolBar
        assert (getActivity() != null); //avoid warnings
    }

}