package fr.ubordeaux.pimp.fragments;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import fr.ubordeaux.pimp.R;

/**
 * Adapter class for macros of effects, see {@link MacroCard} and {@link MacrosFragment}.
 */
class MacroAdapter extends RecyclerView.Adapter<MacroAdapter.MacroViewHolder> {
    private ArrayList<MacroCard> dataset;


    /**
     * Create an MacroAdapter, see layout macro_card.xml.
     *
     * @param data List of macro cards to display.
     */
    public MacroAdapter(ArrayList<MacroCard> data) {
        dataset = data;
    }

    /**
     * Class to bind informations into graphical components
     */
    static class MacroViewHolder extends RecyclerView.ViewHolder { //friendly
        TextView nameLine;
        TextView secondLine;

        MacroViewHolder(View itemView) {
            super(itemView);
            nameLine = itemView.findViewById(R.id.macroName);
            secondLine = itemView.findViewById(R.id.second_line);
        }
    }


    @NonNull
    @Override
    public MacroAdapter.MacroViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.macro_card, parent, false);
        return new MacroAdapter.MacroViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MacroAdapter.MacroViewHolder holder, int position) {
        MacroCard currentItem = dataset.get(position);

        holder.nameLine.setText(currentItem.getName());
        holder.secondLine.setText(currentItem.getInfoLine());
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }
}
