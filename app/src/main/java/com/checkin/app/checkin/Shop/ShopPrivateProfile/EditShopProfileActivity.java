package com.checkin.app.checkin.Shop.ShopPrivateProfile;

import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

import com.checkin.app.checkin.R;

public class EditShopProfileActivity extends AppCompatActivity {


    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager viewPager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_shop_profile);

        mSectionsPagerAdapter =new SectionsPagerAdapter(getSupportFragmentManager());
        viewPager =(ViewPager) findViewById(R.id.container);
        setupViewPager(viewPager);
        TabLayout tabLayout =(TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void setupViewPager(ViewPager viewPager)
    {
        SectionsPagerAdapter adapter =new SectionsPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new BasicInfoFragment(),"Basic Info");
        adapter.addFragment(new AspectFragment(),"Aspect");
        viewPager.setAdapter(adapter);
    }
}
