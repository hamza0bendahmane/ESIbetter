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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.wang.avi.AVLoadingIndicatorView;

public class splashScreen extends AppCompatActivity {
    private AVLoadingIndicatorView logo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        boolean introSeen = getSharedPreferences("seen_intro", MODE_PRIVATE).getBoolean("have_seen_intro", true);
        if (!introSeen) {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }
        setContentView(R.layout.general_splash_screen);
         logo = findViewById(R.id.logo);
        if (introSeen) {

            Animation animation = AnimationUtils.loadAnimation(getBaseContext(), R.anim.fadein);
            logo.startAnimation(animation);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                                Intent intent = new Intent(getApplicationContext(), login.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                    } else if (FirebaseAuth.getInstance().getCurrentUser() != null && !FirebaseAuth.getInstance().getCurrentUser().isEmailVerified()) {
                        Intent iintent = new Intent(getApplicationContext(), login.class);
                        iintent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(iintent);
                        finish();
                    } else if (FirebaseAuth.getInstance().getCurrentUser() != null && FirebaseAuth.getInstance().getCurrentUser().isEmailVerified()) {
                        Intent iintent = new Intent(getApplicationContext(), Profile_Activity.class);
                        iintent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(iintent);
                    }

                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });

        }


    }
}



