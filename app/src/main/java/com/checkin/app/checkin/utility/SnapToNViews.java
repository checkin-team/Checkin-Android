package com.checkin.app.checkin.utility;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.LayoutManager;

public class SnapToNViews extends StartSnapHelper {
    private int mViewsToSnap;
    private int mMaxFlingViews;
    private int mOrigPos;
    private int mItemDimension;

    public SnapToNViews(int viewsToSnap, int maxViewsToSnap) {
        mViewsToSnap = viewsToSnap;
        mMaxFlingViews = maxViewsToSnap;
    }

    @Override
    public void attachToRecyclerView(@NonNull final RecyclerView recyclerView)
            throws IllegalStateException {
        recyclerView.setOnTouchListener((view, motionEvent) -> {
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    setOrigPos((LinearLayoutManager) recyclerView.getLayoutManager());
                    break;
                default:
                    break;
            }
            return false;
        });
        super.attachToRecyclerView(recyclerView);
    }

    @Override
    public int findTargetSnapPosition(LayoutManager layoutManager, int velocityX, int velocityY) {
        setItemDimension(layoutManager);
        Log.e("TargetSnapPosition", "origPos: " + mOrigPos);
        if (velocityX > 0 || velocityY > 0)
            return mOrigPos + mViewsToSnap;
        return mOrigPos - mViewsToSnap;
    }

    private void setItemDimension(final LayoutManager layoutManager) {
        View child;
        if (mItemDimension != 0 || (child = layoutManager.getChildAt(0)) == null)
            return;
        if (layoutManager.canScrollVertically()) {
            mItemDimension = child.getHeight();
        } else {
            mItemDimension = child.getWidth();
        }
    }

    public void setOrigPos(LinearLayoutManager layoutManager) {
        mOrigPos = layoutManager.findFirstVisibleItemPosition();
    }
}
