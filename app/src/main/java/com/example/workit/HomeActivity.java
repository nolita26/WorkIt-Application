package com.example.workit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {

    Intent navIntent;
    private DrawerLayout drawerLayout;
    private NavigationView navBar;
    private Toolbar toolBar;
    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String name = preferences.getString("Email", "");
        Log.e("Email","EMAIL: "+name);
        navIntent = new Intent(this, ViewAllWorkoutActivity.class);
        initRecycler();
        Intent intent = new Intent(this, AddWorkoutSchedule.class);
        findViewById(R.id.viewAllWork).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navIntent.putExtra("isWorkout", true);
                startActivity(navIntent);
            }
        });
        findViewById(R.id.viewAllDiet).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Redirect for view all diet
                navIntent.putExtra("isWorkout", false);
                startActivity(navIntent);
            }
        });
        findViewById(R.id.inc).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Redirect for view all diet
                //navIntent.putExtra("isWorkout", false);
                startActivity(new Intent(HomeActivity.this, MenuActivity.class));
            }
        });
       // toolBar = findViewById(R.id.inc);
//        setSupportActionBar(toolBar);
//        ActionBar action = this.getSupportActionBar();
//
//        if (action != null) {
//            action.setDisplayHomeAsUpEnabled(true);
//        }
//        action.setTitle("");
//        action.setDefaultDisplayHomeAsUpEnabled(true);
//        action.setHomeAsUpIndicator(R.drawable.ic_drawer_icon);
    }

    private void initRecycler() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        RecyclerView recyclerView =findViewById(R.id.recycler);
        recyclerView.setLayoutManager(layoutManager);
        ArrayList<String> mNames = new ArrayList<>();
        mNames.add("23");
        mNames.add("24");
        mNames.add("25");
        mNames.add("26");
        mNames.add("27");
        mNames.add("28");
        mNames.add("29");
//        HomeDateAdapter adapter = new HomeDateAdapter(mNames, this);
//        recyclerView.setAdapter(adapter);
    }


}