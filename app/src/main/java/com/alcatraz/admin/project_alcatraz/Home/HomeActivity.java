package com.alcatraz.admin.project_alcatraz.Home;

import android.content.Intent;
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
import android.view.GestureDetector;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.alcatraz.admin.project_alcatraz.R;
import com.alcatraz.admin.project_alcatraz.Session.SessionUserActivity;
import com.alcatraz.admin.project_alcatraz.Social.ChatActivity;
import com.alcatraz.admin.project_alcatraz.Social.MessageAdapter;
import com.alcatraz.admin.project_alcatraz.Social.MessagesFragment;
import com.alcatraz.admin.project_alcatraz.Utility.Constants;
import com.alcatraz.admin.project_alcatraz.Utility.Util;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by admin on 1/14/2018.
 */

public class HomeActivity extends AppCompatActivity {
    private RecyclerView recyclerView, mRvHorizontal1, mRvHorizontal2;
    private GestureDetectorCompat mDetector;
    private ArrayList<String> Number;
    private View ChildView ;
    int RecyclerViewItemPosition ;


    private final String TAG="HomeActivity";
    private DrawerLayout drawerLayout=null;
    private IntentIntegrator qrScan;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_with_drawer);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        NavigationView navigationView = findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
            drawerLayout.closeDrawers();
            switch (item.getItemId())
            {
                case R.id.drawer_profile:
                    //Toast.makeText(HomeActivity.this, "profile menu item selected", Toast.LENGTH_SHORT).show();
                    //showprofile();
                    Intent intent=new Intent(getApplicationContext(),UserProfileNew.class);
                    startActivity(intent);
                    break;
                case R.id.drawer_settings:
                    //setContentView(R.layout.activity_shop_profile);
                    intent = new Intent(getApplicationContext(), SessionUserActivity.class);
                    startActivity(intent);
                    break;
                case R.id.drawer_privacysettings:
                    intent = new Intent(getApplicationContext(), TransactionActivity.class);
                    startActivity(intent);
                    break;
                    default:
                        Toast.makeText(HomeActivity.this, "yo whats up, default of switch case selected", Toast.LENGTH_SHORT).show();
                        break;
            }
            return true;

            }
        });

        drawerLayout = findViewById(R.id.drawer);
        ActionBarDrawerToggle actionBarDrawerToggle=new ActionBarDrawerToggle(this,drawerLayout, toolbar,R.string.app_name,R.string.app_name){
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
        EndDrawerToggle right_toggle=new EndDrawerToggle(this,drawerLayout, toolbar,R.string.app_name,R.string.app_name){
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
        mRvHorizontal1 = findViewById(R.id.rv_horizontal_1);
        mRvHorizontal2 = findViewById(R.id.rv_horizontal_2);
        initializeHorizontalView(mRvHorizontal1);
        initializeHorizontalView(mRvHorizontal2);
        initializeFeeds();
        qrScan = new IntentIntegrator(this);
        mDetector = new GestureDetectorCompat(this, new ListenToHorizontal());

        messages();
    }
    private void initializeHorizontalView(RecyclerView recyclerView)
    {

        recyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event){
                return mDetector.onTouchEvent(event);
            }
        });
        RecyclerView.LayoutManager recyclerViewLayoutManager = new LinearLayoutManager(getApplicationContext());


        recyclerView.setLayoutManager(recyclerViewLayoutManager);

        // Adding items to RecyclerView.
        AddItemsToRecyclerViewArrayList();

        RecyclerViewAdapter recyclerViewHorizontalAdapter = new RecyclerViewAdapter(Number);

        LinearLayoutManager horizontalLayout = new LinearLayoutManager(HomeActivity.this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(horizontalLayout);

        recyclerView.setAdapter(recyclerViewHorizontalAdapter);


        // Adding on item click listener to RecyclerView.
        recyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {

            GestureDetector gestureDetector = new GestureDetector(HomeActivity.this, new GestureDetector.SimpleOnGestureListener() {

                @Override
                public boolean onSingleTapUp(MotionEvent motionEvent) {

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

        recyclerView = findViewById(R.id.recyclerview2);
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


        // Adding on item click listener to RecyclerView.
        recyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {

            GestureDetector gestureDetector = new GestureDetector(HomeActivity.this, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                    //Log.e("T",(recyclerView1.getLayoutManager().isViewPartiallyVisible(child,true,true))+"");

                    if (distanceY > 0) {
                        // Scrolled downward
                    }
                    if (distanceY < 0) {
                        // Scrolled upward
                    }
                    return false;
                }

                @Override public boolean onSingleTapUp(MotionEvent motionEvent) {

                    return false;
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
        public boolean onDown(MotionEvent e) {
            return super.onDown(e);
        }

        @Override
        public boolean onScroll(MotionEvent event1, MotionEvent event2, float distanceX, float distanceY) {
            Log.e(DEBUG_TAG, "onScroll: ( " + distanceX + ", " + distanceY + " )");
            /*if (Math.abs(distanceX) < 10) {
                if (distanceY > 15) {
                    Util.animateHide(mRvHorizontal2, -getResources().getDimensionPixelSize(R.dimen.home_horizontal_rv_height));
                    return true;
                }
                Util.animateShow(mRvHorizontal2, getResources().getDimensionPixelSize(R.dimen.home_horizontal_rv_height));
                return true;
            }*/
            return false;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            Log.e("HERE","HERE");
            Util.animateShow(mRvHorizontal2, getResources().getDimensionPixelSize(R.dimen.home_horizontal_rv_height));
        }
    }
    public class ListenToVertical extends GestureDetector.SimpleOnGestureListener {
        private static final String DEBUG_TAG = "Gestures";

        @Override
        public boolean onDown(MotionEvent event) {
            Log.e(DEBUG_TAG, "VERTICAL DOWN: " + event.toString());
            Util.animateHide(mRvHorizontal2, -getResources().getDimensionPixelSize(R.dimen.home_horizontal_rv_height));
            return true;
        }

        @Override
        public boolean onFling(MotionEvent event1, MotionEvent event2, float velocityX, float velocityY) {
            if (event1.getY() > event2.getY()) {
                Log.e(TAG, "Down -> Up FLING performed!");
                if (recyclerView != null)
                    recyclerView.getLayoutManager().scrollToPosition(0);
            }
            return true;
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            Log.e("","CONFIRMED");
            return super.onSingleTapUp(e);
        }
    }
    private void messages() {
        final ArrayList<String> feed = new ArrayList<>();
        final ArrayList<String> feed1 = new ArrayList<>();

        final ArrayList<Integer> im=new ArrayList<>();
        String ss = "Hello How are you I1 am awesome erf w 3 3 3 3 3 3  3 6 6 6 6 6 6 6 6 6 6 6 6 6 6";
        Scanner sc = new Scanner(ss);
        while (sc.hasNext()) {
            feed.add("Rahul Dev");
            feed1.add("Deal done.Will See you Later.Bye Bye");
            sc.next();
            im.add(R.drawable.fin);
        }

        final RecyclerView recyclerView = findViewById(R.id.nav_drawer_recycler_view);
       /* recyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return new GestureDetectorCompat(getApplicationContext(), new ListenToVertical()).onTouchEvent(event);
            }
        });*/



        MessageAdapter RecyclerViewHorizontalAdapter = new MessageAdapter(feed,feed1,im);

        LinearLayoutManager HorizontalLayout = new LinearLayoutManager(HomeActivity.this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(HorizontalLayout);

        recyclerView.setAdapter(RecyclerViewHorizontalAdapter);
        recyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {

            GestureDetector gestureDetector = new GestureDetector(HomeActivity.this, new GestureDetector.SimpleOnGestureListener() {

                @Override
                public boolean onSingleTapUp(MotionEvent motionEvent) {

                    return true;
                }
            });
            @Override
            public boolean onInterceptTouchEvent(RecyclerView Recyclerview, MotionEvent motionEvent) {

                ChildView = Recyclerview.findChildViewUnder(motionEvent.getX(), motionEvent.getY());

                if(ChildView != null && gestureDetector.onTouchEvent(motionEvent)) {

                    //Getting clicked value.
                    RecyclerViewItemPosition = Recyclerview.getChildAdapterPosition(ChildView);
                    Intent i=new Intent(getApplicationContext(),ChatActivity.class);
                    startActivity(i);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                // cancelled operation
            } else {
                String requestJson = String.format("{'data': %s}", result.getContents());
                String response = Util.postApi(Constants.API_URL_DECRYPT_QR, requestJson);
                try {
                    JSONObject object = new JSONObject(response);
                    SessionUserActivity.startSession(this, object.getInt("shop_id"), object.getInt("qr_id"));
                } catch (JSONException e) {
                    Toast.makeText(this, "Invalid QR!", Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
