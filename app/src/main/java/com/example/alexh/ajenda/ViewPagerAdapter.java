package com.example.alexh.ajenda;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

public class ViewPagerAdapter extends FragmentPagerAdapter {
    private static int NUM_ITEMS = 2;

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        switch(i) {
            case 0:
                //return contacts
                return ContactList.newInstance("","");
            case 1:
                //return meetings
                return Meetings.newInstance("","");
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return NUM_ITEMS;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int i) {
        if(i == 0) {
            return "Contacts";
        } else {
            return "Meetings";
        }
    }
}
