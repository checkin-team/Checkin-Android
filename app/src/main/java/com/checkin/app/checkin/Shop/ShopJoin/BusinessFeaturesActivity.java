package com.checkin.app.checkin.Shop.ShopJoin;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.checkin.app.checkin.R;
import com.rd.PageIndicatorView;
import com.rd.animation.type.AnimationType;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class BusinessFeaturesActivity extends AppCompatActivity {
    @BindView(R.id.piv_features) PageIndicatorView vPageIndicator;
    @BindView(R.id.pager) ViewPager vPager;

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_features);

        ButterKnife.bind(this);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_appbar_back);
        actionBar.setElevation(10);
        actionBar.setTitle("");

        vPager.setAdapter(new SignUpIntroPager());
        vPageIndicator.setAnimationType(AnimationType.FILL);
        vPageIndicator.setClickListener(position -> vPager.setCurrentItem(position));
    }

    @OnClick(R.id.btn_create_account)
    public void onClickCreateAccount() {
        startActivity(new Intent(this, ContactVerifyActivity.class));
    }


    static class SignUpIntroPager extends PagerAdapter {
        private List<View> views = new ArrayList<>();

        @Override
        public int getCount() {
            return 4;
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view.equals(object);
        }
        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            View view;
            ViewHolder holder;

            if (views.size() <= position) {
                view = LayoutInflater.from(container.getContext())
                        .inflate(R.layout.item_business_feature, null);
                views.add(view);
            } else {
                view = views.get(position);
            }

            if (view.getTag() != null) {
                holder = ((ViewHolder) view.getTag());
            } else {
                holder = new ViewHolder(view);
            }
            holder.bindData(position);
            view.setTag(holder);
            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
        }

        class ViewHolder {
            @BindView(R.id.tv_feature_title) TextView tvTitle;
            @BindView(R.id.tv_feature_subtitle) TextView tvSubTitle;
            @BindView(R.id.im_feature) ImageView imFeature;

            ViewHolder(@NonNull View view) {
                ButterKnife.bind(this, view);
            }

            void bindData(int position) {
                switch (position) {
                    case 0:
                        tvTitle.setText("Business Profile");
                        tvSubTitle.setText("Add phone number, email or location so " +
                                "that Checkin help you grow your business.");
                        imFeature.setImageResource(R.drawable.ic_business_profile);
                        break;
                    case 1:
                        tvTitle.setText("Dynamic Menu");
                        tvSubTitle.setText("Upload your regular menu and have a " +
                                "real time control of managing the inventory.");
                        imFeature.setImageResource(R.drawable.ic_business_menu);
                        break;
                    case 2:
                        tvTitle.setText("Insights");
                        tvSubTitle.setText("Learn about the followers and see " +
                                "how your business is performing.");
                        imFeature.setImageResource(R.drawable.ic_business_insight);
                        break;
                    case 3:
                        tvTitle.setText("Activity");
                        tvSubTitle.setText("Track your in-store traffic, live orders " +
                                "and expand the reach of your business among consumers.");
                        imFeature.setImageResource(R.drawable.ic_business_activity);
                        break;
                }
            }
        }
    }
}
