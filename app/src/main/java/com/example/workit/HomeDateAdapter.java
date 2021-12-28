package com.example.workit;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.workit.custominterface.DateInterface;

import java.util.ArrayList;

public class HomeDateAdapter extends RecyclerView.Adapter<HomeDateAdapter.ViewHolder>{

    private ArrayList<String> mNames = new ArrayList<>();
    private Context context;
    private DateInterface dateListener;
    private ArrayList<String> dates = new ArrayList<>();
    boolean[] boolArray = new boolean[7];
    public HomeDateAdapter(ArrayList<String> mNames, Context context, DateInterface dateListener, ArrayList<String> dates) {
        Log.e("HomeAdapter", "HomeDateAdapter");
        this.mNames = mNames;
        this.context = context;
        this.dateListener = dateListener;
        this.dates = dates;
        boolArray[0] = true;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_date_card,parent, false);
        Log.e("HomeAdapter", "onCreateViewHolder");
        return new ViewHolder(view,this);
    }

    private boolean flag = true;
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.e("HomeAdapter", "onBindViewHolder");
        holder.date.setText(mNames.get(position));
        if (boolArray[position]) {
            holder.view.setCardBackgroundColor(Color.BLACK);
        } else {
            holder.view.setCardBackgroundColor(Color.WHITE);
        }
        if (flag) {
            flag = false;
            dateListener.listenToDateUpdates(dates.get(0));
        }
        holder.date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dateListener != null) {
                    for (int i = 0; i < 7; i ++){
                        boolArray[i] = false;
                    }
                    boolArray[position] = true;
                    dateListener.listenToDateUpdates(dates.get(position));
                    holder.adapter.notifyDataSetChanged();
                }
                Log.d("Position", "onClick: "+position);
            }
        });
        //
    }

    public void setUpAgain() {
        for (int i = 0; i < 7; i ++){
            boolArray[i] = false;
        }
        boolArray[0] = true;
    }
    @Override
    public int getItemCount() {
        Log.e("HomeAdapter", "getItemCount");
        return mNames.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView date;
        CardView view;

        HomeDateAdapter adapter;
        public ViewHolder(@NonNull View itemView, HomeDateAdapter adapter) {
            super(itemView);
            date = itemView.findViewById(R.id.dateTV);
            view = itemView.findViewById(R.id.roundCardView);
            this.adapter = adapter;
        }
        void setColorBlack() {
            view.setCardBackgroundColor(Color.BLACK);
        }
        void setColorWhite() {
            view.setCardBackgroundColor(Color.WHITE);
        }
    }
}
