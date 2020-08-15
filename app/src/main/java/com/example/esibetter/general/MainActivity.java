package com.example.esibetter.general;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.esibetter.R;
import com.example.esibetter.login;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private final int TOTAL_PAGES = 4;
    private ViewPager mPager;
    private PagerAdapter mPagerAdapter;

    @Override
    //intro_1
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLocal();
        setContentView(R.layout.general_activity_main);
        // intro


        final Typeface text_font = Typeface.createFromAsset(getAssets(), "font/CeraPro-Regular.ttf");

        mPager = findViewById(R.id.intro_pager);
        mPagerAdapter = new IntroPagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        final String[] titles = getResources().getStringArray(R.array.titles);
        final ImageView introIndicator1 = findViewById(R.id.intro1);
        final ImageView introIndicator2 = findViewById(R.id.intro2);
        final ImageView introIndicator3 = findViewById(R.id.intro3);
        final ImageView introIndicator4 = findViewById(R.id.intro4);
        final Button btnIntroSkip = findViewById(R.id.intro_skip);
        final ImageView goback = findViewById(R.id.go_back);

        mPager = findViewById(R.id.intro_pager);
        mPagerAdapter = new IntroPagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                btnIntroSkip.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        gotologin();
                    }
                });
                if (position == 0) {
                    goback.setVisibility(View.GONE);
                    btnIntroSkip.setVisibility(View.VISIBLE);
                    introIndicator1.setImageResource(R.drawable.dote_selected);
                    introIndicator2.setImageResource(R.drawable.dote_default);
                    introIndicator3.setImageResource(R.drawable.dote_default);
                    introIndicator3.setImageResource(R.drawable.dote_default);
                    // text title
                    final TextView slideTitle1 = findViewById(R.id.title_slide1);
                    slideTitle1.setText(titles[position]);

                    slideTitle1.setTypeface(text_font);
                    final Button seemore = findViewById(R.id.seemore1);
                    seemore.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mPager.setCurrentItem(mPager.getCurrentItem() + 1);
                        }
                    });


                } else if (position == 1) {
                    introIndicator1.setImageResource(R.drawable.dote_default);
                    introIndicator2.setImageResource(R.drawable.dote_selected);
                    introIndicator3.setImageResource(R.drawable.dote_default);
                    introIndicator4.setImageResource(R.drawable.dote_default);
                    // text title
                    TextView slideTitle2 = findViewById(R.id.title_slide2);
                    slideTitle2.setText(titles[position]);
                    slideTitle2.setTypeface(text_font);
                    goback.setVisibility(View.VISIBLE);
                    btnIntroSkip.setVisibility(View.VISIBLE);


                } else if (position == 2) {
                    introIndicator1.setImageResource(R.drawable.dote_default);
                    introIndicator2.setImageResource(R.drawable.dote_default);
                    introIndicator3.setImageResource(R.drawable.dote_selected);
                    introIndicator4.setImageResource(R.drawable.dote_default);
                    // text title
                    TextView slideTitle3 = findViewById(R.id.title_slide3);
                    slideTitle3.setText(titles[position]);
                    slideTitle3.setTypeface(text_font);
                    goback.setVisibility(View.VISIBLE);
                    btnIntroSkip.setVisibility(View.VISIBLE);

                } else if (position == 3) {
                    btnIntroSkip.setVisibility(View.GONE);
                    goback.setVisibility(View.VISIBLE);
                    introIndicator1.setImageResource(R.drawable.dote_default);
                    introIndicator2.setImageResource(R.drawable.dote_default);
                    introIndicator3.setImageResource(R.drawable.dote_default);
                    introIndicator4.setImageResource(R.drawable.dote_selected);
                    // text title
                    TextView slideTitle4 = findViewById(R.id.title_slide4);
                    slideTitle4.setText(titles[position]);
                    slideTitle4.setTypeface(text_font);


                }
            }

            @Override
            public void onPageSelected(int position) {
                btnIntroSkip.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        gotologin();
                    }
                });
                //text ;;;title

                goback.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mPager.setCurrentItem(mPager.getCurrentItem() - 1);

                    }
                });

                if (position == 0) {

                    final TextView slideTitle1 = findViewById(R.id.title_slide1);
                    slideTitle1.setText(titles[position]);
                    slideTitle1.setTypeface(text_font);

                } else if (position == 1) {

                    // text title
                    final Button seemore = findViewById(R.id.seemore2);
                    seemore.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mPager.setCurrentItem(mPager.getCurrentItem() + 1);
                        }
                    });
                    TextView slideTitle2 = findViewById(R.id.title_slide2);
                    slideTitle2.setText(titles[position]);
                    slideTitle2.setTypeface(text_font);


                } else if (position == 2) {

                    // text title
                    final Button seemore = findViewById(R.id.seemore3);
                    seemore.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mPager.setCurrentItem(mPager.getCurrentItem() + 1);
                        }
                    });
                    TextView slideTitle3 = findViewById(R.id.title_slide3);
                    slideTitle3.setText(titles[position]);
                    slideTitle3.setTypeface(text_font);

                } else if (position == 3) {

                    // text title
                    final Button seemore = findViewById(R.id.joinus);
                    seemore.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            gotologin();
                        }
                    });
                    TextView slideTitle4 = findViewById(R.id.title_slide4);
                    slideTitle4.setText(titles[position]);
                    slideTitle4.setTypeface(text_font);

                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    private void gotologin() {
        getSharedPreferences("seen_intro", MODE_PRIVATE).edit().
                putBoolean("have_seen_intro", true).apply();
        Intent intent = new Intent(getApplicationContext(), login.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        if (mPager.getCurrentItem() == 0) {
            super.onBackPressed();
        } else {
            mPager.setCurrentItem(mPager.getCurrentItem() - 1);
        }
    }


    private class IntroPagerAdapter extends FragmentStatePagerAdapter {
        public IntroPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new intro_1();
                case 1:
                    return new intro_2();
                case 2:
                    return new intro_3();
                case 3:
                    return new intro_4();
                default:
                    break;
            }
            return null;
        }

        @Override
        public int getCount() {
            return TOTAL_PAGES;
        }
    }

       /* ImageView imageView =findViewById(R.id.imageView);
        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.frombottom);
    imageView.setAnimation(animation);*/


    private void setLocale(String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);

        Configuration configuration = new Configuration();
        configuration.locale = locale;


        getBaseContext().getResources().
                updateConfiguration(configuration, getBaseContext().getResources().getDisplayMetrics());
        //save data in shared preferences
        SharedPreferences.Editor editor = getSharedPreferences("Settings", MODE_PRIVATE).edit();
        editor.putString("My_lang", lang);
        editor.apply();
    }


    public void loadLocal() {
        SharedPreferences prefs = getSharedPreferences("Settings", Activity.MODE_PRIVATE);
        String Language = prefs.getString("My_lang", "");
        setLocale(Language);
    }



}