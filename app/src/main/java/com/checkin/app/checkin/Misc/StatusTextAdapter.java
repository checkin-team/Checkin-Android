package com.checkin.app.checkin.Misc;

import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.checkin.app.checkin.R;

import java.util.Arrays;
import java.util.List;

public class StatusTextAdapter extends RecyclerView.Adapter<StatusTextViewHolder> {
    private List<String> mData;

    @DrawableRes
    private int mStatusRes;

    public StatusTextAdapter(int statusRes) {
        mStatusRes = statusRes;
    }

    public void setData(String... data) {
        mData = Arrays.asList(data);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public StatusTextViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        return new StatusTextViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StatusTextViewHolder holder, int position) {
        holder.bindData(mData.get(position), mStatusRes);
    }

    @Override
    public int getItemViewType(int position) {
        return R.layout.item_status_text;
    }

    @Override
    public int getItemCount() {
        return mData != null ? mData.size() : 0;
    }
}
