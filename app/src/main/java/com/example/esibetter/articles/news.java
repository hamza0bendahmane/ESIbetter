package com.example.esibetter.articles;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.esibetter.R;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.SetOptions;

import java.util.Calendar;
import java.util.HashMap;


public class news extends Fragment {
    static RecyclerView recyclerView;
    public static final String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private static RecyclerView.LayoutManager manager;
    public static String name;
    private static ArticlesAdapter adapter;
    public final CollectionReference reference = FirebaseFirestore.getInstance()
            .collection("posts");
    public int menuClickedPosition = -1;
    String posterName, reporterName;
    Query query = reference.orderBy("date", Query.Direction.DESCENDING);
    public final FirestoreRecyclerOptions<Article_item> options =
            new FirestoreRecyclerOptions.Builder<Article_item>()
                    .setQuery(query, Article_item.class).build();
    public news() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(@NonNull View va, @Nullable Bundle savedInstanceState) {
        setupRecyclerAdapter();
        FloatingActionButton fabArt = getActivity().findViewById(R.id.fab_article);
        FloatingActionButton fabInit = getActivity().findViewById(R.id.fab_event);
        fabArt.setVisibility(View.GONE);
        fabInit.setVisibility(View.GONE);
        Articles.setonFabClicked(new Articles.onFabClicked() {
            @Override
            public void FabClicked() {

                final androidx.appcompat.app.AlertDialog dialog = new AlertDialog.Builder(getContext()).create();
                dialog.setTitle(getString(R.string.add_article));
                dialog.setMessage(getString(R.string.please_enter_body_n_title));

                LayoutInflater inflater = LayoutInflater.from(getContext());
                final View view = inflater.inflate(R.layout.ideas_add_article, null);

                final EditText addtext = view.findViewById(R.id.add_text);
                final EditText addtitle = view.findViewById(R.id.add_title);
                final Button add = view.findViewById(R.id.add_art);

                dialog.setView(view);
                dialog.show();
                add.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!TextUtils.isEmpty(addtext.getText().toString()) &&
                                !TextUtils.isEmpty(addtitle.getText().toString())) {
                            CollectionReference ref = FirebaseFirestore.getInstance()
                                    .collection("posts");
                            String tit = addtitle.getText().toString().trim();
                            String tex = addtext.getText().toString().trim();
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
                adapter.notifyDataSetChanged();

            }

            @Override
            public void FabEventClicked() {
                Toast.makeText(getContext(), "clicked ", Toast.LENGTH_SHORT).show();
            }
        });
        setUpTheRefresh(va);
    }

    private void setUpTheRefresh(View v) {
        final SwipeRefreshLayout swipeRefreshLayout = v.findViewById(R.id.swipper);
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);






    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.ideas_fragment_news, container, false);
    }

    private void setupRecyclerAdapter() {


        manager = new LinearLayoutManager(getContext());
        recyclerView = getView().findViewById(R.id.news_recycler);
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

                menuClickedPosition = position;
            }


        });
        interactiveScroll();

    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
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
        return false;

    }

    private void HandleMenu(String action) {

        switch (action) {

            case "edit":
                editPost();
                break;
            case "delete":
                deletePost();
                break;
            case "report":
                reportPost();
                break;
            case "share":
                Toast.makeText(getContext(), action + menuClickedPosition, Toast.LENGTH_SHORT).show();
                break;
            default:
                break;

        }


    }


    // Is the button now checked?


    private void reportPost() {

        final AlertDialog dialog = new AlertDialog.Builder(getContext()).create();
        dialog.setTitle(getString(R.string.report));
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        final View report_layout = inflater.inflate(R.layout.general_report_layout, null);
        final TextInputEditText describe_report = report_layout.findViewById(R.id.describe_report);
        final Button report = report_layout.findViewById(R.id.submit_report);
        final Button cancel = report_layout.findViewById(R.id.cancel_report);
        final HashMap<String, Object> report_map = new HashMap<>();

        final CollectionReference ref = FirebaseFirestore.getInstance().collection("reports");
        Calendar cc = Calendar.getInstance();
        String month = String.valueOf(cc.get(Calendar.MONTH) + 1);
        String date = cc.get(Calendar.YEAR) + month + cc.get(Calendar.DAY_OF_MONTH);
        final String reportKey = date + cc.getTimeInMillis();


        dialog.setView(report_layout);

        RadioButton violence = report_layout.findViewById(R.id.violence);
        RadioButton spam = report_layout.findViewById(R.id.spam);
        RadioButton bug = report_layout.findViewById(R.id.bug);

        // Check which radio button was clicked
        if (violence.isChecked()) {
            report_map.put("category", "violence");
        } else if (spam.isChecked()) {
            report_map.put("category", "spam");

        } else if (bug.isChecked()) {
            report_map.put("category", "bug");
        } else {
            report_map.put("category", "general");

        }
        dialog.show();

        report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String report_text = describe_report.getText().toString().trim();
                if (!TextUtils.isEmpty(report_text)) {
                    getNames(options.getSnapshots().get(menuClickedPosition).getUid());
                    String post_Id = options.getSnapshots().getSnapshot(menuClickedPosition).getId();
                    report_map.put("reporter", reporterName);
                    report_map.put("poster", posterName);
                    report_map.put("post", post_Id);
                    report_map.put("description", report_text);

                    ref.document(reportKey).set(report_map).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(getContext(), "Report Sent Successfully, Thanks for contacting us", Toast.LENGTH_SHORT).show();
                            } else
                                Toast.makeText(getContext(), "Failed Sending Report, Please Try Again", Toast.LENGTH_SHORT).show();

                        }
                    });
                    dialog.dismiss();

                } else {

                    Toast.makeText(getContext(), "Please describe your issue ", Toast.LENGTH_SHORT).show();

                }
            }

        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

    }

    private void editPost() {
        final androidx.appcompat.app.AlertDialog dialog = new AlertDialog.Builder(getContext()).create();
        dialog.setTitle(getString(R.string.edit_article_title));
        dialog.setMessage(getString(R.string.please_enter_body_n_title));

        LayoutInflater inflater = LayoutInflater.from(getContext());
        final View edit_post_layout = inflater.inflate(R.layout.ideas_add_article, null);

        final EditText addtext = edit_post_layout.findViewById(R.id.add_text);
        final EditText addtitle = edit_post_layout.findViewById(R.id.add_title);
        final Button add = edit_post_layout.findViewById(R.id.add_art);
        //show previous Post statue
        addtitle.setText(options.getSnapshots().get(menuClickedPosition).getTitle());
        addtext.setText(options.getSnapshots().get(menuClickedPosition).getBody());
        add.setText(R.string.edit);
        dialog.setView(edit_post_layout);
        dialog.show();
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(addtext.getText().toString()) &&
                        !TextUtils.isEmpty(addtitle.getText().toString())) {
                    CollectionReference ref = FirebaseFirestore.getInstance()
                            .collection("posts");
                    String tit = addtitle.getText().toString().trim();
                    String tex = addtext.getText().toString().trim();
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("title", tit);
                    map.put("body", tex);
                    // map.put("image",0);
                    ref.document(options.getSnapshots().getSnapshot(menuClickedPosition).getId()).
                            set(map, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful())
                                Toast.makeText(getContext(), getString(R.string.post_updated), Toast.LENGTH_SHORT).show();
                            else
                                Toast.makeText(getContext(), getString(R.string.post_notupdated), Toast.LENGTH_SHORT).show();

                        }
                    });


                    dialog.dismiss();
                }

            }

        });
        adapter.notifyDataSetChanged();


    }

    private void deletePost() {
        AlertDialog.Builder builder = new AlertDialog.
                Builder(getContext()).setTitle(getString(R.string.delete_post_title)).
                setMessage(getString(R.string.are_us_sure_delte_post))
                .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).setPositiveButton(getString(R.string.delete_post), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        reference.document(options.getSnapshots().getSnapshot(menuClickedPosition).getId())
                                .delete();
                    }
                });
        builder.show();

    }

    private void handleSearching() {
        SearchView search = getActivity().findViewById(R.id.search_bar);
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //  adapter.getFilter().filter(newText);
                return false;
            }
        });

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


    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
        // getActivity().findViewById(R.id.fab).setOnClickListener(null);
        //  this.getActivity().finish();
        // this.onDetach();
        // this.onDestroy();
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

    public void getNames(String posterUid) {
        FirebaseFirestore.getInstance().collection("users").document(posterUid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                posterName = documentSnapshot.getData().get("name").toString();
            }
        });

        FirebaseFirestore.getInstance().collection("users").document(uid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                reporterName = documentSnapshot.getData().get("name").toString();
            }
        });
    }

}