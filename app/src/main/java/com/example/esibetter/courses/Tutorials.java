package com.example.esibetter.courses;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Gallery;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.esibetter.R;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


public class Tutorials extends Fragment  {

    TutorialsAdapter firstAdapter, secondAdapter ;
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

        setUpModules(getView(), getContext());
        view.findViewById(R.id.fab_one).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (getUserVisibleHint()) {
                    Bundle bundle = new Bundle();
                    bundle.putString("type", "add");
                    //    Intent intent = new Intent(getContext(), Add_Tutorial.class);
                    Intent intent = new Intent(getContext(), Add_Tutorial.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            }

        });


    }



    private void setUpModules(View view, Context context) {
        RecyclerView summ1st =view.findViewById(R.id.first_year_recycler);
        RecyclerView summ2nd =view.findViewById(R.id.second_year_recycler);
        summ1st.setHasFixedSize(true);
        summ1st.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false));
        summ2nd.setHasFixedSize(true);
        summ2nd.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false));
        firstAdapter =  new TutorialsAdapter(context, "1");
        secondAdapter = new TutorialsAdapter(context, "2");
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
