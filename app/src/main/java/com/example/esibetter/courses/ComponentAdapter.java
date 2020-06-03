package com.example.esibetter.courses;

import android.content.Context;
import android.content.res.TypedArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.esibetter.R;


public class ComponentAdapter extends BaseAdapter {


    private Context mContext;
    private String[] titleTab;
    private TypedArray iimgTab;

    // data is passed into the constructor
    ComponentAdapter(Context context, String c) {
        this.mContext = context;


        if (c == "1") {
            this.titleTab = mContext.getResources().getStringArray(R.array.first_modules_titles);
            this.iimgTab = mContext.getResources().obtainTypedArray(R.array.first_modules_images);


        } else {
            this.titleTab = mContext.getResources().getStringArray(R.array.second_modules_titles);
            this.iimgTab = mContext.getResources().obtainTypedArray(R.array.second_modules_images);

        }


    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        convertView = LayoutInflater.from(mContext).inflate(R.layout.general_component_item, parent, false);
        TextView tx = convertView.findViewById(R.id.module_name);
        ImageView imageView = convertView.findViewById(R.id.module_photo);
        tx.setText(titleTab[position]);
        imageView.setImageResource(iimgTab.getResourceId(position, 0));
        return convertView;

    }

    @Override
    public int getCount() {
        return iimgTab.length();
    }

    @Override
    public Object getItem(int position) {
        return new component_item(titleTab[position], iimgTab.getResourceId(position, 0));
    }


}


