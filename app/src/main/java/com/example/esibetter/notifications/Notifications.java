package com.example.esibetter.notifications;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.esibetter.R;
import com.google.android.material.tabs.TabLayout;


public class Notifications extends AppCompatActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.notifications_fragment);
        ViewPager pages = findViewById(R.id.viewPager);
        TabLayout tabs = findViewById(R.id.tabLayout);
        MyTabPagerAdapter tabPager = new Notifications.MyTabPagerAdapter(getSupportFragmentManager());
        pages.setAdapter(tabPager);
        tabs.setupWithViewPager(pages);


    }

    public class MyTabPagerAdapter extends FragmentStatePagerAdapter {
        MyTabPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getString(R.string.notifications_for_you);
                case 1:
                    return getString(R.string.notifications_shared_with_you);
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
