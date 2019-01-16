package com.checkin.app.checkin.Shop.ShopInvoice;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class ShopInvoiceDetailFeedbackPagerAdapter extends FragmentPagerAdapter {

    private int tabCount;

    ShopInvoiceDetailFeedbackPagerAdapter(int tabCount, FragmentManager fm) {
        super(fm);
        this.tabCount = tabCount;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;

        if (position == 0){
            fragment = ShopInvoiceDetailFragment.newInstance();
        }else if (position == 1){
            fragment = ShopInvoiceFeedbackFragment.newInstance();
        }

        return fragment;
    }

    @Override
    public int getCount() {
        return tabCount;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        if (position == 0)
            return "DETAILS";
        else if (position == 1)
            return "FEEDBACKS";
        else
            return null;
    }
}
