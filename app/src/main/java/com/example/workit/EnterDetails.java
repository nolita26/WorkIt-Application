package com.example.workit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.card.MaterialCardView;

public class EnterDetails extends Fragment {

    MaterialCardView card_none;
    MaterialCardView male;
    MaterialCardView female;
    Button next;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater
                .inflate(R.layout.activity_enter_details, container, false);
        card_none = rootView.findViewById(R.id.none);
        male = rootView.findViewById(R.id.male);
        female = rootView.findViewById(R.id.female);
        next = rootView.findViewById(R.id.nextBtn);

        female.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setBackground(card_none, male, female);
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("sex", "F");
                editor.apply();
                select();
            }
        });

        male.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setBackground(card_none, female, male);
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("sex", "M");
                editor.apply();
                select();
            }
        });

        card_none.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setBackground(male, female, card_none);
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("sex", "Prefer not to say");
                editor.apply();
                select();
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, new EnterDetails2());
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        return rootView;
    }

    public void setBackground(MaterialCardView white1, MaterialCardView white2, MaterialCardView change) {
        white1.setCardBackgroundColor(Color.WHITE);
        white2.setCardBackgroundColor(Color.WHITE);
        change.setCardBackgroundColor(Color.rgb(251, 145, 1));
    }

    public void select() {
        next.setEnabled(true);
    }
}