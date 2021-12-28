package com.example.workit;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class IntroActivity extends AppCompatActivity {
    private ViewPager screenPager;
    IntroViewPagerAdapter introViewPagerAdapter;
    TabLayout tabIndicator;
    Button btnNext, btnGetStarted;
    LinearLayout linearLayoutNext, linearLayoutGetStarted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

//        if (restorePreData()){
//            Intent mainActivity = new Intent(getApplicationContext(), MainActivity.class);
//            startActivity(mainActivity);
//            finish();
//        }

//        getSupportActionBar().hide();
        setContentView(R.layout.activity_intro);

        btnNext = findViewById(R.id.btn_next);
        btnGetStarted = findViewById(R.id.btn_get_started);
        linearLayoutNext = findViewById(R.id.linear_layout_next);
        linearLayoutGetStarted = findViewById(R.id.linear_layout_get_started);
        tabIndicator = findViewById(R.id.tab_indicator);

        //Data
        final List<ScreenItem> mList = new ArrayList<>();
        mList.add(new ScreenItem("Work it out!", "Plan your workout instantly from the app.", R.drawable.on1));
        mList.add(new ScreenItem("Keep Nutritional Count", "Track your nutritional value ensuring you're meeting nutrient targets.", R.drawable.on2));
        mList.add(new ScreenItem("Find the best-fit gym nearby you", "Whether you're a beginner or a fitness regular, we'll help you get to a fit place.", R.drawable.on3));

        //Setup viewPager
        screenPager = findViewById(R.id.screen_viewpager);
        introViewPagerAdapter = new IntroViewPagerAdapter(this, mList);
        screenPager.setAdapter(introViewPagerAdapter);

        //Setup tab indicator
        tabIndicator.setupWithViewPager(screenPager);

        //Button Next
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                screenPager.setCurrentItem(screenPager.getCurrentItem()+1, true);
            }
        });

        tabIndicator.addOnTabSelectedListener(new TabLayout.BaseOnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition()==mList.size()-1){
                    loadLastScreen();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        //Button Get Started
        btnGetStarted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mainActivity = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(mainActivity);
                savePrefsData();
                finish();
            }
        });
    }

    private boolean restorePreData(){
        SharedPreferences preferences = getApplicationContext().getSharedPreferences("myPrefs", MODE_PRIVATE);
        Boolean isIntroActivityOpenedBefore = preferences.getBoolean("isIntroOpened", false);
        return isIntroActivityOpenedBefore;
    }

    private void savePrefsData(){
        SharedPreferences preferences = getApplicationContext().getSharedPreferences("myPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("isIntroOpened", true);
        editor.apply();
    }

    private void loadLastScreen(){
        linearLayoutNext.setVisibility(View.INVISIBLE);
        linearLayoutGetStarted.setVisibility(View.VISIBLE);
    }
}
