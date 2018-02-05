package com.alcatraz.admin.project_alcatraz.Home;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.alcatraz.admin.project_alcatraz.R;

import java.util.List;


/**
 * Created by TAIYAB on 05-02-2018.
 */

public class Feeds extends RecyclerView.Adapter<Feeds.MyView> {

    private List<String> list;

    public static class MyView extends RecyclerView.ViewHolder {

        public TextView textView;

        public MyView(View view) {
            super(view);

            textView = (TextView) view.findViewById(R.id.textview2);

        }
    }


    public Feeds(List<String> horizontalList) {
        this.list = horizontalList;
    }

    @Override
    public MyView onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.vertical_item, parent, false);

        return new MyView(itemView);
    }

    @Override
    public void onBindViewHolder(final MyView holder, final int position) {
        Log.e("TAG",list.get(position));
        holder.textView.setText(list.get(position));


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

}