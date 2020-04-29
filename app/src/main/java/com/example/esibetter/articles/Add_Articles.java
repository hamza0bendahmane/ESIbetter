package com.example.esibetter.articles;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.esibetter.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;

public class Add_Articles extends AppCompatActivity {

    public static final String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    public static Uri image_art;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ideas_add_articles);

        Bundle bundle = getIntent().getExtras();
        String type = bundle.getString("type");
        if (type.equals("add")) {
            String category = bundle.getString("category");
            if (category.equals("event")) {
                // add event ...
                Toast.makeText(this, "event", Toast.LENGTH_SHORT).show();
            } else if (category.equals("article")) {
                // add article ...
                Toast.makeText(this, "article", Toast.LENGTH_SHORT).show();

            }
        } else if (type.equals("edit")) {
            // edit Article ....
            int position = bundle.getInt("position");

            Toast.makeText(this, "position : " + position, Toast.LENGTH_SHORT).show();
        }


        // TODO (Djihane) : add the text style butoons manipulation here ....

    }

    public void pick_image() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 12);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 12 && data != null && data.getData() != null) {
            Toast.makeText(getApplicationContext(), "got it", Toast.LENGTH_SHORT).show();
            image_art = data.getData();
        }
    }

    public void add(View v) {
        CollectionReference ref = FirebaseFirestore.getInstance()
                .collection("posts");
        final HashMap<String, Object> map = new HashMap<>();
        Calendar cc = Calendar.getInstance();
        String month = String.valueOf(cc.get(Calendar.MONTH) + 1);
        String date = cc.get(Calendar.YEAR) + "/" + month + "/" + cc.get(Calendar.DAY_OF_MONTH);
        String keyDocument = uid + cc.getTimeInMillis();
        map.put("title", "fsfgbsgfv");
        map.put("body", "fh btr hrty jrt ytr \n" +
                " fgyhj " +
                "tfh" +
                "fhg \n" +
                "fghgfhfgh fghfg  gfhfg f gf gh hg hgfhhg");
        map.put("uid", uid);
        map.put("date", date);
        map.put("likes", Long.parseLong("0"));
        map.put("dislikes", Long.parseLong("0"));
        ref.document(keyDocument).set(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful())
                    Toast.makeText(getApplicationContext(), getString(R.string.post_uploaded), Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(getApplicationContext(), getString(R.string.post_notuploaded), Toast.LENGTH_SHORT).show();

            }
        });


    }

}
