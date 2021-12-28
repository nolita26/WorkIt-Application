package com.example.workit;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.workit.romdb.Diet;
import com.example.workit.romdb.Workout;

import java.util.ArrayList;

public class ViewAllWorkoutAdapter extends RecyclerView.Adapter<ViewAllWorkoutAdapter.ViewHolder2> {

    Context context;
    Boolean isWorkout = false;
    ArrayList<Workout> Work;
    ArrayList<Diet> Diet;
    public ViewAllWorkoutAdapter(Context context, Boolean isWorkout, ArrayList<Workout> Work, ArrayList<Diet> Diet) {
        this.context = context;
        this.isWorkout = isWorkout;
        this.Work = Work;
        this.Diet = Diet;
    }

    @NonNull
    @Override
    public ViewHolder2 onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.workout_cell,parent, false);
        return new ViewAllWorkoutAdapter.ViewHolder2(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder2 holder, int position) {
        if (isWorkout) {
            holder.workoutNameText.setText(Work.get(position).workoutType);
            holder.workoutRepsText.setText(Work.get(position).durationOfWorkout);
            holder.workoutTypeText.setText(Work.get(position).repeatitions);
            if (Work.get(position).workoutType.equals("Weight Training")) {
                holder.workoutTypeImg.setBackgroundResource(R.drawable.wtimg);
            } else if (Work.get(position).workoutType.equals("Cardio")){
                holder.workoutTypeImg.setBackgroundResource(R.drawable.cardio_img);
            } else {
                holder.workoutTypeImg.setBackgroundResource(R.drawable.aerobic);
            }
        } else {
            holder.workoutNameText.setText(Diet.get(position).foodName);
            holder.workoutRepsText.setText(Diet.get(position).time);
            holder.workoutTypeText.setText(Diet.get(position).calCount);
            holder.workoutTypeImg.setBackgroundResource(R.drawable.diet_img2);
        }

    }

    @Override
    public int getItemCount() {
        if (isWorkout) {
            return Work.size();
        } else {
            return Diet.size();
        }
    }

    public class ViewHolder2 extends RecyclerView.ViewHolder {
        ImageView workoutTypeImg;
        TextView workoutTypeText;
        TextView workoutNameText;
        TextView workoutRepsText;
        public ViewHolder2(@NonNull View itemView) {
            super(itemView);
            workoutTypeImg = itemView.findViewById(R.id.workoutImg);
            workoutTypeText = itemView.findViewById(R.id.workoutTypeTV);
            workoutNameText = itemView.findViewById(R.id.exerciseTypeTV);
            workoutRepsText = itemView.findViewById(R.id.repsTV);
        }
    }
}
