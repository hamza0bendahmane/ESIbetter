package com.example.esibetter.courses;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.esibetter.R;
import com.example.esibetter.courses.Tasks.UploadPdfActivity;

import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;


public class Summaries extends Fragment implements View.OnClickListener {
    private String[] titleTab, titleTab2;

    // data is passed into the constructor


    public Summaries() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.courses_fragment_summaries, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        loadLocal();
        titleTab = getContext().getResources().getStringArray(R.array.first_modules_titles);
        titleTab2 = getContext().getResources().getStringArray(R.array.second_modules_titles);

        view.findViewById(R.id.compo01).setOnClickListener(this);
        view.findViewById(R.id.compo02).setOnClickListener(this);
        view.findViewById(R.id.compo03).setOnClickListener(this);
        view.findViewById(R.id.compo04).setOnClickListener(this);
        view.findViewById(R.id.compo05).setOnClickListener(this);
        view.findViewById(R.id.compo06).setOnClickListener(this);
        view.findViewById(R.id.compo07).setOnClickListener(this);
        view.findViewById(R.id.compo08).setOnClickListener(this);
        view.findViewById(R.id.compo09).setOnClickListener(this);
        view.findViewById(R.id.compo010).setOnClickListener(this);
        view.findViewById(R.id.compo011).setOnClickListener(this);
        view.findViewById(R.id.compo012).setOnClickListener(this);
        view.findViewById(R.id.compo013).setOnClickListener(this);
        view.findViewById(R.id.compo014).setOnClickListener(this);
        view.findViewById(R.id.compo015).setOnClickListener(this);
        view.findViewById(R.id.compo1).setOnClickListener(this);
        view.findViewById(R.id.compo2).setOnClickListener(this);
        view.findViewById(R.id.compo3).setOnClickListener(this);
        view.findViewById(R.id.compo4).setOnClickListener(this);
        view.findViewById(R.id.compo5).setOnClickListener(this);
        view.findViewById(R.id.compo6).setOnClickListener(this);
        view.findViewById(R.id.compo7).setOnClickListener(this);
        view.findViewById(R.id.compo8).setOnClickListener(this);
        view.findViewById(R.id.compo9).setOnClickListener(this);
        view.findViewById(R.id.compo10).setOnClickListener(this);
        view.findViewById(R.id.compo11).setOnClickListener(this);
        view.findViewById(R.id.compo12).setOnClickListener(this);
        view.findViewById(R.id.compo13).setOnClickListener(this);
        view.findViewById(R.id.compo14).setOnClickListener(this);
        view.findViewById(R.id.compo15).setOnClickListener(this);
        view.findViewById(R.id.fab_one).setOnClickListener(this);

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

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        switch (viewId) {

            case R.id.fab_one:
                if (getUserVisibleHint()) {
                    Bundle bundle = new Bundle();
                    bundle.putString("type", "add");
                    //    Intent intent = new Intent(getContext(), Add_Tutorial.class);
                    Intent intent = new Intent(getContext(), UploadPdfActivity.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
                break;

            // ..........................


            case R.id.compo01:
                openAll(1, titleTab[0]);

                break;
            case R.id.compo02:
                openAll(1, titleTab[1]);

                break;
            case R.id.compo03:
                openAll(1, titleTab[2]);

                break;
            case R.id.compo04:
                openAll(1, titleTab[3]);

                break;
            case R.id.compo05:
                openAll(1, titleTab[4]);

                break;
            case R.id.compo06:
                openAll(1, titleTab[5]);

                break;
            case R.id.compo07:
                openAll(1, titleTab[6]);

                break;
            case R.id.compo08:
                openAll(1, titleTab[7]);

                break;
            case R.id.compo09:
                openAll(1, titleTab[8]);

                break;
            case R.id.compo010:
                openAll(1, titleTab[9]);

                break;
            case R.id.compo011:
                openAll(1, titleTab[10]);

                break;
            case R.id.compo012:
                openAll(1, titleTab[11]);

                break;
            case R.id.compo013:
                openAll(1, titleTab[12]);

                break;
            case R.id.compo014:
                openAll(1, titleTab[13]);

                break;
            case R.id.compo015:
                openAll(1, titleTab[14]);

                break;
            case R.id.compo1:
                openAll(2, titleTab2[0]);

                break;
            case R.id.compo2:
                openAll(2, titleTab2[1]);

                break;
            case R.id.compo3:
                openAll(2, titleTab2[2]);

                break;
            case R.id.compo4:
                openAll(2, titleTab2[3]);

                break;
            case R.id.compo5:
                openAll(2, titleTab2[4]);

                break;
            case R.id.compo6:
                openAll(2, titleTab2[5]);

                break;
            case R.id.compo7:
                openAll(2, titleTab2[6]);

                break;
            case R.id.compo8:
                openAll(2, titleTab2[7]);

                break;
            case R.id.compo9:
                openAll(2, titleTab2[8]);

                break;
            case R.id.compo10:
                openAll(2, titleTab2[9]);

                break;
            case R.id.compo11:
                openAll(2, titleTab2[10]);

                break;
            case R.id.compo12:
                openAll(2, titleTab2[11]);

                break;
            case R.id.compo13:
                openAll(2, titleTab2[12]);

                break;
            case R.id.compo14:
                openAll(2, titleTab2[13]);

                break;
            case R.id.compo15:
                openAll(2, titleTab2[14]);
                break;


        }
    }

    public void openAll(int k, String m) {
        Bundle b = new Bundle();
        b.putInt("year", k);
        b.putString("name", m);
        Intent ii = new Intent(getActivity(), ShowAllFiles.class);
        ii.putExtras(b);
        getActivity().startActivity(ii);


    }
}
