package fr.ubordeaux.pimp.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import fr.ubordeaux.pimp.R;
import fr.ubordeaux.pimp.image.ImageInfo;

public class InfosFragment extends Fragment {

    private ImageInfo imageInfo;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true); //change toolbar
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_infos, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Get ImageInfo to print
        Bundle bundle = this.getArguments();
        if (bundle != null)
            imageInfo = bundle.getParcelable("info");


        //Fill layout:
        if (imageInfo != null) {
            Log.v("LOG", "test coord :" + imageInfo.getCoordinates());
            Log.v("LOG", "test size :" + imageInfo.getSize());
            Log.v("LOG", "test file size :" + imageInfo.getFileSize());
            ArrayList<InfoCard> exampleList = new ArrayList<>();
            exampleList.add(new InfoCard(android.R.drawable.star_on, imageInfo.getSize(), imageInfo.getLoadedHeight() + " x " + imageInfo.getLoadedWidth()));

            RecyclerView mRecyclerView = getView().findViewById(R.id.info_recycler_view);
            mRecyclerView.setHasFixedSize(true);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
            RecyclerView.Adapter mAdapter = new InfoAdapter(exampleList);
            mRecyclerView.setLayoutManager(mLayoutManager);
            mRecyclerView.setAdapter(mAdapter);
        } else {
            //TODO
        }

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
