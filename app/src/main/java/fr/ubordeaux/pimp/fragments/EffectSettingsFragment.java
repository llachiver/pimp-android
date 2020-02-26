package fr.ubordeaux.pimp.fragments;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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
import fr.ubordeaux.pimp.filters.Convolution;
import fr.ubordeaux.pimp.filters.Retouching;
import fr.ubordeaux.pimp.image.Image;
import fr.ubordeaux.pimp.util.ApplyEffectTask;
import fr.ubordeaux.pimp.util.BitmapRunnable;
import fr.ubordeaux.pimp.util.Effects;

import static fr.ubordeaux.pimp.filters.Retouching.colorize;
import static fr.ubordeaux.pimp.filters.Retouching.keepColor;

public class EffectSettingsFragment extends Fragment {

    //Contains the cancel/confirm buttons + the settings list.
    RelativeLayout settingsLayout;
    //The settings list only (containing buttons, seekbars etc).
    LinearLayout settingsList;

    MainActivity mainActivity;
    //The image we modify.
    Image image;

    BitmapRunnable currentEffect;

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
                currentEffect = new BitmapRunnable(image.getBitmap()) {
                    @Override
                    public void run() {
                        Retouching.histogramEqualization(this.getBmp(),mainActivity);
                    }
                };
                mainActivity.setCurrentTask(new ApplyEffectTask(mainActivity, currentEffect).execute());
                break;
            case TOGRAY:
                image.discard();
                currentEffect = new BitmapRunnable(image.getBitmap()) {
                    @Override
                    public void run() {
                        Retouching.toGray(this.getBmp(), mainActivity);
                    }
                };
                mainActivity.setCurrentTask(new ApplyEffectTask(mainActivity, currentEffect).execute());
                break;
            case INVERT:
                image.discard();
                currentEffect = new BitmapRunnable(image.getBitmap()) {
                    @Override
                    public void run() {
                        Retouching.invert(this.getBmp(), mainActivity);
                    }
                };
                mainActivity.setCurrentTask(new ApplyEffectTask(mainActivity, currentEffect).execute());
                break;
            case SHARPEN:
                image.discard();
                currentEffect = new BitmapRunnable(image.getBitmap()) {
                    @Override
                    public void run() {
                        Convolution.sharpen(this.getBmp(),mainActivity);
                    }
                };
                mainActivity.setCurrentTask(new ApplyEffectTask(mainActivity, currentEffect).execute());
                break;
            case NEON:
                neonView();
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
                if(image.getEffectQueue() != null && currentEffect != null)  //Temporaly
                    image.getEffectQueue().add(currentEffect);
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
            public void onProgressChanged(SeekBar seekBar, final int progress, boolean fromUser) {
                switch(effect){
                    case BRIGHTNESS:
                        image.discard();
                        currentEffect = new BitmapRunnable(image.getBitmap()) {
                            @Override
                            public void run() {
                                Retouching.setBrightness(this.getBmp(), progress, mainActivity);
                            }
                        };
                        currentEffect.run();
                        break;
                    case SATURATION:
                        image.discard();
                        currentEffect = new BitmapRunnable(image.getBitmap()) {
                            @Override
                            public void run() {
                                Retouching.setSaturation(this.getBmp(), progress, mainActivity);
                            }
                        };
                        currentEffect.run();

                        break;
                    case BLUR:
                        image.discard();
                        currentEffect = new BitmapRunnable(image.getBitmap()) {
                            @Override
                            public void run() {
                                Convolution.gaussianBlur(this.getBmp(),progress,mainActivity);
                            }
                        };
                        currentEffect.run();

                        break;
                    case CONTRAST:
                        image.discard();
                        currentEffect = new BitmapRunnable(image.getBitmap()) {
                            @Override
                            public void run() {
                                Retouching.dynamicExtensionRGB(this.getBmp(),progress,mainActivity);
                            }
                        };
                        currentEffect.run();

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
        final CheckBox cbUniform = new CheckBox(super.getContext());

        TextView tvHue = new TextView(super.getContext());
        tvHue.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        tvHue.setText("Hue");

        SeekBar sbChangeHue = new SeekBar(super.getContext());
        sbChangeHue.setMax(255);
        sbChangeHue.setProgress(127);

        sbChangeHue.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, final int progress, boolean fromUser) {
                //Change hue call
                image.discard();
                currentEffect = new BitmapRunnable(image.getBitmap()) {
                    @Override
                    public void run() {
                        colorize(this.getBmp(), progress, mainActivity, cbUniform.isChecked());
                    }
                };
                currentEffect.run();
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
        final int[] args = new int[2];
        args[0] = 127;
        args[1] = 127;
        sbTolerance.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, final int progress, boolean fromUser) {


                //Keep hue call
                image.discard();
                args[1] = progress;

                currentEffect = new BitmapRunnable(image.getBitmap()) {
                    @Override
                    public void run() {
                        keepColor(this.getBmp(), args[0], progress, mainActivity);
                    }
                };
                currentEffect.run();
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
                currentEffect = new BitmapRunnable(image.getBitmap()) {
                    @Override
                    public void run() {
                        keepColor(this.getBmp(), progress, args[1], mainActivity);
                    }
                };
                currentEffect.run();
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



    public void blurView(){
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
        sbBlur.setMax(255);
        sbBlur.setProgress(127);

        sbBlur.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
            public void onProgressChanged(SeekBar seekBar, final int progress, boolean fromUser) {
                if(rbGauss.isChecked()) {
                    image.discard();
                    currentEffect = new BitmapRunnable(image.getBitmap()) {
                        @Override
                        public void run() {
                            Convolution.gaussianBlur(this.getBmp(), progress, mainActivity);
                        }
                    };
                    currentEffect.run();

                }
                if(rbMean.isChecked()){
                    image.discard();
                    currentEffect = new BitmapRunnable(image.getBitmap()) {
                        @Override
                        public void run() {
                            Convolution.meanBlur(this.getBmp(), progress, mainActivity);
                        }
                    };
                    currentEffect.run();
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



    public void neonView(){
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.gravity = Gravity.CENTER_HORIZONTAL;

        RadioGroup rbGroup = new RadioGroup(mainActivity);
        rbGroup.setOrientation(LinearLayout.HORIZONTAL);
        rbGroup.setLayoutParams(lp);

        final RadioButton rbSobel = new RadioButton(mainActivity);
        rbSobel.setText("Sobel");

        final RadioButton rbPrewitt = new RadioButton(mainActivity);
        rbPrewitt.setText("Prewitt");

        final RadioButton rbKirsch = new RadioButton(mainActivity);
        rbKirsch.setText("Kirsch");

        final RadioButton rbLaplace = new RadioButton(mainActivity);
        rbLaplace.setText("Laplace");

        rbGroup.addView(rbSobel);
        rbGroup.addView(rbPrewitt);
        rbGroup.addView(rbKirsch);
        rbGroup.addView(rbLaplace);
        rbGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(rbSobel.isChecked()){
                    image.discard();
                    currentEffect = new BitmapRunnable(image.getBitmap()) {
                        @Override
                        public void run() {
                            Convolution.neonSobel(this.getBmp(),mainActivity);
                        }
                    };
                    mainActivity.setCurrentTask(new ApplyEffectTask(mainActivity, currentEffect).execute());
                }

                else if(rbPrewitt.isChecked()){
                    image.discard();
                    currentEffect = new BitmapRunnable(image.getBitmap()) {
                        @Override
                        public void run() {
                            Convolution.neonPrewitt(this.getBmp(),mainActivity);
                        }
                    };
                    mainActivity.setCurrentTask(new ApplyEffectTask(mainActivity, currentEffect).execute());
                }
                else if(rbKirsch.isChecked()){
                    image.discard();
                    currentEffect = new BitmapRunnable(image.getBitmap()) {
                        @Override
                        public void run() {
                            Convolution.neonKirsch(this.getBmp(),mainActivity);
                        }
                    };
                    mainActivity.setCurrentTask(new ApplyEffectTask(mainActivity, currentEffect).execute());

                }

                else if(rbLaplace.isChecked()){
                    image.discard();
                    currentEffect = new BitmapRunnable(image.getBitmap()) {
                        @Override
                        public void run() {
                            Convolution.laplace(this.getBmp(),mainActivity);
                        }
                    };
                    mainActivity.setCurrentTask(new ApplyEffectTask(mainActivity, currentEffect).execute());

                }
            }
        });

        settingsList.addView(rbGroup);
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