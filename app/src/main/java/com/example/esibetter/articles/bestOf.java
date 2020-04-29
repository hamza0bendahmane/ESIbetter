package com.example.esibetter.articles;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.esibetter.R;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;


public class bestOf extends Fragment {
    RecyclerView recyclerView;
    private RecyclerView.LayoutManager manager;
    public static final String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private ArticlesAdapter adapter;

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
        View fabArt = getActivity().findViewById(R.id.fab_article);
        View fabInit = getActivity().findViewById(R.id.fab_event);
        fabArt.setVisibility(View.GONE);
        fabInit.setVisibility(View.GONE);
        Articles.setonFabClicked(new Articles.onFabClicked() {
            @Override
            public void FabClicked() {
                Toast.makeText(getContext(), "fabArticle", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void FabEventClicked() {
                Toast.makeText(getContext(), "fabEvent", Toast.LENGTH_SHORT).show();
            }
        });
        Articles.setUpTheRefresh(view, adapter);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    private void setupRecyclerAdapter() {
        final Query query = FirebaseFirestore.getInstance()
                .collection("posts")
                .orderBy("likes", Query.Direction.DESCENDING).orderBy("dislikes", Query.Direction.ASCENDING);
        final FirestoreRecyclerOptions<Article_item> options =
                new FirestoreRecyclerOptions.Builder<Article_item>()
                        .setQuery(query, Article_item.class).build();
        manager = new LinearLayoutManager(getContext());
        recyclerView = getView().findViewById(R.id.bestof_recycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(manager);
        adapter = new ArticlesAdapter(options, getContext());

        recyclerView.setAdapter(adapter);
        ArticlesAdapter.setOnitemClickListener(new ArticlesAdapter.onItemClick() {
            @Override
            public void onClick(int position, Long l) {
                Bundle b = new Bundle();
                Article_item item = options.getSnapshots().get(position);
                b.putString("title", item.getTitle());
                b.putString("body", item.getBody());
                b.putString("uid", item.getUid());
                b.putString("date", item.getDate());
                b.putString("likes", String.valueOf(item.getLikes()));
                b.putString("dislikes", String.valueOf(item.getDislikes()));
                b.putString("postKey", options.getSnapshots().getSnapshot(position).getId());
                Intent i = new Intent(getContext(), Article_activity.class);
                i.putExtras(b);
                startActivity(i);

            }

            @Override
            public void onLongClick(int position) {

            }


        });
        interactiveScroll();
    }

    private void interactiveScroll() {
        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                Articles.interactiveScroll(dy, getActivity());

            }
        });
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


}