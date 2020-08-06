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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

public class ShowAllFiles extends AppCompatActivity {
    static int what_yearIs;
    static String moduleName;
FirebaseRecyclerAdapter<File_item,ViewHolder> adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_all_files);
        Bundle b = getIntent().getExtras();
        if (b.isEmpty())
            onBackPressed();
        else{
            what_yearIs = b.getInt("year");
            moduleName = b.getString("name");
        }

        updateView();

        HandleSummaries();

    }



    private void updateView(){
        if (what_yearIs == 1) {
            ((Toolbar) findViewById(R.id.toolbar)).setTitle("First Year "+ " > " +moduleName);

        } else {
            ((Toolbar) findViewById(R.id.toolbar)).setTitle("Second Year"+ " > " +moduleName);

        }

    }


    private void  HandleSummaries() {

      Query query =   FirebaseDatabase.getInstance()
                .getReference().child("Summaries").child(String.valueOf(what_yearIs)).limitToFirst(20)  .orderByChild("module").equalTo(moduleName);

        FirebaseRecyclerOptions<File_item> options = new FirebaseRecyclerOptions.Builder<File_item>()
                .setQuery(query, File_item.class/* new SnapshotParser<File_item>() {
                    @NonNull
                    @Override
                    public File_item parseSnapshot(@NonNull DataSnapshot snapshot) {
                        return new File_item(snapshot.child("uid").getValue().toString(),snapshot.child("title").getValue().toString(),
                                snapshot.child("PostId").getValue().toString(),snapshot.child("module").getValue().toString(),
                                snapshot.child("thumbnail").getValue().toString(),snapshot.child("date").getValue().toString());
                    }
                }*/)
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
               // loader.hide();
                holder.txtTitle.setText(model.getTitle());
                holder.root.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Bundle b = new Bundle();
                        //       if (position != -1 &&  options!=null && options.getSnapshots()!=null && position< options.getSnapshots().size()) {
                        File_item item = adapter.getItem(position);
                        b.putString("title", item.getTitle());
                        b.putString("ref",adapter.getRef(position).toString());
                        b.putString("uid", item.getUid());
                        b.putString("link", item.getPostId());
                        b.putString("PostId", item.getPostId());
                        b.putString("image", item.getThumbnail());
                        Intent i = new Intent(getApplicationContext(), pdf.class);
                        i.putExtras(b);
                        startActivity(i);
                    }
                });
                holder.setImageFile(model.getThumbnail());

            }
            
        };
        RecyclerView recyclerview = findViewById(R.id.mod);
        recyclerview.setLayoutManager( new GridLayoutManager(ShowAllFiles.this,2, GridLayoutManager.VERTICAL,false));
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