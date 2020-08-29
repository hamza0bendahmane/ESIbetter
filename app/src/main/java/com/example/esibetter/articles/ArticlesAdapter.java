package com.example.esibetter.articles;

import android.content.Context;
import android.net.Uri;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.esibetter.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import de.hdodenhof.circleimageview.CircleImageView;

public class ArticlesAdapter extends FirestoreRecyclerAdapter<Article_item, ArticlesAdapter.ViewHolder> {

    public boolean UsersPost = false;


    onItemClick mlistener;
    Context cc;
    static FirestoreRecyclerOptions<Article_item> options;

    public ArticlesAdapter(@NonNull FirestoreRecyclerOptions<Article_item> optioans, Context cc) {
        super(optioans);
        this.cc = cc;
        options = optioans;

    }

    public void setOnitemClickListener(onItemClick listener) {
        mlistener = listener;
    }


    @NonNull
    @Override
    public ArticlesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.ideas_list_article_item, parent, false);
        return new ArticlesAdapter.ViewHolder(view, mlistener);
    }

    @Override
    protected void onBindViewHolder(@NonNull final ArticlesAdapter.ViewHolder holder, final int position, @NonNull final Article_item model) {
        holder.setTxtTitle(model.getTitle());
        holder.root.setOnCreateContextMenuListener(holder);
        holder.setImagePoster(model.getUid());
        holder.setPosterName(model.getUid());
        holder.setLikesNUm(Long.toString(model.getLikes()));
        holder.setDislikesNum(Long.toString(model.getDislikes()));
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                UsersPost = model.getUid().equals(FirebaseAuth.getInstance().getCurrentUser().getUid());
                if (mlistener != null) {
                    if (position != RecyclerView.NO_POSITION) {
                        mlistener.onLongClick(position);

                    }

                }

                return false;

            }
        });


    }


    public interface onItemClick {
        void onClick(int position, Long itemId);

        void onLongClick(int position);

    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return Long.valueOf(position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {


        Uri imageUri = null;
        String name = "";
        private LinearLayout root;
        private TextView txtTitle;
        private TextView poster_name;
        private ImageButton like;
        private TextView likesNUm;
        private CircleImageView imagePoster;
        private TextView dislikesNum;
        private ImageButton dislike;

        public ViewHolder(final View itemView, final onItemClick list) {
            super(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (list != null) {
                        int position = getAdapterPosition();
                        Long l = getItemId();
                        if (position != RecyclerView.NO_POSITION) {
                            list.onClick(position, l);
                        }

                    }
                }

            });
            root = itemView.findViewById(R.id.lin);
            txtTitle = itemView.findViewById(R.id.title_art);
            imagePoster = itemView.findViewById(R.id.poster_image);
            like = itemView.findViewById(R.id.like);
            poster_name = itemView.findViewById(R.id.poster_name);
            likesNUm = itemView.findViewById(R.id.likesNUm);
            dislike = itemView.findViewById(R.id.dislike);
            dislikesNum = itemView.findViewById(R.id.dislikesNUm);

        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            if (v.getId() == R.id.lin) {
                MenuInflater inflater = new MenuInflater(v.getContext());
                if (UsersPost)
                    inflater.inflate(R.menu.user_menu, menu);
                else
                    inflater.inflate(R.menu.anonym_menu, menu);


            }


        }


        public void setTxtTitle(String string) {
            txtTitle.setText(string);
        }

        public void setLikesNUm(String likesNUm) {
            this.likesNUm.setText(likesNUm);
        }

        public void setDislikesNum(String dislikesNum) {
            this.dislikesNum.setText(dislikesNum);
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

        public void setPosterName(String uid) {
            if (uid != null)
                FirebaseFirestore.getInstance().collection("users").document(
                        uid).
                        addSnapshotListener(new EventListener<DocumentSnapshot>() {
                            @Override
                            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                                name = value.getData().get("name").toString();
                                poster_name.setText(name);
                            }
                        });


        }


    }
}