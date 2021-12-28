package com.example.workit;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.text.InputType;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.workit.romdb.Workout;
import com.example.workit.romdb.WorkoutDatabase;
import com.example.workit.romdb.WorkoutNames;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class AddWorkoutSchedule extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    Button Add, viewAll;
    ImageView cancel;
    WorkoutDatabase wdb;
    SliderView sliderView;
    FirebaseFirestore db;
    final Calendar myCalendar = Calendar.getInstance();
    private String m_Text = "";
    EditText workoutDateTV, workoutRespTV,workoutSetTV,workoutDurTV;
    TextView workoutTV;
    int[] images = {
            R.drawable.work_one,
            R.drawable.work_two,
            R.drawable.work_three};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_workout_schedule);
        openNameDialog("Enter the name of your workout");
        wdb = WorkoutDatabase.getInstance(this);
        Add = findViewById(R.id.addMore);
        workoutTV = findViewById(R.id.workoutTypeF);
        workoutTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.app.AlertDialog.Builder mBuilder = new android.app.AlertDialog.Builder(AddWorkoutSchedule.this);
                View mView = getLayoutInflater().inflate(R.layout.dialog_spinner, null);
                mBuilder.setTitle("Select Workout Type");
                Spinner spinner = (Spinner) mView.findViewById(R.id.spinner);
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(AddWorkoutSchedule.this,
                        android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.workout_type));
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(adapter);

                mBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        workoutTV.setText(spinner.getSelectedItem().toString());
                    }
                });

                mBuilder.setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                mBuilder.setView(mView);
                android.app.AlertDialog dialog = mBuilder.create();
                dialog.show();
            }
        });

        workoutDateTV = (EditText) findViewById(R.id.Date);
        workoutDateTV.setFocusable(false);
        workoutDateTV.setClickable(true);
        cancel = findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }

        };

        workoutDateTV.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(AddWorkoutSchedule.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        workoutRespTV = (EditText) findViewById(R.id.reps);
        workoutSetTV = (EditText) findViewById(R.id.numberofSets);
        workoutDurTV = (EditText) findViewById(R.id.durations);
//        workoutDateTV.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                showDatePickerDialog(v);
//            }
//        });
        viewAll = findViewById(R.id.viewALl);
        viewAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                String name = preferences.getString("Email", "");
                List<Workout> workOutData = wdb.workoutDoa().getWorkoutForUserAndDate(name,queryDate);
                if (workOutData.size() > 0) {
                    Log.e("VIEW ALL", "Size"+workOutData.size());
                    Intent sendData = new Intent(AddWorkoutSchedule.this, ViewAllWorkoutActivity.class);
                    sendData.putExtra("isWorkout", true);
                    updateLabel();
                    Bundle args = new Bundle();
                    args.putSerializable("ARRAYLIST",(Serializable)workOutData);
                    args.putSerializable("Date", workoutDateTV.getText().toString());
                    sendData.putExtra("workoutListBundle", args);
                    startActivity(sendData);
                } else {
                    showToast(""+m_Text+" contains no workouts");
                }
            }
        });
        Add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                enterWorkout(workoutTV.getText().toString(),
                        workoutDateTV.getText().toString(),
                        workoutDurTV.getText().toString(),
                        workoutRespTV.getText().toString(),
                        workoutSetTV.getText().toString()
                        );
            }
        });
        sliderView = findViewById(R.id.imgView);
        SliderAdapter sliderAdapter = new SliderAdapter(this,images);
        sliderView.setSliderAdapter(sliderAdapter);
        sliderView.setIndicatorAnimation(IndicatorAnimationType.WORM);
        sliderView.setSliderTransformAnimation(SliderAnimations.DEPTHTRANSFORMATION);
        sliderView.startAutoCycle();
    }

    private void updateLabel() {
        String myFormat = "MM-dd-yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        workoutDateTV.setText(sdf.format(myCalendar.getTime()));
    }

    private String queryDate;
    private boolean flag1 = true;
    private boolean enterWorkout(String workoutType,String workoutDate, String workoutDuration, String workOutReps, String workoutSets) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String name = preferences.getString("Email", "");
        if (name != "") {
            if (workoutType.length() > 0) {
                if (workoutDate.length() > 0) {
                    if (workoutDuration.length() > 0) {
                        if (workOutReps.length() > 0 ) {
                            if (workoutSets.length() > 0) {
                                Workout work = new Workout();
                                work.dateOfWorkout = workoutDate;
                                queryDate = workoutDate;
                                work.durationOfWorkout = workoutDuration;
                                work.workoutType = workoutType;
                                work.numberOfSets = workoutSets;
                                work.repeatitions = workOutReps;
                                work.user = name;
                                work.workoutName = m_Text;
                                wdb.workoutDoa().insertWorkout(work);
                                if (flag1) {
                                    flag1 = false;
                                    WorkoutNames workoutNames1 = new WorkoutNames();
                                    workoutNames1.user = name;
                                    workoutNames1.workoutName = m_Text;
                                    wdb.workoutDoa().insertTable(workoutNames1);
                                }
                                clearFields();
                                upload(work,m_Text);
                            } else {
                                showToast("Please enter the sets");
                            }
                        } else {
                            showToast("Please enter the Repetitions");
                        }
                    } else {
                        showToast("Please enter the name of the exercise");
                    }
                } else {
                    showToast("Please enter the Date");
                }
            } else {
                showToast("Please enter the Workout Type(Cardio, Weight Training etc)");
            }
        } else {
            showToast("Please login again");
        }
        return false;
    }

    private void clearFields() {
        workoutTV.setText("");
        workoutDateTV.setText("");
        workoutDurTV.setText("");
        workoutRespTV.setText("");
        workoutSetTV.setText("");
    }

    private void showToast(String message) {
        Toast.makeText(AddWorkoutSchedule.this,message, Toast.LENGTH_SHORT).show();
    }

    private void upload(Workout workout, String workoutName) {
        List<Workout> workouts = wdb.workoutDoa().getWorkoutForNameAndDate(workoutName,workout.dateOfWorkout,workout.user);
//        if (db != null && workouts.size() > 0) {
//           if (db.collection("Workout")
//                   .document(workout.user)
//                   .collection("Workouts")
//                   .document(workoutName)
//                   .get()
//                   .toString().length() > 0) {
//               workoutName = workoutName + " 2 ";
//           }

        String string = workout.user;
        Map<String,String> dummy = new HashMap<>();
        dummy.put("Dummy", "Dummy");
        db
                .collection("Workout")
                .document(string).set(dummy);
        Map<String, Object> work = new HashMap<>();
        work.put("workouts",workouts);
        db
                .collection("Workout")
                .document(workout.user)
                .collection("Workouts")
                .document(workoutName)
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



//            db.collection("Workout").document(workoutName).set(work).addOnCompleteListener(new OnCompleteListener<Void>() {
//                @Override
//                public void onComplete(@NonNull Task<Void> task) {
//                    if (task.isSuccessful()) {
//                        showToast("Your data is Synced");
//                    } else {
//                        showToast("Could not sync your data, we will try again later");
//                    }
//                }
//            });
//        } else {
//            if (workouts.size() == 0) {
//                showToast("Please try again later");
//            } else {
//                showToast("Cannot Sync your data");
//            }
//
//        }
    }

    @Override
    public void onStart() {
        super.onStart();
        db = FirebaseFirestore.getInstance();
        if (db == null) {
            showToast("Null DB");
        } else {
            showToast("FireStore Initialised");
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Log.i("Date",""+year+""+month+""+dayOfMonth);
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void dismiss() {
        showToast("You need to enter a name for the workout");
        openNameDialog("Please enter the name and try again");
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
                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(AddWorkoutSchedule.this);
                    String name = preferences.getString("Email", "");
//                    if (db.collection("Workout")
//                            .document(name)
//                            .collection("Workouts")
//                            .document(m_Text)
//                            .get()
//                            .toString().length() > 0) {
//                        openNameDialog("Try a different name");
//                    }
                   List<WorkoutNames> workoutNames = wdb.workoutDoa().getWorkoutNames();
                   for(WorkoutNames names : workoutNames) {
                       Log.i("NAMES", ""+names.workoutName+" "+m_Text);
                       if (names.user.equals(name)) {
                           if  (names.workoutName.equals(m_Text)) {
                               openNameDialog("Try a different name");
                           }
                       } else {
                           Log.i("NAMES", "FAILED");
                       }
                   }

                }

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                finish();
            }
        });
        builder.show();
    }
}

