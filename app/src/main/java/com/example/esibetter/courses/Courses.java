package com.example.esibetter.courses;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.example.esibetter.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;

public class Courses extends Fragment {

    public static FloatingActionButton fab;
    public static String posterName, reporterName;
    public static final String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    public static ViewPager2 pages ;
    public static Activity mActivity ;
    public Courses() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.courses_fragment, container, false);
    }

    public static void reportFile(final Context cc,final String posterId ,final String post_Id) {
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
                    getNames(posterId);
                    report_map.put("reporter", reporterName);
                    report_map.put("poster", posterName);
                    report_map.put("file", post_Id);
                    report_map.put("description", report_text);
                    ref.document(reportKey).set(report_map).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(cc, R.string.repo_sensucc, Toast.LENGTH_SHORT).show();
                            } else
                                Toast.makeText(cc, R.string.failed_sen_rep, Toast.LENGTH_SHORT).show();

                        }
                    });
                    dialog.dismiss();

                } else {

                    Toast.makeText(cc, R.string.desc_iss, Toast.LENGTH_SHORT).show();

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

    public static void getNames(String posterUid) {

        FirebaseFirestore.getInstance().collection("users").document(
                uid).
                addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        reporterName = value.getData().get("name").toString();

                    }
                });
        FirebaseFirestore.getInstance().collection("users").document(
                posterUid).
                addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        posterName = value.getData().get("name").toString();

                    }
                });


    }

    public static void HandleBack(){
        if (pages.getCurrentItem() == 0) {

            AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
            builder.setTitle(R.string.close_app)
                    .setMessage(R.string.are_you_sure_close_appp)
                    .setCancelable(false)
                    .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            Intent a = new Intent(Intent.ACTION_MAIN);
                            a.addCategory(Intent.CATEGORY_HOME);
                            a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            mActivity.startActivity(a);

                        }
                    })
                    .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();

                        }
                    });
            //Creating dialog box
            AlertDialog dialog = builder.create();
            dialog.show();
        } else {

            pages.setCurrentItem(pages.getCurrentItem() - 1);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        //((EditText)view.findViewById(R.id.searchbarview)).setVisibility(View.INVISIBLE);
        loadLocal();
        pages = view.findViewById(R.id.viewPager_fragment);
        TabLayout tabs = view.findViewById(R.id.tabLayout);
        MyTabPagerAdapter tabPager = new Courses.MyTabPagerAdapter(getActivity());
        pages.setOffscreenPageLimit(3);
        pages.setAdapter(tabPager);
        mActivity = getActivity();
        new TabLayoutMediator(tabs, pages,
                new TabLayoutMediator.TabConfigurationStrategy() {
                    @Override
                    public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                        if (position == 0)
                            tab.setText(R.string.tutorialq);
                        else
                            tab.setText(R.string.summaries);
                    }
                }
        ).attach();
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

    public class MyTabPagerAdapter extends FragmentStateAdapter {
        MyTabPagerAdapter(FragmentActivity fa) {
            super(fa);
        }


        @NonNull
        @Override
        public Fragment createFragment(int position) {
            switch (position) {
                case 0:
                    return new Tutorials();
                case 1:
                    return new Summaries();
                default:
                    break;
            }
            return null;
        }

        @Override
        public int getItemCount() {
            return 2;
        }

    }

}