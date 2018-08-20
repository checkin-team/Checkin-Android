package com.checkin.app.checkin.Session;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.checkin.app.checkin.R;

import java.util.ArrayList;

/**
 * Created by shivanshs9 on 11/5/18.
 */

public class MenuSearchAdapter extends RecyclerView.Adapter<MenuSearchAdapter.SearchHolder> {
    private ArrayList<String> results;
    private final LayoutInflater layoutInflater;

    public MenuSearchAdapter(@NonNull Context context, ArrayList<String> previousResults) {
        layoutInflater = LayoutInflater.from(context);
        results = previousResults;
    }

    @Override
    public @NonNull SearchHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = layoutInflater.inflate(R.layout.menu_search_result_item, parent, false);
        return new SearchHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchHolder holder, int position) {
        final String result = results.get(position);
        holder.result.setText(result);
    }

    @Override
    public int getItemCount() {
        return results.size();
    }

    public void add(String result) {
        results.add(result);
        notifyItemInserted(results.size() - 1);
    }

    class SearchHolder extends RecyclerView.ViewHolder {
        TextView result;

        SearchHolder(View itemView) {
            super(itemView);
            result = itemView.findViewById(R.id.menu_search_result);
        }
    }
}
