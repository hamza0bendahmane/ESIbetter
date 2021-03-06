package com.example.esibetter.articles;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

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

import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;


public class news extends Fragment {

    public String name;
    public CollectionReference reference = FirebaseFirestore.getInstance()
            .collection("posts");
    public String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    public FirestoreRecyclerOptions<Article_item> options;
    Query query;
    RecyclerView recyclerView;
    String TAG = "hbhb";


    private RecyclerView.LayoutManager manager;
    private ArticlesAdapter adapter;
    private int menuClickedPosition = -1;


    public news() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.ideas_fragment_news, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View va, @Nullable Bundle savedInstanceState) {
        loadLocal();
        query = reference.orderBy("date", Query.Direction.DESCENDING);
        options = new FirestoreRecyclerOptions.Builder<Article_item>()
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
        Articles.setUpTheRefresh((SwipeRefreshLayout) va.findViewById(R.id.swipper), adapter);


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

    public String loadLocal() {

        SharedPreferences prefs = getContext().getSharedPreferences("Settings", MODE_PRIVATE);
        String Language = prefs.getString("My_lang", "");
        setLocale(Language);
        return Language;
    }

    private void setLocale(String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);

        Configuration configuration = new Configuration();
        configuration.locale = locale;


        getContext().getResources().
                updateConfiguration(configuration, getContext().getResources().getDisplayMetrics());
        //save data in shared preferences
        SharedPreferences.Editor editor = getContext().getSharedPreferences("Settings", MODE_PRIVATE).edit();
        editor.putString("My_lang", lang);
        editor.apply();
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
        manager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView = getView().findViewById(R.id.news_recycler);
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

                menuClickedPosition = position;
            }


        });
        interactiveScroll();

    }


    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        adapter.notifyDataSetChanged();
        if (menuClickedPosition != RecyclerView.NO_POSITION) {

            if (getUserVisibleHint()) {
                switch (item.getItemId()) {

                    case R.id.edit_post:
                        HandleMenu("edit", adapter.getItem(menuClickedPosition));
                        return true;
                    case R.id.delete_post:
                        HandleMenu("delete", adapter.getItem(menuClickedPosition));
                        return true;
                    case R.id.report_post:
                        HandleMenu("report", adapter.getItem(menuClickedPosition));
                        return true;
                    default:
                        break;


                }
            }
        }
        return super.onContextItemSelected(item);

    }

    private void HandleMenu(String action, Article_item item) {
        Log.d(TAG, "HandleMenu: news");
        adapter.notifyDataSetChanged();

        if (menuClickedPosition != -1) {
            switch (action) {

                case "edit":
                    Articles.editPost(getContext(), menuClickedPosition,
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
                default:
                    break;

            }
        }

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
        adapter.notifyDataSetChanged();

    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }


}