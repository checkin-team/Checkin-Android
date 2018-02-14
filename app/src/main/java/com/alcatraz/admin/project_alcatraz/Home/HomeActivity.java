package com.alcatraz.admin.project_alcatraz.Home;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.Toast;

import com.alcatraz.admin.project_alcatraz.R;
import com.google.zxing.integration.android.IntentIntegrator;

import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by admin on 1/14/2018.
 */

public class HomeActivity extends AppCompatActivity {
    private  RecyclerView recyclerView;
    private GestureDetectorCompat mDetector;
    private ArrayList<String> Number;
    private RecyclerView.LayoutManager RecyclerViewLayoutManager;
    private RecyclerViewAdapter RecyclerViewHorizontalAdapter;
    private LinearLayoutManager HorizontalLayout ;
    private View ChildView ;
    int RecyclerViewItemPosition ;


    private final String TAG="HomeActivity";
    private NavigationView navigationView=null,nav_right=null;
    private DrawerLayout drawerLayout=null,drawerLayout1=null;
    private Toolbar toolbar=null;
    private IntentIntegrator qrScan;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_with_drawer);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
       setSupportActionBar(toolbar);
        Log.d(TAG,"onCreate: starting.");
       nav_right=findViewById(R.id.navigation_view_right);
      navigationView=(NavigationView)findViewById(R.id.navigation_view);
       navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                drawerLayout.closeDrawers();
                switch (item.getItemId())
                {

                    case R.id.drawer_profile:
                        Toast.makeText(HomeActivity.this, "profile menu item selected", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.drawer_settings:
                        Toast.makeText(HomeActivity.this, "settings menu item selected", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.drawer_privacysettings:
                        Toast.makeText(HomeActivity.this, "privacy settings menu item selected", Toast.LENGTH_SHORT).show();
                        break;
                        default:
                            Toast.makeText(HomeActivity.this, "yo whats up, default of switch case selected", Toast.LENGTH_SHORT).show();
                            break;



                }
                return true;

            }
        });

        nav_right.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                drawerLayout.closeDrawers();
                Toast.makeText(HomeActivity.this, item.getTitle(), Toast.LENGTH_SHORT).show();
                return true;

            }
        });

        drawerLayout=(DrawerLayout)findViewById(R.id.drawer);
        drawerLayout1=(DrawerLayout)findViewById(R.id.right_nav);

        ActionBarDrawerToggle actionBarDrawerToggle=new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.app_name,R.string.app_name){
            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);

            }

            @Override
            public void onDrawerOpened(View drawerView) {

                super.onDrawerOpened(drawerView);
            }

        };
        actionBarDrawerToggle.syncState();
       drawerLayout.addDrawerListener(actionBarDrawerToggle);
        EndDrawerToggle right_toggle=new EndDrawerToggle(this,drawerLayout,toolbar,R.string.app_name,R.string.app_name){
            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);

            }

            @Override
            public void onDrawerOpened(View drawerView) {

                super.onDrawerOpened(drawerView);
            }

        };
        right_toggle.syncState();
        drawerLayout.addDrawerListener(right_toggle);
        getSupportActionBar().setTitle("");

       // getSupportActionBar().setHideOnContentScrollEnabled(true);}
//
//        //calling sync state is necessary or else your hamburger icon wont show up




//       setupBottomNavigationView();
//        setupViewPager();
        initializeHorizontalView(R.id.recyclerview1);
        initializeHorizontalView(R.id.recyclerviewhorizontal2);
        initializeFeeds();
        qrScan = new IntentIntegrator(this);
        mDetector = new GestureDetectorCompat(this, new ListenToHorizontal());


    }
    private void initializeHorizontalView(int id)
    {

        recyclerView = (RecyclerView)findViewById(id);
        recyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event){
                return mDetector.onTouchEvent(event);
            }
        });
        RecyclerViewLayoutManager = new LinearLayoutManager(getApplicationContext());

        recyclerView.setLayoutManager(RecyclerViewLayoutManager);

        // Adding items to RecyclerView.
        AddItemsToRecyclerViewArrayList();

        RecyclerViewHorizontalAdapter = new RecyclerViewAdapter(Number);

        HorizontalLayout = new LinearLayoutManager(HomeActivity.this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(HorizontalLayout);

        recyclerView.setAdapter(RecyclerViewHorizontalAdapter);


        // Adding on item click listener to RecyclerView.
        recyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {

            GestureDetector gestureDetector = new GestureDetector(HomeActivity.this, new GestureDetector.SimpleOnGestureListener() {

                @Override public boolean onSingleTapUp(MotionEvent motionEvent) {

                    return true;
                }

            });
            @Override
            public boolean onInterceptTouchEvent(RecyclerView Recyclerview, MotionEvent motionEvent) {

                ChildView = Recyclerview.findChildViewUnder(motionEvent.getX(), motionEvent.getY());

                if(ChildView != null && gestureDetector.onTouchEvent(motionEvent)) {

                    //Getting clicked value.
                    RecyclerViewItemPosition = Recyclerview.getChildAdapterPosition(ChildView);

                    // Showing clicked item value on screen using toast message.
                    Toast.makeText(HomeActivity.this, Number.get(RecyclerViewItemPosition), Toast.LENGTH_SHORT).show();

                }

                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView Recyclerview, MotionEvent motionEvent) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });
    }
    private void initializeFeeds()
    {
        final ArrayList<String> feed=new ArrayList<>();
        String ss="THIS IS A RANDOM TEXT OF RANDOM LENGTH WITH NO MEANING AT ALL";
        Scanner sc=new Scanner(ss);
        while(sc.hasNext())
            feed.add(sc.next());

        final RecyclerView recyclerView = (RecyclerView)findViewById(R.id.recyclerview2);
        recyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event){
                return new GestureDetectorCompat(getApplicationContext(), new ListenToVertical()).onTouchEvent(event);
            }
        });

        LinearLayoutManager RecyclerViewLayoutManager = new LinearLayoutManager(getApplicationContext());

        recyclerView.setLayoutManager(RecyclerViewLayoutManager);

        // Adding items to RecyclerView.

        FeedsAdapter RecyclerViewHorizontalAdapter = new FeedsAdapter(feed);

        LinearLayoutManager HorizontalLayout = new LinearLayoutManager(HomeActivity.this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(HorizontalLayout);

        recyclerView.setAdapter(RecyclerViewHorizontalAdapter);
        final RecyclerView r1=findViewById(R.id.recyclerview1);


        // Adding on item click listener to RecyclerView.
        recyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {

            boolean showing=true;
            GestureDetector gestureDetector = new GestureDetector(HomeActivity.this, new GestureDetector.SimpleOnGestureListener() {
               /* @Override                public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {

                    if (distanceY > 0) {
                        // Scrolled upward
                        if (e2.getAction() == MotionEvent.ACTION_MOVE&&showing==true)
                        {
                            showing=false;
                            Log.e("g","GOing uP");
                            toolbar.setVisibility(View.GONE);
                            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) recyclerView.getLayoutParams();
                            params.topMargin =(int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 121-56, getResources().getDisplayMetrics());
                            ViewGroup.MarginLayoutParams params1 = (ViewGroup.MarginLayoutParams) r1.getLayoutParams();
                            params1.topMargin =(int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getResources().getDisplayMetrics());

                            // The pointer has gone up, ending the gesture
                        }
                    }
                    if (distanceY < 0) {
                        Log.e("g","GOing DOWNTOWn");

                        // Scrolled upward
                        if (e2.getAction() == MotionEvent.ACTION_MOVE&&showing==false)
                        {
                            showing=true;
                            toolbar.setVisibility(View.VISIBLE);
                            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) recyclerView.getLayoutParams();
                            params.topMargin =(int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 121, getResources().getDisplayMetrics());
                            ViewGroup.MarginLayoutParams params1 = (ViewGroup.MarginLayoutParams) r1.getLayoutParams();
                            params1.topMargin =(int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 61, getResources().getDisplayMetrics());

                            // The pointer has gone up, ending the gesture
                        }
                    }
                    return false;
                }*/

                @Override public boolean onSingleTapUp(MotionEvent motionEvent) {

                    return true;
                }

            });
            @Override
            public boolean onInterceptTouchEvent(RecyclerView Recyclerview, MotionEvent motionEvent) {

                ChildView = Recyclerview.findChildViewUnder(motionEvent.getX(), motionEvent.getY());

                if(ChildView != null && gestureDetector.onTouchEvent(motionEvent)) {

                    //Getting clicked value.
                    RecyclerViewItemPosition = Recyclerview.getChildAdapterPosition(ChildView);

                    // Showing clicked item value on screen using toast message.
                    Toast.makeText(HomeActivity.this, feed.get(RecyclerViewItemPosition), Toast.LENGTH_LONG).show();

                }

                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView Recyclerview, MotionEvent motionEvent) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });



    }

    /**
     * *
     * for adding the fragments in ghome activity
     *
     */
    public void startQr(View v)
    {
        qrScan.initiateScan();

    }


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
    public void AddItemsToRecyclerViewArrayList(){

        Number = new ArrayList<>();
        Number.add("ONE");
        Number.add("TWO");
        Number.add("THREE");
        Number.add("FOUR");
        Number.add("FIVE");
        Number.add("SIX");
        Number.add("SEVEN");
        Number.add("EIGHT");
        Number.add("NINE");
        Number.add("TEN");

    }

    public class ListenToHorizontal extends GestureDetector.SimpleOnGestureListener {
        private static final String DEBUG_TAG = "Gestures";

        @Override
        public boolean onDown(MotionEvent event) {
            Log.e(DEBUG_TAG, "onDown: " + event.toString());
            return true;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            Log.e("HERE","HERE");
            final RecyclerView r = findViewById(R.id.recyclerviewhorizontal2);
            r.animate()
                    .translationY(0)
                    .alpha(1.0f)
                    .setDuration(500)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            r.setVisibility(View.VISIBLE);
                        }
                    });


        }
    }
    public class ListenToVertical extends GestureDetector.SimpleOnGestureListener {
        private static final String DEBUG_TAG = "Gestures";

        @Override
        public boolean onDown(MotionEvent event) {
            final View view=findViewById(R.id.recyclerviewhorizontal2);
            Log.e(DEBUG_TAG, "VERTICLA DOWN: " + event.toString());view.animate()
                    .translationY(-view.getHeight())
                    .alpha(0.0f)
                    .setDuration(300)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            view.clearAnimation();
                            view.setVisibility(View.GONE);
                        }
                    });


            return true;
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            Log.e("","CONFIRMED");
            return super.onSingleTapUp(e);
        }
    }
}
