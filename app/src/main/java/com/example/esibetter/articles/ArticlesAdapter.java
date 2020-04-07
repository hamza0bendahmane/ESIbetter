package com.example.esibetter.articles;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.esibetter.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Source;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import de.hdodenhof.circleimageview.CircleImageView;

import static androidx.core.content.ContextCompat.startActivity;

public  class ArticlesAdapter extends FirebaseRecyclerAdapter<Article_item, ArticlesAdapter.ViewHolder> {
public static boolean UsersPost = false;


    public ArticlesAdapter(@NonNull FirebaseRecyclerOptions<Article_item> options) {
        super(options);


    }



    @Override
    protected void onBindViewHolder(@NonNull final ArticlesAdapter.ViewHolder holder, int position, @NonNull final Article_item model) {
        holder.setTxtTitle(model.getTitle());
        holder.root.setOnCreateContextMenuListener(holder);
        holder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {holder.root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Bundle bundle = new Bundle();
                    bundle.putString("title",model.getTitle());
                    bundle.putString("body",model.getBody());
                    bundle.putString("uid",model.getUid());
                    //bundle.putString("imagePost",Article_item.getImage().toString());
                    bundle.putString("likes",String.valueOf(model.getLikes()));
                    bundle.putString("dislikes",String.valueOf(model.getDislikes()));
                    bundle.putString("date",model.getDate());
                    Intent intent = new Intent(view.getContext(),Article_activity.class);
                    intent.putExtras(bundle);
                    startActivity(view.getContext(),intent,null);
                }
            });
            }
        });


        holder.setImagePoster(model.getUid());
        holder.setPosterName(model.getUid());
        holder.setLikesNUm(String.valueOf(model.getLikes()));
        holder.setDislikesNum(String.valueOf(model.getDislikes()));
        holder.root.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (model.getUid().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()))
                    UsersPost = true;
                else
                    UsersPost = false;
                return false;
            }
        });

    }

    @NonNull
    @Override
    public ArticlesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.ideas_list_article_item, parent, false);
        return new ArticlesAdapter.ViewHolder(view);
    }


public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
    private RelativeLayout root;
    Uri imageUri = null ;
    String name = "";
    private TextView txtTitle;
    private TextView poster_name;
    private ImageButton like;
    private TextView likesNUm;
    private CircleImageView imagePoster;
    private TextView dislikesNum;
    private ImageButton dislike;

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId()==R.id.lin) {
            MenuInflater inflater = new MenuInflater(v.getContext());
            if (UsersPost)
                inflater.inflate(R.menu.user_menu, menu);
            else
                inflater.inflate(R.menu.anonym_menu, menu);

        }

    }



    public ViewHolder(final View itemView) {
        super(itemView);
        root = itemView.findViewById(R.id.lin);
        txtTitle = itemView.findViewById(R.id.title_art);
        imagePoster = itemView.findViewById(R.id.poster_image);
        like = itemView.findViewById(R.id.like);
        poster_name = itemView.findViewById(R.id.poster_name);
        likesNUm = itemView.findViewById(R.id.likesNUm);
        dislike = itemView.findViewById(R.id.dislike);
        dislikesNum = itemView.findViewById(R.id.dislikesNUm);

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
        StorageReference  reference = FirebaseStorage.getInstance().getReference("Images/" + uid);
        StorageReference photo = reference.child("/prof_pic.png");
        photo.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(itemView.getContext())
                        .load(uri)
                        .into(imagePoster);
                imageUri = uri;

            }
        });

        return imageUri;



    }
    public String setPosterName(String uid) {
        FirebaseFirestore ref = FirebaseFirestore.getInstance();
        ref.collection("users").document(uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                name = task.getResult().get("name").toString();
                poster_name.setText("By :"+task.getResult().get("name").toString());
            }
        });

        return name;
    }
}

}
