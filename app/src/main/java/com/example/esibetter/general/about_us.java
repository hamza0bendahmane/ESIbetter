package com.example.esibetter.general;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.esibetter.R;
import com.example.esibetter.notifications.Profile_Activity;

public class about_us extends AppCompatActivity {
    @Override
    public void onBackPressed() {
        back();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.general_activity_about_us);
        findViewById(R.id.goback).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                back();
            }
        });


    }
    private  void back(){
        startActivity(new Intent(this, Profile_Activity.class));
        finish();
    }
}
