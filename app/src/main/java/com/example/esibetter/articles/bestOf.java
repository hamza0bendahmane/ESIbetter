package com.example.esibetter.articles;

import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.esibetter.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;


public class bestOf extends Fragment {
    RecyclerView recyclerView;
    ContextMenu lv;
    SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView.LayoutManager manager;
    private FirebaseRecyclerAdapter adapter;
    private boolean UsersPost = false;
    public bestOf() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.ideas_fragment_best_of, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        setupRecyclerAdapter();

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



    }


    private void setupRecyclerAdapter() {
        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child("posts").orderByChild("likes");
        final FirebaseRecyclerOptions<Article_item> options =
                new FirebaseRecyclerOptions.Builder<Article_item>()
                        .setQuery(query, Article_item.class).build();
        manager = new LinearLayoutManager(getContext());
        recyclerView = getView().findViewById(R.id.bestof_recycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(manager);
        adapter = new ArticlesAdapter(options);
        recyclerView.setAdapter(adapter);


    }


    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
        getActivity().findViewById(R.id.fab).setVisibility(View.GONE);

    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
        getActivity().findViewById(R.id.fab).setVisibility(View.VISIBLE);

    }



}