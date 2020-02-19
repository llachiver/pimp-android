package fr.ubordeaux.pimp.activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import fr.ubordeaux.pimp.R;

public class InfosFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true); //change toolbar
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_infos, container, false);
    }

    /**
     * Change ToolBar for this fragment.
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear(); //hide main ToolBar
        assert (getActivity() != null); //avoid warnings
        ActionBar toolBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        assert (toolBar != null);
        toolBar.setDisplayHomeAsUpEnabled(true); //Display Back button
        toolBar.setTitle("Détails de l'image");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) { //back button
            assert (getFragmentManager() != null);
            getFragmentManager().popBackStack();
        }

        return super.onOptionsItemSelected(item);
    }

}
