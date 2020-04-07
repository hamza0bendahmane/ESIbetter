package com.example.esibetter.general;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.esibetter.R;
import com.example.esibetter.login;
import com.example.esibetter.notifications.Profile_Activity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;

public class SettingsActivity extends AppCompatActivity {
    LinearLayout edit_profile;
    private Switch notification;
    public static final String NOTIFICATION = "notification";
    private boolean notificationState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.general_activity_settings);
        // initializing vars ....
        notification = findViewById(R.id.setNotification);
        edit_profile = findViewById(R.id.edit_profi);


        // opening user profile ....
        edit_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), User_Profile.class));
                finish();
            }
        });
        //function for notifications ....

        notification.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                saveData();
                String not = "Notifications ";
                if (notificationState)
                    not += "are Enabled";
                else
                    not += "are Disabled";
                Toast.makeText(SettingsActivity.this, not,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void saveData() {
        getSharedPreferences(NOTIFICATION, MODE_PRIVATE).edit().
                putBoolean(NOTIFICATION, notification.isChecked()).apply();
        notificationState = getSharedPreferences(NOTIFICATION, MODE_PRIVATE).getBoolean(NOTIFICATION, false);

    }




    public void reset_password(View view) {
        // password reset

        AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this).setTitle("Reset Password").
                setMessage("Do you really want to log out and reset password ?")
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).setPositiveButton(" Reset Password ", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        updatePassword();
                    }
                });
        builder.show();
    }

    public void updatePassword() {
        final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.sendPasswordResetEmail(firebaseAuth.getCurrentUser().getEmail()).
                addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Snackbar.make(findViewById(R.id.gender), "we have sent you an email" +
                                    " to reset password, check your inbox ", Snackbar.LENGTH_LONG).show();
                            firebaseAuth.signOut();
                            startActivity(new Intent(SettingsActivity.this, login.class));
                            finish();
                        }
                    }
                });

    }

    public void signOutAndgoTologin(View item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this).setTitle("Sign out").setMessage("Do you really want to log out ?")
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).setPositiveButton("log out", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FirebaseAuth.getInstance().signOut();
                        startActivity(new Intent(SettingsActivity.this, login.class));
                    }
                });
        builder.show();
    }

    public void changeLanguage(View view) {
        //TODO : WALID BOUSSAADA ..
        Toast.makeText(this, "for walid", Toast.LENGTH_SHORT).show();


    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(), Profile_Activity.class));
        finish();
    }
}
