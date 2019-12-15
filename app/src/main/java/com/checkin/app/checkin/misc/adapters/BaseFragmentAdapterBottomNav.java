package com.checkin.app.checkin.misc.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.checkin.app.checkin.R;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.DrawableRes;
import androidx.annotation.LayoutRes;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

public abstract class BaseFragmentAdapterBottomNav extends FragmentStatePagerAdapter {
    private ViewPager mPager;
    private int mSelectedPos = -1;
    private List<TabSelectionHandler> selectionHandlers = new ArrayList<>();

    public BaseFragmentAdapterBottomNav(FragmentManager fm) {
        super(fm);
    }

    @DrawableRes
    public abstract int getTabDrawable(int position);

    @LayoutRes
    public int getCustomView(int position) {
        return R.layout.view_tab_bottom_nav;
    }

    protected void bindTabText(TextView tvTitle, int position) {
        if (getPageTitle(position) != null) {
            tvTitle.setText(getPageTitle(position));
            tvTitle.setVisibility(View.VISIBLE);
        } else
            tvTitle.setVisibility(View.GONE);
    }

    protected void bindTabIcon(ImageView imIcon, int position) {
        imIcon.setImageResource(getTabDrawable(position));
    }

    protected void bindCustomView(View view, int position) {
        TextView tvTitle = view.findViewById(R.id.tv_bnav_title);
        ImageView imIcon = view.findViewById(R.id.iv_bnav_icon);

        bindTabText(tvTitle, position);
        bindTabIcon(imIcon, position);
    }

    public void setupWithTab(TabLayout tabLayout, ViewPager viewPager) {
        mPager = viewPager;

        int count = getCount();
        if (count > 0)
            mSelectedPos = 0;
        for (int pos = 0; pos < count; pos++) {
            View itemView = LayoutInflater.from(tabLayout.getContext()).inflate(getCustomView(pos), null, false);
            this.bindCustomView(itemView, pos);

            selectionHandlers.add(new TabSelectionHandler(pos, itemView));

            TabLayout.Tab tab = tabLayout.getTabAt(pos);
            if (tab != null) {
                tab.setCustomView(itemView);
            }
        }

        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                if (mSelectedPos != -1) {
                    selectionHandlers.get(mSelectedPos).deselect();
                }
                mSelectedPos = position;
                selectionHandlers.get(mSelectedPos).select();
            }
        });
    }

    protected void onTabClick(int position) {
        mPager.setCurrentItem(position, true);
    }

    private class TabSelectionHandler implements View.OnClickListener {
        private int mPos;
        private View mView;

        TabSelectionHandler(int pos, View view) {
            mPos = pos;
            mView = view;
            mView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onTabClick(mPos);
        }

        void deselect() {
            mView.setSelected(false);
        }

        void select() {
            mView.setSelected(true);
        }
    }
}
