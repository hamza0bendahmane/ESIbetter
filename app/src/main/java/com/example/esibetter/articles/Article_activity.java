package com.example.esibetter.articles;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.esibetter.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import de.hdodenhof.circleimageview.CircleImageView;

public class Article_activity extends AppCompatActivity {
    CircleImageView imagePoster;
    TextView datee, posterName, bodye, likese, dislikese;

    ImageView imagePost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ideas_article_activity);

        // init ...
        imagePoster = findViewById(R.id.imagePoster);
        //imagePost = findViewById(R.id.imagePost);
        bodye = findViewById(R.id.body_post);
        posterName = findViewById(R.id.posterName);
        datee = findViewById(R.id.date_post);
        likese = findViewById(R.id.likes);
        dislikese = findViewById(R.id.dislikes);

        Toolbar toolbar = findViewById(R.id.toolbar_art);
        setSupportActionBar(toolbar);
        UpdateUi();

        FloatingActionButton fab = findViewById(R.id.fabu);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }
    public void UpdateUi(){
        Bundle bundle = getIntent().getExtras();
        String title =bundle.getString("title");
        String body = bundle.getString("body");
        String posterUid = bundle.getString("uid");
//        Uri imagePoste= Uri.parse(bundle.getString("imagePost"));
        String likes = bundle.getString("likes");
        String dislikes = bundle.getString("dislikes");
        String date = bundle.getString("date");

        // .....
        Toolbar toolbar = findViewById(R.id.toolbar_art);
        setSupportActionBar(toolbar);
        toolbar.setTitle(title);
        //imagePost.setImageURI(imagePoste);
        bodye.setText(body);
        setImagePoster(posterUid);
        setPosterName(posterUid);
        datee.setText("On : "+date);
        likese.setText(likes);
        dislikese.setText(dislikes);
    }
    public void setImagePoster(String uid) {
        StorageReference reference = FirebaseStorage.getInstance().getReference("Images/" + uid);
        StorageReference photo = reference.child("/prof_pic.png");
        photo.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(getApplicationContext())
                        .load(uri)
                        .into(imagePoster);

            }
        });




    }
    public void setPosterName(String uid) {
        FirebaseFirestore ref = FirebaseFirestore.getInstance();
        ref.collection("users").document(uid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                posterName.setText("By :"+documentSnapshot.getData().get("name").toString());
            }
        });

    }
}
