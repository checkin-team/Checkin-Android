package com.checkin.app.checkin.Utility;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.Transformation;

import androidx.viewpager.widget.ViewPager;

import com.checkin.app.checkin.R;

public class ChildSizeMeasureViewPager extends ViewPager {
    public ChildSizeMeasureViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        addOnPageChangeListener(new SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                requestLayout();
            }
        });
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int mode = MeasureSpec.getMode(heightMeasureSpec);
        if (mode == MeasureSpec.UNSPECIFIED || mode == MeasureSpec.AT_MOST) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(getChildHeight(getCurrentItem(), widthMeasureSpec), MeasureSpec.EXACTLY);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    int getChildHeight(int pos, int widthMeasureSpec) {
        View child = getChildAt(pos);
        if (child != null) {
            /*child.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    child.getViewTreeObserver().removeOnPreDrawListener(this);
                    Log.e("PreDraw", "Width: " + child.getWidth());
                    Log.e("PreDraw", "Height: " + child.getHeight());
                    return true;
                }
            });*/

            child.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
            return child.getMeasuredHeight();
        }
        return 0;
    }
}