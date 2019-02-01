package com.checkin.app.checkin.Misc;

import android.support.annotation.DrawableRes;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.checkin.app.checkin.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public abstract class BaseFragmentAdapterBottomNav extends FragmentStatePagerAdapter {
    private ViewPager mPager;
    private int mSelectedPos = -1;
    private List<TabSelectionHandler> selectionHandlers = new ArrayList<>();

    public BaseFragmentAdapterBottomNav(FragmentManager fm) {
        super(fm);
    }

    @DrawableRes
    public abstract int getTabDrawable(int position);

    public void setupWithTab(TabLayout tabLayout, ViewPager viewPager) {
        mPager = viewPager;
        int count = getCount();
        if (count > 0)
            mSelectedPos = 0;
        for (int pos = 0; pos < count; pos++) {
            View itemView = LayoutInflater.from(tabLayout.getContext()).inflate(R.layout.view_tab_bottom_nav, null, false);
            TextView tvTitle = itemView.findViewById(R.id.tv_bnav_title);
            ImageView imIcon = itemView.findViewById(R.id.iv_bnav_icon);

            if (getPageTitle(pos) != null) {
                tvTitle.setText(getPageTitle(pos));
                tvTitle.setVisibility(View.VISIBLE);
            }
            else
                tvTitle.setVisibility(View.GONE);

            imIcon.setImageResource(getTabDrawable(pos));

            selectionHandlers.add(new TabSelectionHandler(pos, itemView));

            Objects.requireNonNull(tabLayout.getTabAt(pos)).setCustomView(itemView);
        }
    }

    private class TabSelectionHandler implements View.OnClickListener {
        private int mPos;
        private View mView;

        TabSelectionHandler(int pos, View  view) {
            mPos = pos;
            mView = view;
            mView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mSelectedPos != -1) {
                selectionHandlers.get(mSelectedPos).deselect();
            }
            mSelectedPos = mPos;
            mView.setSelected(true);
            mPager.setCurrentItem(mSelectedPos, true);
        }

        void deselect() {
            mView.setSelected(false);
        }
    }
}
