package com.example.esibetter.articles;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.esibetter.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import de.hdodenhof.circleimageview.CircleImageView;


public class comment_adapter extends FirestoreRecyclerAdapter<comment_item, comment_adapter.ViewHolder> {
    public boolean USersComment;
    public onLikedListner mlistner;

    public comment_adapter(@NonNull FirestoreRecyclerOptions<comment_item> options) {
        super(options);
    }


    @Override
    protected void onBindViewHolder(@NonNull final comment_adapter.ViewHolder holder,
                                    final int position, @NonNull final comment_item model) {

        holder.setImagePoster(model.getUid());
        holder.setPosterName(model.getUid());
        holder.setDate(model.getDate());
        holder.setComment(model.getComment());
        holder.setLikesNUm(model.getLikes());

        holder.root.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                USersComment = model.getUid().equals(FirebaseAuth.getInstance().getCurrentUser().getUid());
                return false;
            }
        });

    }


    @NonNull
    @Override
    public comment_adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.ideas_comment_layout, parent, false);
        return new comment_adapter.ViewHolder(view);
    }

    public void setOnLikedListner(onLikedListner listner) {
        mlistner = listner;
    }

    public interface onLikedListner {
        void onLiked(int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        Uri imageUri = null;
        String name = "";
        TextView date;
        private RelativeLayout root;
        private TextView poster_name;
        private TextView comment;
        private ImageButton like;
        private TextView likesNUm;
        private CircleImageView imagePoster;


        public ViewHolder(final View itemView) {
            super(itemView);
            root = itemView.findViewById(R.id.comment_container);
            imagePoster = itemView.findViewById(R.id.picture_profile);
            comment = itemView.findViewById(R.id.comment);
            date = itemView.findViewById(R.id.time);
            likesNUm = itemView.findViewById(R.id.comm_likes);
            like = itemView.findViewById(R.id.like_comm);
            poster_name = itemView.findViewById(R.id.userName);
            like.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mlistner != null) {
                        if (getAdapterPosition() != RecyclerView.NO_POSITION) {
                            mlistner.onLiked(getAdapterPosition());
                        }

                    }
                }
            });
        }

        public void setComment(String s) {
            comment.setText(s);

        }

        public void setDate(String s) {
            date.setText(s);

        }

        // update number of  likes for the comment ....
        public void setLikesNUm(Long numLikes) {

            if (numLikes != 0)
                likesNUm.setText(Long.toString(numLikes));
            else
                likesNUm.setVisibility(View.GONE);
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
    }
}
