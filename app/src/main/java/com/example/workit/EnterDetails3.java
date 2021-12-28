package com.example.workit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class EnterDetails3 extends Fragment {

    public static final String TAG = "TAG";
    ImageView back;
    MaterialCardView option1;
    MaterialCardView option2;
    MaterialCardView option3;
    MaterialCardView option4;
    Button next;
    FirebaseFirestore fstore;
    String _first, _last, _dob, email, _sex, _height, _weight, _goal;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater
                .inflate(R.layout.activity_enter_details3, container, false);
        back = rootView.findViewById(R.id.back);
        option1 = rootView.findViewById(R.id.option1);
        option2 = rootView.findViewById(R.id.option2);
        option3 = rootView.findViewById(R.id.option3);
        option4 = rootView.findViewById(R.id.option4);
        next = rootView.findViewById(R.id.nextBtn);
        fstore = FirebaseFirestore.getInstance();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, new EnterDetails2());
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        option1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setBackground(option2, option3, option4, option1);
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("goal", "Lose weight");
                editor.apply();
                select();
            }
        });
        option2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setBackground(option1, option3, option4, option2);
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("goal", "Get Stronger");
                editor.apply();
                select();
            }
        });
        option3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setBackground(option2, option1, option4, option3);
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("goal", "General Fitness");
                editor.apply();
                select();
            }
        });
        option4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setBackground(option2, option3, option1, option4);
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("goal", "Be more active");
                editor.apply();
                select();
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                _first = preferences.getString("first", "John");
                _last = preferences.getString("last", "Doe");
                _dob = preferences.getString("dob", "");
                email = preferences.getString("Email", "");
                _sex = preferences.getString("sex", "");
                _height = preferences.getString("height", "5'11");
                _weight = preferences.getString("weight", "180");
                _goal = preferences.getString("goal", "");

                DocumentReference documentReference = fstore.collection("users").document(email);
                Map<String, Object> user = new HashMap<>();
                user.put("firstName", _first);
                user.put("lastName", _last);
                user.put("email", email);
                user.put("dob", _dob);
                user.put("sex", _sex);
                user.put("height", _height);
                user.put("weight", _weight);
                user.put("goal", _goal);
                user.put("picture", "android.resource://com.example.workit/drawable/mt_img");
                documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(TAG, "onSuccess, user profile is created for " + email);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: " + e.toString());
                    }
                });

                startActivity(new Intent(getActivity(), NavActivity.class));
            }
        });

        return rootView;
    }

    public void setBackground(MaterialCardView white1, MaterialCardView white2,
                              MaterialCardView white3, MaterialCardView change) {
        white1.setCardBackgroundColor(Color.WHITE);
        white2.setCardBackgroundColor(Color.WHITE);
        white3.setCardBackgroundColor(Color.WHITE);
        change.setCardBackgroundColor(Color.rgb(251, 145, 1));
    }

    public void select() {
        next.setEnabled(true);
    }
}