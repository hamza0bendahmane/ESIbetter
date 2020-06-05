package com.example.esibetter.articles;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.onegravity.rteditor.RTEditText;
import com.onegravity.rteditor.RTManager;
import com.onegravity.rteditor.RTToolbar;
import com.onegravity.rteditor.api.RTApi;
import com.onegravity.rteditor.api.RTProxyImpl;
import com.onegravity.rteditor.api.format.RTFormat;

import java.util.Calendar;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;


public class Add_Articles extends AppCompatActivity {
    public static final String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    public static final CollectionReference reference = FirebaseFirestore.getInstance()
            .collection("posts");
    public static Uri image_art;
    final Calendar cc = Calendar.getInstance();
    final String month = String.valueOf(cc.get(Calendar.MONTH) + 1);
    final String date = cc.get(Calendar.YEAR) + "/" + month + "/" + cc.get(Calendar.DAY_OF_MONTH);
    String titleArt;
    TextView post_article, mPreview;
    String dislikes = "0";

    StorageReference images_url;
    ImageView show_picked_image;
    String BodyArt;
    String likes = "0";
    RTManager mRTManager;
    EditText TitleBody;
    CircleImageView photo;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        mRTManager.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mRTManager.onDestroy(isFinishing());
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ideas_add_articles);
        // init vars ......
        String type = getIntent().getExtras().getString("type");

        post_article = findViewById(R.id.post_article);
        photo = findViewById(R.id.photo);
        show_picked_image = findViewById(R.id.show_picked_image);
        TitleBody = findViewById(R.id.articleTitle);
        images_url = FirebaseStorage.getInstance().getReference("Images/" + uid);
        // init vars ......

// create RTManager
        RTApi rtApi = new RTApi(this, new RTProxyImpl(this), null);
        mRTManager = new RTManager(rtApi, savedInstanceState);

// register toolbar

        RTToolbar rtToolbar = findViewById(R.id.rte_toolbar);
        if (rtToolbar != null) {
            mRTManager.registerToolbar(null, rtToolbar);
        }

// register editor & set text
        RTEditText articleBody = findViewById(R.id.editor_view);
        mRTManager.registerEditor(articleBody, true);
        // setting view for user ....

        if (type.equals("edit")) {
            String postId = getIntent().getExtras().getString("PostId");
            final StorageReference Photos = FirebaseStorage.getInstance().
                    getReference("Images/" + uid + "/" + postId);
            DocumentReference post_ref = reference.document(postId);
            post_ref.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    Photos.child("post_pic.png").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            image_art = uri;
                            Glide.with(getApplicationContext()).asBitmap()
                                    .diskCacheStrategy(DiskCacheStrategy.ALL).load(uri).into(show_picked_image);
                        }
                    });
                    likes = String.valueOf(documentSnapshot.get("likes"));
                    dislikes = String.valueOf(documentSnapshot.get("dislikes"));
                    TitleBody.setText(Html.fromHtml(documentSnapshot.get("title").toString()));
                    articleBody.setText(Html.fromHtml(documentSnapshot.get("body").toString()));

                }
            });


        } else {
            articleBody.setRichTextEditing(true, "add article");

        }


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

                Bundle bundle = getIntent().getExtras();
                // String postId = bundle.getString("postId");
                //    int position = bundle.getInt("position");

                // handle the event/Article .. edit/add ..


                String body = articleBody.getText(RTFormat.HTML);
                String title = TitleBody.getText().toString().trim();

                boolean imageIsSET = image_art != null;
                if (TextUtils.isEmpty(title))
                    Toast.makeText(Add_Articles.this, "title should not be empty", Toast.LENGTH_SHORT).show();
                else if (TextUtils.isEmpty(body))
                    Toast.makeText(Add_Articles.this, "body should not be empty", Toast.LENGTH_SHORT).show();
                else if (!imageIsSET)
                    Toast.makeText(Add_Articles.this, "Add an Image", Toast.LENGTH_SHORT).show();
                else {

                    BodyArt = articleBody.getText(RTFormat.HTML);
                    titleArt = TitleBody.getText().toString().trim();
                    final String keyDocument = uid + cc.getTimeInMillis();
                    StorageReference reference = FirebaseStorage.getInstance().
                            getReference("Images/" + uid + "/" + keyDocument);
                    final StorageReference photoArt = reference.child("post_pic.png");
                    View vv = LayoutInflater.from(Add_Articles.this).inflate(R.layout.general_layout_image, null, false);
                    final AlertDialog dialog = new AlertDialog.Builder(Add_Articles.this).setTitle("Uploading Post")
                            .setView(vv).create();
                    dialog.show();

                    if (type.equals("add")) {
                        managePost(photoArt, dialog, keyDocument);

                    } else if (type.equals("edit")) {
                        // edit Article ....
                        managePost(photoArt, dialog, getIntent().getExtras().getString("PostId"));


                    }


                }
            }
        });


    }

    private void managePost(StorageReference photoArt, AlertDialog dialog, String keyDocument) {
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

                    add(titleArt, BodyArt, task.getResult(), dialog, keyDocument, likes, dislikes);


                }
            }

        });


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

    public void add(String title, String body, Uri image, final AlertDialog dialog, String keyDocument, String likes, String dislikes) {
        DocumentReference ref = FirebaseFirestore.getInstance()
                .collection("posts").document(keyDocument);
        final HashMap<String, Object> map = new HashMap<>();

        map.put("title", title);
        map.put("body", body);
        map.put("uid", uid);
        map.put("date", date);
        map.put("image", image.toString());
        map.put("likes", Long.parseLong(likes));
        map.put("dislikes", Long.parseLong(dislikes));
        map.put("PostId", keyDocument);
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









