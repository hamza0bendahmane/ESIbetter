package com.example.esibetter.articles;

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


public class Articles extends Fragment {

    public Articles() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        ViewPager pages = view.findViewById(R.id.viewPager);
        TabLayout tabs = view.findViewById(R.id.tabLayout);
        pages.setAdapter(new MyTabPagerAdapter(getChildFragmentManager()));
        tabs.setupWithViewPager(pages);





    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.ideas_fragment_articles, container, false);
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
                    return "NEW";
                case 1:
                    return "TRENDING";
                case 2:
                    return "BEST OF";
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return 3;
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new news();
                case 1:
                    return new trending();
                case 2:
                    return new bestOf();
                default:
                    break;
            }
            return null;
        }
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onStop() {
        super.onStop();
    }
}