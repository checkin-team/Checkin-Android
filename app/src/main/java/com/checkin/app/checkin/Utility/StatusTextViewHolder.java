package com.checkin.app.checkin.Utility;

import android.support.annotation.DrawableRes;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.checkin.app.checkin.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StatusTextViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.tv_data) TextView tvData;
    @BindView(R.id.im_status) ImageView imStatus;

    public StatusTextViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void bindData(String data, @DrawableRes int drawableRes) {
        tvData.setText(data);
        imStatus.setImageResource(drawableRes);
    }

    public void setStatusClickListener(View.OnClickListener clickListener) {
        imStatus.setOnClickListener(clickListener);
    }

    public String getData() {
        return tvData.getText().toString();
    }
}
