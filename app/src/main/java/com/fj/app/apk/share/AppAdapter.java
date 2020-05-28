package com.fj.app.apk.share;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class AppAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable {

    Context context;
    List<App> apps;
    List<App> appsFullList;
    private OnClickItemListener onClickItemListener;

    AppAdapter(Context context, List<App> apps, OnClickItemListener onClickItemListener){
        this.context = context;
        this.apps = apps;
        this.appsFullList = new ArrayList<>(apps);
        this.onClickItemListener = onClickItemListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.app_item, viewGroup, false);
        return new AppViewHolder(view,onClickItemListener);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        ((AppViewHolder)holder).appName.setText(apps.get(position).getName());
        long apkSize = apps.get(position).getApkSize();

        ((AppViewHolder)holder).apkSize.setText(getHumanReadableSize(apkSize));
        ((AppViewHolder)holder).appIcon.setImageDrawable(apps.get(position).getIcon());
    }

    private String getHumanReadableSize(long apkSize) {
        String humanReadableSize;
        if (apkSize < 1024) {
            humanReadableSize = String.format(
                    context.getString(R.string.app_size_b),
                    (double) apkSize
            );
        } else if (apkSize < Math.pow(1024, 2)) {
            humanReadableSize = String.format(
                    context.getString(R.string.app_size_kib),
                    (double) (apkSize / 1024)
            );
        } else if (apkSize < Math.pow(1024, 3)) {
            humanReadableSize = String.format(
                    context.getString(R.string.app_size_mib),
                    (double) (apkSize / Math.pow(1024, 2))
            );
        } else {
            humanReadableSize = String.format(
                    context.getString(R.string.app_size_gib),
                    (double) (apkSize / Math.pow(1024, 3))
            );
        }
        return humanReadableSize;
    }

    @Override
    public int getItemCount() {
        return apps.size();
    }

    // convenience method for getting data at click position
    public App getItem(int id) {
        return apps.get(id);
    }

    @Override
    public Filter getFilter() {
        return appFilter;
    }

    private Filter appFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            List<App> filteredList = new ArrayList<>();

            if (charSequence == null || charSequence.length() == 0){
                filteredList.addAll(appsFullList);
            }else {
                String filterPattern = charSequence.toString().toLowerCase().trim();

                for (App item: appsFullList) {
                    if (item.getName().toLowerCase().contains(filterPattern)){
                        filteredList.add(item);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;

            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            apps.clear();
            apps.addAll((List)filterResults.values);
            notifyDataSetChanged();
        }
    };
}

