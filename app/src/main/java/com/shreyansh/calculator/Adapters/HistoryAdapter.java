package com.shreyansh.calculator.Adapters;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.shreyansh.calculator.R;
import com.shreyansh.calculator.models.Operation;

import java.util.ArrayList;

import androidx.recyclerview.widget.RecyclerView;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    private ArrayList<Operation> listOperation;

    public HistoryAdapter(ArrayList<Operation> listOperation) {
        this.listOperation=listOperation;
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView input;
        public TextView output;
        public ViewHolder(View itemView) {
            super(itemView);
            this.input = itemView.findViewById(R.id.input);
            this.output = (TextView) itemView.findViewById(R.id.output);

        }
    }


    @Override
    public HistoryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                          int viewType) {
        // create a new view
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem= layoutInflater.inflate(R.layout.item_history, parent, false);

        ViewHolder vh = new ViewHolder(listItem);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.input.setText("INPUT - "+listOperation.get(position).input);
        holder.output.setText("OUTPUT - "+ listOperation.get(position).output);

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return listOperation.size();
    }
}
