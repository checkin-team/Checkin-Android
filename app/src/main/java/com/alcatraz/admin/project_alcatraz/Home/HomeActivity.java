package com.alcatraz.admin.project_alcatraz.Home;

import android.nfc.Tag;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.alcatraz.admin.project_alcatraz.R;

/**
 * Created by admin on 1/14/2018.
 */

public class HomeActivity extends AppCompatActivity {
    private final String TAG="HomeActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Log.d(TAG,"onCreate: starting.");

//        setupBottomNavigationView();

    }
    private void setupViewPager()
    {

        SectionsPagerAdapter adapter=new SectionsPagerAdapter(getSupportFragmentManager());
        adapter.addfragment(new HomeFragment());
        adapter.addfragment(new MessagesFragment());
        adapter.addfragment(new NavigationFragment());


    }

}
