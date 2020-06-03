package com.example.esibetter.courses;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Gallery;
import android.widget.GridView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.esibetter.R;


public class SecondYear extends Fragment {

    ComponentAdapter adapter, adapter2;
    int what_yearIs;
    TypedArray modules;
    String[] modules_names;

    public SecondYear() {
        // Required empty public constructor
    }

    public SecondYear(int what_year) {
        // Required empty public constructor
        what_yearIs = what_year;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.general_second_year, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {


        setUpModules(getView(), getContext());


    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void setUpModules(View view, Context context) {


        // set up the RecyclerView
        Gallery recyclerView = view.findViewById(R.id.horizontal_gallery);
        adapter = new ComponentAdapter(getContext(), String.valueOf(what_yearIs));
        recyclerView.setAdapter(adapter);
        recyclerView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                openAll(2, "all");
            }
        });


        // set up the RecyclerView
        GridView recyclerView2 = view.findViewById(R.id.grid_modules);
        adapter2 = new ComponentAdapter(context, String.valueOf(what_yearIs));
        recyclerView2.setAdapter(adapter2);
        recyclerView2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String mod = context.getResources().getStringArray(R.array.second_modules_titles)[position];
                openAll(2, mod);
            }
        });


    }

    private void openAll(int k, String m) {

        getActivity().getSupportFragmentManager().beginTransaction()
                .remove(new Courses()).replace(R.id.fragment_container_view_tag, new showAll(k, m)).addToBackStack(null).commit();


    }

}
