package com.example.esibetter.articles;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.esibetter.R;

public class ShowImage extends AppCompatActivity {
    Uri refer;
    ImageView vv;
    String title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_image);
        refer = getIntent().getExtras().getParcelable("ref");
        title = getIntent().getExtras().getString("title");
        vv = findViewById(R.id.vv);
        showIma();
    }

    private void showIma() {
        Glide.with(ShowImage.this).load(refer).into(vv);
        ((TextView) findViewById(R.id.text_tit)).setText(title);
    }

    @Override
    protected void onResume() {
        super.onResume();
        showIma();
    }

    public void go_back(View vvc) {
        onBackPressed();
    }
}