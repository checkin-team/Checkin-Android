package com.alcatraz.admin.project_alcatraz.Profile.ShopProfile;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.alcatraz.admin.project_alcatraz.R;
import com.alcatraz.admin.project_alcatraz.R.id;

import it.sephiroth.android.library.bottomnavigation.BottomNavigation;

public class ShopProfileActivity extends AppCompatActivity {

    private TextView mTextMessage;
    private ViewPager viewPager;

    FragmentShopProfile fragmentprofile;
    FragmentShopMenu fragmentmenu;
    FragmentShopInsights fragmentinsights;
    FragmentShopDailyActivity fragmentdailyactivity;
    MenuItem prevMenuItem;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_shop_profile:
                    //mTextMessage.setText("Profile");
                    viewPager.setCurrentItem(0);
                    break;
                case R.id.navigation_shop_menu:
                   // mTextMessage.setText("Menu");
                    viewPager.setCurrentItem(1);
                    break;
                case R.id.navigation_shop_insights:
                    viewPager.setCurrentItem(2);
                    break;
                case R.id.navigation_shop_daily_activity:
                    viewPager.setCurrentItem(3);
                    break;

                    default:
                        Toast.makeText(ShopProfileActivity.this, "Error occurred", Toast.LENGTH_SHORT).show();
                        break;

            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_shop_profile);

        viewPager=(ViewPager)findViewById(id.shop_acivity_viewpager);

        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(id.bottomnavbar);
        BottomNavigation navigation1=(BottomNavigation)findViewById(id.bottomnavbar) ;
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if(prevMenuItem!=null)
                {
                    prevMenuItem.setCheckable(false);
                }
                else
                {
                    navigation.getMenu().getItem(0).setChecked(false);
                }
                Log.d("page", "onPageSelected: "+position);
                navigation.getMenu().getItem(position).setChecked(true);
                prevMenuItem = navigation.getMenu().getItem(position);

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
       // setupViewPager(viewPager);

        /*  //Disable ViewPager Swipe
       viewPager.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                return true;
            }
        });
        */
        
    }

//    private void setupViewPager(ViewPager viewPager) {
//
//        ViewPagerAdapter adapter=new ViewPagerAdapter(getSupportFragmentManager());
//
//        fragmentprofile=new FragmentShopProfile();
//        fragmentmenu=new FragmentShopMenu();
//        fragmentinsights=new FragmentShopInsights();
//        fragmentdailyactivity=new FragmentShopDailyActivity();
//        adapter.addFragment(fragmentprofile);
//        adapter.addFragment(fragmentmenu);
//        adapter.addFragment(fragmentinsights);
//        adapter.addFragment(fragmentdailyactivity);
//
//        viewPager.setAdapter(adapter);
//    }

}
