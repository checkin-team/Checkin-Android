package com.checkin.app.checkin.Search;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.checkin.app.checkin.R;
import com.checkin.app.checkin.Utility.GlideApp;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by Jogi Miglani on 28-10-2018.
 */

public class SearchTabsAdapter extends RecyclerView.Adapter<SearchTabsAdapter.SearchTabViewHolder> {
    private List<SearchModel> searchItems=new ArrayList<>();

    @NonNull
    @Override
    public SearchTabViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView= LayoutInflater.from(parent.getContext()).inflate(R.layout.search_rv_tab_item_view,parent,false);
        return new SearchTabViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchTabViewHolder holder, int position) {
        SearchModel searchModel =searchItems.get(position);
        holder.requiredTitle.setText(searchModel.getName());
        GlideApp.with(getApplicationContext()).load(searchModel.getImageUrl()).into(holder.imageView);

    }

    @Override
    public int getItemCount() {
        return searchItems.size();
    }

    class SearchTabViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView requiredTitle;
        CircleImageView imageView;
        public SearchTabViewHolder(View itemView) {
            super(itemView);
            requiredTitle=itemView.findViewById(R.id.search_item_title);
            imageView=itemView.findViewById(R.id.im_search_item);

        }

        @Override
        public void onClick(View v) {

        }
    }

    public void setListContent(List<SearchModel> list_members){
        this.searchItems=list_members;
        notifyDataSetChanged();
    }

    public void removeAt(int position) {
        searchItems.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(0, searchItems.size());
    }
}
