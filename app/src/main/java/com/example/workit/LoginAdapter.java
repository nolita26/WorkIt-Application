package com.example.workit;

import android.content.Context;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.PagerAdapter;

public class LoginAdapter extends PagerAdapter {         //extends FragmentPagerAdapter

    private Context context;
//    int totalTabs;

    public LoginAdapter(FragmentManager fm, Context context, int totalTabs){
        super();            //super(fm)
        this.context = context;
//        this.totalTabs = totalTabs;

    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return false;
    }

//    @Override
//    public int getCount(){
//        return totalTabs;
//    }
//
//    public Fragment getItem(int position){
//        switch (position){
//            case 0:
//                LoginTabFragment loginTabFragment = new LoginTabFragment();
//                return loginTabFragment;
//            case 1:
//                SignupTabFragment signupTabFragment = new SignupTabFragment();
//                return signupTabFragment;
//            default:
//                return null;
//
//        }
//    }
}
