package com.checkin.app.checkin.Waiter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class WaiterTablePagerAdapter extends FragmentStatePagerAdapter {

    private List<WaiterTableFragment> waiterTableFragmentList = new ArrayList<>();

    WaiterTablePagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return waiterTableFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return waiterTableFragmentList.size();
    }

    void addWaiterTableFragment(WaiterTableFragment waiterTableFragment) {
        waiterTableFragmentList.add(waiterTableFragment);
    }

    void removeWaiterTableAtThisPosition(int position) {
        if (waiterTableFragmentList!=null  && position < waiterTableFragmentList.size()) {
            waiterTableFragmentList.remove(position);
            notifyDataSetChanged();
        }
    }

    void addWaiterTableAtThisPosition(int position, WaiterTableFragment waiterTableFragment) {
        if (waiterTableFragmentList!=null  && waiterTableFragment != null) {
            waiterTableFragmentList.add(position,waiterTableFragment);
            notifyDataSetChanged();
        }
    }
}
