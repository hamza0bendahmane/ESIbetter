package com.example.esibetter.articles;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.esibetter.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.Calendar;
import java.util.HashMap;


public class bestOf extends Fragment {
    RecyclerView recyclerView;
    static FloatingActionButton fab_articl, fab_event, fab_ideas;
    private RecyclerView.LayoutManager manager;
    private FirebaseRecyclerAdapter adapter;
    private static boolean isopen = false;

    public bestOf() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.ideas_fragment_best_of, container, false);
        setupRecyclerAdapter(v);

        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {


    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        fab_articl = getActivity().findViewById(R.id.fab_articl);
        fab_ideas = getActivity().findViewById(R.id.fab_ideas);
        fab_event = getActivity().findViewById(R.id.fab_event);
        getActivity().findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isopen) {
                    hideFabs();
                    isopen = false;
                } else {
                    showFabs();
                    isopen = true;
                }

            }
        });

        fab_articl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final androidx.appcompat.app.AlertDialog dialog = new AlertDialog.Builder(getContext()).create();
                dialog.setTitle("ADD Article");
                dialog.setMessage("Please enter Title and Body :");
                hideFabs();
                LayoutInflater inflater = LayoutInflater.from(getContext());
                final View login_layout = inflater.inflate(R.layout.ideas_add_article, null);

                final EditText addtext = login_layout.findViewById(R.id.add_text);
                final EditText addtitle = login_layout.findViewById(R.id.add_title);
                final Button add = login_layout.findViewById(R.id.add_art);

                dialog.setView(login_layout);
                add.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!TextUtils.isEmpty(addtext.getText().toString()) &&
                                !TextUtils.isEmpty(addtitle.getText().toString())) {
                            DatabaseReference ref = FirebaseDatabase.getInstance()
                                    .getReference()
                                    .child("posts").push();
                            String tit = addtitle.getText().toString();
                            String tex = addtext.getText().toString();
                            HashMap<String, Object> map = new HashMap<>();
                            Calendar cc = Calendar.getInstance();
                            String month = String.valueOf(cc.get(Calendar.MONTH) + 1);
                            String date = cc.get(Calendar.DAY_OF_MONTH) + "/" + month + "/" + cc.get(Calendar.YEAR);
                            map.put("title", tit);
                            map.put("body", tex);
                            map.put("uid", FirebaseAuth.getInstance().getCurrentUser().getUid());
                            map.put("date", date);
                            // map.put("image",0);
                            map.put("likes", 27);
                            map.put("comments", null);
                            map.put("dislikes", 13);
                            ref.setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful())
                                        Toast.makeText(getContext(), "success", Toast.LENGTH_SHORT).show();
                                    else
                                        Toast.makeText(getContext(), "failed", Toast.LENGTH_SHORT).show();

                                }
                            });


                            dialog.dismiss();
                        }

                    }

                });
                dialog.show();
            }
        });


    }

    private void setupRecyclerAdapter(View v) {
        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child("posts").orderByChild("likes");
        final FirebaseRecyclerOptions<Article_item> options =
                new FirebaseRecyclerOptions.Builder<Article_item>()
                        .setQuery(query, Article_item.class).build();
        manager = new LinearLayoutManager(getContext());
        recyclerView = v.findViewById(R.id.bestof_recycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(manager);
        adapter = new ArticlesAdapter(options);
        recyclerView.setAdapter(adapter);
        adapter.startListening();


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

    public void hideFabs() {
        fab_articl.setVisibility(View.GONE);
        fab_ideas.setVisibility(View.GONE);
        fab_event.setVisibility(View.GONE);
    }

    public void showFabs() {
        fab_articl.setVisibility(View.VISIBLE);
        fab_ideas.setVisibility(View.VISIBLE);
        fab_event.setVisibility(View.VISIBLE);
    }

}