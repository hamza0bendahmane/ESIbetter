package com.example.esibetter.general;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.esibetter.R;
import com.example.esibetter.login;
import com.example.esibetter.notifications.Profile_Activity;
import com.google.firebase.auth.FirebaseAuth;

public class splashScreen extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        boolean introSeen = getSharedPreferences("seen_intro", MODE_PRIVATE).getBoolean("have_seen_intro", true);
        if (!introSeen) {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }
        setContentView(R.layout.general_splash_screen);
        if (introSeen) {
            if (isWorkingInternet()) {
                splash();
            } else {
                showAlertDialog(splashScreen.this, getString(R.string.internet_connection),
                        getString(R.string.u_dont_have_internet_connecion));
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(getApplicationContext(), favoris_offline.class));
                        finish();
                    }
                }, 1000);

            }
        }
    }

    public void splash() {
        final Thread timerTread = new Thread() {
            public void run() {
                try {

                    ImageView imageView = findViewById(R.id.splash_photo);
                    Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.frombottom);
                    imageView.setAnimation(animation);
                    sleep(500);
                    Intent intent;
                    if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                        intent = new Intent(getApplicationContext(), login.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    } else {
                        intent = new Intent(getApplicationContext(), Profile_Activity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                    finish();

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        timerTread.start();
    }

    public boolean isWorkingInternet() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getBaseContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo[] info = connectivityManager.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }

        }
        return false;
    }

    public void showAlertDialog(Context context, String title, String message) {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();

        // Setting Dialog Title
        alertDialog.setTitle(title);

        // Setting Dialog Message
        alertDialog.setMessage(message);

        // Setting alert dialog icon
        alertDialog.setIcon(android.R.drawable.stat_notify_error);

        // Setting OK Button


        // Showing Alert Message
        alertDialog.show();
    }


}