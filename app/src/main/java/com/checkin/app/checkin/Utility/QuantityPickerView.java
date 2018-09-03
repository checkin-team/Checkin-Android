package com.checkin.app.checkin.Utility;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.checkin.app.checkin.R;
import com.yarolegovich.discretescrollview.DiscreteScrollView;

import java.util.concurrent.Callable;

public class QuantityPickerView extends DiscreteScrollView {
    private Callable<Void> mCallable;
    private float mDownX;
    private static final int MAX_CLICK_DURATION = 200;
    private static final int MAX_CLICK_DISTANCE = 10;
    private long startClickTime;
    public enum Direction {
        NONE, START, END
    }

    private Direction mDisabledScrollDirection = Direction.NONE;

    public QuantityPickerView(Context context) {
        super(context);
        init();
    }

    public QuantityPickerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public QuantityPickerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setAdapter(new TextBaseAdapter(
                Util.range(0, 30),
                getResources().getColor(R.color.pinkish_grey),
                getResources().getColor(R.color.brownish_grey))
        );
    }

    public void setCallable(Callable<Void> callable) {
        mCallable = callable;
    }

    public void setDisabledDirection(Direction direction) {
        mDisabledScrollDirection = direction;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {
        if (e.getAction() == MotionEvent.ACTION_DOWN) {
            mDownX = e.getX();
            startClickTime = System.currentTimeMillis();
        }
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        if (e.getAction() == MotionEvent.ACTION_UP) {
            float dx = e.getX() - mDownX;
            long clickDuration = System.currentTimeMillis() - startClickTime;
            if (clickDuration < MAX_CLICK_DURATION && dx <= MAX_CLICK_DISTANCE) {
                final View view = findChildViewUnder(e.getX(), e.getY());
                try {
                    ViewHolder holder = findContainingViewHolder(view);
                    if (holder != null && view.performClick())
                        return false;
                } catch (Exception exc) {
                    exc.printStackTrace();
                }
            }
        }
        if (mDisabledScrollDirection != Direction.NONE) {
            boolean result = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                    if (mDisabledScrollDirection == Direction.START)
                        return e2.getX() - mDownX > 0;
                    else
                        return e2.getX() - mDownX < 0;
                }
            }).onTouchEvent(e);
            if (result && mCallable != null) {
                try {
                    mCallable.call();
                } catch (Exception exc) {
                    exc.printStackTrace();
                }
                return false;
            }
        }
        return super.onTouchEvent(e);
    }
}
