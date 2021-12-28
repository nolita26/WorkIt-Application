package com.example.workit;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.InputType;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.workit.custominterface.DateInterface;
import com.example.workit.romdb.Diet;
import com.example.workit.romdb.DietDb;
import com.example.workit.romdb.DietDb_Impl;
import com.example.workit.romdb.DietNames;
import com.example.workit.romdb.Workout;
import com.example.workit.romdb.WorkoutDatabase;
import com.example.workit.romdb.WorkoutNames;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.NotificationCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.io.Serializable;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class NavActivity extends AppCompatActivity implements DateInterface {

    private AppBarConfiguration mAppBarConfiguration;
    private DrawerLayout mDrawer;
    private Toolbar toolbar;
    private Boolean guestFlag = false;
    private NavigationView nvDrawer;
    private RecyclerView recycle;
    Intent navIntent;
    int index;
    WorkoutDatabase wdb;
    DietDb ddb;
    HomeDateAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav);
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(NavActivity.this);
        Boolean guest = preferences.getBoolean("Guest",false);
        String Email = preferences.getString("Email", "");
        if (guest) {
            if (Email == "") {
                openNameDialog("Hello Guest please enter your email ID");
            }
        }
        ddb = DietDb.getInstance(NavActivity.this);
        wdb = WorkoutDatabase.getInstance(NavActivity.this);
        // Set a Toolbar to replace the ActionBar.
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.white, null));
        // This will display an Up icon (<-), we will replace it with hamburger later
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_drawer_icon);
        // Find our drawer view
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        nvDrawer = (NavigationView) findViewById(R.id.nav_view);
        recycle = (RecyclerView) findViewById(R.id.recycler);
        setupDrawerContent(nvDrawer);
        navIntent = new Intent(this, ViewAllWorkoutActivity.class);
        initRecycler();
        Intent intent = new Intent(this, AddWorkoutSchedule.class);
        findViewById(R.id.viewAllWork).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Workout> workouts = wdb.workoutDoa().getWorkoutOfDate(date);
                Log.e("VIEW ALL", "Size"+workouts.size());
                Intent sendData = new Intent(NavActivity.this, ViewAllWorkoutActivity.class);
                sendData.putExtra("isWorkout", true);
                Bundle args = new Bundle();
                args.putSerializable("ARRAYLIST",(Serializable)workouts);
                args.putSerializable("Date", date);
                sendData.putExtra("workoutListBundle", args);
                startActivity(sendData);
                //startActivity(navIntent);
            }
        });
        findViewById(R.id.viewAllDiet).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Diet> diets = ddb.dieDao().getDietForDate(date);
                if (diets.size() > 0) {
                    Log.e("DIET ALL", "Size: "+diets.size());
                    Intent sendData = new Intent(NavActivity.this, ViewAllWorkoutActivity.class);
                    sendData.putExtra("isWorkout", false);
                    Bundle args = new Bundle();
                    args.putSerializable("ARRAYLIST",(Serializable)diets);
                    args.putSerializable("Date", date);
                    sendData.putExtra("workoutListBundle", args);
                    startActivity(sendData);
                } else {
//                    showToast("Please add some enteries to your diet");
                }
                //startActivity(navIntent);
            }
        });

        findViewById(R.id.addWK).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addWtIntent = new Intent(NavActivity.this,AddWorkoutSchedule.class);
                startActivity(addWtIntent);
            }
        });

        findViewById(R.id.addDT).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addDtIntent = new Intent(NavActivity.this,design_diet.class);
                startActivity(addDtIntent);
            }
        });
    }
    String date;

    private String m_Text;

    public void dismiss() {
        showToast("Please Enter a  ");
    }

    private void showToast(String message) {
        Toast.makeText(NavActivity.this,message, Toast.LENGTH_LONG).show();
    }

    public void openNameDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(message);
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                    m_Text = input.getText().toString();
                    if (isValidEmailAddress(m_Text)) {
                        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(NavActivity.this);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("Email",m_Text);
                        editor.apply();
                    } else {
                        openNameDialog("Please enter a valid Email ID and try again");
                    }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                logout();
            }
        });

        builder.show();
    }

    public boolean isValidEmailAddress(String email) {
        String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
        java.util.regex.Matcher m = p.matcher(email);
        return m.matches();
    }


    @Override
    protected void onResume() {
        super.onResume();
        Calendar calendar = new GregorianCalendar();
        calendar.add(Calendar.DATE, 0);
        date = new SimpleDateFormat("MM-dd-yy").format(calendar.getTime());
        setUpViews(date);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(NavActivity.this);
        TextView title = findViewById(R.id.helloTV);
        String first = preferences.getString("first", "User");
        title.setText("Hello " + first);
        ImageView profile = findViewById(R.id.userIV);
        String image = preferences.getString("profile_img", "");
        Uri getImage = Uri.parse(image);
        profile.setImageURI(getImage);
        if (preferences.getBoolean("Guest", false)) {
            //showToast("Please head over to profile page and update your details");
        } else {
            //showToast("Please register yourself at your earliest convenience");
        }
        if (adapter != null) {
            adapter.setUpAgain();
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.nav, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar home/up action should open or close the drawer.
        switch (item.getItemId()) {
            case android.R.id.home:
                nvDrawer.getMenu().getItem(index).setChecked(false);
                mDrawer.openDrawer(GravityCompat.START);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        selectDrawerItem(menuItem);
                        return true;
                    }
                });
    }

    public void selectDrawerItem(MenuItem menuItem) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(NavActivity.this);
        Boolean guest = preferences.getBoolean("Guest",false);
        switch(menuItem.getItemId()) {
            case R.id.nav_diet:
                index = 2;
                Intent diet = new Intent(NavActivity.this, design_diet.class);
                startActivity(diet);
                break;
            case R.id.nav_profile:
                index = 0;
                if (guest == false) {
                    Intent prof = new Intent(NavActivity.this, Profile.class);
                    startActivity(prof);
                } else {
                    showToast("Please register yourself");
                }
                break;
            case R.id.nav_workout:
                index = 1;
                Intent work = new Intent(NavActivity.this, AddWorkoutSchedule.class);
                startActivity(work);
                break;
            case R.id.nav_gym:
                index = 3;
                if (isInternetAvailable()) {
                    Intent map = new Intent(NavActivity.this, Maps.class);
                    startActivity(map);
                } else {
                    showToast("You need to be connected to the Internet");
                }
                break;
            case R.id.nav_logout:
                logout();
                break;
            case R.id.nav_sync:
                index = 4;
                //FireStore Data Sync Up
                syncData();

            default:
                mDrawer.closeDrawers();
        }
        mDrawer.closeDrawers();
    }

    private void syncData() {
        createNotificationChannel();
        makeNotification();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(NavActivity.this);
        String email = preferences.getString("Email","");
        if (email != "") {
            List<WorkoutNames> workoutNames = this.wdb.workoutDoa().getWorkoutNames();
            List<DietNames> dietNames = this.ddb.dieDao().getDietNames1();
            List<Diet> dietSt = this.ddb.dieDao().getAllDiet();
            List<Workout> workouts = this.wdb.workoutDoa().getWorkoutOfDate(date);
            List<Workout> appendWork = new ArrayList<>();
            List<Diet> appendDiet = new ArrayList<>();
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            if (db != null) {
                showToast("Please wait while we sync your data");
                for (WorkoutNames workA: workoutNames) {
                    for(Workout works: workouts) {
                        if (works.workoutName.equals(workA.workoutName)) {
                            appendWork.add(works);
                        }
                    }
                    if (appendWork.size() > 0) {
                        String string = email;
                        Map<String,String> dummy = new HashMap<>();
                        dummy.put("Dummy", "Dummy");
                        db
                                .collection("Workout")
                                .document(string).set(dummy);
                        Map<String, Object> work = new HashMap<>();
                        work.put("workouts",workouts);
                        db
                                .collection("Workout")
                                .document(string)
                                .collection("Workouts")
                                .document(workA.workoutName)
                                .set(work).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                if (task.isSuccessful()) {
                                    showToast("Your data is Synced");

                                } else {
                                    showToast("Could not sync your data, we will try again later");
                                }
                            }
                        });
                    }
                    appendWork.clear();
                }
                for(DietNames dietN: dietNames) {
                    for(Diet diets: dietSt) {
                        if (dietN.dietName.equals(diets.dietName)) {
                            appendDiet.add(diets);
                        }
                    }
                    if (appendDiet.size() > 0) {
                        String string = email;
                        Map<String,String> dummy = new HashMap<>();
                        dummy.put("Dummy", "Dummy");
                        db
                                .collection("Diet")
                                .document(string).set(dummy);
                        Map<String, Object> Diets = new HashMap<>();
                        Diets.put("diets",appendDiet);
                        db
                                .collection("Diet")
                                .document(string)
                                .collection("Diets")
                                .document(dietN.dietName)
                                .set(Diets).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    //showToast("Your data is Synced");
                                } else {
                                    //showToast("Could not sync your data, we will try again later");
                                }
                            }
                        });
                        appendDiet.clear();
                    }
                }

            } else {
                showToast("Check Your Internet connection");
            }
            List<Diet> diets = this.ddb.dieDao().getDietForDate(date);
        }
    }

    private void logout() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(NavActivity.this);
        //LogOut _/\_ Initiated
        if (preferences.getBoolean("Guest",false) == false) {
            FirebaseAuth fAuth = FirebaseAuth.getInstance();
            fAuth.signOut();
        }

        //Clearing Shared Preference _/\_
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("Email","");
        editor.putBoolean("Guest",false);
        editor.putString("height", "");
        editor.putString("sex", "");
        editor.putString("Email", "");
        editor.putString("first","");
        editor.putString("dob", "");
        editor.putString("goal", "");
        editor.putString("last", "");
        editor.putString("weight", "");
        editor.apply();

        //Clearing Workout DB _/\_
        WorkoutDatabase wdb = WorkoutDatabase.getInstance(NavActivity.this);
        wdb.workoutDoa().nukeWorkoutNames();
        wdb.workoutDoa().nukeWorkout();

        //Clearing Diet DB _/\_
        DietDb ddb = DietDb.getInstance(NavActivity.this);
        ddb.dieDao().nukeTableDiet();
        ddb.dieDao().nukeTableDietNames();

        //Clearing Back Stack
        Intent splash = new Intent(NavActivity.this, LoginActivity.class);
        splash.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(splash);

    }

    private void initRecycler() {
        ArrayList<String> mNames = new ArrayList<>();
        ArrayList<String> ldbDates = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            Calendar calendar = new GregorianCalendar();
            calendar.add(Calendar.DATE, i);
            ldbDates.add(new SimpleDateFormat("MM-dd-yy").format(calendar.getTime()));
            mNames.add( new SimpleDateFormat("MM/dd").format(calendar.getTime()));
        }
        LinearLayoutManager layoutManager = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        RecyclerView recyclerView = findViewById(R.id.recycler);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new HomeDateAdapter(mNames, this, this,ldbDates);
        recyclerView.setAdapter(adapter);

    }

    @Override
    public void listenToDateUpdates(String date) {
        if (date != null) {
            this.date = date;
            Log.e("DRAWER","Date Changed");
            setUpViews(date);
        }
    }

    public boolean isInternetAvailable() {

        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    private void setUpViews(String date) {

        List<Workout> workouts = this.wdb.workoutDoa().getWorkoutOfDate(date);
        List<Diet> diets = this.ddb.dieDao().getDietForDate(date);
        CardView view = findViewById(R.id.mainCard);
        CardView view2 = findViewById(R.id.emptyAddCard);
        if ((workouts.size() >= 2) && (diets.size() >= 1)){
            view2.setVisibility(View.GONE);
           // findViewById(R.id.gifCard).setVisibility(View.GONE);
            findViewById(R.id.gifView).setVisibility(View.GONE);
            view.setVisibility(View.VISIBLE);
            //Set data to static UI _/\_
            if (workouts.size() > 0) {
                TextView first = (TextView) findViewById(R.id.firstText);
                first.setText(workouts.get(0).workoutType);
                TextView second = (TextView) findViewById(R.id.secondText);
                second.setText(workouts.get(0).durationOfWorkout);
                TextView thirdText = (TextView) findViewById(R.id.thirdText);
                thirdText.setText("Sets: "+workouts.get(0).numberOfSets+" Reps: "+workouts.get(0).repeatitions);
                if (workouts.get(0).workoutType.equals("Weight Training")) {
                    findViewById(R.id.img).setBackgroundResource(R.drawable.wtimg);
                } else if (workouts.get(0).workoutType.equals("Cardio")){
                    findViewById(R.id.img).setBackgroundResource(R.drawable.cardio_img);
                } else {
                    findViewById(R.id.img).setBackgroundResource(R.drawable.aerobic);
                }
            }
            if (workouts.size() > 1) {
                TextView first = (TextView) findViewById(R.id.firstText2);
                first.setText(workouts.get(1).workoutType);
                TextView second = (TextView) findViewById(R.id.secondText2);
                second.setText(workouts.get(1).durationOfWorkout);
                TextView thirdText = (TextView) findViewById(R.id.thirdText2);
                thirdText.setText("Sets: "+workouts.get(1).numberOfSets+" Reps: "+workouts.get(0).repeatitions);
                if (workouts.get(1).workoutType.equals( "Weight Training")) {
                    findViewById(R.id.img1).setBackgroundResource(R.drawable.wtimg);
                } else if (workouts.get(1).workoutType.equals("Cardio")){
                    findViewById(R.id.img1).setBackgroundResource(R.drawable.cardio_img);
                } else {
                    findViewById(R.id.img1).setBackgroundResource(R.drawable.aerobic);
                }
            }
            if (diets.size() > 0) {
                TextView first = (TextView) findViewById(R.id.firstText3);
                first.setText(diets.get(0).foodName);
                TextView second = (TextView) findViewById(R.id.secondText3);
                second.setText(diets.get(0).time);
            }


        } else {
            view.setVisibility(View.GONE);
            //findViewById(R.id.gifCard).setVisibility(View.VISIBLE);
            findViewById(R.id.gifView).setVisibility(View.VISIBLE);
            view2.setVisibility(View.VISIBLE);
        }
    }
    private void createNotification() {
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.location)
                        .setContentTitle("Unique Andro Code")
                        .setContentText("welcome To Unique Andro Code");

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0, builder.build());
    }

    NotificationManager notificationManager;
    public static final String NOTIFICATION_CHANNEL_ID = "channel_id";
    public static final String CHANNEL_NAME = "Notification Channel";
    public static final int NOTIFICATION_ID = 101;

    private void createNotificationChannel() {
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, CHANNEL_NAME, importance);
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }

    public void makeNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setContentTitle("WorkIt")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentText("Your Data is synced");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setGroup(NOTIFICATION_CHANNEL_ID);
        }
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0, builder.build());
    }
}