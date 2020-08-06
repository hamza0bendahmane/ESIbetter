package com.example.esibetter.articles;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import com.example.esibetter.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Calendar;
import java.util.HashMap;


public class Articles extends Fragment {
    public static final CollectionReference reference = FirebaseFirestore.getInstance()
            .collection("posts");
    public static boolean isopen = false;
    public static final String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    public static String posterName, reporterName;
    public static searchable onsearchListner;
    public static FloatingActionButton fab;
    public static onFabClicked mlistener;

    String TAG = "hbhb";

    public static void setSearchable(searchable onsearstner) {
        onsearchListner = onsearstner;

    }

    public static void editPost(Context cc, int position, String postId) {
        Bundle bundle = new Bundle();
        bundle.putString("type", "edit");
        bundle.putInt("position", position);
        bundle.putString("PostId", postId);
        Intent intent = new Intent(cc, Add_Articles.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtras(bundle);
        cc.startActivity(intent);


    }
    public Articles() {
        // Required empty public constructor
    }

    public static void setonFabClicked(onFabClicked listener) {
        mlistener = listener;
    }

    public static void setUpTheRefresh(final SwipeRefreshLayout swipeRefreshLayout, final ArticlesAdapter adapter) {
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

    public static void reportPost(final Context cc, final Article_item item, final String post_Id) {
        final AlertDialog dialog = new AlertDialog.Builder(cc).create();
        dialog.setTitle(cc.getString(R.string.report));
        final View report_layout = LayoutInflater.from(cc).inflate(R.layout.general_report_layout, null);
        final EditText describe_report = report_layout.findViewById(R.id.describe_report);
        final Button report = report_layout.findViewById(R.id.submit_report);
        final Button cancel = report_layout.findViewById(R.id.cancel_report);
        final HashMap<String, Object> report_map = new HashMap<>();

        final CollectionReference ref = FirebaseFirestore.getInstance().collection("reports");
        final Calendar ccc = Calendar.getInstance();
        String month = String.valueOf(ccc.get(Calendar.MONTH) + 1);
        String date = ccc.get(Calendar.YEAR) + month + ccc.get(Calendar.DAY_OF_MONTH);
        final String reportKey = date + ccc.getTimeInMillis();


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
                    getNames(item.getUid());
                    report_map.put("reporter", reporterName);
                    report_map.put("poster", posterName);
                    report_map.put("post", post_Id);
                    report_map.put("description", report_text);
                    ref.document(reportKey).set(report_map).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(cc, "Report Sent Successfully, Thanks for contacting us", Toast.LENGTH_SHORT).show();
                            } else
                                Toast.makeText(cc, "Failed Sending Report, Please Try Again", Toast.LENGTH_SHORT).show();

                        }
                    });
                    dialog.dismiss();

                } else {

                    Toast.makeText(cc, "Please describe your issue ", Toast.LENGTH_SHORT).show();

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

    public interface searchable {
        void search(CharSequence serachText, Context context);

    }

    public static void deletePost(final Activity activity, final Context cc, final String postId) {

        AlertDialog.Builder builder = new AlertDialog.
                Builder(cc).setTitle(cc.getString(R.string.delete_post_title)).
                setMessage(cc.getString(R.string.are_us_sure_delte_post))
                .setNegativeButton(cc.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).setPositiveButton(cc.getString(R.string.delete_post), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        final CollectionReference query = FirebaseFirestore.getInstance()
                                .collection("comments").document("posts").collection(postId);
                        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                                    Articles.deleteComment(cc, postId, document.get("commentId").toString());
                                }
                            }

                        });

                        final StorageReference Photos = FirebaseStorage.getInstance().
                                getReference("Images/" + uid + "/" + postId);
                        Photos.child("post_pic.png").delete();
                        reference.document(postId)
                                .delete();

                        activity.onBackPressed();

                    }
                });
        builder.show();

    }
    public static void deletePost(final Context cc, final String postId) {

        AlertDialog.Builder builder = new AlertDialog.
                Builder(cc).setTitle(cc.getString(R.string.delete_post_title)).
                setMessage(cc.getString(R.string.are_us_sure_delte_post))
                .setNegativeButton(cc.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).setPositiveButton(cc.getString(R.string.delete_post), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        final CollectionReference query = FirebaseFirestore.getInstance()
                                .collection("comments").document("posts").collection(postId);
                        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                                    Articles.deleteComment(cc, postId, document.get("commentId").toString());
                                }
                            }

                        });

                        final StorageReference Photos = FirebaseStorage.getInstance().
                                getReference("Images/" + uid + "/" + postId);
                        Photos.child("post_pic.png").delete();
                        reference.document(postId)
                                .delete();


                    }
                });
        builder.show();

    }

    public static void interactiveScroll(int dy, Activity cc) {

        View bottomNav = cc.findViewById(R.id.bottom_nav);
        View fab = cc.findViewById(R.id.fab);

        if (dy > 0) {
            fadeOut(bottomNav);
            fadeOut(fab);

        } else {
            fadeIn(bottomNav);
            fadeIn(fab);

        }

    }

    public static void editComment(final Context applicationContext, final comment_item comment, final String postId) {
        final DocumentReference query = FirebaseFirestore.getInstance()
                .collection("comments").document("posts").
                        collection(postId).document(comment.getCommentId());
        View layout = LayoutInflater.from(applicationContext).inflate(R.layout.general_edit_comment, null);
        final EditText comment_editText = layout.findViewById(R.id.edit_comment);
        comment_editText.setText(comment.getComment());
        final AlertDialog builder = new AlertDialog.Builder(applicationContext).setTitle("Edit Comment").setPositiveButton(
                "Edit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, int which) {
                        // manipulate the edited comment

                        if (!TextUtils.isEmpty(comment_editText.getText().toString()) && !comment_editText.getText().toString().equals(comment.getComment())) {
                            HashMap<String, Object> commMap = new HashMap<>();
                            String tex = comment_editText.getText().toString().trim();
                            Calendar cc = Calendar.getInstance();
                            String month = String.valueOf(cc.get(Calendar.MONTH) + 1);
                            String date = cc.get(Calendar.YEAR) + "/" + month + "/" + cc.get(Calendar.DAY_OF_MONTH);
                            commMap.put("uid", uid);
                            commMap.put("likes", comment.getLikes());
                            commMap.put("date", date);
                            commMap.put("comment", tex);
                            commMap.put("commentId", comment.getCommentId());
                            query.set(commMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(applicationContext, applicationContext.getString(R.string.succes), Toast.LENGTH_SHORT).show();
                                        dialog.dismiss();

                                    } else {
                                        Toast.makeText(applicationContext, applicationContext.getString(R.string.failed), Toast.LENGTH_SHORT).show();
                                        dialog.dismiss();

                                    }
                                }

                            });
                        }


                        // dismiss ....
                    }
                }
        ).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).setView(layout).show();


    }

    public static void deleteComment(final Context applicationContext, String postId, final String commentId) {
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("replies")
                .child(postId).child(commentId);

        final DocumentReference comment_likes = FirebaseFirestore.getInstance().collection("comments_emotions")
                .document("posts").collection(postId).document(commentId);
        final CollectionReference query = FirebaseFirestore.getInstance()
                .collection("comments").document("posts").collection(postId);
        AlertDialog.Builder builder = new AlertDialog.
                Builder(applicationContext).setTitle(applicationContext.getString(R.string.delete_post_title)).
                setMessage(applicationContext.getString(R.string.are_us_sure_delte_post))
                .setNegativeButton(applicationContext.getString(R.string.cancel),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).setPositiveButton(applicationContext.getString(R.string.delete_post),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                reference.removeValue();
                                query.document(commentId)
                                        .delete();
                                comment_likes.delete();

                            }
                        });
        builder.show();

    }

    public static void reportComment(final Context applicationContext, final comment_item comment_item, final String postId) {
        final AlertDialog dialog = new AlertDialog.Builder(applicationContext).create();
        dialog.setTitle(applicationContext.getString(R.string.report));
        final View report_layout = LayoutInflater.from(applicationContext).inflate(R.layout.general_report_layout, null);
        final EditText describe_report = report_layout.findViewById(R.id.describe_report);
        final Button report = report_layout.findViewById(R.id.submit_report);
        final Button cancel = report_layout.findViewById(R.id.cancel_report);
        final HashMap<String, Object> report_map = new HashMap<>();

        final CollectionReference ref = FirebaseFirestore.getInstance().collection("reports");
        final Calendar ccc = Calendar.getInstance();
        String month = String.valueOf(ccc.get(Calendar.MONTH) + 1);
        String date = ccc.get(Calendar.YEAR) + month + ccc.get(Calendar.DAY_OF_MONTH);
        final String reportKey = date + ccc.getTimeInMillis();


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
                    getNames(comment_item.getUid());
                    report_map.put("reporter", reporterName);
                    report_map.put("poster", posterName);
                    report_map.put("post", postId);
                    report_map.put("comment", comment_item.getCommentId());
                    report_map.put("description", report_text);
                    ref.document(reportKey).set(report_map).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(applicationContext, "Report Sent Successfully, Thanks for contacting us", Toast.LENGTH_SHORT).show();
                            } else
                                Toast.makeText(applicationContext, "Failed Sending Report, Please Try Again", Toast.LENGTH_SHORT).show();

                        }
                    });
                    dialog.dismiss();

                } else {

                    Toast.makeText(applicationContext, "Please describe your issue ", Toast.LENGTH_SHORT).show();

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

    public static void updateLikes(final View itemView, final View vv, String commentId, final Long likes_count, String postId) {

        final DocumentReference query = FirebaseFirestore.getInstance()
                .collection("comments").document("posts").collection(postId).document(commentId);
        final DocumentReference comment_likes = FirebaseFirestore.getInstance().collection("comments_emotions")
                .document("posts").collection(postId).document(commentId);
        final HashMap<String, Object> map = new HashMap<>();
        final HashMap<String, Long> comment = new HashMap<>();
        comment_likes.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (!documentSnapshot.contains(uid)) {
                    map.put(uid, "1");
                    comment_likes.set(map, SetOptions.merge());
                    Long likes = likes_count + 1;
                    comment.put("likes", likes);
                    vv.setEnabled(false);


                } else {

                    map.put(uid, FieldValue.delete());
                    comment_likes.set(map, SetOptions.merge());
                    Long likes = likes_count - 1;
                    comment.put("likes", likes);
                    vv.setEnabled(false);

                }
                setLikedStatus(itemView, comment, query);

            }
        });


    }

    public static void setLikedStatus(View itemView, HashMap<String, Long> emotions, DocumentReference query) {
        TextView comm = itemView.findViewById(R.id.comm_likes);

        itemView.findViewById(R.id.like_comm).setEnabled(true);
        // ;;;;;;;;;;;;
        query.set(emotions, SetOptions.merge());
        if (!emotions.get("likes").equals("0")) {
            comm.setVisibility(View.VISIBLE);

            comm.setText(Long.toString(emotions.get("likes")));
        } else
            comm.setVisibility(View.GONE);


    }

    public static void setLikedStatus(View itemView, HashMap<String, Object> emotions, DatabaseReference query) {
        TextView comm = itemView.findViewById(R.id.comm_likes);

        itemView.findViewById(R.id.like_comm).setEnabled(true);
        // ;;;;;;;;;;;;
        query.updateChildren(emotions);
        if (!emotions.get("likes").equals("0")) {
            comm.setVisibility(View.VISIBLE);

            comm.setText(Long.toString(((Long) emotions.get("likes")

            )));
        } else
            comm.setVisibility(View.GONE);


    }

    public static void editReply(final Context cc, String commentID, final reply_item comment, String postId) {

        final DatabaseReference query = FirebaseDatabase.getInstance().
                getReference().child("replies/" + postId).child(commentID).child(comment.getCommentId());
        View layout = LayoutInflater.from(cc).inflate(R.layout.general_edit_comment, null);
        final EditText comment_editText = layout.findViewById(R.id.edit_comment);
        comment_editText.setText(comment.getComment());
        final AlertDialog builder = new AlertDialog.Builder(cc).setTitle("Edit Reply").setPositiveButton(
                "Edit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, int which) {
                        // manipulate the edited comment

                        if (!TextUtils.isEmpty(comment_editText.getText().toString()) && !comment_editText.getText().toString().equals(comment.getComment())) {
                            HashMap<String, Object> commMap = new HashMap<>();
                            String tex = comment_editText.getText().toString().trim();
                            Calendar ccc = Calendar.getInstance();
                            String month = String.valueOf(ccc.get(Calendar.MONTH) + 1);
                            String date = ccc.get(Calendar.YEAR) + "/" + month + "/" + ccc.get(Calendar.DAY_OF_MONTH);
                            commMap.put("uid", uid);
                            commMap.put("likes", comment.getLikes());
                            commMap.put("date", date);
                            commMap.put("comment", tex);
                            commMap.put("commentId", comment.getCommentId());
                            query.setValue(commMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(cc, cc.getString(R.string.succes), Toast.LENGTH_SHORT).show();
                                        dialog.dismiss();

                                    } else {
                                        Toast.makeText(cc, cc.getString(R.string.failed), Toast.LENGTH_SHORT).show();
                                        dialog.dismiss();

                                    }
                                }

                            });
                        }


                        // dismiss ....
                    }
                }
        ).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).setView(layout).show();


    }

    public static void deleteReply(Context cc, String postId, final String commentID, String id) {
        final DatabaseReference reference = FirebaseDatabase.getInstance().
                getReference().child("replies/" + postId).child(commentID).child(id);
        final DocumentReference comment_likes = FirebaseFirestore.getInstance().collection("replies_emotions")
                .document("posts").collection(commentID).document(id);

        AlertDialog.Builder builder = new AlertDialog.
                Builder(cc).setTitle(cc.getString(R.string.delete_post_title)).
                setMessage(cc.getString(R.string.are_us_sure_delte_post))
                .setNegativeButton(cc.getString(R.string.cancel),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).setPositiveButton(cc.getString(R.string.delete_post),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                reference.removeValue();
                                comment_likes.delete();

                            }
                        });
        builder.show();

    }

    public static void reportReply(final Context applicationContext, final reply_item comment, final String commentID, final String postId) {

        final AlertDialog dialog = new AlertDialog.Builder(applicationContext).create();
        dialog.setTitle(applicationContext.getString(R.string.report));
        final View report_layout = LayoutInflater.from(applicationContext).inflate(R.layout.general_report_layout, null);
        final EditText describe_report = report_layout.findViewById(R.id.describe_report);
        final Button report = report_layout.findViewById(R.id.submit_report);
        final Button cancel = report_layout.findViewById(R.id.cancel_report);
        final HashMap<String, Object> report_map = new HashMap<>();

        final CollectionReference ref = FirebaseFirestore.getInstance().collection("reports");
        final Calendar ccc = Calendar.getInstance();
        String month = String.valueOf(ccc.get(Calendar.MONTH) + 1);
        String date = ccc.get(Calendar.YEAR) + month + ccc.get(Calendar.DAY_OF_MONTH);
        final String reportKey = date + ccc.getTimeInMillis();


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
                    getNames(comment.getUid());
                    report_map.put("reporter", reporterName);
                    report_map.put("poster", posterName);
                    report_map.put("post", postId);
                    report_map.put("reply", comment.getCommentId());
                    report_map.put("comment", commentID);
                    report_map.put("description", report_text);
                    ref.document(reportKey).set(report_map).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(applicationContext, "Report Sent Successfully, Thanks for contacting us", Toast.LENGTH_SHORT).show();
                            } else
                                Toast.makeText(applicationContext, "Failed Sending Report, Please Try Again", Toast.LENGTH_SHORT).show();

                        }
                    });
                    dialog.dismiss();

                } else {

                    Toast.makeText(applicationContext, "Please describe your issue ", Toast.LENGTH_SHORT).show();

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

    public static void updateRepliesLikes(final View itemView, final View vv,
                                          String commentId, String replyId, final Long likes_count, String postId) {


        final DatabaseReference query = FirebaseDatabase.getInstance().
                getReference().child("replies/" + postId).child(commentId).child(replyId);
        final DocumentReference comment_likes = FirebaseFirestore.getInstance().collection("replies_emotions")
                .document("posts").collection(commentId).document(replyId);
        final HashMap<String, Object> map = new HashMap<>();
        final HashMap<String, Object> comment = new HashMap<>();
        comment_likes.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (!documentSnapshot.contains(uid)) {
                    map.put(uid, "1");
                    comment_likes.set(map, SetOptions.merge());
                    Long likes = likes_count + 1;
                    comment.put("likes", likes);
                    vv.setEnabled(false);


                } else {

                    map.put(uid, FieldValue.delete());
                    comment_likes.set(map, SetOptions.merge());
                    Long likes = likes_count - 1;
                    comment.put("likes", likes);
                    vv.setEnabled(false);

                }
                setLikedStatus(itemView, comment, query);

            }
        });


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.ideas_fragment_articles, container, false);
    }

    public static void getNames(String posterUid) {

        FirebaseDatabase ref = FirebaseDatabase.getInstance();
        ref.getReference("users/" + posterUid + "/name").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                posterName = dataSnapshot.getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        ref.getReference("users/" + uid + "/name").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                reporterName = dataSnapshot.getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    public static void fadeIn(final View loadingView) {
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

    public static void fadeOut(final View loadingView) {
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

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        ViewPager pages = view.findViewById(R.id.viewPager_fragment);
        TabLayout tabs = view.findViewById(R.id.tabLayout);
        pages.setAdapter(new MyTabPagerAdapter(getChildFragmentManager()));
        tabs.setupWithViewPager(pages);
        fab = view.findViewById(R.id.fab);
        // fabs ..manipulation ...
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mlistener.FabEventClicked();

            }
        });
    }

    public interface onFabClicked {

        void FabEventClicked();
    }

    public class MyTabPagerAdapter extends FragmentPagerAdapter {
        MyTabPagerAdapter(FragmentManager fm) {
            super(fm, FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getString(R.string.news);
                case 1:
                    return getString(R.string.bestof);
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return 2;
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new news();
                case 1:
                    return new bestOf();
                default:
                    break;
            }
            return null;
        }
    }

}