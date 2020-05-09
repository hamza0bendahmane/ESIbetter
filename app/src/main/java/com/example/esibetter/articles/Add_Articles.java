package com.example.esibetter.articles;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.esibetter.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Calendar;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class Add_Articles extends AppCompatActivity {
    final Calendar cc = Calendar.getInstance();
    public static final String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    public static Uri image_art;
    final String month = String.valueOf(cc.get(Calendar.MONTH) + 1);
    final String date = cc.get(Calendar.YEAR) + "/" + month + "/" + cc.get(Calendar.DAY_OF_MONTH);
    TextView post_article;
    String titleArt;
    StorageReference images_url;
    ImageView show_picked_image;
    String BodyArt;
    EditText articleBody, TitleBody;
    CircleImageView photo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ideas_add_articles);
        // init vars ......

        post_article = findViewById(R.id.post_article);
        photo = findViewById(R.id.photo);
        show_picked_image = findViewById(R.id.show_picked_image);
        articleBody = findViewById(R.id.articleBody);
        TitleBody = findViewById(R.id.articleTitle);
        images_url = FirebaseStorage.getInstance().getReference("Images/" + uid);
        // setting view for user ....
        images_url.child("/prof_pic.png").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(getApplicationContext()).asBitmap()
                        .diskCacheStrategy(DiskCacheStrategy.ALL).load(uri).into(photo);
            }
        });


        // on click post the Article .....

        post_article.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
/*
                Bundle bundle = getIntent().getExtras();
                String type = bundle.getString("type");

                // handle the event/Article .. edit/add ..

                if (type.equals("add")) {
                    String category = bundle.getString("category");
                    if (category.equals("event")) {
                        // add event ...
                    } else if (category.equals("article")) {
                        // add article ...

                    }
                } else if (type.equals("edit")) {
                    // edit Article ....
                    int position = bundle.getInt("position");


                }

                */
                String body = articleBody.getText().toString().trim();
                String title = TitleBody.getText().toString().trim();

                boolean imageIsSET = image_art != null;
                if (TextUtils.isEmpty(title))
                    Toast.makeText(Add_Articles.this, "title should not be empty", Toast.LENGTH_SHORT).show();
                else if (TextUtils.isEmpty(body))
                    Toast.makeText(Add_Articles.this, "body should not be empty", Toast.LENGTH_SHORT).show();
                else if (!imageIsSET)
                    Toast.makeText(Add_Articles.this, "Add an Image", Toast.LENGTH_SHORT).show();
                else {
                    BodyArt = articleBody.getText().toString().trim();
                    titleArt = TitleBody.getText().toString().trim();
                    final String keyDocument = uid + cc.getTimeInMillis();
                    StorageReference reference = FirebaseStorage.getInstance().
                            getReference("Images/" + uid + "/" + keyDocument);
                    final StorageReference photoArt = reference.child("post_pic.png");
                    View vv = LayoutInflater.from(Add_Articles.this).inflate(R.layout.general_layout_image, null, false);
                    final AlertDialog dialog = new AlertDialog.Builder(Add_Articles.this).setTitle("Uploading Post")
                            .setView(vv).create();
                    dialog.show();
                    photoArt.putFile(image_art).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                throw task.getException();
                            }
                            return photoArt.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (!task.isSuccessful()) {
                                Toast.makeText(Add_Articles.this, "upload failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            } else {

                                add(titleArt, BodyArt, task.getResult(), dialog, keyDocument);


                            }
                        }

                    });


                }
            }
        });





        // TODO (Djihane) : add the text style butoons manipulation here ....

    }

    public void pick_image(View v) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 12);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 12 && data != null && data.getData() != null) {
            image_art = data.getData();
            show_picked_image.setImageURI(image_art);

        }
    }

    public void add(String title, String body, Uri image, final AlertDialog dialog, String keyDocument) {
        DocumentReference ref = FirebaseFirestore.getInstance()
                .collection("posts").document(keyDocument);
        final HashMap<String, Object> map = new HashMap<>();

        map.put("title", title);
        map.put("body", body);
        map.put("uid", uid);
        map.put("date", date);
        map.put("image", image.toString());
        map.put("likes", Long.parseLong("0"));
        map.put("dislikes", Long.parseLong("0"));
        ref.set(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), getString(R.string.post_uploaded), Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    onBackPressed();

                } else
                    Toast.makeText(getApplicationContext(), getString(R.string.post_notuploaded), Toast.LENGTH_SHORT).show();

            }
        });


    }

    public void cancel(View view) {
        startActivity(new Intent(getApplicationContext(), news.class));
        finish();
    }
}
