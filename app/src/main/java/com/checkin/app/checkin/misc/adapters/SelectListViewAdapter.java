package com.checkin.app.checkin.misc.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.checkin.app.checkin.R;
import com.checkin.app.checkin.misc.SelectListItem;
import com.checkin.app.checkin.utility.GlideApp;

import java.util.List;

public class SelectListViewAdapter extends ArrayAdapter<SelectListItem> {

    public SelectListViewAdapter(Context context, List<SelectListItem> items) {
        super(context, R.layout.item_select_layout, items);
    }

    @NonNull
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder;
        SelectListItem item = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_select_layout, parent, false);
            holder = new ViewHolder();
            holder.tvText = convertView.findViewById(R.id.tv_text);
            holder.imImage = convertView.findViewById(R.id.im_image);
            convertView.setTag(holder);
        } else
            holder = (ViewHolder) convertView.getTag();

        if (item != null) {
            holder.tvText.setText(item.toString());
            GlideApp.with(getContext()).load(item.getImageUrl()).into(holder.imImage);
        }

        return convertView;
    }

    private static class ViewHolder {
        ImageView imImage;
        TextView tvText;
    }
}