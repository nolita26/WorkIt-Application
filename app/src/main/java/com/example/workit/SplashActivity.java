package com.example.workit;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.preference.PreferenceManager;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

public class SplashActivity extends AppCompatActivity {

    ImageView logo, weight, food, sweat, sweat2;
    private  static  int SPLASH_SCREEN =2500;
    FirebaseAuth fAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.splash_screen);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(SplashActivity.this);
        String email = preferences.getString("Email","");
        logo = findViewById(R.id.logo);
        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(logo, "alpha", 0f, 1.0f);
        fadeIn.setDuration(2000).setStartDelay(4500);
        fadeIn.start();

        weight = findViewById(R.id.weight);
        ObjectAnimator rotate = ObjectAnimator.ofFloat(weight, "rotation", 0f, 720f);
        rotate.setDuration(3000); // miliseconds
        rotate.start();
        ObjectAnimator moverightX = ObjectAnimator.ofFloat(weight, "translationX", 100);
        moverightX.setDuration(3000);
        moverightX.start();

        food = findViewById(R.id.food);
        ObjectAnimator rotateFood = ObjectAnimator.ofFloat(food, "rotation", 720f, 0f);
        rotateFood.setDuration(3000); // miliseconds
        rotateFood.start();
        ObjectAnimator moveLeftX = ObjectAnimator.ofFloat(food, "translationX", -620);
        moveLeftX.setDuration(3000);
        moveLeftX.start();

        sweat = findViewById(R.id.sweat);
        ObjectAnimator sweatIn = ObjectAnimator.ofFloat(sweat, "alpha", 0f, 1.0f);
        sweatIn.setDuration(1000).setStartDelay(3000);
        ObjectAnimator sweatDown = ObjectAnimator.ofFloat(sweat, "translationY", 100);
        sweatDown.setDuration(1000).setStartDelay(3000);
        sweatIn.start();
        sweatDown.start();
        ObjectAnimator sweatOut = ObjectAnimator.ofFloat(sweat, "alpha", 1.0f, 0f);
        sweatOut.setDuration(500).setStartDelay(3500);
        sweatOut.start();

        sweat2 = findViewById(R.id.sweat2);
        ObjectAnimator sweatIn2 = ObjectAnimator.ofFloat(sweat2, "alpha", 0f, 1.0f);
        sweatIn2.setDuration(1000).setStartDelay(3500);
        ObjectAnimator sweatDown2 = ObjectAnimator.ofFloat(sweat2, "translationY", 100);
        sweatDown2.setDuration(1000).setStartDelay(3500);
        sweatIn2.start();
        sweatDown2.start();
        ObjectAnimator sweatOut2 = ObjectAnimator.ofFloat(sweat2, "alpha", 1.0f, 0f);
        sweatOut2.setDuration(500).setStartDelay(4000);
        sweatOut2.start();


        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                /* Create an Intent that will start the Menu-Activity. */
                if (email != "") {
                    Intent mainIntent = new Intent(SplashActivity.this,NavActivity.class);
                    mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(mainIntent);
                } else {
                    Intent mainIntent = new Intent(SplashActivity.this,IntroActivity.class);
                    mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(mainIntent);
                }

                finish();
            }
        }, 7300);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        FirebaseApp.initializeApp(this);
    }
}