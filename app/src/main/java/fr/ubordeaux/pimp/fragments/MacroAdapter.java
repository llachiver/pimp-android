package fr.ubordeaux.pimp.fragments;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import fr.ubordeaux.pimp.R;

/**
 * Adapter class for macros of effects, see {@link Macro} and {@link MacrosFragment}.
 */
class MacroAdapter extends RecyclerView.Adapter<MacroAdapter.MacroViewHolder> {
    private ArrayList<Macro> dataset;
    private MacroListener adapterListener;


    /**
     * Create an MacroAdapter, see layout macro_card.xml.
     *
     * @param data            List of macro cards to display.
     * @param adapterListener Listener
     */
    public MacroAdapter(ArrayList<Macro> data, MacroListener adapterListener) {
        this.dataset = data;
        this.adapterListener = adapterListener;
    }

    /**
     * Class to bind informations into graphical components
     */
    static class MacroViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener { //friendly
        TextView nameLine;
        TextView secondLine;
        MacroListener listener;

        MacroViewHolder(View itemView, MacroListener listener) {
            super(itemView);
            nameLine = itemView.findViewById(R.id.macroName);
            secondLine = itemView.findViewById(R.id.second_line);
            itemView.findViewById(R.id.applyMacro).setOnClickListener(this);
            itemView.findViewById(R.id.deleteMacro).setOnClickListener(this);
            itemView.findViewById(R.id.infoMacro).setOnClickListener(this);

            this.listener = listener;
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.applyMacro) {
                listener.onClickApply(getAdapterPosition());
                return;
            }
            if (v.getId() == R.id.infoMacro) {
                listener.onClickInfo(getAdapterPosition());
                return;
            }
            listener.onClickDelete(getAdapterPosition());
        }
    }


    @NonNull
    @Override
    public MacroAdapter.MacroViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.macro_card, parent, false);
        return new MacroAdapter.MacroViewHolder(v, adapterListener);
    }

    @Override
    public void onBindViewHolder(@NonNull MacroAdapter.MacroViewHolder holder, int position) {
        Macro currentItem = dataset.get(position);

        holder.nameLine.setText(currentItem.getName());
        holder.secondLine.setText(currentItem.getInfoLine());
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }

    public interface MacroListener {
        void onClickApply(int position);

        void onClickDelete(int position);

        void onClickInfo(int position);
    }
}
