package fr.ubordeaux.pimp.activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import fr.ubordeaux.pimp.R;

public class MainFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true); //Needed to inflate toolbar
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.fragment_main, menu);
        assert (getActivity() != null); //avoid warnings
        ActionBar toolBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        assert (toolBar != null);
        toolBar.setTitle(R.string.app_name);
    }

}
