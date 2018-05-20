package com.alcatraz.admin.project_alcatraz.Session;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alcatraz.admin.project_alcatraz.R;
import com.alcatraz.admin.project_alcatraz.Utility.Util;

import java.util.ArrayList;

/**
 * Created by shivanshs9 on 6/5/18.
 */

public class MenuGroupAdapter extends RecyclerView.Adapter<MenuGroupAdapter.GroupViewHolder> {

    private ArrayList<MenuGroup> groupList;
    private GroupViewHolder mPrevExpandedViewHolder = null;
    private RecyclerView mRecyclerView;

    public MenuGroupAdapter(ArrayList<MenuGroup> groupsList) {
        this.groupList = groupsList;
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mRecyclerView = recyclerView;
        mRecyclerView.requestDisallowInterceptTouchEvent(true);
    }

    @Override
    public int getItemCount() {
        return groupList.size();
    }

    @Override
    public int getItemViewType(final int position) {
        return R.layout.menu_group_card_layout;
    }

    @NonNull
    @Override
    public GroupViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, final int viewType) {
        final View itemView = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        final GroupViewHolder holder = new GroupViewHolder(itemView, this);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("GroupView", "Clicked");
                final int position = holder.getAdapterPosition();
                if (!holder.isExpanded) {
                    if (mPrevExpandedViewHolder != null)
                        contractView(mPrevExpandedViewHolder);
                    ((LinearLayoutManager) mRecyclerView.getLayoutManager()).scrollToPositionWithOffset(position, 0);
                    expandView(holder);
                }
                else {
                    contractView(holder);
                }
            }
        });
        return holder;
    }

    public void contractView(GroupViewHolder groupViewHolder) {
        groupViewHolder.hideMenu(groupViewHolder.itemView);
        mPrevExpandedViewHolder = null;
    }

    public void expandView(GroupViewHolder groupViewHolder) {
        groupViewHolder.showMenu(groupViewHolder.itemView);
        mPrevExpandedViewHolder = groupViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final GroupViewHolder groupViewHolder, final int position) {
        groupViewHolder.bindData(groupList.get(position));
        if (groupViewHolder.isExpanded) {
            contractView(groupViewHolder);
        }
    }

    static class GroupViewHolder extends RecyclerView.ViewHolder {
        TextView vTitle;
        ImageView vImage;
        RecyclerView vGroupMenu;
//        RecyclerView vRecMenu;
        MenuGroupAdapter menuGroupAdapter;
        boolean isExpanded = false;

        GroupViewHolder(View v, MenuGroupAdapter groupAdapter) {
            super(v);
            vTitle = v.findViewById(R.id.group_title);
            vImage = v.findViewById(R.id.group_image);
            vGroupMenu = v.findViewById(R.id.group_menu);
//            vRecMenu = v.findViewById(R.id.group_menu_list);
            menuGroupAdapter = groupAdapter;
        }

        void bindData(final MenuGroup menuGroup) {
            Log.e("MenuItemAdapter", "Binding data...");
            vTitle.setText(menuGroup.title);
            vImage.setImageBitmap(menuGroup.image);

            MenuItemAdapter menuItemAdapter = new MenuItemAdapter(menuGroup.items, menuGroupAdapter, this);
            vGroupMenu.setAdapter(menuItemAdapter);
            Log.e("MenuItemAdapter", "Done!");
        }

        void showMenu(View view) {
            Animator sizeChangeAnim = Util.changeViewSize(view, Util.NO_CHANGE, (int) view.getResources().getDimension(R.dimen.menu_group_expanded_height));
            Animator hideTitleAnim = Util.hideView(vTitle);
            Animator hideImageAnim = Util.hideView(vImage);
            Animator showMenuAnim = Util.showView(vGroupMenu);
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.playTogether(sizeChangeAnim, hideImageAnim, hideTitleAnim, showMenuAnim);
            isExpanded = true;
            animatorSet.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    vGroupMenu.setVisibility(View.VISIBLE);
                }
            });
            animatorSet.start();
        }

        void hideMenu(View view) {
            Animator sizeChangeAnim = Util.changeViewSize(view, Util.NO_CHANGE, (int) view.getResources().getDimension(R.dimen.menu_group_default_height));
            Animator showTitleAnim = Util.showView(vTitle);
            Animator showImageAnim = Util.showView(vImage);
            Animator hideMenuAnim = Util.hideView(vGroupMenu);
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.playTogether(sizeChangeAnim, showImageAnim, showTitleAnim, hideMenuAnim);
            isExpanded = false;
            animatorSet.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    vGroupMenu.setVisibility(View.GONE);
                }
            });
            animatorSet.start();
        }
    }
}
