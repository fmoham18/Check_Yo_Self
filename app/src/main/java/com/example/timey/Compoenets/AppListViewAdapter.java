package com.example.timey.Compoenets;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.timey.R;

import java.util.HashSet;
import java.util.Set;

public class AppListViewAdapter extends ArrayAdapter<String> {
    Context context;

    String[] appNames, packageNames;
    Drawable[] icons;
    Set<String> selectedApps;

    public AppListViewAdapter(@NonNull Context context, String[] appNames, String[] packageNames, Drawable[] icons, HashSet<String> selectedApps) {
        super(context, R.layout.single_app_list_item, R.id.app_item_title, appNames);
        this.context = context;
        this.appNames = appNames;
        this.packageNames = packageNames;
        this.icons = icons;
        this.selectedApps = selectedApps;
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

        String packageName = packageNames[position];
        pvh.itemTitle.setText(appNames[position]);
        pvh.itemIcon.setImageDrawable(icons[position]);
        pvh.itemDesc.setText(packageName);
        // TODO: the background coloring on click
        // TODO: can cause bug when the list is already populated
        convertView.setOnClickListener(view -> {
            if (selectedApps.contains(packageName)) {
                selectedApps.remove(packageName);
                view.setBackgroundColor(Color.WHITE);

            } else {
                selectedApps.add(packageName);

                view.setBackgroundColor(Color.BLUE);
            }
        });

        if (selectedApps.contains(packageName)) {
            convertView.setBackgroundColor(Color.BLUE);
        } else {
            convertView.setBackgroundColor(Color.WHITE);
        }
        return convertView;
    }
}
