package com.checkin.app.checkin.Menu.ShopMenu.Adapter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.checkin.app.checkin.Menu.MenuItemInteraction;
import com.checkin.app.checkin.Menu.Model.MenuGroupModel;
import com.checkin.app.checkin.Menu.ShopMenu.Fragment.MenuItemsFragment;
import com.checkin.app.checkin.R;
import com.checkin.app.checkin.Utility.AnimUtils;
import com.checkin.app.checkin.Utility.DynamicSwipableViewPager;
import com.checkin.app.checkin.Utility.GlideApp;
import com.checkin.app.checkin.Utility.Utils;
import com.google.android.material.tabs.TabLayout;

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

    public boolean hasData() {
        return mGroupList != null && !mGroupList.isEmpty();
    }

    public boolean isGroupExpanded() {
        return mPrevExpandedViewHolder != null && mPrevExpandedViewHolder.isExpanded;
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mRecyclerView = recyclerView;
        /*mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView rvNearbyRestaurants, int dx, int dy) {
                super.onScrolled(rvNearbyRestaurants, dx, dy);
                if (Math.abs(dy) > ViewConfiguration.get(rvNearbyRestaurants.getContext()).getScaledTouchSlop())
                    contractView(mPrevExpandedViewHolder);
            }
        });*/
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
        protected boolean isExpanded = false;

        @BindView(R.id.tv_menu_group_name)
        TextView tvGroupName;
        @BindView(R.id.im_menu_group_icon)
        ImageView imGroupIcon;
        @BindView(R.id.tabs_menu_sub_groups)
        TabLayout vTabs;
        @BindView(R.id.pager_menu_items)
        DynamicSwipableViewPager vPager;
        @BindView(R.id.container_menu_sub_groups)
        ViewGroup vSubGroupWrapper;
        private MenuGroupModel mMenuGroup;

        GroupViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Utils.setTabsFont(vTabs, itemView.getResources().getFont(R.font.arial_rounded_mt_bold));
            }

            vPager.setEnabled(false);

            itemView.setOnClickListener(view -> {
                tvGroupName.performClick();
            });
            tvGroupName.setOnClickListener(v -> {
                if (this.isExpanded)
                    contractView(this);
                else {
                    contractView();
                    expandView(this);
                }
            });
        }

        void bindData(final MenuGroupModel menuGroup) {
            vSubGroupWrapper.setVisibility(View.GONE);
            mMenuGroup = menuGroup;

            tvGroupName.setText(menuGroup.getName());
            GlideApp.with(itemView).load(menuGroup.getIcon()).into(imGroupIcon);
            SubGroupPagerAdapter pagerAdapter = new SubGroupPagerAdapter(menuGroup);
            vPager.setAdapter(pagerAdapter);
            if (menuGroup.hasSubGroups()) {
                vTabs.setVisibility(View.VISIBLE);
                vTabs.setupWithViewPager(vPager);
                setupTabIcons();
                vTabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                    @Override
                    public void onTabSelected(TabLayout.Tab tab) {
                        if (tab.getPosition() == 0) {
                            vTabs.setSelectedTabIndicatorColor(vTabs.getContext().getResources().getColor(R.color.apple_green));
                        } else {
                            vTabs.setSelectedTabIndicatorColor(vTabs.getContext().getResources().getColor(R.color.primary_red));
                        }
                    }

                    @Override
                    public void onTabUnselected(TabLayout.Tab tab) {
                    }

                    @Override
                    public void onTabReselected(TabLayout.Tab tab) {
                    }
                });
            } else {
                vTabs.setVisibility(View.GONE);
            }
        }

        void showMenu(View view) {
            Animator sizeChangeAnim = AnimUtils.changeViewSize(view, AnimUtils.NO_CHANGE, (int) view.getResources().getDimension(R.dimen.height_menu_group_expanded));
            Animator hideImageAnim = AnimUtils.hideView(imGroupIcon);
            int newImageSize = ((int) view.getResources().getDimension(R.dimen.button_height_short));
            Animator sizeDecreaseImageAnim = AnimUtils.changeViewSize(imGroupIcon, newImageSize, newImageSize);
            Animator showMenuAnim = AnimUtils.showView(vSubGroupWrapper);
            ValueAnimator scrollAnimator = ValueAnimator.ofInt(1, 2, 3, 4);
            scrollAnimator.setDuration(AnimUtils.DEFAULT_DURATION)
                    .addUpdateListener(animation ->
                            ((LinearLayoutManager) mRecyclerView.getLayoutManager()).scrollToPositionWithOffset(getAdapterPosition(), 0));
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.playTogether(hideImageAnim, sizeDecreaseImageAnim, sizeChangeAnim, showMenuAnim, scrollAnimator);
            isExpanded = true;
            animatorSet.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    if (isExpanded) {
                        vSubGroupWrapper.setVisibility(View.VISIBLE);
                    }
                }
            });
            animatorSet.start();
        }

        void hideMenu(View view) {
            Animator sizeChangeAnim = AnimUtils.changeViewSize(view, AnimUtils.NO_CHANGE, (int) view.getResources().getDimension(R.dimen.height_menu_group_collapsed));
            Animator showImageAnim = AnimUtils.showView(imGroupIcon);
            int newImageSize = ((int) view.getResources().getDimension(R.dimen.button_height_medium));
            Animator sizeIncreaseImageAnim = AnimUtils.changeViewSize(imGroupIcon, newImageSize, newImageSize);
            Animator hideMenuAnim = AnimUtils.hideView(vSubGroupWrapper);
            Animator showTitleAnim = AnimUtils.animateAlpha(tvGroupName, 1.5f, 350L);
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.playTogether(sizeChangeAnim, sizeIncreaseImageAnim, hideMenuAnim, showImageAnim, showTitleAnim);
            isExpanded = false;
            animatorSet.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    if (!isExpanded) {
                        vSubGroupWrapper.setVisibility(View.GONE);
                    }
                }
            });
            animatorSet.start();
        }

        public boolean isScrollableList() {
            return mMenuGroup.getItems().size() >= 4;
        }

        private void setupTabIcons() {
            TabLayout.Tab tab = vTabs.getTabAt(0);
            if (tab != null) {
                View tabOne = LayoutInflater.from(vTabs.getContext()).inflate(R.layout.tab_menu_subgroup, null);
                TextView tv = tabOne.findViewById(R.id.tv_tab);
                ImageView im = tabOne.findViewById(R.id.im_tab);
                tv.setText("  Veg");
                im.setImageDrawable(mRecyclerView.getContext().getResources().getDrawable(R.drawable.ic_veg));
                tab.setCustomView(tabOne);
            }

            tab = vTabs.getTabAt(1);
            if (tab != null) {
                View tabTwo = LayoutInflater.from(vTabs.getContext()).inflate(R.layout.tab_menu_subgroup, null);
                TextView tvTwo = tabTwo.findViewById(R.id.tv_tab);
                ImageView imTwo = tabTwo.findViewById(R.id.im_tab);
                tvTwo.setText("  Non-Veg");
                imTwo.setImageDrawable(mRecyclerView.getContext().getResources().getDrawable(R.drawable.ic_non_veg));
                tab.setCustomView(tabTwo);
            }
        }
    }

    public class SubGroupPagerAdapter extends FragmentStatePagerAdapter {
        private List<MenuItemsFragment> mListFragment;

        SubGroupPagerAdapter(MenuGroupModel menuGroup) {
            super(mFragmentManager);
            mListFragment = new ArrayList<>();
            if (menuGroup.hasSubGroups()) {
                mListFragment.add(MenuItemsFragment.newInstance(menuGroup.getVegItems(), mListener, mIsSessionActive));
                mListFragment.add(MenuItemsFragment.newInstance(menuGroup.getNonVegItems(), mListener, mIsSessionActive));
            } else {
                mListFragment.add(MenuItemsFragment.newInstance(menuGroup.getItems(), mListener, mIsSessionActive));
            }
        }

        @Override
        public Fragment getItem(int position) {
            return mListFragment.get(position);
        }

        @Override
        public int getCount() {
            return mListFragment.size();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            String title = null;
            switch (position) {
                case 0:
                    title = "Veg";
                    break;
                case 1:
                    title = "Non-Veg";
                    break;
                default:
                    break;
            }
            return title;
        }
    }
}
