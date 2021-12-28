package com.example.workit;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.hardware.camera2.CaptureRequest;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.InputType;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.workit.romdb.Diet;
import com.example.workit.romdb.DietDb;
import com.example.workit.romdb.DietNames;
import com.example.workit.romdb.WorkoutNames;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.security.Key;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class design_diet extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
    Button Add,viewAll;
    ImageView cancel;
    private String m_Text = "";
    EditText foodName, calCount, time, date;
    SliderView sliderView;
    DietDb dietDB;
    List<Diet> object;
    FirebaseFirestore db;
    final Calendar myCalendar = Calendar.getInstance();
    int[] images = {
            R.drawable.work_one,
            R.drawable.work_two,
            R.drawable.work_three};

    int hour, min;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_design_diet);
        dietDB = DietDb.getInstance(design_diet.this);
        Add = findViewById(R.id.addMore);
        viewAll = findViewById(R.id.viewALl);
        foodName = findViewById(R.id.workoutTypeF);
        calCount = findViewById(R.id.numberofSets);
        time = findViewById(R.id.reps);
        date = findViewById(R.id.Date);
        date.setFocusable(false);
        cancel = findViewById(R.id.cancel);
        openNameDialog("");

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //Search();
        DatePickerDialog.OnDateSetListener date1 = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }

        };
        date.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(design_diet.this, date1, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        Add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enterDiet();

            }
        });

        viewAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(design_diet.this);
                String name = preferences.getString("Email", "");
                object = dietDB.dieDao().getDietForUserAndDateAndName(name,date12,m_Text);
                if (object.size() > 0) {
                    Log.e("DIET ALL", "Size: "+object.size());
                    Intent sendData = new Intent(design_diet.this, ViewAllWorkoutActivity.class);
                    sendData.putExtra("isWorkout", false);
                    updateLabel();
                    Bundle args = new Bundle();
                    args.putSerializable("ARRAYLIST",(Serializable)object);
                    args.putSerializable("Date", date.getText().toString());
                    sendData.putExtra("workoutListBundle", args);
                    startActivity(sendData);
                } else {
                    showToast("Please add some entries to your diet");
                }
            }
        });

        findViewById(R.id.detectBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startML();
            }
        });
        sliderView = findViewById(R.id.imgView);
        SliderAdapter sliderAdapter = new SliderAdapter(this,images);
        sliderView.setSliderAdapter(sliderAdapter);
        sliderView.setIndicatorAnimation(IndicatorAnimationType.WORM);
        sliderView.setSliderTransformAnimation(SliderAnimations.DEPTHTRANSFORMATION);
        sliderView.startAutoCycle();
//        openNameDialog("");
//        EditText time = findViewById(R.id.time);
        time.setFocusable(false);
        time.setClickable(true);
        TimePickerDialog.OnTimeSetListener mTimeSetListener =
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(android.widget.TimePicker view,
                                          int hourOfDay, int minute) {
                        Log.i("",""+hourOfDay+":"+minute);
                        time.setText(hourOfDay + ":" + minute);
                    }
                };

        time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                new TimePickerDialog(design_diet.this,mTimeSetListener,hour,minute,true).show();
            }
        });
    }

    private void updateLabel() {
        String myFormat = "MM-dd-yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        date.setText(sdf.format(myCalendar.getTime()));
    }

    private void startML() {

        Intent n = new Intent(getApplicationContext(),ClassifierActivity.class);
        startActivityForResult(n, 1);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if(resultCode == RESULT_OK) {
                String strEditText = data.getStringExtra("editTextValue");
                String detected = data.getStringExtra("Detected");
                if (detected.length() > 0) {
                    foodName.setText(detected);
                }
                Log.d("Dummy",strEditText);
            }
        }
    }

    public void updateFireStore(Diet diet, String User, String dietName) {
        List<Diet> diets = dietDB.dieDao().getDietForUserAndDateAndName(diet.user,diet.dateOfDiet,dietName);
        if (diets.size() > 0) {
//            Map<String, Object> Diets = new HashMap<>();
//            Diets.put("Workouts"+diet.dietName,diets);
            Map<String,String> dummy = new HashMap<>();
            dummy.put("Dummy", "Dummy");
            db
                    .collection("Diet")
                    .document(User).set(dummy);
            Map<String, Object> Diets = new HashMap<>();
            Diets.put("diets",diets);
            db
                    .collection("Diet")
                    .document(diet.user)
                    .collection("Diets")
                    .document(dietName)
                    .set(Diets).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    if (task.isSuccessful()) {

                        showToast("Your data is Synced");
                    } else {
                        showToast("Could not sync your data, we will try again later");
                    }
                }
            });
        } else {
            showToast("Something went wrong");
        }
    }

    private void clearFields(){
        foodName.setText("");
        calCount.setText("");
        time.setText("");
        date.setText("");
    }
    private boolean flag1 = true;
    private String date12 = "";
    private void enterDiet() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String name = preferences.getString("Email", "");
        if (name != "") {
            if (foodName.getText().toString().length() > 0) {
                if (calCount.getText().toString().length() > 0) {
                    if (time.getText().toString().length() > 0) {
                        if (date.getText().toString().length() > 0) {
                            Diet diet = new Diet();
                            date12 = date.getText().toString();
                            diet.dateOfDiet = date.getText().toString();
                            diet.calCount = calCount.getText().toString();
                            diet.time = time.getText().toString();
                            diet.foodName = foodName.getText().toString();
                            diet.user = name;
                            diet.dietName = m_Text;
                            dietDB.dieDao().insertDiet(diet);

                            if (flag1) {
                                flag1 = false;
                                DietNames dietName = new DietNames();
                                dietName.user = name;
                                dietName.dietName = m_Text;
                                dietDB.dieDao().insertDietNames(dietName);
                            }
                            updateFireStore(diet,diet.user,diet.dietName);
                            clearFields();
                        } else {
                            showToast("Please enter the Date");
                        }
                    } else {
                        showToast("Please enter the Time");
                    }
                } else {
                    showToast("Please enter the Calorie Count");
                }
            } else {
                showToast("Please enter the Food Name");
            }
        } else {
            showToast("Please try to login again");
        }

    }

    private void showToast(String message) {
        Toast.makeText(design_diet.this,message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

    }

    public void openNameDialog(String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        if (msg.isEmpty()) {
            builder.setTitle("Enter the name of your diet");
        } else {
            builder.setTitle(msg);
        }
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (input.getText().toString().isEmpty()){
                    dismiss();
                } else {
                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(design_diet.this);
                    String name = preferences.getString("Email", "");
                    m_Text = input.getText().toString();
                    List<DietNames> dietNames = dietDB.dieDao().getDietNames(name);
                    for(DietNames names : dietNames) {
                        if (names.user.equals(name)) {
                            if  (names.dietName.equals(m_Text)) {
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

    public void dismiss() {
        showToast("You need to enter a name for your diet");
        openNameDialog("");
    }

    @Override
    public void onStart() {
        super.onStart();
        db = FirebaseFirestore.getInstance();
        if (db == null) {
            showToast("Null DB");
        } else {
            showToast("FireStore Initialised");
            Search();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //remove redundant diet from local db
    }

    public void Search() {
        db.collection("Diet").get();
        db.collection("Diet").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    List<String> list = new ArrayList<>();
                    Log.i("SEARCH_DIET",""+task.getResult());
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        list.add(document.getId());
                        Log.i("SEARCH_DIET",""+list.size());
                        for (String name:list ) {
                            List<String> dietsList = new ArrayList<>();
                            dietsList.add(db.collection("Diets").document(name).collection("Diets").getId());
                            Log.i("SEARCH_DIET",""+dietsList.size());
//                            for (String diet_name:dietsList) {
//                                db.collection("Diets").document(name).collection("Diets").whereGreaterThanOrEqualTo("diet_name", "diet");
//                            }
                        }
                    }
                    Log.d("TAG", list.toString());
                } else {
                    Log.d("TAG", "Error getting documents: ", task.getException());
                }
            }
        });
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

    }
}