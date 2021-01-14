package com.example.timey.Compoenets;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.timey.R;

public class ProgramViewHolder {
    ImageView itemIcon;
    TextView itemTitle,itemDesc;
    ProgramViewHolder(View v){
        itemIcon = v.findViewById(R.id.imageView);
        itemTitle = v.findViewById(R.id.app_item_title);
        itemDesc = v.findViewById(R.id.app_item_desc);
    }
}
