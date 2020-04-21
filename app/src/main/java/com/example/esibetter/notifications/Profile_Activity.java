package com.example.esibetter.notifications;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.esibetter.R;
import com.example.esibetter.articles.Articles;
import com.example.esibetter.community.Community;
import com.example.esibetter.courses.Courses;
import com.example.esibetter.general.HelpActivity;
import com.example.esibetter.general.SettingsActivity;
import com.example.esibetter.general.about_us;
import com.example.esibetter.general.favoris_offline;
import com.example.esibetter.login;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class Profile_Activity extends AppCompatActivity {

    public static int TOTAL_PAGES = 3;
    public static StorageReference images_url;
    public static TextView user_name, status;
    public static CircleImageView image_user;
    public static ImageView logo;
    public static ViewPager mPager;
    public static PagerAdapter mPagerAdapter;
    public static DrawerLayout drawer;
    public static AppBarConfiguration mAppBarConfiguration;
    public static FirebaseAuth firebaseAuth;
    public static BottomNavigationView bottomNavigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String l = loadLocal();

        setContentView(R.layout.general_activity_profile);


        // ini vars ...
        drawer = findViewById(R.id.drawer_layout);
        logo = findViewById(R.id.logo);
        mPager = findViewById(R.id.bottom_pager);
        final NavigationView navigationView = findViewById(R.id.nav_view);
        Toolbar toolbar = findViewById(R.id.toolbar);
        bottomNavigationView = findViewById(R.id.bottom_nav);
        firebaseAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = firebaseAuth.getCurrentUser();
        images_url = FirebaseStorage.getInstance().getReference("Images/" + user.getUid());
        setSupportActionBar(toolbar);
        mPagerAdapter = new IntroPagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);

        // actions ...



        // logo ---> Navigation view ..
        logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.openDrawer(GravityCompat.START);
            }
        });
        // on navigation view item clicked
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });
        //open the Bar ...
        mAppBarConfiguration = new AppBarConfiguration.Builder(R.menu.activity_profil_drawer)
                .setDrawerLayout(drawer)
                .build();
        //  bottom navi ++ fragments show + mani ...


        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                bottomNavigationView.getMenu().getItem(position).setChecked(true);

            }

            @Override
            public void onPageSelected(int position) {

                bottomNavigationView.getMenu().getItem(position).setChecked(true);

            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }

        });
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {

                    case R.id.courses:
                        mPager.setCurrentItem(0);
                        break;

                    case R.id.community:
                        mPager.setCurrentItem(1);
                        break;

                    case R.id.articles:
                        mPager.setCurrentItem(2);
                        break;
                    default:
                        return false;
                }
                return true;
            }
        });
    }


    private void updateUI(final FirebaseUser user) {

        if (user != null) {

            final FirebaseFirestore database = FirebaseFirestore.getInstance();
            database.collection("users").document(user.getUid()).get().
                    addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful() && task.getResult().getData() != null) {
                                status = findViewById(R.id.status_user);
                                image_user = findViewById(R.id.image_user);
                                user_name = findViewById(R.id.name_user);

                                //............ collecting then saving user data
                                user_name.setText(task.getResult().get("name").toString());
                                status.setText(task.getResult().get("status").toString());
                                // saving data locally ....

                                images_url.child("/prof_pic.png").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        Log.d("uriuri", "onSuccess: " + uri);
                                        Glide.with(getApplicationContext()).asBitmap()
                                                .diskCacheStrategy(DiskCacheStrategy.ALL).load(uri).into(image_user);
                                    }
                                });

                                getSharedPreferences("prof", 0).edit().
                                        putString("name", task.getResult().get("name").toString()).
                                        putString("status", task.getResult().get("status").toString()).
                                        putString("wilaya", task.getResult().get("wilaya").toString()).
                                        putString("birthday", task.getResult().get("birthday").toString()).
                                        putBoolean("gender", (boolean) task.getResult().get("isMale")).apply();

                            }

                        }
                    });


        } else {
            startActivity(new Intent(Profile_Activity.this, login.class));
            finish();
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        // Check if user is signed in (non-null) and update UI accordingly.
        updateUI(FirebaseAuth.getInstance().getCurrentUser());
        // TODO : Anoura Manaa
        Toast.makeText(this, "Animations FOR Anouar", Toast.LENGTH_SHORT).show();

        // add  animations for the views that u think that they need animations ..
        // just to say that u are not going to put animations here .. this is just to drive u carsy
        // hell yeah when you are done delete the toast
    }


    public void signOutAndgoTologin(MenuItem item) {
        drawer.closeDrawer(GravityCompat.START);
        AlertDialog.Builder builder = new AlertDialog.Builder(Profile_Activity.this).setTitle(getString(R.string.sign_out)).setMessage(getString(R.string.u_wnt_logout))
                .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).setPositiveButton(getString(R.string.log_out), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        firebaseAuth.signOut();
                        startActivity(new Intent(Profile_Activity.this, login.class));
                    }
                });
        builder.show();
    }

    public void openSettingsLayout(MenuItem item) {
        drawer.closeDrawer(GravityCompat.START);
        startActivity(new Intent(Profile_Activity.this, SettingsActivity.class));
        finish();

    }

    public void openHelpLayout(MenuItem item) {
        drawer.closeDrawer(GravityCompat.START);
        Intent ii = new Intent(Profile_Activity.this, HelpActivity.class);
        startActivity(ii);
        finish();

    }

    public void openAboutUs(MenuItem item) {
        drawer.closeDrawer(GravityCompat.START);
        startActivity(new Intent(Profile_Activity.this, about_us.class));
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


    public void openFavoris(MenuItem item) {
        startActivity(new Intent(getApplicationContext(), favoris_offline.class));
        finish();
    }

    public void openNotificationsLayout(View view) {
        startActivity(new Intent(getApplicationContext(), Notifications.class));
    }

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

    @Override
    protected void onStart() {
        super.onStart();
       /* boolean em_ver = FirebaseAuth.getInstance().getCurrentUser().isEmailVerified();
        boolean userExist = FirebaseAuth.getInstance().getUid() != null && FirebaseAuth.getInstance().getCurrentUser() != null;

        if (userExist)
            if (em_ver) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
            }*/
    }

    public String loadLocal() {

        SharedPreferences prefs = getSharedPreferences("Settings", Activity.MODE_PRIVATE);
        String Language = prefs.getString("My_lang", "");
        setLocale(Language);
        return Language;
    }

    private class IntroPagerAdapter extends FragmentStatePagerAdapter {
        public IntroPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {

                case 0:
                    return new Courses();
                case 1:
                    return new Community();
                case 2:
                    return new Articles();
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


}