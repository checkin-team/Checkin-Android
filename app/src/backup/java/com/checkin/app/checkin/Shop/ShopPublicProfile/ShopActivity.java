package com.checkin.app.checkin.Shop.ShopPublicProfile;

import androidx.lifecycle.ViewModelProviders;
import android.os.Bundle;
import androidx.annotation.IdRes;
import androidx.annotation.MenuRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.checkin.app.checkin.Menu.SessionMenuActivity;
import com.checkin.app.checkin.Misc.BaseActivity;
import com.checkin.app.checkin.R;
import com.checkin.app.checkin.Shop.RecentCheckin.RecentCheckinFragment;
import com.checkin.app.checkin.Utility.DynamicSwipableViewPager;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.sephiroth.android.library.bottomnavigation.BottomNavigation;

public class ShopActivity extends BaseActivity {
    private static final String TAG = ShopActivity.class.getSimpleName();

    @BindView(R.id.view_pager)
    protected DynamicSwipableViewPager mViewPager;
    @BindView(R.id.bottom_navigation)
    protected BottomNavigation mBottomNavigation;

    public static final String KEY_SHOP_PK = "shop_public.pk";

    private long mShopPk;
    private ShopProfileViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_public);

        ButterKnife.bind(this);

        setupPagers(new ShopPagerAdapter(getSupportFragmentManager()), R.menu.menu_shop_profile_public);

        mViewModel = ViewModelProviders.of(this).get(ShopProfileViewModel.class);

        mShopPk = getIntent().getLongExtra(KEY_SHOP_PK, 0);
        mViewModel.fetchShop(mShopPk);
    }

    protected void setupPagers(FragmentPagerAdapter pagerAdapter, @MenuRes int menuId) {
        mViewPager.setEnabled(false);
        mViewPager.setAdapter(pagerAdapter);
        mBottomNavigation.inflateMenu(menuId);
        mBottomNavigation.setOnMenuItemClickListener(new BottomNavigation.OnMenuItemSelectionListener() {
            @Override
            public void onMenuItemSelect(@IdRes final int itemId, final int position, final boolean fromUser) {
                if (position == 2) {
                    mBottomNavigation.setSelectedIndex(0, false);
                    SessionMenuActivity.withoutSession(getApplicationContext(), mShopPk);
                    return;
                }
                mViewPager.setCurrentItem(position, true);
            }

            @Override
            public void onMenuItemReselect(@IdRes final int itemId, final int position, final boolean fromUser) {
            }
        });
    }

    private class ShopPagerAdapter extends FragmentPagerAdapter {

        public ShopPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return ShopProfileFragment.newInstance();
                case 1:
                    return RecentCheckinFragment.newInstance(mShopPk);
            }
            return RecentCheckinFragment.newInstance(mShopPk);
        }

        @Override
        public int getCount() {
            return 2;
        }
    }
}
