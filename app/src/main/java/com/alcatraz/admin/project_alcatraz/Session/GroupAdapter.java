package com.alcatraz.admin.project_alcatraz.Session;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alcatraz.admin.project_alcatraz.R;

import java.util.ArrayList;

/**
 * Created by shivanshs9 on 6/5/18.
 */

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.GroupViewHolder> {

    private ArrayList<MenuGroup> groupList;

    public GroupAdapter(ArrayList<MenuGroup> groupsList) {
        this.groupList = groupsList;
    }

    @Override
    public int getItemCount() {
        return groupList.size();
    }

    @Override
    public int getItemViewType(final int position) {
        return R.layout.menu_card_layout;
    }

    @Override
    public GroupViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        final View itemView = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        return new GroupViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final GroupViewHolder groupViewHolder, final int position) {
        groupViewHolder.bindData(groupList.get(position));
    }

    public static class GroupViewHolder extends RecyclerView.ViewHolder {
        protected TextView vTitle;
        protected ImageView vImage;

        public GroupViewHolder(View v) {
            super(v);
            vTitle = v.findViewById(R.id.group_title);
            vImage = v.findViewById(R.id.group_image);
        }

        public void bindData(final MenuGroup menuGroup) {
            vTitle.setText(menuGroup.title);
            vImage.setImageBitmap(menuGroup.image);
        }
    }
}
