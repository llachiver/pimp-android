package fr.ubordeaux.pimp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import fr.ubordeaux.pimp.R;

public class MacrosFragment extends Fragment implements MacroAdapter.MacroListener {


    private ArrayList<Macro> macrosList = new ArrayList<>();
    private RecyclerView recyclerView;
    private int effectNumber = 0;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true); //change toolbar
        // Inflate the layout for this fragment

        ConstraintLayout layout = (ConstraintLayout) inflater.inflate(R.layout.fragment_macros, container, false);


        //add list to recycler :
        recyclerView = layout.findViewById(R.id.macrosList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        MacroAdapter adapter = new MacroAdapter(macrosList, this);
        recyclerView.setAdapter(adapter);


        Button bAdd = layout.findViewById(R.id.addMacro);
        bAdd.setOnClickListener(v -> { //Add a new macro:

            effectNumber++;

            macrosList.add(0, new Macro("Personal Effect " + effectNumber,
                    "0 effect(s)"));
            adapter.notifyItemInserted(0); //insert at top, must use .add(0, ...) !!!!


            Toast.makeText(getActivity(), macrosList.size() + "", Toast.LENGTH_SHORT).show();

        });


        return layout;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

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
        toolBar.setTitle("Personal effects menu");
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

    @Override
    public void onClickApply(int position) {
        Toast.makeText(getActivity(), "Apply on " + macrosList.get(position).getName(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClickInfo(int position) {
        Toast.makeText(getActivity(), "Info on " + macrosList.get(position).getName(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClickDelete(int position) {
        macrosList.remove(position);
        recyclerView.removeViewAt(position);
        MacroAdapter adapter = (MacroAdapter) recyclerView.getAdapter();
        if (adapter == null) return;
        adapter.notifyItemRemoved(position);
        adapter.notifyItemRangeChanged(position, macrosList.size());
    }
}
