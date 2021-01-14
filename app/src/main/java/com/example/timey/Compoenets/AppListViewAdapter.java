package com.example.timey.Compoenets;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.timey.R;

public class AppListViewAdapter extends ArrayAdapter<String> {
Context context;
int[]icons;
String[] appNames;

    public AppListViewAdapter(@NonNull Context context, int[]icons,String[] appNames) {
        super(context, R.layout.single_app_list_item,R.id.app_item_title,appNames);
        this.context = context;
        this.icons = icons;
        this.appNames = appNames;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View singleItem = convertView;
        ProgramViewHolder pvh =  null;
        if (singleItem == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            singleItem = inflater.inflate(R.layout.single_app_list_item,parent,false);
            pvh = new ProgramViewHolder(singleItem);
            singleItem.setTag(pvh);
        }else{
            pvh = (ProgramViewHolder) singleItem.getTag();
        }
        pvh.itemTitle.setText(appNames[position]);
        return super.getView(position, convertView, parent);
    }
}
