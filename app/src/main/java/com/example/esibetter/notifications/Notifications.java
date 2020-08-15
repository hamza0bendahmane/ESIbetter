package com.example.esibetter.notifications;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.esibetter.R;
import com.example.esibetter.articles.Article_activity;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

import java.util.Date;


public class Notifications extends AppCompatActivity {


    FirebaseRecyclerAdapter<Notifi_item, ViewHolder> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notifications_fragment);
        HandleNotifications();

    }


    private void HandleNotifications() {

        com.google.firebase.database.Query query = FirebaseDatabase.getInstance()
                .getReference().child("Notifications").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).orderByChild("time");

        FirebaseRecyclerOptions<Notifi_item> options = new FirebaseRecyclerOptions.Builder<Notifi_item>()
                .setQuery(query, Notifi_item.class)
                .build();


        adapter = new FirebaseRecyclerAdapter<Notifi_item, ViewHolder>(options) {
            @NonNull
            @Override
            public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_layout, parent, false);
                return new ViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull Notifi_item model) {
                //loader.hide();
                ((TextView) findViewById(R.id.text_no_notifi)).setVisibility(View.GONE);
                Date time = new Date((long) model.getTime());
                String commentText = getString(R.string.new_comment) + model.getName() + getString(R.string.it_said) + model.getComment() + getString(R.string.your_post_has) + model.getNumber() + getString(R.string.comments);
                String replyText = getString(R.string.you_got_new_reply) + model.getComment() + getString(R.string.from) + model.getName() + getString(R.string.it_said) + model.getReply() + getString(R.string.in_th_post) + model.getTitle() + "  .";

                String likeText = getString(R.string.new_reactions) + model.getLikes() + getString(R.string.likes) + model.getDislikes() + getString(R.string.dislikes);
                if (model.getType().equals("like")) {
                    holder.subtitle.setText(likeText);
                    holder.marker.setBackground(getResources().getDrawable(R.drawable.ic_liked));
                } else if (model.getType().equals("comment")) {
                    holder.subtitle.setText(commentText);
                    holder.marker.setBackground(getResources().getDrawable(R.drawable.comment));
                } else if (model.getType().equals("reply")) {
                    holder.subtitle.setText(replyText);
                    holder.marker.setBackground(getResources().getDrawable(R.drawable.reply_draw));
                }
                if (!model.isSeen()) {
                    holder.root.setBackgroundColor(Color.parseColor("#339999FF"));
                }
                holder.title.setText(model.getTitle());
                holder.setImageFile(model.getImage());
                holder.date.setText(time.toString());
                holder.root.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Notifi_item item = adapter.getItem(position);
                        Intent i = new Intent(getApplicationContext(), Article_activity.class);
                        adapter.getRef(position).child("seen").setValue(true);
                        adapter.notifyDataSetChanged();
                        Bundle rresultIntent = new Bundle();
                        rresultIntent.putString("likes", String.valueOf(item.getLikes()));
                        rresultIntent.putString("dislikes", String.valueOf(item.getDislikes()));
                        rresultIntent.putString("PostId", item.getPost_id());
                        rresultIntent.putString("title", item.getTitle());
                        rresultIntent.putString("date", item.getDate());
                        rresultIntent.putString("image", item.getImage());
                        rresultIntent.putString("uid", FirebaseAuth.getInstance().getCurrentUser().getUid());
                        rresultIntent.putString("body", item.getBody());
                        i.putExtras(rresultIntent);
                        startActivity(i);
                    }
                });

            }

        };
        RecyclerView recyclerview = findViewById(R.id.grid_modules);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(Notifications.this, LinearLayoutManager.VERTICAL, false);
        ((LinearLayoutManager) mLayoutManager).setReverseLayout(true);
        ((LinearLayoutManager) mLayoutManager).setStackFromEnd(true);
        recyclerview.setLayoutManager(
                mLayoutManager);

        recyclerview.setItemViewCacheSize(50);
        recyclerview.setAdapter(adapter);
        if (adapter.getItemCount() == 0)
            ((TextView) findViewById(R.id.text_no_notifi)).setVisibility(View.VISIBLE);
        else
            ((TextView) findViewById(R.id.text_no_notifi)).setVisibility(View.GONE);


        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT | ItemTouchHelper.DOWN | ItemTouchHelper.UP) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                                  RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                //Remove swiped item from list and notify the RecyclerView
                int position = viewHolder.getAdapterPosition();
                adapter.getRef(position).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(Notifications.this, R.string.delete, Toast.LENGTH_SHORT).show();
                    }
                });
                adapter.notifyDataSetChanged();

            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerview);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (adapter != null) {
            adapter.startListening();
            adapter.notifyDataSetChanged();
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {


        LinearLayout root;
        TextView title;
        TextView subtitle;
        TextView date;
        ImageView imagePoster;
        ImageView marker;


        public ViewHolder(final View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            subtitle = itemView.findViewById(R.id.subtitle);
            marker = itemView.findViewById(R.id.marker);
            date = itemView.findViewById(R.id.date);
            imagePoster = itemView.findViewById(R.id.poster_image);
            root = itemView.findViewById(R.id.card_n);


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


}