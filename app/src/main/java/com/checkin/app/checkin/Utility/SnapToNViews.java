package com.checkin.app.checkin.Utility;

import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

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
            switch(motionEvent.getAction()) {
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
    public int findTargetSnapPosition(RecyclerView.LayoutManager layoutManager, int velocityX, int velocityY) {
        setItemDimension(layoutManager);
        Log.e("TargetSnapPosition", "origPos: " + mOrigPos);
        if (velocityX > 0 || velocityY > 0)
            return mOrigPos + mViewsToSnap;
        return mOrigPos - mViewsToSnap;
    }

    private void setItemDimension(final RecyclerView.LayoutManager layoutManager) {
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
