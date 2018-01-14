package com.alcatraz.admin.project_alcatraz.Home;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 1/15/2018.
 *
 * class that stores fragments for tabs
 */

public class SectionsPagerAdapter extends FragmentPagerAdapter {
    private final String TAG="SectionsPagerAdapter";

    private final List<Fragment> fragmentlist=new ArrayList<>();
    public SectionsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return fragmentlist.get(position);
    }

    @Override
    public int getCount() {
        return fragmentlist.size();
    }
    public void addfragment(Fragment fragment)
    {
        fragmentlist.add(fragment);
    }
}
