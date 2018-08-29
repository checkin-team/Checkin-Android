package com.checkin.app.checkin.Menu;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.checkin.app.checkin.R;
import com.checkin.app.checkin.Utility.DynamicSwipableViewPager;
import com.checkin.app.checkin.Utility.Util;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by shivanshs9 on 6/5/18.
 */

public class MenuGroupAdapter extends RecyclerView.Adapter<MenuGroupAdapter.GroupViewHolder> {
    private final String TAG = MenuGroupAdapter.class.getSimpleName();

    private List<MenuGroupModel> groupList;
    private GroupViewHolder mPrevExpandedViewHolder;
    private RecyclerView mRecyclerView;
    private Context mContext;
    private MenuItemAdapter.OnItemInteractionListener mItemInteractionListener;

    MenuGroupAdapter(List<MenuGroupModel> groupsList, Context context, MenuItemAdapter.OnItemInteractionListener itemInteractionListener) {
        this.groupList = groupsList;
        mContext = context;
        mItemInteractionListener = itemInteractionListener;
    }

    public void setGroupList(List<MenuGroupModel> groupList) {
        this.groupList = groupList;
        notifyDataSetChanged();
    }

    public boolean isGroupExpanded() {
        return mPrevExpandedViewHolder != null && mPrevExpandedViewHolder.isExpanded;
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mRecyclerView = recyclerView;
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (Math.abs(dy) > ViewConfiguration.get(mContext).getScaledTouchSlop())
                    contractView(mPrevExpandedViewHolder);
            }
        });
        mRecyclerView.addOnItemTouchListener(new RecyclerView.SimpleOnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                View v = rv.findChildViewUnder(e.getX(), e.getY());
                if (v == null)
                    return false;
                GroupViewHolder holder = ((GroupViewHolder) rv.findContainingViewHolder(v));
                if (holder != null && holder.isExpanded && holder.isScrollableList())
                    rv.requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return groupList != null ? groupList.size() : 0;
    }

    @Override
    public int getItemViewType(final int position) {
        return R.layout.menu_group_card_layout;
    }

    @NonNull
    @Override
    public GroupViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, final int viewType) {
        final View itemView = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        final GroupViewHolder holder = new GroupViewHolder(itemView);

        itemView.setOnClickListener(view -> {
            if (!holder.isExpanded) {
                contractView(mPrevExpandedViewHolder);
                expandView(holder);
            }
        });
        holder.vTitle.setOnClickListener(v -> {
            if (holder.isExpanded)
                contractView(holder);
            else {
                contractView(mPrevExpandedViewHolder);
                expandView(holder);
            }
        });
        return holder;
    }

    public void contractView() {
        contractView(mPrevExpandedViewHolder);
    }

    private void contractView(GroupViewHolder groupViewHolder) {
        if (groupViewHolder != null) {
            groupViewHolder.hideMenu(groupViewHolder.itemView);
            mPrevExpandedViewHolder = null;
        }
    }

    private void expandView(GroupViewHolder groupViewHolder) {
        if (groupViewHolder != null) {
            groupViewHolder.showMenu(groupViewHolder.itemView);
            ((LinearLayoutManager) mRecyclerView.getLayoutManager()).scrollToPositionWithOffset(groupViewHolder.getAdapterPosition(), 0);
            mPrevExpandedViewHolder = groupViewHolder;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final GroupViewHolder groupViewHolder, final int position) {
        groupViewHolder.bindData(groupList.get(position));
        if (groupViewHolder.isExpanded) {
            contractView(groupViewHolder);
        }
    }

    public class SubGroupPagerAdapter extends PagerAdapter {
        private List<String> mPages;
        private List<List<MenuItemModel>> mListItems;
        SubGroupPagerAdapter(MenuGroupModel menuGroup) {
            super();
            mPages = menuGroup.getSubGroups();
            mListItems = new ArrayList<>(mPages.size());
            for (int i = 0; i < mPages.size(); i++) {
                mListItems.add(new ArrayList<>());
            }
            List<MenuItemModel> items = menuGroup.getItems();
            for (MenuItemModel item: items) {
                mListItems.get(item.getSubGroupIndex()).add(item);
            }
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            ViewGroup layout = (ViewGroup) LayoutInflater.from(mContext).inflate(R.layout.menu_sub_group_layout, container, false);
            container.addView(layout);
            RecyclerView itemsList = layout.findViewById(R.id.sub_group_items);
            MenuItemAdapter itemAdapter = new MenuItemAdapter(mListItems.get(position));
            itemAdapter.setItemInteractionListener(mItemInteractionListener);
            itemsList.setAdapter(itemAdapter);
            return layout;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
        }

        @Override
        public int getCount() {
            return mPages.size();
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return mPages.get(position);
        }
    }

    public class GroupViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.group_title) TextView vTitle;
        @BindView(R.id.group_image) ImageView vImage;
        @BindView(R.id.sub_group_tabs) TabLayout vTabs;
        @BindView(R.id.sub_group_container) DynamicSwipableViewPager viewPager;
        @BindView(R.id.sub_group_wrapper) ViewGroup vSubGroupWrapper;
        SubGroupPagerAdapter pagerAdapter;
        boolean isExpanded = false;
        private MenuGroupModel mMenuGroup;

        GroupViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Util.setTabsFont(vTabs, mContext.getResources().getFont(R.font.arial_rounded_mt_bold));
            }
        }

        void bindData(final MenuGroupModel menuGroup) {
            mMenuGroup = menuGroup;
            vTitle.setText(menuGroup.getName());
            vImage.setImageResource(R.drawable.ic_beer);
            pagerAdapter = new SubGroupPagerAdapter(menuGroup);
            viewPager.setEnabled(false);
            if (menuGroup.getSubGroupsCount() > 1) {
                vTabs.setVisibility(View.VISIBLE);
                vTabs.setupWithViewPager(viewPager);
            } else {
                vTabs.setVisibility(View.GONE);
            }
            viewPager.setAdapter(pagerAdapter);
        }

        void showMenu(View view) {
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) vTitle.getLayoutParams();
            layoutParams.gravity = Gravity.TOP;
            vTitle.setLayoutParams(layoutParams);
            Animator sizeChangeAnim = Util.changeViewSize(view, Util.NO_CHANGE, (int) view.getResources().getDimension(R.dimen.menu_group_expanded_height));
            Animator hideImageAnim = Util.hideView(vImage);
            Animator showMenuAnim = Util.showView(vSubGroupWrapper);
            ValueAnimator scrollAnimator = ValueAnimator.ofInt(1, 2, 3, 4);
            scrollAnimator.setDuration(Util.DEFAULT_DURATION).addUpdateListener(animation ->
                    ((LinearLayoutManager) mRecyclerView.getLayoutManager()).scrollToPositionWithOffset(getAdapterPosition(), 0));
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.playTogether(sizeChangeAnim, hideImageAnim, showMenuAnim, scrollAnimator);
            isExpanded = true;
            animatorSet.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    vSubGroupWrapper.setVisibility(View.VISIBLE);
                }
            });
            animatorSet.start();
        }

        void hideMenu(View view) {
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) vTitle.getLayoutParams();
            layoutParams.gravity = Gravity.CENTER;
            vTitle.setLayoutParams(layoutParams);
            Animator sizeChangeAnim = Util.changeViewSize(view, Util.NO_CHANGE, (int) view.getResources().getDimension(R.dimen.menu_group_default_height));
            Animator showImageAnim = Util.showView(vImage);
            Animator hideMenuAnim = Util.hideView(vSubGroupWrapper);
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.playTogether(sizeChangeAnim, showImageAnim, hideMenuAnim);
            isExpanded = false;
            animatorSet.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    vSubGroupWrapper.setVisibility(View.GONE);
                }
            });
            animatorSet.start();
        }

        public boolean isScrollableList() {
            return mMenuGroup.getItems().size() > 4;
        }
    }
}
