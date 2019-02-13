package com.checkin.app.checkin.Utility;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public abstract class DragTouchListener implements View.OnTouchListener {
    private static final String TAG= DragTouchListener.class.getSimpleName();
    private static final int INVALID_POINTER_ID = -1;
    private int mActivePointerId = INVALID_POINTER_ID;
    private float mLastTouchX;
    private float mLastTouchY;

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        final int action = event.getActionMasked();
        final int location[] = {0, 0};
        v.getLocationOnScreen(location);
        float x, y;
        final int actionIndex = action >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
        x = event.getX(actionIndex) + location[0];
        y = event.getY(actionIndex) + location[1];
        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                final int pointerIndex = event.getActionIndex();
                mLastTouchX = x;
                mLastTouchY = y;

                mActivePointerId = event.getPointerId(pointerIndex);
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                final float dx = x - mLastTouchX;
                final float dy = y - mLastTouchY;

                mLastTouchY = y;
                mLastTouchX = x;

                boolean draggedX = false, draggedY = false;

                draggedX = onDragX(dx);
                draggedY = onDragY(dy);

                break;
            }
            case MotionEvent.ACTION_UP: {
                mActivePointerId = INVALID_POINTER_ID;
                onDragCancel();
                break;
            }
            case MotionEvent.ACTION_CANCEL: {
                mActivePointerId = INVALID_POINTER_ID;
                onDragCancel();
                break;
            }
            case MotionEvent.ACTION_POINTER_UP: {
                Log.e(TAG, "Action Pointer Up");
                final int pointerIndex = event.getActionIndex();
                final int pointerId = event.getPointerId(pointerIndex);

                if (pointerId == mActivePointerId) {
                    final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
                    mLastTouchX = event.getX(newPointerIndex) + location[0];
                    mLastTouchY = event.getY(newPointerIndex) + location[0];
                    mActivePointerId = event.getPointerId(newPointerIndex);
                }
                break;
            }
        }
        return false;
    }
    public abstract  boolean shouldDrag();
    public abstract boolean onDragX(final float dx);

    public abstract boolean onDragY(final float dy);

    public abstract void onDragCancel();
}
