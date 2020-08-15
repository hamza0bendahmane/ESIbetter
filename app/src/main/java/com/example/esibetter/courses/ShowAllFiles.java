package com.example.esibetter.courses;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.esibetter.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;

public class ShowAllFiles extends AppCompatActivity {
    int what_yearIs;
    String moduleName;
    Query query;
    FirebaseRecyclerOptions<File_item> options;
    FirebaseRecyclerAdapter<File_item, ViewHolder> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_all_files);
        Bundle b = getIntent().getExtras();
        if (b.isEmpty())
            onBackPressed();
        else {
            what_yearIs = b.getInt("year");
            moduleName = b.getString("name");
        }

        updateView();
        query = FirebaseDatabase.getInstance()
                .getReference().child("Summaries").child(String.valueOf(what_yearIs)).child(String.valueOf(moduleName));

        options = new FirebaseRecyclerOptions.Builder<File_item>()
                .setQuery(query, File_item.class)
                .build();

        HandleSummaries();

        // open activity to ad articles ...
        ((EditText) findViewById(R.id.searchbarview)).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().trim().isEmpty())
                    SearchFor(s.toString().trim());
                else
                    adapter.updateOptions(options);


            }
        });

    }

    private void SearchFor(String field) {
        Query query1 = query.orderByChild("title").startAt(field).
                endAt(field + "\uf8ff");
        FirebaseRecyclerOptions<File_item> options1 = new FirebaseRecyclerOptions.Builder<File_item>()
                .setQuery(query1, File_item.class)
                .build();
        adapter.updateOptions(options1);


    }

    private void updateView() {
        if (what_yearIs == 1) {
            ((TextView) findViewById(R.id.title_sub11)).setText(R.string.st_year);
            ((TextView) findViewById(R.id.title_mod)).setText(moduleName);
            ((ImageView) findViewById(R.id.icon_year1)).setImageDrawable(getDrawable(R.drawable.ic_one));


        } else {
            ((ImageView) findViewById(R.id.icon_year1)).setImageDrawable(getDrawable(R.drawable.ic_222));
            ((TextView) findViewById(R.id.title_sub11)).setText(R.string.st_year);
            ((TextView) findViewById(R.id.title_mod)).setText(moduleName);
        }

    }


    private void  HandleSummaries() {




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