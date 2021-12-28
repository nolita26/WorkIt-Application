package com.example.workit;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ViewTrialAdapter extends RecyclerView.Adapter<ViewTrialAdapter.ViewHolder> {

    Context context;
    public ViewTrialAdapter(Context context) {
        Log.e("ViewTrialAdapter", "ViewTrialAdapter");
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_row,null, false);
        Log.e("ViewTrialAdapter", "onCreateViewHolder");
        return new ViewTrialAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.e("ViewTrialAdapter", "onBindViewHolder");
    }

    @Override
    public int getItemCount() {
        return 20;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CardView card;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            card = itemView.findViewById(R.id.Trial);
        }
    }
}
