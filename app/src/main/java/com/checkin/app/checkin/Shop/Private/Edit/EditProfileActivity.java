package com.checkin.app.checkin.Shop.Private.Edit;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;

import com.checkin.app.checkin.R;
import com.checkin.app.checkin.Shop.Private.ShopProfileViewModel;
import com.checkin.app.checkin.Shop.RestaurantModel;
import com.checkin.app.checkin.data.resource.Resource;
import com.checkin.app.checkin.utility.Utils;
import com.google.android.material.tabs.TabLayout;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EditProfileActivity extends AppCompatActivity implements EditAspectFragment.AspectFragmentInteraction, EditBasicFragment.BasicFragmentInteraction {
    public static final String KEY_SHOP_PK = "shop_edit.pk";
    private static final String TAG = EditProfileActivity.class.getSimpleName();
    @BindView(R.id.container)
    ViewPager mViewPager;
    @BindView(R.id.tabs)
    TabLayout vTabs;
    private ShopProfileViewModel mViewModel;
    private boolean isBasicValid = true;
    private boolean isAspectValid = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_edit_profile);

        ButterKnife.bind(this);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_back_grey);
            actionBar.setElevation(0);
        }

        mViewPager.setAdapter(new SectionsPagerAdapter(getSupportFragmentManager()));
        vTabs.setupWithViewPager(mViewPager);

        long shopPk = getIntent().getLongExtra(KEY_SHOP_PK, 0);
        mViewModel = ViewModelProviders.of(this).get(ShopProfileViewModel.class);
        mViewModel.fetchShopManage(shopPk);

        mViewModel.getObservableData().observe(this, resource -> {
            if (resource == null)
                return;
            if (resource.getStatus() == Resource.Status.SUCCESS && resource.getData() != null) {
                String msg = resource.getData().get("detail").asText("Success!");
                Utils.toast(this, msg);
            } else if (resource.getStatus() == Resource.Status.ERROR_INVALID_REQUEST) {
                mViewModel.showError(resource.getErrorBody());
                Utils.toast(this, "Error in updating data.");
            }
        });
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
                submitData();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void submitData() {
        if (!isBasicValid) {
            Toast.makeText(getApplicationContext(), "Basic data is invalid!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!isAspectValid) {
            Toast.makeText(getApplicationContext(), "Aspect data is invalid!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (mViewPager.getCurrentItem() == 0)
            mViewModel.collectBasicData();
        else if (mViewPager.getCurrentItem() == 1)
            mViewModel.collectAspectData();
        else
            mViewModel.collectData();
    }

    @Override
    public void updateShopAspects(RestaurantModel shop) {
        mViewModel.updateShop(shop);
    }

    @Override
    public void onAspectDataValidStatus(boolean isValid) {
        isAspectValid = isValid;
    }

    @Override
    public void updateShopBasics(RestaurantModel shop) {
        mViewModel.updateShop(shop);
    }

    @Override
    public void onBasicDataValidStatus(boolean isValid) {
        isBasicValid = isValid;
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
                    return EditBasicFragment.newInstance(EditProfileActivity.this);
                case 1:
                    return EditAspectFragment.newInstance(EditProfileActivity.this);
            }
            return null;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getResources().getString(R.string.title_shop_basic_info);
                case 1:
                    return getResources().getString(R.string.title_shop_aspects);
            }
            return "";
        }

        @Override
        public int getCount() {
            return 2;
        }
    }

}
