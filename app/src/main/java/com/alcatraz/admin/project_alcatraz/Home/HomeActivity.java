package com.alcatraz.admin.project_alcatraz.Home;

import android.nfc.Tag;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.alcatraz.admin.project_alcatraz.R;

/**
 * Created by admin on 1/14/2018.
 */

public class HomeActivity extends AppCompatActivity {

    private final String TAG="HomeActivity";
    private NavigationView navigationView=null;
    private DrawerLayout drawerLayout=null;
    private Toolbar toolbar=null;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
//        toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
        Log.d(TAG,"onCreate: starting.");
//        navigationView=(NavigationView)findViewById(R.id.navigation_view);
//        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
//            @Override
//            public boolean onNavigationItemSelected(MenuItem item) {
//                drawerLayout.closeDrawers();
//                switch (item.getItemId())
//                {
//
//                    case R.id.drawer_profile:
//                        Toast.makeText(HomeActivity.this, "profile menu item selected", Toast.LENGTH_SHORT).show();
//                        break;
//                    case R.id.drawer_settings:
//                        Toast.makeText(HomeActivity.this, "settings menu item selected", Toast.LENGTH_SHORT).show();
//                        break;
//                    case R.id.drawer_privacysettings:
//                        Toast.makeText(HomeActivity.this, "privacy settings menu item selected", Toast.LENGTH_SHORT).show();
//                        break;
//                        default:
//                            Toast.makeText(HomeActivity.this, "yo whats up, default of switch case selected", Toast.LENGTH_SHORT).show();
//                            break;
//
//
//
//                }
//                return true;
//
//            }
//        });
//
//        drawerLayout=(DrawerLayout)findViewById(R.id.drawer);
//        ActionBarDrawerToggle actionBarDrawerToggle=new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.app_name,R.string.app_name){
//            @Override
//            public void onDrawerClosed(View drawerView) {
//                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
//                super.onDrawerClosed(drawerView);
//            }
//
//            @Override
//            public void onDrawerOpened(View drawerView) {
//
//
//                super.onDrawerOpened(drawerView);
//            }
//
//        };
//        drawerLayout.addDrawerListener(actionBarDrawerToggle);
//
//        //calling sync state is necessay or else your hamburger icon wont show up
//        actionBarDrawerToggle.syncState();



//        setupBottomNavigationView();
        setupViewPager();

    }

    /**
     * *
     * for adding the fragments in ghome activity
     *
     */


    private void setupViewPager()
    {

        SectionsPagerAdapter adapter=new SectionsPagerAdapter(getSupportFragmentManager());
        adapter.addfragment(new NavigationFragment());//index 0
        adapter.addfragment(new HomeFragment());//index 1
        adapter.addfragment(new MessagesFragment());// index 2

       // adapter.addfragment(new BarCodeFragment());
        ViewPager viewpager=(ViewPager)findViewById(R.id.viewpagercontainer);
        viewpager.setAdapter(adapter);
        TabLayout tabLayout=(TabLayout)findViewById(R.id.tablayout);
        tabLayout.setupWithViewPager(viewpager);
        tabLayout.getTabAt(0).setIcon(R.drawable.ic_navbar_icon);
        tabLayout.getTabAt(1).setIcon(R.drawable.checkin_logo0);
        tabLayout.getTabAt(2).setIcon(R.drawable.ic_msg_pic);


    }

}
