package com.alcatraz.admin.project_alcatraz.Profile;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;
import android.widget.Toast;

import com.alcatraz.admin.project_alcatraz.R;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import me.relex.circleindicator.CircleIndicator;

/**
 * Created by admin on 1/20/2018.
 */

public class UserProfileActivity extends AppCompatActivity {
    private static final String TAG="ProfileACtivity";
    private static final int ACTIVITY_NUM=4;
    private Context context=UserProfileActivity.this;
    private GridView gridView;
    private GridItemAdapter gridAdapter;


        private static ViewPager mPager;
        private static int currentPage = 0;

    private static final Integer[] XMEN1= {R.drawable.water,R.drawable.download,R.drawable.download1,R.drawable.fin,R.drawable.hat,R.drawable.water,R.drawable.download};

    private static final Integer[] XMEN= {R.drawable.download,R.drawable.download1,R.drawable.fin,R.drawable.hat,R.drawable.water};
        private ArrayList<Integer> XMENArray = new ArrayList<Integer>();

    public UserProfileActivity() {
    }

    @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_user_profile);
        gridView = (GridView) findViewById(R.id.gridView);
        gridAdapter = new GridItemAdapter(this, R.layout.griditem, getData());
        gridView.setAdapter(gridAdapter);
            init();
        }
        private void init() {
            for(int i=0;i<XMEN.length;i++)
                XMENArray.add(XMEN[i]);

            mPager = (ViewPager) findViewById(R.id.pager);
            mPager.setAdapter(new ImageAdapterProfile(UserProfileActivity.this,XMENArray));
            CircleIndicator indicator = (CircleIndicator) findViewById(R.id.indicator);
            indicator.setViewPager(mPager);

            XMENArray.clear();
            for(int i=0;i<XMEN1.length;i++)
                XMENArray.add(XMEN1[i]);
            mPager.setAdapter(new ImageAdapterProfile(UserProfileActivity.this,XMENArray));            //mPager.setCurrentItem(1);
            ViewPager.OnPageChangeListener listener = new ViewPager.OnPageChangeListener() {
                boolean var=false;
                @Override
                public void onPageScrolled(int position, float positionOffset, int pixels) {
                /*Log.e("ss",position+" by pixels "+pixels);
                if(pixels!=0)
                    var=false;
                if(position==0&&pixels==0&&var==true)
                {
                    var=false;
                    mPager.setCurrentItem(mPager.getAdapter().getCount()-1,true);
                    return;
                }
                    if(position==mPager.getAdapter().getCount()-1&&pixels==0&&var==true)
                    {
                        var=false;
                        mPager.setCurrentItem(0,true);
                        return;
                    }


                    if((position==mPager.getAdapter().getCount()-1||position==0)&&pixels==0)
                    var=true;*/
                }

                @Override
                public void onPageSelected(int position) {
                }

                @Override
              public void onPageScrollStateChanged(int state) {
                        if (state == ViewPager.SCROLL_STATE_IDLE) {
                            int curr = mPager.getCurrentItem();
                            int lastReal = mPager.getAdapter().getCount() - 2;
                            if (curr == 0) {
                                mPager.setCurrentItem(lastReal, false);
                            } else if (curr > lastReal) {
                                mPager.setCurrentItem(1, false);
                            }
                        }
                    }



            };
            mPager.addOnPageChangeListener(listener);

            // Auto start of viewpager
//            final Handler handler = new Handler();
//            final Runnable Update = new Runnable() {
//                public void run() {
//                    if (currentPage == XMEN.length) {
//                        currentPage = 0;
//                    }
//                    mPager.setCurrentItem(currentPage++, true);
//                }
//            };
//            Timer swipeTimer = new Timer();
//            swipeTimer.schedule(new TimerTask() {
//                @Override
//                public void run() {
//                    handler.post(Update);
//                }
//            }, 2500, 2500);
        }
    private ArrayList<ImageItem> getData() {
        final ArrayList<ImageItem> imageItems = new ArrayList<>();
        TypedArray imgs = getResources().obtainTypedArray(R.array.image_ids);
        for (int i = 0; i < imgs.length(); i++) {
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), imgs.getResourceId(i, -1));
            imageItems.add(new ImageItem(bitmap, "Image#" + i));
        }
        return imageItems;
    }

     private void setupToolbar()
     {

         Toolbar toolbar=(Toolbar)findViewById(R.id.profile_toolbar);
         setSupportActionBar(toolbar);
         toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
             @Override
             public boolean onMenuItemClick(MenuItem item) {
                 Log.d(TAG,"onMenuItemClick:Navigating to Profile Prefs");
                 return false;
             }
         });
     }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.profile_menu,menu);
        return true;

    }
    public void editProfile(View v)
    {
        setContentView(R.layout.edit_profile);

        Intent i=new Intent(getApplicationContext(),EditProfile.class);
        startActivity(i);

    }
}
