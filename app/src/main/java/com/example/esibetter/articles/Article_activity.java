package com.example.esibetter.articles;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.esibetter.FacebookActivity;
import com.example.esibetter.R;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Calendar;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class Article_activity extends AppCompatActivity {
    static CircleImageView imagePoster;
    public static final String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    public static String postId;
    public static String posterUid;
    public static ImageButton like, dislike;
    public static ImageView imageofpost;
    static TextView datee, posterName, bodye, likese, dislikese;
    public static boolean userHasEmotion;
    public static boolean userHasLikedThePost;
    public static boolean userHasDislikedThePost;
    static RecyclerView recyclerView;
    private static RecyclerView.LayoutManager manager;
    private static comment_adapter adapter;
    public final HashMap<String, Long> likesMap = new HashMap<>();
    public Long likes, dislikes;
    public DocumentReference post_emotions, post;
    public Long likes_count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ideas_article_activity);


        // initializing vars ...

        imagePoster = findViewById(R.id.imagePoster);
        imageofpost = findViewById(R.id.imageofpost);
        bodye = findViewById(R.id.body_post);
        posterName = findViewById(R.id.posterName);
        datee = findViewById(R.id.date_post);
        like = findViewById(R.id.like);
        dislike = findViewById(R.id.dislike);
        likese = findViewById(R.id.likes);
        dislikese = findViewById(R.id.dislikes);
        postId = getIntent().getExtras().getString("postKey");
        post_emotions = FirebaseFirestore.getInstance().collection("posts_emotions")
                .document(postId);
        likes = Long.parseLong(getIntent().getExtras().getString("likes"));
        dislikes = Long.parseLong(getIntent().getExtras().getString("dislikes"));
        likesMap.put("likes", likes);
        likesMap.put("dislikes", dislikes);
        setupRecyclerAdapter(postId);
        //
        update(likesMap);
        Toolbar toolbar = findViewById(R.id.toolbar_art);
        setSupportActionBar(toolbar);
        UpdateUi();
        saveUserEmotion();


    }

    private void saveUserEmotion() {
        final HashMap<String, Object> emotion = new HashMap<>();
        like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                // create the emotion for the user ...
                if (userHasEmotion) {
                    // update emotion ...
                    if (userHasDislikedThePost) {
                        // +1 for likes and -1 for dislikes ...
                        emotion.put(uid, "1");
                        post_emotions.set(emotion, SetOptions.merge());
                        likes++;
                        likesMap.put("likes", likes);
                        dislikes--;
                        likesMap.put("dislikes", dislikes);
                        like.setEnabled(false);

                    } else {
                        // -1 for likes ...
                        emotion.put(uid, FieldValue.delete());
                        post_emotions.set(emotion, SetOptions.merge());
                        likes--;
                        likesMap.put("likes", likes);
                        like.setEnabled(false);

                    }

                } else {
                    // +1 for likes
                    emotion.put(uid, "1");
                    post_emotions.set(emotion, SetOptions.merge());
                    likes++;
                    likesMap.put("likes", likes);
                    like.setEnabled(false);

                }
                update(likesMap);

            }
        });

        dislike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // create the emotion for the user ...
                if (userHasEmotion) {
                    // update emotion ...
                    if (userHasDislikedThePost) {
                        // -1 for dislikes ...
                        emotion.put(uid, FieldValue.delete());
                        post_emotions.set(emotion, SetOptions.merge());
                        dislikes--;
                        likesMap.put("dislikes", dislikes);
                        dislike.setEnabled(false);

                    } else {
                        // -1 for likes and +1 for dislikes ...
                        emotion.put(uid, "0");
                        post_emotions.set(emotion, SetOptions.merge());
                        likes--;
                        dislikes++;
                        likesMap.put("likes", likes);
                        likesMap.put("dislikes", dislikes);
                        dislike.setEnabled(false);

                    }


                } else {

                    // +1 for dislikes ...
                    emotion.put(uid, "0");
                    post_emotions.set(emotion, SetOptions.merge());
                    dislikes++;
                    likesMap.put("dislikes", dislikes);
                    dislike.setEnabled(false);

                }
                update(likesMap);

            }
        });

    }

    private void update(final HashMap<String, Long> emotions) {
        post = FirebaseFirestore.getInstance().collection("posts").document(postId);
        post_emotions = FirebaseFirestore.getInstance().collection("posts_emotions")
                .document(postId);
        post_emotions.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                userHasEmotion = documentSnapshot.contains(uid);
                if (userHasEmotion) {
                    userHasLikedThePost = documentSnapshot.getString(uid).equals("1");
                    userHasDislikedThePost = documentSnapshot.getString(uid).equals("0");
                    if (userHasLikedThePost) {
                        like.setBackgroundResource(R.drawable.ic_liked);
                        dislike.setBackgroundResource(R.drawable.ic_thumb_down);

                    } else if (userHasDislikedThePost) {
                        dislike.setBackgroundResource(R.drawable.ic_disliked);
                        like.setBackgroundResource(R.drawable.ic_thumb_up);

                    }
                } else {
                    dislike.setBackgroundResource(R.drawable.ic_thumb_down);
                    like.setBackgroundResource(R.drawable.ic_thumb_up);
                }

                post.set(emotions, SetOptions.merge());
                like.setEnabled(true);
                dislike.setEnabled(true);

                likese.setText(Long.toString(emotions.get("likes")));
                dislikese.setText(Long.toString(emotions.get("dislikes")));
            }
        });

    }


    public void UpdateUi(){

        Bundle bundle = getIntent().getExtras();
        String title =bundle.getString("title");
        String body = bundle.getString("body");
        posterUid = bundle.getString("uid");
        //        Uri imagePoste= Uri.parse(bundle.getString("imagePost"));
        String date = bundle.getString("date");
        // .....
        Glide.with(getApplicationContext()).load(bundle.getString("image")).
                diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imageofpost);
        Toolbar toolbar = findViewById(R.id.toolbar_art);
        setSupportActionBar(toolbar);
        toolbar.setTitle(title);
        //imagePost.setImageURI(imagePoste);
        bodye.setText(body);
        setImagePoster(posterUid);
        setPosterName(posterUid);
        datee.setText(date);



    }
    public void setImagePoster(String uid) {
        StorageReference reference = FirebaseStorage.getInstance().getReference("Images/" + uid);
        StorageReference photo = reference.child("/prof_pic.png");
        photo.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(getApplicationContext())
                        .asBitmap()
                        .override(300, 300)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .load(uri)
                        .into(imagePoster);

            }
        });




    }
    public void setPosterName(String uid) {
        FirebaseDatabase ref = FirebaseDatabase.getInstance();
        ref.getReference("users/" + uid + "/name").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                posterName.setText(dataSnapshot.getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void setupRecyclerAdapter(final String postId) {
        final Query query = FirebaseFirestore.getInstance()
                .collection("comments").document("posts").collection(postId);


        final FirestoreRecyclerOptions<comment_item> options =
                new FirestoreRecyclerOptions.Builder<comment_item>()
                        .setQuery(query, comment_item.class).build();
        manager = new LinearLayoutManager(getApplicationContext());
        recyclerView = findViewById(R.id.comment_recycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(manager);
        adapter = new comment_adapter(options);
        recyclerView.setAdapter(adapter);


        adapter.setOnLikedListner(new comment_adapter.onLikedListner() {
            @Override
            public void onLiked(int position) {
                //  likes_count = options.getSnapshots().get(position).getLikes();
                //updateLikes(options.getSnapshots().get(position).commentId);

            }
        });


    }

    private void updateLikes(String commentId) {
        final DocumentReference query = FirebaseFirestore.getInstance()
                .collection("comments").document("posts").collection(postId).document(commentId);
        final DocumentReference comment_likes = FirebaseFirestore.getInstance().collection("comments_emotions")
                .document("posts").collection(postId).document(commentId);
        final HashMap<String, Object> map = new HashMap<>();
        final HashMap<String, Object> comment = new HashMap<>();
        comment_likes.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (!documentSnapshot.contains(uid)) {

                    likes_count++;
                    map.put(uid, "1");
                    comment.put("likes", likes_count);
                    comment_likes.set(map, SetOptions.merge());


                } else {
                    likes_count--;
                    map.put(uid, FieldValue.delete());
                    comment.put("likes", likes_count);
                    query.set(comment, SetOptions.merge());
                    comment_likes.set(map, SetOptions.merge());


                }
                query.set(comment, SetOptions.merge());

            }
        });


    }


    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    public void add_comment(View view) {


        final TextInputEditText addtext = findViewById(R.id.add_commento);
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        if (!TextUtils.isEmpty(addtext.getText().toString())) {
            FirebaseFirestore dataBase = FirebaseFirestore.getInstance();
            HashMap<String, Object> commMap = new HashMap<>();
            String tex = addtext.getText().toString().trim();
            Calendar cc = Calendar.getInstance();
            String month = String.valueOf(cc.get(Calendar.MONTH) + 1);
            String date = cc.get(Calendar.YEAR) + "/" + month + "/" + cc.get(Calendar.DAY_OF_MONTH);
            commMap.put("uid", uid);
            commMap.put("likes", 0);
            commMap.put("date", date);
            commMap.put("comment", tex);
            DocumentReference ref = dataBase.collection("comments").
                    document("posts");
            // generate a very specific random reference ///----->TIME(ms)+Date ;
            String ss = cc.getTimeInMillis() + cc.get(Calendar.DAY_OF_MONTH) + month + cc.get(Calendar.YEAR);
            commMap.put("commentId", ss);
            ref.collection(postId).document(ss).set(commMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(getApplicationContext(), getString(R.string.succes), Toast.LENGTH_SHORT).show();

                    } else
                        Toast.makeText(getApplicationContext(), getString(R.string.failed), Toast.LENGTH_SHORT).show();
                }

            });
            addtext.setText("");
            adapter.startListening();
            adapter.notifyDataSetChanged();


        }


    }


    public void open_facebook(View view) {

        startActivity(new Intent(getApplicationContext(), FacebookActivity.class));
        //TODO( Walid ) : open intent to share the article via Facebook or email ...
        // about facebook approximately it 's ready just make a little changes ...
        // don't forget to do your last task the language switch < AR--EN > ..

    }

    public void manipulate_post(View view) {

        ImageButton button = findViewById(R.id.manipulate_post);
        PopupMenu popup = new PopupMenu(getApplicationContext(), button);

        if (posterUid.equals(uid)) {  // the current user is the one who posted this Article...
            popup.getMenuInflater().inflate(R.menu.user_menu, popup.getMenu());
        } else { // the current user doesn't write this Article ...
            popup.getMenuInflater().inflate(R.menu.anonym_menu, popup.getMenu());
        }
        popup.show();//showing popup menu
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.share_post:
                        HandleMenu("share");
                        return true;
                    case R.id.edit_post:
                        HandleMenu("edit");
                        return true;
                    case R.id.delete_post:
                        HandleMenu("delete");
                        return true;
                    case R.id.report_post:
                        HandleMenu("report");
                        return true;
                    default:
                        break;
                }
                return false;
            }
        });
    }

    private void HandleMenu(String action) {

        // TODO(MANEL) :  add the action to item selected in the menu ....

// you  can use switch(action ) ... like the  method above ...
        //case "delete" :
        // break ;
        // ...ext
    }
}
