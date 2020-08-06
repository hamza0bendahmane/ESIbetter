package com.example.esibetter.courses;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.esibetter.R;

public class TutorialsAdapter extends RecyclerView.Adapter<TutorialsAdapter.ViewHolder> {


    private Context mContext;
    private String[] titleTab;
    private String c ;
    private TypedArray iimgTab;

    // data is passed into the constructor
    public TutorialsAdapter(Context context, String c) {
        this.mContext = context;
        this.c =c ;

        if (c == "1") {
            this.titleTab = mContext.getResources().getStringArray(R.array.first_modules_titles);
            this.iimgTab = mContext.getResources().obtainTypedArray(R.array.first_modules_images);


        } else {
            this.titleTab = mContext.getResources().getStringArray(R.array.second_modules_titles);
            this.iimgTab = mContext.getResources().obtainTypedArray(R.array.second_modules_images);

        }


    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.general_component_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        holder.tx.setText(titleTab[position]);
        holder.imageView.setImageResource(iimgTab.getResourceId(position,0));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAll(Integer.parseInt(c), titleTab[position]);
            }
        });
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return titleTab.length;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tx;
        public ImageView imageView;

        public ViewHolder(final View convertView ) {
            super(convertView);
            tx = convertView.findViewById(R.id.module_name);
            imageView = convertView.findViewById(R.id.module_photo);

        }
    }
    public  void openAll(int k, String m) {
        Bundle b = new Bundle();
        b.putInt("year",k);
        b.putString("name",m);
        Intent ii = new Intent(mContext, ShowAllTutorials.class);
        ii.putExtras(b);
        mContext.startActivity(ii);


    }

}



