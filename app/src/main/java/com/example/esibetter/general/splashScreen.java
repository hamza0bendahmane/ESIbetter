package com.example.esibetter.general;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

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
                splash();

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




}