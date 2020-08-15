package com.example.esibetter.general;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Locale;

public class SettingsActivity extends AppCompatActivity {
    LinearLayout edit_profile;
    private Switch notification;
    public final String NOTIFICATION = "notification";
    private boolean notificationState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLocal();
        setContentView(R.layout.general_activity_settings);
        // initializing vars ....
        notification = findViewById(R.id.setNotification);
        edit_profile = findViewById(R.id.edit_profi);
        notification.setChecked(getSharedPreferences(NOTIFICATION, MODE_PRIVATE).getBoolean(NOTIFICATION, false));

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
                String not = getString(R.string.notifications) + " ";
                if (notificationState)
                    not += getString(R.string.are_enabled);
                else
                    not += getString(R.string.are_disabled);
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
                setMessage(getString(R.string.logout_rst_pswrd))
                .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).setPositiveButton(getString(R.string.reset_password), new DialogInterface.OnClickListener() {
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
                            Snackbar.make(findViewById(R.id.gender), getString(R.string.email_reset_pass), Snackbar.LENGTH_LONG).show();
                            firebaseAuth.signOut();
                            startActivity(new Intent(SettingsActivity.this, login.class));
                            finish();
                        }
                    }
                });

    }

    public void signOutAndgoTologin(View item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this).setTitle(getString(R.string.sign_out)).
                setMessage(getString(R.string.u_wnt_logout))
                .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).setPositiveButton(getString(R.string.logout), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FirebaseAuth.getInstance().signOut();
                        startActivity(new Intent(SettingsActivity.this, login.class));
                    }
                });
        builder.show();
    }

    public void changeLanguage(View view) {
        final String[] listItem = {"English", "العربية"};
        AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
        builder.setTitle(getString(R.string.choose_your_language));
        builder.setSingleChoiceItems(listItem, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == 0) {
                    setLocale("");
                    recreate();
                } else if (i == 1) {
                    setLocale("ar");
                    recreate();
                }

                //dimiss alert when language is selected
                dialogInterface.dismiss();
            }
        });
        AlertDialog mDialog = builder.create();
        mDialog.show();


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


    public void loadLocal() {
        SharedPreferences prefs = getSharedPreferences("Settings", Activity.MODE_PRIVATE);
        String Language = prefs.getString("My_lang", "");
        setLocale(Language);
    }









}
