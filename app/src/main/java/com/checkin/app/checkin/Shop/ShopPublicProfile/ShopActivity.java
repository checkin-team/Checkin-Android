package com.checkin.app.checkin.Shop.ShopPublicProfile;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.R;
import com.checkin.app.checkin.Shop.ReviewsAdapter;
import com.checkin.app.checkin.Shop.ReviewsViewModel;
import com.checkin.app.checkin.Shop.ShopBaseActivity;

import java.util.ArrayList;

public class ShopActivity extends ShopBaseActivity {
    private static final String TAG = ShopActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_public);
        setupPagers(new ShopPagerAdapter(getSupportFragmentManager()), R.menu.menu_shop_profile_public);
//        setUpNavigation();
    }

    void setUpNavigation()
    {
        RecyclerView rv=findViewById(R.id.rv_shop_reviews);
        rv.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        ReviewsViewModel model=ViewModelProviders.of(this).get(ReviewsViewModel.class);

        model.getShopReviews(1L).observe(this, reviewsModel ->{
            if(reviewsModel == null) return;
            if (reviewsModel.status == Resource.Status.SUCCESS) {
                if(reviewsModel.data==null)
                    rv.setAdapter(new ReviewsAdapter(new ArrayList<>()));
                else
                    rv.setAdapter(new ReviewsAdapter(reviewsModel.data));
            } else
                if (reviewsModel.status == Resource.Status.LOADING) {
                    //rv.setAdapter(new ReviewsAdapter(reviewsModel.data));
                    // LOADING
            } else{
                Toast.makeText(this, "Error In getting Reviews", Toast.LENGTH_SHORT).show();
            }

                });
        /*model.getShopReviews().observe(this, new Observer<Resource<List<ShopReview>>>() {

            @Override
            public void onChanged(@Nullable Resource<List<ShopReview>> reviewsItems) {
                rv.setAdapter(new ReviewsAdapter(reviewsItems));
                rv.getAdapter().notifyDataSetChanged();
            }
        });*/

    }

    @Override
    protected int isDrawerEnabled(int position) {
        return position == 0 ? DrawerLayout.LOCK_MODE_UNLOCKED : DrawerLayout.LOCK_MODE_LOCKED_CLOSED;
    }

    private class ShopPagerAdapter extends FragmentPagerAdapter {

        public ShopPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return ShopProfileFragment.getInstance(1);
                case 1:
                    return ShopProfileFragment.getInstance(1);
                case 2:
                    return ShopProfileFragment.getInstance(1);
            }
            return null;
        }

        @Override
        public int getCount() {
            return 3;
        }
    }
}
