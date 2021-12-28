package com.example.workit;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Switch;

public class MenuActivity extends AppCompatActivity  {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
    }
    public void onClick(View view){
        if (view == findViewById(R.id.mapsTV)){
            startActivity(new Intent(this, Maps.class));
        } else if (view == findViewById(R.id.workoutTV)) {
            startActivity(new Intent(this, AddWorkoutSchedule.class));
        } else if (view == findViewById(R.id.dietTV)) {
            startActivity(new Intent(this, design_diet.class));
        }else if (view == findViewById(R.id.profileTv)) {
            startActivity(new Intent(this, Profile.class));
        }
    }
}