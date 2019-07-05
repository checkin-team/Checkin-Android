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
    public int viewpagerHeight;
    Context context;

    public CustomViewPager(Context context, CustomViewPager viewPager) {
        super(context);
        this.context = context;
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
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public int viewPagerMescHeight(){
        return viewPager.getMeasuredHeight();
    }


    public void expand(ViewGroup vSubGroupWrapper, int height) {
//        int matchParentMeasureSpec = View.MeasureSpec.makeMeasureSpec(viewPager.getWidth(), View.MeasureSpec.EXACTLY);
//        int wrapContentMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);

//        viewPager.measure(ViewGroup.LayoutParams.MATCH_PARENT, wrapContentMeasureSpec);
//        viewPager.measure(matchParentMeasureSpec, viewPager.getHeight());

        int targetHeight = viewPager.getMeasuredHeight();
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





//        viewPager.measure(CustomViewPager.LayoutParams.MATCH_PARENT, CustomViewPager.LayoutParams.WRAP_CONTENT);
//        final int targetHeight = viewpagerHeight;
//
//        viewPager.getLayoutParams().height = 0;
//        viewPager.setVisibility(View.VISIBLE);
//        vSubGroupWrapper.setVisibility(View.VISIBLE);
//
//        ValueAnimator anim = ValueAnimator.ofInt(viewPager.getMeasuredHeight(), targetHeight);
//        anim.setInterpolator(new AccelerateInterpolator());
//        anim.setDuration(1000);
//        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//            @Override
//            public void onAnimationUpdate(ValueAnimator animation) {
//                ViewGroup.LayoutParams layoutParams = viewPager.getLayoutParams();
//                layoutParams.height = (int) (targetHeight * animation.getAnimatedFraction());
//                viewPager.setLayoutParams(layoutParams);
//            }
//        });
//        anim.addListener(new AnimatorListenerAdapter() {
//            @Override
//            public void onAnimationEnd(Animator animation) {
//                // At the end of animation, set the height to wrap content
//                // This fix is for long views that are not shown on screen
//                ViewGroup.LayoutParams layoutParams = viewPager.getLayoutParams();
//                layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
//            }
//        });
//        anim.start();


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