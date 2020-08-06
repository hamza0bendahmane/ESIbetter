package com.example.esibetter.articles;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.esibetter.R;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;


public class bestOf extends Fragment {


    public String name;
    public CollectionReference reference = FirebaseFirestore.getInstance()
            .collection("posts");
    public String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    public FirestoreRecyclerOptions<Article_item> options;
    RecyclerView recyclerView;
    Query query;


    private RecyclerView.LayoutManager manager;
    private ArticlesAdapter adapter;
    String TAG = "hbhb";
    private int clickedPosition = -1;


    //

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
        query = FirebaseFirestore.getInstance()
                .collection("posts")
                .orderBy("likes", Query.Direction.DESCENDING).orderBy("dislikes", Query.Direction.ASCENDING);
        options =
                new FirestoreRecyclerOptions.Builder<Article_item>()
                        .setQuery(query, Article_item.class).build();
        setupRecyclerAdapter();

        // open activity to ad articles ...
        Articles.setonFabClicked(new Articles.onFabClicked() {

            @Override
            public void FabEventClicked() {
                Bundle bundle = new Bundle();
                bundle.putString("type", "add");
                Intent intent = new Intent(getContext(), Add_Articles.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        Articles.setUpTheRefresh((SwipeRefreshLayout) view.findViewById(R.id.swipper_bestof), adapter);

        // open activity to ad articles ...
        ((EditText) getActivity().findViewById(R.id.searchbarview)).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                Log.d(TAG, "search:111 " + s);

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.d(TAG, "search: 111" + s);
                if (!s.toString().trim().isEmpty())
                    SearchFor(s.toString().trim());
                else
                    adapter.updateOptions(options);


            }
        });



    }

    private void SearchFor(String field) {
        Query query1 = reference.whereGreaterThanOrEqualTo("title", field).
                whereLessThanOrEqualTo("title", field + "\uf8ff");
        FirestoreRecyclerOptions<Article_item> options1 =
                new FirestoreRecyclerOptions.Builder<Article_item>()
                        .setQuery(query1, Article_item.class).build();
        adapter.updateOptions(options1);


    }

    private void setupRecyclerAdapter() {
        manager = new LinearLayoutManager(getContext());
        recyclerView = getView().findViewById(R.id.bestof_recycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(manager);
        adapter = new ArticlesAdapter(options, getContext());
        recyclerView.setAdapter(adapter);
        adapter.setOnitemClickListener(new ArticlesAdapter.onItemClick() {
            @Override
            public void onClick(int position, Long l) {
                Bundle b = new Bundle();
                //       if (position != -1 &&  options!=null && options.getSnapshots()!=null && position< options.getSnapshots().size()) {
                Article_item item = adapter.getItem(position);
                b.putString("title", item.getTitle());
                b.putString("body", item.getBody());
                b.putString("uid", item.getUid());
                b.putString("date", item.getDate());
                b.putString("likes", String.valueOf(item.getLikes()));
                b.putString("image", item.getImage());
                b.putString("dislikes", String.valueOf(item.getDislikes()));
                b.putString("PostId", item.getPostId());
                Intent i = new Intent(getContext(), Article_activity.class);
                i.putExtras(b);
                startActivity(i);

            }

            @Override
            public void onLongClick(int position) {

                clickedPosition = position;
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
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        adapter.notifyDataSetChanged();

        if (getUserVisibleHint()) {
            switch (item.getItemId()) {
                case R.id.share_post:
                    HandleMenu("share", adapter.getItem(clickedPosition));
                    return true;
                case R.id.edit_post:
                    HandleMenu("edit", adapter.getItem(clickedPosition));
                    return true;
                case R.id.delete_post:
                    HandleMenu("delete", adapter.getItem(clickedPosition));
                    return true;
                case R.id.report_post:
                    HandleMenu("report", adapter.getItem(clickedPosition));
                    return true;
                default:
                    break;


            }
        }
        return super.onContextItemSelected(item);

    }

    private void HandleMenu(String action, Article_item item) {
        Log.d(TAG, "HandleMenu: news");
        adapter.notifyDataSetChanged();

        if (clickedPosition != -1) {
            switch (action) {

                case "edit":
                    Articles.editPost(getContext(), clickedPosition,
                            item.getPostId());
                    break;
                case "delete":
                    Articles.deletePost(getContext(),
                            item.getPostId());
                    adapter.notifyDataSetChanged();
                    break;
                case "report":
                    Articles.reportPost(getContext(), item,
                            item.getPostId());
                    break;
                case "share":
                    Toast.makeText(getContext(), action + clickedPosition, Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "HandleMenu: news" + clickedPosition);
                    break;
                default:
                    break;

            }
        }

    }
    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();

    }


}



















