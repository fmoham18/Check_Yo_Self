package com.example.timey.Compoenets;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.timey.R;

public class AppListViewAdapter extends ArrayAdapter<String> {
    Context context;

    String[] appNames;
    Drawable[] icons;

    public AppListViewAdapter(@NonNull Context context, String[] appNames, Drawable [] icons) {
        super(context, R.layout.single_app_list_item, R.id.app_item_title, appNames);
        this.context = context;
        this.appNames = appNames;
        this.icons = icons;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ProgramViewHolder pvh = null;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.single_app_list_item, parent, false);
            pvh = new ProgramViewHolder(convertView);
            convertView.setTag(pvh);
        } else {
            pvh = (ProgramViewHolder) convertView.getTag();
        }
        pvh.itemTitle.setText(appNames[position]);
        pvh.itemIcon.setImageDrawable(icons[position]);

        return convertView;
    }
}
