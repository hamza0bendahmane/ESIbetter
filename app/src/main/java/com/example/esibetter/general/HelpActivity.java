package com.example.esibetter.general;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ShareCompat;

import com.example.esibetter.R;
import com.example.esibetter.notifications.Profile_Activity;

public class HelpActivity extends AppCompatActivity {

    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(), Profile_Activity.class));
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.general_activity_help);


    }

    public void emailReport(View view) {
        ShareCompat.IntentBuilder.from(HelpActivity.this)
                .setType("message/rfc822")
                .addEmailTo("hamza0bendahmane@gmail.com")
                .setSubject("Report issue (ESI better)")
                .setText("please describe your issue here \n" +
                        "you can illustrate it with screenshots if needed ... \n" +
                        "and we will contact you soon ...\n" +
                        "Respectfully .")
                //.setHtmlText(body) //If you are using HTML in your body text
                .setChooserTitle("Please pick an app to send your report")
                .startChooser();
    }
}
