package com.example.esibetter.courses;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.esibetter.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class ShowAllTutorials extends AppCompatActivity {

    FirebaseRecyclerAdapter<File_item, ViewHolder> options1st;
    Query query0 ;
    static int what_yearIs;
    static String moduleName;
    FirebaseRecyclerAdapter<File_item, ViewHolder> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_all_tutorials);
        Bundle b = getIntent().getExtras();
        if (b.isEmpty())
            onBackPressed();
        else{
            what_yearIs = b.getInt("year");
            moduleName = b.getString("name");
        }

        updateView();

        HandleTutos();

    }




    private void updateView(){
        if (what_yearIs == 1) {
            ((Toolbar) findViewById(R.id.toolbar)).setTitle("First Year "+ " > " +moduleName);

        } else {
            ((Toolbar) findViewById(R.id.toolbar)).setTitle("Second Year"+ " > " +moduleName);

        }

    }



    private void  HandleTutos() {

        com.google.firebase.database.Query query =   FirebaseDatabase.getInstance()
                .getReference().child("Tutorials").child(String.valueOf(what_yearIs)).limitToFirst(20).orderByChild("module").equalTo(moduleName);

        FirebaseRecyclerOptions<File_item> options = new FirebaseRecyclerOptions.Builder<File_item>()
                .setQuery(query, File_item.class)
                .build();



        adapter = new FirebaseRecyclerAdapter<File_item, ViewHolder>(options) {
            @NonNull
            @Override
            public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.courses_file_item, parent, false);
                return new ViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull File_item model) {
                 //loader.hide();
                holder.txtTitle.setText(model.getTitle());
                holder.root.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Bundle b = new Bundle();
                        File_item item = adapter.getItem(position);
                        b.putString("date", item.getDate());
                        b.putString("title", item.getTitle());
                        b.putString("ref",adapter.getRef(position).toString());
                        b.putString("uid", item.getUid());
                        b.putString("link", item.getPostId());
                        b.putString("PostId", item.getPostId());
                        b.putString("image", item.getThumbnail());
                        Intent i = new Intent(getApplicationContext(), VideoPlayer.class);
                        i.putExtras(b);
                        startActivity(i);
                    }
                });
                holder.setImageFile(model.getThumbnail());

            }

        };
        RecyclerView recyclerview = findViewById(R.id.grid_modules);
        recyclerview.setLayoutManager( new GridLayoutManager(ShowAllTutorials.this,2, GridLayoutManager.VERTICAL,false));
        recyclerview.setItemViewCacheSize(50);
        recyclerview.setAdapter(adapter);


    }


public class ViewHolder extends RecyclerView.ViewHolder  {



    private MaterialCardView root;
    private TextView txtTitle;
    private ImageView imagePoster;

    public ViewHolder(final View itemView) {
        super(itemView);
        txtTitle = itemView.findViewById(R.id.textGrid);
        imagePoster = itemView.findViewById(R.id.image_game);
        root =itemView.findViewById(R.id.card_n);


    }



    public void setImageFile(String path) {


        FirebaseStorage.getInstance().getReferenceFromUrl(path).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(getApplicationContext()).asBitmap()
                        .diskCacheStrategy(DiskCacheStrategy.ALL).load(uri).into(imagePoster);
            }
        });







    }




}

    @Override
    protected void onStart() {
        super.onStart();
        if (adapter != null) {
            adapter.startListening();
            adapter.notifyDataSetChanged();
        }
    }


}