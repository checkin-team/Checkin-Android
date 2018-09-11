package com.checkin.app.checkin.Session;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.checkin.app.checkin.R;
import com.checkin.app.checkin.Utility.DynamicSwipableViewPager;
import com.checkin.app.checkin.Utility.Util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.objectbox.Box;

/**
 * Created by Bhavik Patel on 28/08/2018.
 */

public class MenuGroupFilterAdapter extends RecyclerView.Adapter {

    public static final int VIEW_TYPE_CATEGORY = R.layout.menu_category_item;
    public static final int VIEW_TYPE_GROUP = R.layout.menu_group_card_layout;

    private final String TAG = MenuGroupAdapter.class.getSimpleName();

    private List<MenuGroupModel> groupList;
    GroupViewHolder mPrevExpandedViewHolder = null;
    private RecyclerView mRecyclerView;
    private Context mContext;
    private MenuItemAdapter.OnItemInteractionListener mItemInteractionListener;
    private List<Object> items;

    MenuGroupFilterAdapter(List<MenuGroupModel> groupsList, Context context, MenuItemAdapter.OnItemInteractionListener itemInteractionListener) {
        this.groupList = groupsList;
        mContext = context;
        mItemInteractionListener = itemInteractionListener;
    }

    public void setGroupList(List<MenuGroupModel> groupList) {
        this.groupList = groupList;
        setItems();
        notifyDataSetChanged();
    }


    public int getCategoryPosition(String categroy){
        int i = 0;
        for(Object o : items){
            if(o instanceof String){
                if(((String)o).contentEquals(categroy)){
                    return i;
                }
            }
            i++;
        }
        return 0;
    }

    private void setItems(){
        if(groupList != null && groupList.size() > 0){
            items = new ArrayList<>();
            String category = groupList.get(0).getCategory();
            for(MenuGroupModel menuGroupModel : groupList){
                if(!category.contentEquals(menuGroupModel.getCategory())){
                    category = menuGroupModel.getCategory();
                    items.add(category);
                }
                items.add(menuGroupModel);
            }
        }


    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mRecyclerView = recyclerView;
        /*mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (Math.abs(dy) > ViewConfiguration.get(mContext).getScaledTouchSlop())
                    contractView(mPrevExpandedViewHolder);
            }
        });*/
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    @Override
    public int getItemViewType(final int position) {
        if(items.get(position) instanceof MenuGroupModel) return VIEW_TYPE_GROUP;
        else return VIEW_TYPE_CATEGORY;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, final int viewType) {
        final View itemView = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);

        if(viewType == VIEW_TYPE_CATEGORY){
            final CategoryViewHolder holder = new CategoryViewHolder(itemView);
            return holder;
        }else {
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
    }

    public void contractView(GroupViewHolder groupViewHolder) {
        if (groupViewHolder != null) {
            groupViewHolder.hideMenu(groupViewHolder.itemView);
            mPrevExpandedViewHolder = null;
        }
    }
    public void expandView(GroupViewHolder groupViewHolder) {
        if (groupViewHolder != null) {
            final int position = groupViewHolder.getAdapterPosition();
            groupViewHolder.showMenu(groupViewHolder.itemView);
            ((LinearLayoutManager) mRecyclerView.getLayoutManager()).scrollToPositionWithOffset(position, 0);
            mPrevExpandedViewHolder = groupViewHolder;
        }
    }
    public Object getItem(int position){
        return items.get(position);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(holder.getItemViewType() == VIEW_TYPE_CATEGORY){
            CategoryViewHolder categoryViewHolder = (CategoryViewHolder) holder;
            categoryViewHolder.bind((String)items.get(position));
        }else {
            GroupViewHolder groupViewHolder = (GroupViewHolder) holder;
            groupViewHolder.bindData((MenuGroupModel) items.get(position));
            if (groupViewHolder.isExpanded) {
                contractView(groupViewHolder);
            }
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
        @BindView(R.id.sub_group_wrapper) View vSubGroupWrapper;
        SubGroupPagerAdapter pagerAdapter;
        boolean isExpanded = false;

        GroupViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Util.setTabsFont(vTabs, mContext.getResources().getFont(R.font.arial_rounded_mt_bold));
            }
        }

        void bindData(final MenuGroupModel menuGroup) {
            vTitle.setText(menuGroup.getName());
            vImage.setImageResource(R.drawable.ic_beer);
            pagerAdapter = new SubGroupPagerAdapter(menuGroup);
            if (menuGroup.getSubGroupsCount() > 1) {
                vTabs.setVisibility(View.VISIBLE);
                vTabs.setupWithViewPager(viewPager);
            } else {
                vTabs.setVisibility(View.GONE);
            }
            viewPager.setAdapter(pagerAdapter);
            viewPager.setEnabled(false);
        }

        void showMenu(View view) {
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) vTitle.getLayoutParams();
            layoutParams.gravity = Gravity.TOP;
            vTitle.setLayoutParams(layoutParams);
            Animator sizeChangeAnim = Util.changeViewSize(view, Util.NO_CHANGE, (int) view.getResources().getDimension(R.dimen.menu_group_expanded_height));
            Animator hideImageAnim = Util.hideView(vImage);
            Animator showMenuAnim = Util.showView(vSubGroupWrapper);
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.playTogether(sizeChangeAnim, hideImageAnim, showMenuAnim);
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
    }
    public class CategoryViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.title) TextView title;
        View container;
        public CategoryViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
            container = itemView;
        }
        public void bind(String title){
            this.title.setText(title);
        }
    }

}
