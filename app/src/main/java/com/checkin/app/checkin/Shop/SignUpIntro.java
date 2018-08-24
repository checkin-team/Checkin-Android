package com.checkin.app.checkin.Shop;

import android.app.ActionBar;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toolbar;

import com.checkin.app.checkin.R;
import com.rd.PageIndicatorView;
import com.rd.animation.type.AnimationType;
import com.rd.draw.controller.DrawController;

public class SignUpIntro extends AppCompatActivity {
    public PageIndicatorView pageIndicatorView;

    private static final String TAG = "SignUpIntro";

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_intro);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        Log.e(TAG, "onCreate: "+actionBar );
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.back_appbar);
        actionBar.setElevation(20);
        actionBar.setTitle("");
  //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
      //getSupportActionBar().setHomeButtonEnabled(true);
      //getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_appbar);

        ViewPager viewPager = findViewById(R.id.viewpager);
        viewPager.setAdapter(new SignUpIntroPager());




        pageIndicatorView = (PageIndicatorView)findViewById(R.id.pageIndicatorView);
        //position = pageIndicatorView.getCount();
        pageIndicatorView.setAnimationType(AnimationType.FILL);
        pageIndicatorView.setClickListener(new DrawController.ClickListener() {
            @Override
            public void onIndicatorClicked(int position) {
                viewPager.setCurrentItem(position);
            }
        });
         /*View.OnClickListener mIndicatorListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                viewPager.setCurrentItem(position);
            }
        };
        pageIndicatorView.setOnClickListener(mIndicatorListener);*/

    }
}
