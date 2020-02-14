package fr.ubordeaux.pimp.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.SeekBar;

import androidx.fragment.app.Fragment;
import fr.ubordeaux.pimp.R;

public class EffectFragment extends Fragment {



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle args = getArguments();
        System.out.println("onCreateView : " + args);
        int index = args.getInt("index", 0);
        LinearLayout ll =  getView().findViewById(R.id.effect_settings);
        SeekBar seekBar = new SeekBar(super.getContext());
        seekBar.setMax(15);
        ll.addView(seekBar);
        setWidgets();
        View view = inflater.inflate(R.layout.fragment_effect, container, false);
        return ll;
    }

    //TODO : instanciate widgets regarding the effect opened by the user
    private void setWidgets(){
        SeekBar seekBar = new SeekBar(super.getContext());
        seekBar.setMax(15);
        //LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        //seekBar.setLayoutParams(lp);
    }

}
