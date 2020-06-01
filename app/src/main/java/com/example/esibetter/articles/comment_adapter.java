package com.example.esibetter.articles;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.esibetter.R;
import com.firebase.ui.database.FirebaseListOptions;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;


public class comment_adapter extends FirestoreRecyclerAdapter<comment_item, comment_adapter.ViewHolder> {
    public static final String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    public static boolean USersComment;
    public static Context cc;
    public static onLikedListner mlistner;
    static String postId;


    public comment_adapter(@NonNull FirestoreRecyclerOptions<comment_item> options, Context cc, String postId) {
        super(options);
        comment_adapter.cc = cc;
        comment_adapter.postId = postId;
    }

    public static void setOnLikedListner(onLikedListner listner) {
        mlistner = listner;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return Long.valueOf(position);
    }

    @Override
    protected void onBindViewHolder(@NonNull final comment_adapter.ViewHolder holder,
                                    final int position, @NonNull final comment_item model) {
        final DocumentReference comment_likes = FirebaseFirestore.getInstance().collection("comments_emotions")
                .document("posts").collection(postId).document(model.getCommentId());
        DatabaseReference db = FirebaseDatabase.getInstance().
                getReference().child("replies/" + postId).child(model.getCommentId());

        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean hasReplies = dataSnapshot.hasChildren();
                holder.showRepliesButton(hasReplies);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        final HashMap<String, Long> map = new HashMap<>();
        map.put("likes", model.getLikes());
        comment_likes.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.contains(uid)) {
                    holder.like.setBackgroundResource(R.drawable.ic_liked);
                } else {
                    holder.like.setBackgroundResource(R.drawable.ic_thumb_up);
                }
            }
        });
        holder.setLikedStatus(model.getLikes());
        holder.setImagePoster(model.getUid());
        holder.setPosterName(model.getUid());
        holder.setDate(model.getDate());
        holder.setComment(model.getComment());
        holder.setUpRecyclerAdapter(model.getCommentId());
        holder.root.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                USersComment = model.getUid().equals(FirebaseAuth.getInstance().getCurrentUser().getUid());
                PopupMenu popup = new PopupMenu(cc, holder.root);

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

    }

    private void HandleMenuComment(String action, comment_item comment) {
        String Id = comment.getCommentId();
        switch (action) {

            case "edit":
                Articles.editComment(cc, comment,
                        postId);
                break;
            case "delete":
                Articles.deleteComment(cc, postId, Id);
                break;
            case "report":
                Articles.reportComment(cc, comment, postId);
                break;
            default:
                break;

        }


    }

    @NonNull
    @Override
    public comment_adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.ideas_comment_layout, parent, false);
        return new comment_adapter.ViewHolder(view, mlistner);
    }

    public interface onLikedListner {
        void onLiked(int position, View itemView, onLikedListner listner);

        void onReplied(int adapterPosition, View itemView, onLikedListner listner);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        Uri imageUri = null;
        String name = "";
        TextView date;
        private RelativeLayout root;
        private TextView poster_name;
        private TextView comment;
        private ImageButton like, show_replies;
        private TextView likesNUm, reply;
        private ListView lv;
        private CircleImageView imagePoster;


        public ViewHolder(final View itemView, final onLikedListner listner) {
            super(itemView);
            root = itemView.findViewById(R.id.comment_container);
            imagePoster = itemView.findViewById(R.id.picture_profile);
            comment = itemView.findViewById(R.id.comment);
            date = itemView.findViewById(R.id.time);
            likesNUm = itemView.findViewById(R.id.comm_likes);
            reply = itemView.findViewById(R.id.reply);
            like = itemView.findViewById(R.id.like_comm);
            show_replies = itemView.findViewById(R.id.show_replies);
            lv = itemView.findViewById(R.id.replies);
            poster_name = itemView.findViewById(R.id.userName);
            show_replies.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (lv.getVisibility() == View.VISIBLE) {
                        lv.setVisibility(View.GONE);
                        show_replies.animate().setDuration(200)
                                .setListener(new AnimatorListenerAdapter() {
                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        super.onAnimationEnd(animation);
                                    }
                                })
                                .rotation(0f);
                    } else {
                        lv.setVisibility(View.VISIBLE);
                        show_replies.animate().setDuration(200)
                                .setListener(new AnimatorListenerAdapter() {
                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        super.onAnimationEnd(animation);
                                    }
                                })
                                .rotation(90f);
                    }

                }
            });
            reply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listner != null) {
                        if (getAdapterPosition() != RecyclerView.NO_POSITION) {
                            listner.onReplied(getAdapterPosition(), itemView, listner);
                        }

                    }
                }
            });
            like.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listner != null) {
                        if (getAdapterPosition() != RecyclerView.NO_POSITION) {
                            listner.onLiked(getAdapterPosition(), itemView, listner);
                        }

                    }
                }
            });
        }

        public void showRepliesButton(boolean hasReplies) {
            if (hasReplies)
                show_replies.setVisibility(View.VISIBLE);
            else
                show_replies.setVisibility(View.GONE);


        }

        public void setComment(String s) {
            comment.setText(s);

        }

        public void setDate(String s) {
            date.setText(s);

        }

        public Uri setImagePoster(String uid) {
            StorageReference reference = FirebaseStorage.getInstance().getReference("Images/" + uid);
            StorageReference photo = reference.child("/prof_pic.png");
            photo.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Glide.with(itemView.getContext())
                            .asBitmap()
                            .override(300, 300)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .load(uri)
                            .into(imagePoster);
                    imageUri = uri;

                }
            });

            return imageUri;


        }

        public String setPosterName(String uid) {
            FirebaseDatabase ref = FirebaseDatabase.getInstance();
            ref.getReference("users/" + uid + "/name").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    name = dataSnapshot.getValue().toString();
                    poster_name.setText(dataSnapshot.getValue().toString());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            return name;
        }


        public void setLikedStatus(Long likes) {
            if (likes != 0) {
                likesNUm.setVisibility(View.VISIBLE);

                likesNUm.setText(Long.toString(likes));
            } else
                likesNUm.setVisibility(View.GONE);
        }


        public void setUpRecyclerAdapter(final String commentId) {
            DatabaseReference db = FirebaseDatabase.getInstance().
                    getReference().child("replies/" + postId).child(commentId);
            final FirebaseListOptions<reply_item> options =
                    new FirebaseListOptions.Builder<reply_item>()
                            .setQuery(db, reply_item.class)
                            .setLayout(R.layout.ideas_reply_layout)
                            .build();
            final reply_adapter adapter = new reply_adapter(cc, options, postId, commentId);
            lv.setAdapter(adapter);
            adapter.startListening();

        }
    }

}
