package com.example.esibetter.articles;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;


public class Articles extends Fragment {
    public static onFabClicked mlistener;
    public static boolean isopen = false;
    public static FloatingActionButton fabArticle, fabInitiative, fab;
    public Articles() {
        // Required empty public constructor
    }

    public static void setonFabClicked(onFabClicked listener) {
        mlistener = listener;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.ideas_fragment_articles, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        ViewPager pages = view.findViewById(R.id.viewPager);
        TabLayout tabs = view.findViewById(R.id.tabLayout);
        pages.setAdapter(new MyTabPagerAdapter(getChildFragmentManager()));
        tabs.setupWithViewPager(pages);
        fab = view.findViewById(R.id.fab);
        fabArticle = view.findViewById(R.id.fab_article);
        fabInitiative = view.findViewById(R.id.fab_event);
        fabArticle.setVisibility(View.GONE);
        fabInitiative.setVisibility(View.GONE);
        // fabs ..manipulation ...
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isopen) {
                    fabArticle.setVisibility(View.GONE);
                    fabInitiative.setVisibility(View.GONE);
                    fab.animate().setDuration(200)
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    super.onAnimationEnd(animation);
                                }
                            })
                            .rotation(0f);
                    isopen = false;
                } else {

                    fabArticle.setVisibility(View.VISIBLE);
                    fabInitiative.setVisibility(View.VISIBLE);
                    fab.animate().setDuration(200)
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    super.onAnimationEnd(animation);
                                }
                            })
                            .rotation(135f);
                    isopen = true;
                }

            }
        });


        // fab Action ...
        fabArticle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mlistener != null) {
                    mlistener.FabClicked();
                }
            }
        });
        fabInitiative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mlistener != null) {
                    mlistener.FabEventClicked();
                }
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onStop() {
        super.onStop();
    }

    public interface onFabClicked {
        void FabClicked();

        void FabEventClicked();
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
                    return getString(R.string.news);
                case 1:
                    return getString(R.string.trending);
                case 2:
                    return getString(R.string.bestof);
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


}