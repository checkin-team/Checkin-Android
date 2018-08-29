package com.checkin.app.checkin.Shop;

import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.checkin.app.checkin.R;
import com.rd.PageIndicatorView;
import com.rd.animation.type.AnimationType;
import com.rd.draw.controller.DrawController;

public class SignUpIntroActivity extends AppCompatActivity {
    public PageIndicatorView pageIndicatorView;
    public Button btnCreateAccount;

    private static final String TAG = "SignUpIntroActivity";
//    @BindView(R.id.btn_create_account)
//    Button btnCreateAccount;

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_intro);

        btnCreateAccount = (Button)findViewById(R.id.btn_create_account);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        Log.e(TAG, "onCreate: "+actionBar );
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.back_appbar);
        actionBar.setElevation(20);
        actionBar.setTitle("");

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

    btnCreateAccount.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            startActivity(new Intent(SignUpIntroActivity.this, SignUpVerifyActivity.class));
        }
    });
    }

}
