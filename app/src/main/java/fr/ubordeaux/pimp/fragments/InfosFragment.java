package fr.ubordeaux.pimp.fragments;

import android.os.Bundle;
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
        if (getView() == null) return;

        // Get ImageInfo to print
        Bundle bundle = this.getArguments();
        int prH = 0, prW = 0;
        if (bundle != null) {
            imageInfo = bundle.getParcelable("info");
            prW = bundle.getInt("prW");
            prH = bundle.getInt("prH");
        }

        if (imageInfo == null) return;

        //Fill layout:

        ArrayList<InfoCard> infoCardsList = new ArrayList<>();

        //insert cards of information:
        String fileName = imageInfo.getFileName();
        if (fileName != null) { // File and resolution information
            String resolution = imageInfo.getCaptorResolution();
            String size = imageInfo.getSize();
            String fileSize = imageInfo.getFileSize();
            infoCardsList.add(new InfoCard(R.drawable.ic_image_white_24dp, fileName,
                    (resolution == null ? "" : resolution + "    ") + (size == null ? "" : size + "    ") + (fileSize == null ? "" : fileSize)));
        }

        String date = imageInfo.getDate();
        if (date != null) // Date information
            infoCardsList.add(new InfoCard(R.drawable.ic_date_range_white_24dp, "Date", date));


        String device = imageInfo.getDeviceModel();
        String exposition = imageInfo.getExpositionTime();
        String focal = imageInfo.getFocalLength();
        String iso = imageInfo.getISO();
        if (device != null || exposition != null || focal != null || iso != null) { // Captor information
            infoCardsList.add(new InfoCard(R.drawable.ic_camera_white_24dp, (device == null ? "Unknown device" : device),
                    (exposition == null ? "" : exposition + "    ") + (focal == null ? "" : focal + "    ") + (iso == null ? "" : iso)
            ));
        }

        String location = imageInfo.getCoordinates();
        if (location != null) // Location information
            infoCardsList.add(new InfoCard(R.drawable.ic_location_on_white_24dp, "Location", location));

        // App information:
        String sizes;
        if (prH != 0 && prW != 0)
            sizes = imageInfo.getLoadedWidth() + " x " + imageInfo.getLoadedHeight() + " / " + prW + " x " + prH;
        else
            sizes = imageInfo.getLoadedWidth() + " x " + imageInfo.getLoadedHeight();

        infoCardsList.add(new InfoCard(R.drawable.ic_settings_white_24dp, "Loaded preview", sizes));

        RecyclerView mRecyclerView = getView().findViewById(R.id.info_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(new InfoAdapter(infoCardsList));
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
        toolBar.setTitle("Picture details");
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
