package fr.ubordeaux.pimp.fragments;

import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Queue;

import fr.ubordeaux.pimp.R;
import fr.ubordeaux.pimp.activity.MainActivity;
import fr.ubordeaux.pimp.image.ImageEffect;
import fr.ubordeaux.pimp.util.Effects;

public class MacrosFragment extends Fragment implements MacroAdapter.MacroListener {


    private ArrayList<Macro> macrosList = new ArrayList<>();
    private RecyclerView recyclerView;
    private int effectNumber = 0;
    private Macro lastAded;
    private int lastNumber = 0;


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

            if (getActivity() == null) return;

            Queue<ImageEffect> queue = ((MainActivity) getActivity()).getImage().getEffectsHistory();

            if (queue.size() <= lastNumber) {
                Toast.makeText(getActivity(), "Please apply effect(s) before", Toast.LENGTH_LONG).show();
                return;
            }


            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Name your effect :");

            final EditText input = new EditText(getActivity());
            // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
            input.setInputType(InputType.TYPE_CLASS_TEXT);
            builder.setView(input);

            // Set up the buttons
            builder.setPositiveButton("OK", (dialog, which) -> {
                macrosList.get(0).setName(input.getText().toString());
                adapter.notifyItemChanged(0);
            });

            builder.show();

            effectNumber++;

            macrosList.add(0, new Macro("Effect " + (effectNumber + 1),
                    queue.size() + " effects", queue));
            adapter.notifyItemInserted(0); //insert at top, must use .add(0, ...) !!!!

            lastAded = macrosList.get(0);
            lastNumber = lastAded.getEffects().size();
        });


        return layout;
    }

    /**
     * Call it when you load or reset a picture, this will reset the value used to limit the user to avoid that he creates macro for 0 effects or that he duplicates macros
     */
    public void resetCounter() {
        lastNumber = 0;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }


    /**
     * Change ToolBar for this fragment.
     */
    @Override
    public void onCreateOptionsMenu(@NotNull Menu menu, @NotNull MenuInflater inflater) {
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
        MainActivity main = (MainActivity) getActivity();
        if (main == null) return;
        main.getImage().quickSave();
        main.onBackPressed();
        main.inflateEffectSettings(Effects.MACRO, macrosList.get(position).getEffects());
    }

    @Override
    public void onClickInfo(int position) {
        if (getActivity() == null) return;

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setIcon(R.drawable.ic_arrow_downward_white_24dp);
        builder.setTitle("Effects :");

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_expandable_list_item_1);

        ArrayList<String> values = new ArrayList<>();
        for (ImageEffect effect : macrosList.get(position).getEffects()) {
            values.add(effect.getName() + (effect.getArgs().length > 0 ? " " + Arrays.toString(effect.getArgs()) : ""));
        }

        arrayAdapter.addAll(values);


        builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());

        builder.setAdapter(arrayAdapter, null); //no action when click on items
        builder.show();
    }

    @Override
    public void onClickDelete(int position) {
        if (macrosList.get(position) == lastAded) {
            lastNumber -= macrosList.get(position).getEffects().size();
        }
        macrosList.remove(position);
        recyclerView.removeViewAt(position);
        MacroAdapter adapter = (MacroAdapter) recyclerView.getAdapter();
        if (adapter == null) return;
        adapter.notifyItemRemoved(position);
        adapter.notifyItemRangeChanged(position, macrosList.size());
    }
}
