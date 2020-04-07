package com.example.esibetter.notifications;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.esibetter.R;
import com.google.android.material.tabs.TabLayout;


public class Notifications extends Fragment {


    public Notifications() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.notifications_fragment, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        ViewPager pages = view.findViewById(R.id.viewPager);
        TabLayout tabs = view.findViewById(R.id.tabLayout);
        MyTabPagerAdapter tabPager = new Notifications.MyTabPagerAdapter(getChildFragmentManager());
        pages.setAdapter(tabPager);
        tabs.setupWithViewPager(pages);

    }


    public static class MyTabPagerAdapter extends FragmentStatePagerAdapter {
        MyTabPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "FOR YOU";
                case 1:
                    return "SHARED WITH YOU";
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return 2;
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new For_you();
                case 1:
                    return new Shared_with_you();
                default:
                    break;
            }
            return null;
        }
    }
}
