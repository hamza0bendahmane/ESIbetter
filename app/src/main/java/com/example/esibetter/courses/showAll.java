package com.example.esibetter.courses;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.esibetter.R;


public class showAll extends Fragment {

    int what_yearIs;
    String moduleName;

    public showAll() {
        // Required empty public constructor
    }

    public showAll(int what_year, String moduleName) {
        // Required empty public constructor
        what_yearIs = what_year;
        this.moduleName = moduleName;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.general_all, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        if (what_yearIs == 1) {
            ((TextView) view.findViewById(R.id.parent_title)).setText("First Year");
            if (!moduleName.equals("all"))
                ((TextView) view.findViewById(R.id.child_title)).setText(moduleName);
            else
                ((TextView) view.findViewById(R.id.child_title)).setText("All Modules");

        } else {
            ((TextView) view.findViewById(R.id.parent_title)).setText("First Year");
            if (!moduleName.equals("all"))
                ((TextView) view.findViewById(R.id.child_title)).setText(moduleName);
            else
                ((TextView) view.findViewById(R.id.child_title)).setText("All Modules");
        }


    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    private void showTuto(String module) {

    }

}
