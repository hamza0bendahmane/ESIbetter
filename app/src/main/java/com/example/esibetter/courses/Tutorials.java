package com.example.esibetter.courses;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Gallery;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.esibetter.R;


public class Tutorials extends Fragment {

    ComponentAdapter adapter, adapter2;
    TypedArray first_modules;
    TypedArray second_modules;

    public Tutorials() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.courses_fragment_tutorials, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        first_modules = getResources().obtainTypedArray(R.array.first_modules_images);
        second_modules = getResources().obtainTypedArray(R.array.second_modules_images);
        setUpModules(getView(), getContext());


    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void setUpModules(View view, Context context) {

        Gallery recyclerView = view.findViewById(R.id.first_year_recycler);
        adapter = new ComponentAdapter(getContext(), "1");
        recyclerView.setAdapter(adapter);
        recyclerView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                open1stYear();
            }
        });

        Gallery recyclerView2 = view.findViewById(R.id.second_year_recycler);
        adapter2 = new ComponentAdapter(context, "2");
        recyclerView2.setAdapter(adapter2);
        recyclerView2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                open2ndYear();
            }
        });
        getView().findViewById(R.id.click_second).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                open2ndYear();
            }
        });
        getView().findViewById(R.id.click_first).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                open1stYear();
            }
        });


    }

    private void open1stYear() {
        getActivity().getSupportFragmentManager().beginTransaction()
                .remove(new Courses()).replace(R.id.fragment_container_view_tag, new FirstYear(1)).addToBackStack(null).commit();


    }


    private void open2ndYear() {

        getActivity().getSupportFragmentManager().beginTransaction()
                .remove(new Courses()).replace(R.id.fragment_container_view_tag, new SecondYear(2)).addToBackStack(null).commit();


    }
}
