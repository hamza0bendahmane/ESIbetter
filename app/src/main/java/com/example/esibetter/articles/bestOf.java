package com.example.esibetter.articles;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;


public class bestOf extends Fragment {
    RecyclerView recyclerView;
    public static String name;
    private RecyclerView.LayoutManager manager;
    private static int clickedPosition = -1;
    private ArticlesAdapter adapter;
    //
    public final String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    public final CollectionReference reference = FirebaseFirestore.getInstance()
            .collection("posts");
    final Query query = FirebaseFirestore.getInstance()
            .collection("posts")
            .orderBy("likes", Query.Direction.DESCENDING).orderBy("dislikes", Query.Direction.ASCENDING);
    final FirestoreRecyclerOptions<Article_item> options =
            new FirestoreRecyclerOptions.Builder<Article_item>()
                    .setQuery(query, Article_item.class).build();
    String TAG = "hbhb";
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
        Articles.setUpTheRefresh(view.findViewById(R.id.swipper_bestof), adapter);




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
                Article_item item = options.getSnapshots().get(position);

                b.putString("title", item.getTitle());
                b.putString("body", item.getBody());
                b.putString("uid", item.getUid());
                b.putString("date", item.getDate());
                b.putString("likes", String.valueOf(item.getLikes()));
                b.putString("image", item.getImage());
                b.putString("dislikes", String.valueOf(item.getDislikes()));
                b.putString("postKey", options.getSnapshots().getSnapshot(position).getId());
                Intent i = new Intent(getContext(), Article_activity.class);
                i.putExtras(b);
                startActivity(i);

            }

            @Override
            public void onLongClick(int position) {
                Log.d(TAG, "HandleMenu: best of00000" + clickedPosition);

                clickedPosition = position;
                Log.d(TAG, "HandleMenu: best ofLLLLLL" + clickedPosition);

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
        adapter.notifyDataSetChanged();


    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        Log.d(TAG, "onContextItemSelected: bestof");
        adapter.notifyDataSetChanged();

        switch (item.getItemId()) {
            case R.id.share_post:
                HandleMenu("share");
                return true;
            case R.id.edit_post:
                HandleMenu("edit");
                return true;
            case R.id.delete_post:
                HandleMenu("delete");
                return true;
            case R.id.report_post:
                HandleMenu("report");
                return true;
            default:
                break;


        }
        return super.onContextItemSelected(item);

    }

    private void HandleMenu(String action) {
        adapter.notifyDataSetChanged();

        Log.d(TAG, "HandleMenu: bestof");
        if (clickedPosition != -1 && clickedPosition != options.getSnapshots().size()) {
            switch (action) {

                case "edit":
                    Articles.editPost(getContext(), clickedPosition,
                            options.getSnapshots().getSnapshot(clickedPosition).getId());
                    break;
                case "delete":
                    Articles.deletePost(getContext(),
                            options.getSnapshots().getSnapshot(clickedPosition).getId());
                    adapter.notifyDataSetChanged();

                    break;
                case "report":
                    Articles.reportPost(getContext(), options.getSnapshots().get(clickedPosition),
                            options.getSnapshots().getSnapshot(clickedPosition).getId());
                    break;
                case "share":
                    Toast.makeText(getContext(), action + clickedPosition, Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "HandleMenu: best of" + clickedPosition);

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



















