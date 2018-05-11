package com.alcatraz.admin.project_alcatraz.Utility;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by shivanshs9 on 11/5/18.
 */

public class DynamicSwipableViewPager extends ViewPager{
    private boolean mEnabled = true;

    public DynamicSwipableViewPager(Context context) {
        super(context);
    }

    public DynamicSwipableViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setEnabled(boolean enabled) {
        this.mEnabled = enabled;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return this.mEnabled && super.onTouchEvent(event);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        return this.mEnabled && super.onInterceptTouchEvent(event);
    }

}
