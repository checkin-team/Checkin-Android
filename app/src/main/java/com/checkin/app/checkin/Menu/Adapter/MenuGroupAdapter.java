package com.checkin.app.checkin.Menu.Adapter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.checkin.app.checkin.Menu.Fragment.MenuItemsFragment;
import com.checkin.app.checkin.Menu.MenuItemInteraction;
import com.checkin.app.checkin.Menu.Model.MenuGroupModel;
import com.checkin.app.checkin.Menu.Model.MenuItemModel;
import com.checkin.app.checkin.R;
import com.checkin.app.checkin.Utility.DynamicSwipableViewPager;
import com.checkin.app.checkin.Utility.GlideApp;
import com.checkin.app.checkin.Utility.Utils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by shivanshs9 on 6/5/18.
 */

public class MenuGroupAdapter extends RecyclerView.Adapter<MenuGroupAdapter.GroupViewHolder> {
    private static final String TAG = MenuGroupAdapter.class.getSimpleName();

    private List<MenuGroupModel> mGroupList;
    private GroupViewHolder mPrevExpandedViewHolder;
    private RecyclerView mRecyclerView;
    private FragmentManager mFragmentManager;

    @Nullable
    private MenuItemInteraction mListener;
    private boolean mIsSessionActive = true;

    public MenuGroupAdapter(List<MenuGroupModel> groupsList, FragmentManager fragmentManager, @Nullable MenuItemInteraction listener) {
        this.mGroupList = groupsList;
        this.mFragmentManager = fragmentManager;
        this.mListener = listener;
    }

    public void setSessionActive(boolean value) {
        mIsSessionActive = value;
    }

    public void setGroupList(List<MenuGroupModel> mGroupList) {
        this.mGroupList = mGroupList;
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
                if (Math.abs(dy) > ViewConfiguration.get(recyclerView.getContext()).getScaledTouchSlop())
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
        return mGroupList != null ? mGroupList.size() : 0;
    }

    @Override
    public int getItemViewType(final int position) {
        return R.layout.item_menu_group;
    }

    @NonNull
    @Override
    public GroupViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, final int viewType) {
        final View itemView = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        return new GroupViewHolder(itemView);
    }

    public String getCurrentCategory() {
        int position = ((LinearLayoutManager) mRecyclerView.getLayoutManager()).findFirstVisibleItemPosition();
        try {
            return mGroupList.get(position).getCategory();
        } catch (ArrayIndexOutOfBoundsException ignored) {
            return "";
        }
    }

    public int getCategoryPosition(@NonNull String category) {
        int i = 0;
        if (mGroupList != null) {
            for (MenuGroupModel groupModel : mGroupList) {
                if (category.contentEquals(groupModel.getCategory()))
                    break;
                i++;
            }
        }
        return i;
    }

    public void contractView() {
        contractView(mPrevExpandedViewHolder);
    }

    public void expandView() {
        expandView(mPrevExpandedViewHolder);
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
        groupViewHolder.bindData(mGroupList.get(position));
        if (groupViewHolder.isExpanded) {
            contractView(groupViewHolder);
        }
    }

    public class GroupViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_menu_group_name) TextView tvGroupName;
        @BindView(R.id.im_menu_group_icon) ImageView imGroupIcon;

        @BindView(R.id.tabs_menu_sub_groups) TabLayout vTabs;
        @BindView(R.id.pager_menu_items) DynamicSwipableViewPager vPager;
        @BindView(R.id.container_menu_sub_groups) ViewGroup vSubGroupWrapper;

        private MenuGroupModel mMenuGroup;

        protected boolean isExpanded = false;

        GroupViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Utils.setTabsFont(vTabs, itemView.getResources().getFont(R.font.arial_rounded_mt_bold));
            }
            vPager.setEnabled(false);

            itemView.setOnClickListener(view -> {
                if (!this.isExpanded) {
                    contractView(mPrevExpandedViewHolder);
                    expandView(this);
                }
            });
            tvGroupName.setOnClickListener(v -> {
                if (this.isExpanded)
                    contractView(this);
                else {
                    contractView(mPrevExpandedViewHolder);
                    expandView(this);
                }
            });
        }

        void bindData(final MenuGroupModel menuGroup) {
            mMenuGroup = menuGroup;

            tvGroupName.setText(menuGroup.getName());
            GlideApp.with(itemView).load(menuGroup.getIcon()).into(imGroupIcon);
            SubGroupPagerAdapter pagerAdapter = new SubGroupPagerAdapter(menuGroup);
            if (menuGroup.hasSubGroups()) {
                vTabs.setVisibility(View.VISIBLE);
                vTabs.setupWithViewPager(vPager);
            } else {
                vTabs.setVisibility(View.GONE);
            }
            vPager.setAdapter(pagerAdapter);
        }

        void showMenu(View view) {
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) tvGroupName.getLayoutParams();
            layoutParams.gravity = Gravity.TOP;
            tvGroupName.setLayoutParams(layoutParams);
            Animator sizeChangeAnim = Utils.changeViewSize(view, Utils.NO_CHANGE, (int) view.getResources().getDimension(R.dimen.height_menu_group_expanded));
            Animator hideImageAnim = Utils.hideView(imGroupIcon);
            Animator showMenuAnim = Utils.showView(vSubGroupWrapper);
            ValueAnimator scrollAnimator = ValueAnimator.ofInt(1, 2, 3, 4);
            scrollAnimator.setDuration(Utils.DEFAULT_DURATION).addUpdateListener(animation ->
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
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) tvGroupName.getLayoutParams();
            layoutParams.gravity = Gravity.CENTER;
            tvGroupName.setLayoutParams(layoutParams);
            Animator sizeChangeAnim = Utils.changeViewSize(view, Utils.NO_CHANGE, (int) view.getResources().getDimension(R.dimen.height_menu_group_collapsed));
            Animator showImageAnim = Utils.showView(imGroupIcon);
            Animator hideMenuAnim = Utils.hideView(vSubGroupWrapper);
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
            return mMenuGroup.getItems().size() >= 4;
        }
    }

    public class SubGroupPagerAdapter extends FragmentStatePagerAdapter {
        private List<List<MenuItemModel>> mListItems;

        SubGroupPagerAdapter(MenuGroupModel menuGroup) {
            super(mFragmentManager);
            mListItems = new ArrayList<>();
            if (menuGroup.hasSubGroups()) {
                mListItems.add(menuGroup.getVegItems());
                mListItems.add(menuGroup.getNonVegItems());
            } else {
                mListItems.add(menuGroup.getItems());
            }
        }

        @Override
        public Fragment getItem(int position) {
            return MenuItemsFragment.newInstance(mListItems.get(position), mListener, mIsSessionActive);
        }

        @Override
        public int getCount() {
            return mListItems.size();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Veg";
                case 1:
                    return "Non-Veg";
            }
            return "";
        }
    }
}
