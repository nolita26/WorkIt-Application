package com.example.workit;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.viewpager.widget.ViewPager;

import com.example.workit.romdb.Diet;
import com.example.workit.romdb.DietDb;
import com.example.workit.romdb.DietNames;
import com.example.workit.romdb.Workout;
import com.example.workit.romdb.WorkoutDatabase;
import com.example.workit.romdb.WorkoutNames;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    public static final String TAG = "TAG";
    EditText FirstName, LastName, mEmail, mPassword, cnfmPass;
    Button RegisterBtn;
    Button LoginBtn;
    FirebaseAuth fAuth;
    FirebaseFirestore fstore;
    String userID;
    ViewPager viewPager;
    float v=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        viewPager = findViewById(R.id.view_pager);
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        FirstName = findViewById(R.id.firstname);
        LastName = findViewById(R.id.lastname);
        mEmail = findViewById(R.id.email);
        mPassword = findViewById(R.id.pass);
        cnfmPass = findViewById(R.id.confirm_pass);
        RegisterBtn = findViewById(R.id.register_button_reg);

        fAuth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();

//        if(fAuth.getCurrentUser() != null) {
//            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
//            finish();
//        }

        RegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String first = FirstName.getText().toString().trim();
                String last = LastName.getText().toString().trim();
                String email = mEmail.getText().toString().trim();
                String password = mPassword.getText().toString().trim();
                String confirm = cnfmPass.getText().toString().trim();

                if(TextUtils.isEmpty(first)) {
                    FirstName.setError("First name is required.");
                    return;
                }

                if(TextUtils.isEmpty(last)) {
                    LastName.setError("Last name is required.");
                    return;
                }

                if(TextUtils.isEmpty(email)) {
                    mEmail.setError("Email is required.");
                    return;
                }

                if(TextUtils.isEmpty(email)) {
                    mPassword.setError("Password is required.");
                    return;
                }

                if(password.length() < 6) {
                    mPassword.setError("Password must be >=6 characters.");
                    return;
                }

                if (!confirm.equals(password)) {
                    cnfmPass.setError("Password does not match");
                    return;
                }

                fAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener((task) -> {
                    if(task.isSuccessful()) {
                        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(RegisterActivity.this);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("Email",fAuth.getCurrentUser().getEmail());
                        editor.putString("first", first);
                        editor.putString("last", last);
                        editor.putString("profile_img", "android.resource://com.example.workit/drawable/mt_img");
                        editor.apply();
                        createNotificationChannel();
                        makeNotification();
//                        String emailID = fAuth.getCurrentUser().getEmail();
//                        DocumentReference documentReference = fstore.collection("users").document(emailID);
//                        Map<String, Object> user = new HashMap<>();
//                        user.put("firstName", first);
//                        user.put("lastName", last);
//                        user.put("email", email);
//                        documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
//                            @Override
//                            public void onSuccess(Void unused) {
//                                Log.d(TAG, "onSuccess, user profile is created for " + emailID);
//                            }
//                        }).addOnFailureListener(new OnFailureListener() {
//                            @Override
//                            public void onFailure(@NonNull Exception e) {
//                                Log.d(TAG, "onFailure: " + e.toString());
//                            }
//                        });

                        Toast.makeText(RegisterActivity.this, "User Created.", Toast.LENGTH_SHORT).show();
                        getListItems(fAuth.getCurrentUser().getEmail());
                    } else {
                        Toast.makeText(RegisterActivity.this, "Error!" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
//                Intent home = new Intent(RegisterActivity.this, EnterDetails.class);
//                startActivity(home);
            }
        });

//        LoginBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
//            }
//        });
        //        tabLayout.addTab(tabLayout.newTab().setText("Login"));
//        tabLayout.addTab(tabLayout.newTab().setText("Signup"));
//        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

//        final LoginAdapter adapter = new LoginAdapter(getSupportFragmentManager(), this, tabLayout.getTabCount());
//        viewPager.setAdapter(adapter);

//        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

//        tabLayout.setTranslationY(300);
//
//        tabLayout.setAlpha(v);
    }
    FirebaseFirestore db;

    public void getListItems(String name) {
        //Fetching Data from FireStore
        db = FirebaseFirestore.getInstance();
        db.collection("Workout").document(name).collection("Workouts").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (queryDocumentSnapshots.isEmpty()) {
                    Log.d("WORKOUT_DB", "onSuccess: LIST EMPTY");

                } else {
                    WorkoutDatabase wdb = WorkoutDatabase.getInstance(RegisterActivity.this);
                    List<DocumentSnapshot> types = queryDocumentSnapshots.getDocuments();
                    ArrayList<DocumentSnapshot> mArrayList = new ArrayList<DocumentSnapshot>();
                    mArrayList.addAll(types);
                    for (int i = 0; i < mArrayList.size(); i++ ) {
                        Map<String, Object> mValue = mArrayList.get(i).getData();
                        ArrayList<Map<String, String>> mWork = (ArrayList<Map<String, String>>) mValue.get("workouts");
                        Workout work = new Workout();
                        for (Map<String, String> map: mWork) {
                            work.numberOfSets = map.get("numberOfSets");
                            work.workoutType = map.get("workoutType");
                            work.dateOfWorkout = map.get("dateOfWorkout");
                            work.durationOfWorkout = map.get("durationOfWorkout");
                            work.workoutName = map.get("workoutName");
                            work.repeatitions = map.get("repeatitions");
                            work.user = map.get("user");
                            wdb.workoutDoa().insertWorkout(work);
                        }
                        if (work.user.length() > 0) {
                            WorkoutNames names = new WorkoutNames();
                            names.user = work.user;
                            names.workoutName = work.workoutName;
                            wdb.workoutDoa().insertTable(names);
                        }
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i("ISSUE",""+e.getMessage());
            }
        });

        db.collection("Diet").document(name).collection("Diets").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (queryDocumentSnapshots.isEmpty()) {
                    Log.d("DIET_DB", "onSuccess: LIST EMPTY");

                } else {
                    DietDb ddb = DietDb.getInstance(RegisterActivity.this);
                    List<DocumentSnapshot> types = queryDocumentSnapshots.getDocuments();
                    ArrayList<DocumentSnapshot> mArrayList = new ArrayList<DocumentSnapshot>();
                    mArrayList.addAll(types);
                    for (int i = 0; i < mArrayList.size(); i++ ) {
                        Map<String, Object> mValue = mArrayList.get(i).getData();
                        ArrayList<Map<String, String>> mDiet = (ArrayList<Map<String, String>>) mValue.get("diets");
                        Diet diet = new Diet();
                        for (Map<String, String> map: mDiet) {
                            diet.dietName = map.get("dietName");
                            diet.dateOfDiet = map.get("dateOfDiet");
                            diet.foodName = map.get("foodName");
                            diet.user = map.get("user");
                            diet.calCount = map.get("calCount");
                            ddb.dieDao().insertDiet(diet);
                        }
                        if (diet.user.length() > 0) {
                            DietNames names = new DietNames();
                            names.dietName = diet.dietName;
                            names.user = diet.user;
                            ddb.dieDao().insertDietNames(names);
                        }
                    }

                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i("ISSUE",""+e.getMessage());
            }
        });
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(getApplicationContext(), Details.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
            }
        }, 2000);
    }

    public void Login(View view) {
       finish();
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
                .setContentText("Registration Successful");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setGroup(NOTIFICATION_CHANNEL_ID);
        }
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0, builder.build());
    }
}
