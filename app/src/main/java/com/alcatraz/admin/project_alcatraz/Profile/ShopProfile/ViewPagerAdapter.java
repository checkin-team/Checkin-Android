package com.alcatraz.admin.project_alcatraz.Profile.ShopProfile;

/**
 * Created by admin on 7/4/2018.
 */
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;




/**
 * Created by Jaison on 23/10/16.
 */


public class ViewPagerAdapter extends FragmentPagerAdapter {
    private final List<Fragment> mFragmentList = new ArrayList<>();

    public ViewPagerAdapter(FragmentManager manager) {
        super(manager);
    }


    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }

    public void addFragment(Fragment fragment) {
        //to remove exception of fragment already added
        if(fragment.isAdded())
            return;
        mFragmentList.add(fragment);
    }

}

//class ViewPagerAdapter extends FragmentPagerAdapter {
//
//    private final int mCount;
//
//    public ViewPagerAdapter(final AppCompatActivity activity, int count) {
//        super(activity.getSupportFragmentManager());
//        this.mCount = count;
//    }
//
//    @Override
//    public android.support.v4.app.Fragment getItem(final int position) {
//        switch (position) {
//
//
//            case 0:
//                return new FragmentShopProfile();
//
//
//            case 1:
//                return new FragmentShopMenu();
//
//            case 2:
//                return new FragmentShopInsights();
//
//            case 3:
//                return new FragmentShopDailyActivity();
//
//
//
//
//
//
//
//        }
//        return null;
//    }
//
//    @Override
//    public int getCount() {
//        return mCount;
//    }
//}
