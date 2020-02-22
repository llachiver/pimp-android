package fr.ubordeaux.pimp.fragments;

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
 * Adapter class for Info button, see {@link InfoCard} and {@link InfosFragment}.
 */
public class InfoAdapter extends RecyclerView.Adapter<InfoAdapter.InfoViewHolder> {

    private ArrayList<InfoCard> dataset;


    /**
     * Create an InfoAdapter, see layout info_card.xml.
     *
     * @param data List of info cards to display.
     */
    public InfoAdapter(ArrayList<InfoCard> data) {
        dataset = data;
    }

    /**
     * Class to bind informations into graphical components
     */
    static class InfoViewHolder extends RecyclerView.ViewHolder { //friendly
        ImageView icone;
        TextView mainLine;
        TextView secondLine;

        InfoViewHolder(View itemView) {
            super(itemView);
            icone = itemView.findViewById(R.id.icone);
            mainLine = itemView.findViewById(R.id.main_line);
            secondLine = itemView.findViewById(R.id.second_line);
        }
    }


    @NonNull
    @Override
    public InfoAdapter.InfoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.info_card, parent, false);
        return new InfoViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull InfoAdapter.InfoViewHolder holder, int position) {
        InfoCard currentItem = dataset.get(position);

        holder.icone.setImageResource(currentItem.getImageResource());
        holder.mainLine.setText(currentItem.getMainLine());
        holder.secondLine.setText(currentItem.getSecondLine());
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }


}
