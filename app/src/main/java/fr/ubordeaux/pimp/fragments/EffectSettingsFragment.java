package fr.ubordeaux.pimp.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import fr.ubordeaux.pimp.R;
import fr.ubordeaux.pimp.util.Effects;

public class EffectSettingsFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle args = getArguments();

        Effects effect = (Effects) args.getSerializable("effect");

        LinearLayout ll = new LinearLayout(super.getContext());
        ll.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        for(String seekbarName : effect.getSeekbars()){
            TextView tv = new TextView(super.getContext());
            tv.setText(seekbarName);
            tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            SeekBar sb = new SeekBar(super.getContext());
            sb.setMax(255);

            sb.setLayoutParams(lp);
            tv.setLayoutParams(lp);

            ll.addView(tv);
            ll.addView(sb);
        }

        if(effect.getButtons() != null){
            LinearLayout llButtons = new LinearLayout(super.getContext());
            llButtons.setOrientation(LinearLayout.HORIZONTAL);
            LinearLayout.LayoutParams lpB = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            lpB.weight = 1;
            for(String buttonName : effect.getButtons()){
                Button bt = new Button(super.getContext());
                bt.setText(buttonName);
                bt.setLayoutParams(lpB);
                llButtons.addView(bt);

            }
            ll.addView(llButtons);
        }


        return ll;
    }

}
