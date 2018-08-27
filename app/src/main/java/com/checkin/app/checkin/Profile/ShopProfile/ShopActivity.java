package com.checkin.app.checkin.Profile.ShopProfile;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.checkin.app.checkin.Data.Resource;

import java.util.ArrayList;
import java.util.List;

import it.sephiroth.android.library.bottomnavigation.BottomNavigation;

public class ShopActivity extends AppCompatActivity {
    ViewPager viewPager;
    BottomNavigation bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);
        viewPager = findViewById(R.id.pager);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if(bottomNavigation.getSelectedIndex() != position)
                    bottomNavigation.setSelectedIndex(position,true);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        bottomNavigation = findViewById(R.id.bottomNavigation);
        viewPager.setAdapter(new ShopViewPager(getSupportFragmentManager()));
        bottomNavigation.setOnMenuItemClickListener(new BottomNavigation.OnMenuItemSelectionListener() {
            @Override
            public void onMenuItemSelect(int i, int i1, boolean b) {
                Toast.makeText(getApplicationContext(),"Showing page " + i1,Toast.LENGTH_SHORT).show();
                if(viewPager.getCurrentItem() != i1)viewPager.setCurrentItem(i1);
            }

            @Override
            public void onMenuItemReselect(int i, int i1, boolean b) {

            }
        });
        setUpNavigation();
    }
    void setUpNavigation()
    {

        RecyclerView rv=findViewById(R.id.reviews_recycler);
        rv.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        ReviewsViewModel model=ViewModelProviders.of(this).get(ReviewsViewModel.class);
        model.getReviewsItemLiveData().observe(this, reviewsModel ->{
            if(reviewsModel == null) return;
            if (reviewsModel.status == Resource.Status.SUCCESS) {
                rv.setAdapter(new AdapterReview(reviewsModel.data));
            } else
                if (reviewsModel.status == Resource.Status.LOADING) {
                // LOADING
            } else{
                Toast.makeText(this, "Error In getting Reviews", Toast.LENGTH_SHORT).show();
            }

                });
        /*model.getReviewsItemLiveData().observe(this, new Observer<Resource<List<ReviewsItem>>>() {

            @Override
            public void onChanged(@Nullable Resource<List<ReviewsItem>> reviewsItems) {
                rv.setAdapter(new AdapterReview(reviewsItems));
                rv.getAdapter().notifyDataSetChanged();
            }
        });*/

    }
    private class ShopViewPager extends FragmentPagerAdapter {

        List<Fragment> fragments;

        public ShopViewPager(FragmentManager fm) {
            super(fm);
            fragments = new ArrayList<>();
            fragments.add(new ShopHomeFragment());
            fragments.add(new ShopHomeFragment());
            fragments.add(new ShopHomeFragment());
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }
    }

    //Temporary Workthrough

}
