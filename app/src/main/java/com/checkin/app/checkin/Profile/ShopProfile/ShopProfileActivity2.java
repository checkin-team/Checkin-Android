package com.checkin.app.checkin.Profile.ShopProfile;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.checkin.app.checkin.R;
import com.checkin.app.checkin.R.id;

import it.sephiroth.android.library.bottomnavigation.BottomNavigation;

public class ShopProfileActivity2 extends AppCompatActivity implements BottomNavigation.OnMenuItemSelectionListener,MenuItem.OnMenuItemClickListener {

    private TextView mTextMessage;
    private ViewPager viewPager;

    FragmentShopProfile fragmentprofile;
    FragmentShopMenu fragmentmenu;
    FragmentShopInsights fragmentinsights;
    FragmentShopDailyActivity fragmentdailyactivity;
    MenuItem prevMenuItem;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_shop_profile);

        viewPager=(ViewPager)findViewById(id.shop_acivity_viewpager);



        BottomNavigation navigation=(BottomNavigation)findViewById(id.bottomnavbar);

        navigation.setDefaultSelectedIndex(0);

        navigation.setOnMenuChangedListener(new BottomNavigation.OnMenuChangedListener() {
            @Override
            public void onMenuChanged(BottomNavigation parent) {
               // viewPager.setAdapter(new ViewPagerAdapter(ShopProfileActivity2.this,parent.getMenuItemCount()));
                viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                    }

                    @Override
                    public void onPageSelected(int position) {
                        if(navigation.getSelectedIndex()!=position)
                        {
                            navigation.setSelectedIndex(position,false);
                        }

                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {

                    }
                });
            }
        });
        
        setupviewpager(viewPager);

        /*  //Disable ViewPager Swipe
       viewPager.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public_selected boolean onTouch(View v, MotionEvent event)
            {
                return true;
            }
        });
        */





    }

    private void setupviewpager(ViewPager viewPager) {
        ViewPagerAdapter adapter=new ViewPagerAdapter(getSupportFragmentManager());

        fragmentprofile=new FragmentShopProfile();
        fragmentmenu=new FragmentShopMenu();
        fragmentinsights=new FragmentShopInsights();
        fragmentdailyactivity=new FragmentShopDailyActivity();
        adapter.addFragment(fragmentprofile);
        adapter.addFragment(fragmentmenu);
        adapter.addFragment(fragmentinsights);
        adapter.addFragment(fragmentdailyactivity);

        viewPager.setAdapter(adapter);
    }



    @Override
    public void onMenuItemSelect(int i, int i1, boolean b) {
        switch(i1) {

            case R.id.navigation_shop_profile:
                viewPager.setCurrentItem(0);
                break;
            case R.id.navigation_shop_menu:

                viewPager.setCurrentItem(1);
                break;
            case R.id.navigation_shop_insights:
                viewPager.setCurrentItem(2);
                break;
            case R.id.navigation_shop_daily_activity:
                viewPager.setCurrentItem(3);
                break;

            default:
                Toast.makeText(ShopProfileActivity2.this, "Error occurred", Toast.LENGTH_SHORT).show();
                break;
        }


        }
//funciont for what happens if a fragment is reselected
    @Override
    public void onMenuItemReselect(int i, int i1, boolean b) {
        FragmentManager manager=getSupportFragmentManager();

        switch(i1) {

            case R.id.navigation_shop_profile:
                FragmentShopProfile fragment1=(FragmentShopProfile)manager.findFragmentById(id.fragment_shop_profile);
                //write code for scrolling to top and do the same for the rest
                break;
            case R.id.navigation_shop_menu:

                viewPager.setCurrentItem(1);
                break;
            case R.id.navigation_shop_insights:
                viewPager.setCurrentItem(2);
                break;
            case R.id.navigation_shop_daily_activity:
                viewPager.setCurrentItem(3);
                break;

            default:
                Toast.makeText(ShopProfileActivity2.this, "Error occurred", Toast.LENGTH_SHORT).show();
                break;
        }


    }


    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        switch(menuItem.getItemId()) {

            case R.id.navigation_shop_profile:
                viewPager.setCurrentItem(0);
                break;
            case R.id.navigation_shop_menu:

                viewPager.setCurrentItem(1);
                break;
            case R.id.navigation_shop_insights:
                viewPager.setCurrentItem(2);
                break;
            case R.id.navigation_shop_daily_activity:
                viewPager.setCurrentItem(3);
                break;

            default:
                Toast.makeText(ShopProfileActivity2.this, "Error occurred", Toast.LENGTH_SHORT).show();
                break;
        }


    return false;
    }

}



