package com.checkin.app.checkin.Shop.ShopPrivateProfile;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.checkin.app.checkin.R;
import com.checkin.app.checkin.Shop.RestaurantModel;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EditProfileActivity extends AppCompatActivity implements EditAspectFragment.AspectFragmentInteraction {
    private static final String TAG = EditProfileActivity.class.getSimpleName();

    @BindView(R.id.container) ViewPager mViewPager;
    @BindView(R.id.tabs) TabLayout vTabs;

    public static final String KEY_SHOP_PK = "shop_edit.pk";

    private ShopProfileViewModel mViewModel;
    private SectionsPagerAdapter mSectionsPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_edit_profile);

        ButterKnife.bind(this);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setElevation(0);

        mViewModel = ViewModelProviders.of(this).get(ShopProfileViewModel.class);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(vTabs));
        vTabs.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_appbar_done, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.menu_item_done:
                Toast.makeText(this, "Done!", Toast.LENGTH_SHORT).show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void updateShopAspects(RestaurantModel shop) {

    }

    @Override
    public void onAspectDataValidStatus(boolean isValid) {

    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return EditBasicFragment.newInstance();
                case 1:
                    return EditAspectFragment.newInstance(EditProfileActivity.this);
            }
            return null;
        }

        @Override
        public int getCount() {
            return 2;
        }
    }

}
