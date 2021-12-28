package com.example.workit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.workit.romdb.Diet;
import com.example.workit.romdb.Workout;

import java.util.ArrayList;

public class ViewAllWorkoutActivity extends AppCompatActivity {
   // ActivityViewAllWorkoutBinding binding;
    Intent intent;
    ArrayList<Workout> object;
    ArrayList<Diet> objectD;
    Button back;
    TextView title;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //binding = ActivityViewAllWorkoutBinding.inflate(getLayoutInflater());
        setContentView(R.layout.activity_view_all_workout);
        back = findViewById(R.id.backBtn);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        title = findViewById(R.id.workoutDate);

    }

    @Override
    protected void onStart() {
        super.onStart();
        intent = getIntent();

        if (intent.getBooleanExtra("isWorkout", false)) {
            Bundle args = intent.getBundleExtra("workoutListBundle");
            object = (ArrayList<Workout>) args.getSerializable("ARRAYLIST");
            title.setText("Workout on " + args.getSerializable("Date"));
            Log.e("VIEW ALL", "Size: "+object.size());
            initRecycler(true);
        } else {
            Bundle args = intent.getBundleExtra("workoutListBundle");
            objectD = (ArrayList<Diet>) args.getSerializable("ARRAYLIST");
            title.setText("Diet on " + args.getSerializable("Date"));
            Log.e("VIEW ALL", "Size: "+objectD.size());
            initRecycler(false);
        }

    }

    private void initRecycler(Boolean isWorkout) {
//        RecyclerView recycle = binding.workoutRecycler;
//        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
//        binding.workoutRecycler.setLayoutManager(layoutManager);
//        ViewAllWorkoutAdapter adapter = new ViewAllWorkoutAdapter(this,isWorkout);
//        binding.workoutRecycler.setAdapter(adapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        RecyclerView recyclerView = findViewById(R.id.workoutRecycler);
        recyclerView.setLayoutManager(layoutManager);
        if (isWorkout) {
            ViewAllWorkoutAdapter adapter = new ViewAllWorkoutAdapter(this,isWorkout,object, null);
            recyclerView.setAdapter(adapter);
        } else {
            ViewAllWorkoutAdapter adapter = new ViewAllWorkoutAdapter(this,isWorkout,null, objectD);
            recyclerView.setAdapter(adapter);
        }


    }

}