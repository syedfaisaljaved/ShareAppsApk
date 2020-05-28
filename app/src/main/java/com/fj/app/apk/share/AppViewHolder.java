package com.fj.app.apk.share;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AppViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    LinearLayout parent;
    TextView appName;
    TextView apkSize;
    ImageView appIcon;
    OnClickItemListener mClickListener;

    public AppViewHolder(@NonNull View itemView, OnClickItemListener onClickItemListener) {
        super(itemView);
        mClickListener = onClickItemListener;
        appName = (TextView) itemView.findViewById(R.id.app_name);
        apkSize = (TextView) itemView.findViewById(R.id.app_size);
        appIcon = (ImageView) itemView.findViewById(R.id.image_icon);
        parent = itemView.findViewById(R.id.parent);
        itemView.setOnClickListener(this);
    }
    @Override
    public void onClick(View view) {
        mClickListener.onItemClick(getAdapterPosition());
    }
}
