package com.example.esibetter.general;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.esibetter.R;
import com.example.esibetter.login;
import com.example.esibetter.notifications.Profile_Activity;
import com.google.firebase.auth.FirebaseAuth;

public class favoris_offline extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.general_favoris_offline);
    }

    @Override
    public void onBackPressed() {
        if (isWorkingInternetPersent()) {
            if (FirebaseAuth.getInstance().getCurrentUser() != null)
                startActivity(new Intent(getApplicationContext(), Profile_Activity.class));
            else
                startActivity(new Intent(getApplicationContext(), login.class));
            finish();

        }
        super.onBackPressed();
    }

    public boolean isWorkingInternetPersent() {
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
}
