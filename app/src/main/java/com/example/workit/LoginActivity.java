package com.example.workit;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.preference.PreferenceManager;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
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

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    EditText FullName, mEmail, mPassword;
    Button loginBtn, registerBtn;
    FirebaseAuth fAuth;
    FirebaseFirestore db;
    ViewPager viewPager;
    float v = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        viewPager = findViewById(R.id.view_pager);
        FullName = findViewById(R.id.fullname);
        mEmail = findViewById(R.id.email);
        mPassword = findViewById(R.id.pass);
        loginBtn = findViewById(R.id.login_button);
        fAuth = FirebaseAuth.getInstance();
        findViewById(R.id.forget_pass).setClickable(true);
        findViewById(R.id.forget_pass).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openNameDialog("Please enter the your registered email ID");
            }
        });

        findViewById(R.id.guest).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean("Guest",true);
                editor.putString("first","Guest");
                editor.putString("profile_img", "android.resource://com.example.workit/drawable/mt_img");
                editor.apply();
                startActivity(new Intent(getApplicationContext(), NavActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
            }
        });
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = mEmail.getText().toString().trim();
                String password = mPassword.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    mEmail.setError("Email is required.");
                    return;
                }

                if (TextUtils.isEmpty(email)) {
                    mPassword.setError("Password is required.");
                    return;
                }

                if (password.length() < 6) {
                    mPassword.setError("Password must be >=6 characters.");
                    return;
                }

                fAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            db = FirebaseFirestore.getInstance();
                            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putString("Email", fAuth.getCurrentUser().getEmail());
                            editor.apply();
                            getProfileData(fAuth.getCurrentUser().getEmail());
                            Toast.makeText(LoginActivity.this, "Logged in Successfully.", Toast.LENGTH_SHORT).show();
                            getListItems(fAuth.getCurrentUser().getEmail());
                            createNotificationChannel();
                            makeNotification();
                        } else {
                            Toast.makeText(LoginActivity.this, "Error!" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    private String m_Text;

    public void dismiss() {
        showToast("You need to enter a valid EmailID");
    }

    private void showToast(String message) {
        Toast.makeText(LoginActivity.this,message, Toast.LENGTH_LONG).show();
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
                if (input.getText().toString().isEmpty()){
                    dismiss();
                } else {
                    m_Text = input.getText().toString();
                    sendPasswordResetLink(m_Text);
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
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

    private void sendPasswordResetLink(String email) {
        if (isValidEmailAddress(email)) {
            fAuth.sendPasswordResetEmail(email)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                showToast("Password reset email has been sent to you.");
                                Log.d("FAUTH", "Email sent.");
                            } else {
                                if (!task.isSuccessful()) {
                                    showToast(task.getException().getMessage());
                                }
                            }
                        }
                    });
        } else {
            dismiss();
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        FirebaseApp.initializeApp(this);
    }

    public void signup(View view) {
        startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
    }

    private void getProfileData(String name) {
        if (db != null) {
            db.collection("users").document(name).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    Log.i("Profile_tag", "onSuccess: "+documentSnapshot.get("email"));
                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("height", documentSnapshot.get("height").toString());
                    editor.putString("sex", documentSnapshot.get("sex").toString());
                    editor.putString("Email", documentSnapshot.get("email").toString());
                    editor.putString("first", documentSnapshot.get("firstName").toString());
                    editor.putString("dob", documentSnapshot.get("dob").toString());
                    editor.putString("goal", documentSnapshot.get("goal").toString());
                    editor.putString("last", documentSnapshot.get("lastName").toString());
                    editor.putString("weight", documentSnapshot.get("weight").toString());
                    editor.putString("profile_img", documentSnapshot.get("picture").toString());
                    editor.apply();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });
        }
    }

    private void getListItems(String name) {
        //Fetching Data from FireStore
        db.collection("Workout").document(name).collection("Workouts").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (queryDocumentSnapshots.isEmpty()) {
                    Log.d("WORKOUT_DB", "onSuccess: LIST EMPTY");

                } else {
                    WorkoutDatabase wdb = WorkoutDatabase.getInstance(LoginActivity.this);
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

            }
        });

        db.collection("Diet").document(name).collection("Diets").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (queryDocumentSnapshots.isEmpty()) {
                    Log.d("DIET_DB", "onSuccess: LIST EMPTY");

                } else {
                    DietDb ddb = DietDb.getInstance(LoginActivity.this);
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

            }
        });
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(getApplicationContext(), NavActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
            }
        }, 2000);
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
                .setContentText("Login Successful");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setGroup(NOTIFICATION_CHANNEL_ID);
        }
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0, builder.build());
    }
}
