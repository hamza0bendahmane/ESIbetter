package com.example.esibetter.courses;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.esibetter.R;
import com.example.esibetter.courses.Tasks.UploadPdfActivity;


public class Summaries extends Fragment {

    SummariesAdapter firstAdapter, secondAdapter ;
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
        setUpModules(view, getContext());
        view.findViewById(R.id.fab_one).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (getUserVisibleHint()) {
                    Bundle bundle = new Bundle();
                    bundle.putString("type", "add");
                    //    Intent intent = new Intent(getContext(), Add_Tutorial.class);
                    Intent intent = new Intent(getContext(), UploadPdfActivity.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            }

        });
    }

    private void setUpModules(View view, Context context) {
        RecyclerView summ1st =view.findViewById(R.id.first_year_summar);
        RecyclerView summ2nd =view.findViewById(R.id.second_year_summar);
        summ1st.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false));
        summ2nd.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false));
        firstAdapter =  new SummariesAdapter(context, "1");
        secondAdapter = new SummariesAdapter(context, "2");
        summ1st.setAdapter(firstAdapter);
        summ2nd.setAdapter(secondAdapter);

        firstAdapter.notifyDataSetChanged();
        secondAdapter.notifyDataSetChanged();




    }

    @Override
    public void onResume() {
        super.onResume();
        firstAdapter.notifyDataSetChanged();
        secondAdapter.notifyDataSetChanged();
    }

    @Override
    public void onStart() {
        super.onStart();
        firstAdapter.notifyDataSetChanged();
        secondAdapter.notifyDataSetChanged();
    }
}
