package com.example.workit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

public class EnterDetails2 extends Fragment {

    ImageView back;
    TextView tvDate;
    DatePickerDialog.OnDateSetListener setListener;
    Button next;
    EditText height;
    EditText weight;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater
                .inflate(R.layout.activity_enter_details2, container, false);

        back = rootView.findViewById(R.id.back);
        height = rootView.findViewById(R.id.edt_height);
        weight = rootView.findViewById(R.id.edt_weight);
        next = rootView.findViewById(R.id.nextBtn);

        tvDate = rootView.findViewById(R.id.tv_date);
        Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);
        tvDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth, setListener, year, month,
                        day);
                datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                datePickerDialog.show();
                select();
            }
        });
        setListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month + 1;
                String date = day + "/" + month + "/" + year;
                tvDate.setText(date);
            }
        };

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, new EnterDetails());
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ht = height.getText().toString();
                String wt = weight.getText().toString();
                String bday = tvDate.getText().toString();

                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("height", ht);
                editor.putString("weight", wt);
                editor.putString("dob", bday);
                editor.apply();

                if (ht.equals("") && wt.equals("") && bday.equals("Birthday")) {
                    Toast.makeText(getActivity(), "Please fill in all fields", Toast.LENGTH_LONG).show();
                    return;
                }
                else {
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.fragment_container, new EnterDetails3());
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }
            }
        });

        return rootView;
    }

    public void select() {
        next.setEnabled(true);
    }
}