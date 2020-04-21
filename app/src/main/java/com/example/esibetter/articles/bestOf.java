package com.example.esibetter.articles;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.esibetter.R;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.Calendar;
import java.util.HashMap;


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
        View v = inflater.inflate(R.layout.ideas_fragment_best_of, container, false);

        return v;
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
                final androidx.appcompat.app.AlertDialog dialog = new AlertDialog.Builder(getContext()).create();
                dialog.setTitle(getString(R.string.add_article));
                dialog.setMessage(getString(R.string.please_enter_body_n_title));

                LayoutInflater inflater = LayoutInflater.from(getContext());
                final View login_layout = inflater.inflate(R.layout.ideas_add_article, null);

                final EditText addtext = login_layout.findViewById(R.id.add_text);
                final EditText addtitle = login_layout.findViewById(R.id.add_title);
                final Button add = login_layout.findViewById(R.id.add_art);

                dialog.setView(login_layout);
                dialog.show();
                add.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!TextUtils.isEmpty(addtext.getText().toString()) &&
                                !TextUtils.isEmpty(addtitle.getText().toString())) {
                            CollectionReference ref = FirebaseFirestore.getInstance()
                                    .collection("posts");
                            String tit = addtitle.getText().toString();
                            String tex = addtext.getText().toString();
                            HashMap<String, Object> map = new HashMap<>();
                            Calendar cc = Calendar.getInstance();
                            String month = String.valueOf(cc.get(Calendar.MONTH) + 1);
                            String date = cc.get(Calendar.YEAR) + "/" + month + "/" + cc.get(Calendar.DAY_OF_MONTH);
                            String keyDocument = uid + cc.getTimeInMillis();
                            map.put("title", tit);
                            map.put("body", tex);
                            map.put("uid", uid);
                            map.put("date", date);
                            // map.put("image",0);
                            map.put("likes", Long.parseLong("0"));
                            map.put("dislikes", Long.parseLong("0"));
                            ref.document(keyDocument).set(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful())
                                        Toast.makeText(getContext(), getString(R.string.post_uploaded), Toast.LENGTH_SHORT).show();
                                    else
                                        Toast.makeText(getContext(), getString(R.string.post_notuploaded), Toast.LENGTH_SHORT).show();

                                }
                            });


                            dialog.dismiss();
                        }

                    }

                });
                adapter.notifyItemInserted(adapter.getItemCount());
            }

            @Override
            public void FabEventClicked() {
                Toast.makeText(getContext(), "fabEvent", Toast.LENGTH_SHORT).show();
            }
        });
        setUpTheRefresh(view);
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
        adapter = new ArticlesAdapter(options);

        recyclerView.setAdapter(adapter);
        adapter.setOnitemClickListener(new ArticlesAdapter.onItemClick() {
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
                View bottomNav = getActivity().findViewById(R.id.bottom_nav);
                View fab = getActivity().findViewById(R.id.fab);
                View fabArt = getActivity().findViewById(R.id.fab_article);
                View fabInit = getActivity().findViewById(R.id.fab_event);

                if (dy > 0) {
                    fadeOut(bottomNav);
                    fadeOut(fab);
                    fadeOut(fabArt);
                    fadeOut(fabInit);

                } else {
                    fadeIn(bottomNav);
                    fadeIn(fab);
                    fadeIn(fabArt);
                    fadeIn(fabInit);

                }


            }
        });
    }

    private void setUpTheRefresh(View v) {
        final SwipeRefreshLayout swipeRefreshLayout = v.findViewById(R.id.swipper_bestof);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                adapter.notifyDataSetChanged();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, 2000);
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

    private void fadeIn(final View loadingView) {
        loadingView.animate()
                .alpha(1f)
                .setDuration(120)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        loadingView.setVisibility(View.VISIBLE);
                    }
                });
    }

    private void fadeOut(final View loadingView) {
        loadingView.animate()
                .alpha(0f)
                .setDuration(120)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        loadingView.setVisibility(View.GONE);
                    }
                });
    }


}