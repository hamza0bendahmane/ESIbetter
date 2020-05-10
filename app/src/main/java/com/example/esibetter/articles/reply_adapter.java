package com.example.esibetter.articles;

import android.content.Context;
import android.net.Uri;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.esibetter.R;
import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import de.hdodenhof.circleimageview.CircleImageView;


public class reply_adapter extends FirebaseListAdapter<reply_item> {
    public static final String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    public static boolean USersComment;
    public static onLikedListner mlistner;
    public Context cc;
    String postId, commentId;
    FirebaseListOptions<reply_item> options;

    public reply_adapter(Context cc, @NonNull FirebaseListOptions<reply_item> options, String postId, String commentId) {
        super(options);
        this.cc = cc;
        this.options = options;
        this.postId = postId;
        this.commentId = commentId;
    }

    public static void setOnLikedListner(onLikedListner listner) {
        mlistner = listner;
    }

    private void HandleMenuComment(String action, reply_item reply) {
        String Id = reply.getCommentId();
        switch (action) {
            case "edit":
                Articles.editReply(cc, commentId, reply, postId);
                break;
            case "delete":
                Articles.deleteReply(cc, postId, commentId, Id);
                break;
            case "report":
                Articles.reportReply(cc, reply, commentId, postId);
                break;
            default:
                break;

        }


    }

    @Override
    protected void populateView(@NonNull final View itemView, @NonNull final reply_item model, final int position) {
        final String uid = model.getUid();
        final RelativeLayout root = itemView.findViewById(R.id.comment_container);
        final ImageButton like = itemView.findViewById(R.id.like_comm);
        CircleImageView imagePoster = itemView.findViewById(R.id.picture_profile);
        TextView comment = itemView.findViewById(R.id.comment);
        TextView date = itemView.findViewById(R.id.time);
        TextView likesNUm = itemView.findViewById(R.id.comm_likes);
        TextView poster_name = itemView.findViewById(R.id.userName);
        root.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                USersComment = model.getUid().equals(FirebaseAuth.getInstance().getCurrentUser().getUid());
                PopupMenu popup = new PopupMenu(cc, root);

                if (model.getUid().equals(uid)) {  // the current user is the one who posted this Article...
                    popup.getMenuInflater().inflate(R.menu.comment_user, popup.getMenu());
                } else { // the current user doesn't write this Article ...
                    popup.getMenuInflater().inflate(R.menu.comment_anony, popup.getMenu());
                }
                popup.show();//showing popup menu
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.edit_comment:
                                HandleMenuComment("edit", model);
                                return true;
                            case R.id.delete_comment:
                                HandleMenuComment("delete", model);
                                return true;
                            case R.id.report_comment:
                                HandleMenuComment("report", model);
                                return true;
                            default:
                                break;

                        }
                        return false;
                    }
                });
                return false;
            }
        });
        like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Long likes_count = options.getSnapshots().get(position).getLikes();
                String ss = options.getSnapshots().get(position).commentId;
                like.setEnabled(false);
                Articles.updateRepliesLikes(itemView, like, commentId, ss, likes_count, postId);
            }
        });
// setting views .....
        final DocumentReference comment_likes = FirebaseFirestore.getInstance().collection("replies_emotions")
                .document("posts").collection(commentId).document(model.getCommentId());
        comment_likes.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.contains(uid)) {
                    like.setBackgroundResource(R.drawable.ic_liked);
                } else {
                    like.setBackgroundResource(R.drawable.ic_thumb_up);
                }
            }
        });
        setComment(model.getComment(), comment);
        setImagePoster(uid, imagePoster);
        setPosterName(uid, poster_name);
        setLikedStatus(model.getLikes(), likesNUm);
        setDate(model.getDate(), date);
    }

    public void setComment(String s, TextView comment) {
        comment.setText(s);

    }

    public void setDate(String s, TextView date) {
        date.setText(s);

    }

    public void setImagePoster(String uid, final CircleImageView imagePoster) {
        StorageReference reference = FirebaseStorage.getInstance().getReference("Images/" + uid);
        StorageReference photo = reference.child("/prof_pic.png");
        photo.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(cc)
                        .asBitmap()
                        .override(300, 300)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .load(uri)
                        .into(imagePoster);

            }
        });


    }

    public void setPosterName(String uid, final TextView poster_name) {
        FirebaseDatabase ref = FirebaseDatabase.getInstance();
        ref.getReference("users/" + uid + "/name").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                poster_name.setText(dataSnapshot.getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void setLikedStatus(Long likes, TextView likesNUm) {
        if (likes != 0) {
            likesNUm.setVisibility(View.VISIBLE);
            likesNUm.setText(Long.toString(likes));
        } else
            likesNUm.setVisibility(View.GONE);
    }


    public interface onLikedListner {
        void onLiked(int position, View itemView, reply_item model, onLikedListner listner);
    }


}

