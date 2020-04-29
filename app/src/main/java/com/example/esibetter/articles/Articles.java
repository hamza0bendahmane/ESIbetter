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
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import com.example.esibetter.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;


public class Articles extends Fragment {
    public static onFabClicked mlistener;
    public static final CollectionReference reference = FirebaseFirestore.getInstance()
            .collection("posts");
    public static boolean isopen = false;
    public static final String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    public static String posterName, reporterName;
    public static FloatingActionButton fabArticle, fabInitiative, fab;


    public Articles() {
        // Required empty public constructor
    }

    public static void setonFabClicked(onFabClicked listener) {
        mlistener = listener;
    }

    public static void setUpTheRefresh(View v, final ArticlesAdapter adapter) {
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

    public static void reportPost(final Context cc, final Article_item item, final String post_Id) {

        final AlertDialog dialog = new AlertDialog.Builder(cc).create();
        dialog.setTitle(cc.getString(R.string.report));
        final View report_layout = LayoutInflater.from(cc).inflate(R.layout.general_report_layout, null);
        final TextInputEditText describe_report = report_layout.findViewById(R.id.describe_report);
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

    public static void editPost(Context cc, int position, String postId) {
        Bundle bundle = new Bundle();
        bundle.putString("type", "edit");
        bundle.putInt("position", position);
        bundle.putString("postId", postId);
        Intent intent = new Intent(cc, Add_Articles.class);
        intent.putExtras(bundle);
        cc.startActivity(intent);


    }

    public static void deletePost(Context cc, final String postId) {

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
                        reference.document(postId)
                                .delete();
                    }
                });
        builder.show();

    }

    public static void interactiveScroll(int dy, Activity cc) {

        View bottomNav = cc.findViewById(R.id.bottom_nav);
        View fab = cc.findViewById(R.id.fab);
        View fabArt = cc.findViewById(R.id.fab_article);
        View fabInit = cc.findViewById(R.id.fab_event);

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


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.ideas_fragment_articles, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        ViewPager pages = view.findViewById(R.id.viewPager);
        TabLayout tabs = view.findViewById(R.id.tabLayout);
        pages.setAdapter(new MyTabPagerAdapter(getChildFragmentManager()));
        tabs.setupWithViewPager(pages);
        fab = view.findViewById(R.id.fab);
        fabArticle = view.findViewById(R.id.fab_article);
        fabInitiative = view.findViewById(R.id.fab_event);
        fabArticle.setVisibility(View.GONE);
        fabInitiative.setVisibility(View.GONE);
        // fabs ..manipulation ...
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isopen) {
                    fabArticle.setVisibility(View.GONE);
                    fabInitiative.setVisibility(View.GONE);
                    fab.animate().setDuration(200)
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    super.onAnimationEnd(animation);
                                }
                            })
                            .rotation(0f);
                    isopen = false;
                } else {

                    fabArticle.setVisibility(View.VISIBLE);
                    fabInitiative.setVisibility(View.VISIBLE);
                    fab.animate().setDuration(200)
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    super.onAnimationEnd(animation);
                                }
                            })
                            .rotation(135f);
                    isopen = true;
                }

            }
        });


        // fab Action ...
        fabArticle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mlistener != null) {
                    mlistener.FabClicked();
                }
            }
        });
        fabInitiative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mlistener != null) {
                    mlistener.FabEventClicked();
                }
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onStop() {
        super.onStop();
    }

    public interface onFabClicked {
        void FabClicked();

        void FabEventClicked();
    }

    public class MyTabPagerAdapter extends FragmentStatePagerAdapter {
        MyTabPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getString(R.string.news);
                case 1:
                    return getString(R.string.trending);
                case 2:
                    return getString(R.string.bestof);
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return 3;
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new news();
                case 1:
                    return new trending();
                case 2:
                    return new bestOf();
                default:
                    break;
            }
            return null;
        }
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

    public static void getNames(String posterUid) {
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