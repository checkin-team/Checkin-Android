package com.checkin.app.checkin.Shop.ShopInvoice;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

public class ShopInvoiceDetailFeedbackPagerAdapter extends FragmentPagerAdapter {

    private ArrayList<Fragment> mFragmentList;
    private ArrayList<String> mTitleList;

    public ShopInvoiceDetailFeedbackPagerAdapter(ArrayList<Fragment> mFragmentList, ArrayList<String> mTitleList, FragmentManager supportFragmentManager) {
        super(supportFragmentManager);
        this.mFragmentList = mFragmentList;
        this.mTitleList = mTitleList;
    }

    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mTitleList.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mTitleList.get(position);
    }
}
