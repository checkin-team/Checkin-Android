package com.checkin.app.checkin.Utility;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;

import androidx.viewpager.widget.ViewPager;

public class CustomViewPager extends ViewPager {
    CustomViewPager viewPager;
    int viewpagerHeight;

    public CustomViewPager(Context context, CustomViewPager viewPager) {
        super(context);
        this.viewPager = viewPager;
    }

    public CustomViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int mode = MeasureSpec.getMode(heightMeasureSpec);
        if (mode == MeasureSpec.UNSPECIFIED || mode == MeasureSpec.AT_MOST) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            int height = 0;
            for (int i = 0; i < getChildCount(); i++) {
                View child = getChildAt(i);
                child.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
                int childMeasuredHeight = child.getMeasuredHeight();
                if (childMeasuredHeight > height) {
                    height = childMeasuredHeight;
                }
            }
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
        }
        viewpagerHeight = heightMeasureSpec;
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public void expand(ViewGroup vSubGroupWrapper) {
//        int matchParentMeasureSpec = View.MeasureSpec.makeMeasureSpec(viewPager.getWidth(), View.MeasureSpec.EXACTLY);
//        int wrapContentMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);

//        viewPager.measure(ViewGroup.LayoutParams.MATCH_PARENT, wrapContentMeasureSpec);
//        viewPager.measure(matchParentMeasureSpec, viewPager.getHeight());


//        viewPager.measure(matchParentMeasureSpec, viewpagerHeight);
        final int targetHeight = viewPager.getMeasuredHeight();
//
        viewPager.getLayoutParams().height = 1;
        Animation a = new Animation()
        {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                viewPager.getLayoutParams().height = interpolatedTime == 1 ? CustomViewPager.LayoutParams.WRAP_CONTENT
                        : (int)(targetHeight * interpolatedTime);
                viewPager.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // Expansion speed of 1dp/ms
        a.setDuration((int)(targetHeight / viewPager.getContext().getResources().getDisplayMetrics().density));

        viewPager.setVisibility(View.VISIBLE);
        vSubGroupWrapper.setVisibility(View.VISIBLE);
        viewPager.startAnimation(a);
    }

    public void collapse(ViewGroup vSubGroupWrapper) {
        final int initialHeight = viewPager.getMeasuredHeight();

        Animation a = new Animation()
        {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if(interpolatedTime == 1){
                    viewPager.setVisibility(View.GONE);
                    vSubGroupWrapper.setVisibility(GONE);
                }else{
                    viewPager.getLayoutParams().height = initialHeight - (int)(initialHeight * interpolatedTime);
                    viewPager.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // Collapse speed of 1dp/ms
        a.setDuration((int)(initialHeight / viewPager.getContext().getResources().getDisplayMetrics().density));
        viewPager.startAnimation(a);
    }


}