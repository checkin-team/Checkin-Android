package com.alcatraz.admin.project_alcatraz.Utility;

import android.view.MotionEvent;
import android.view.View;

public abstract class DragTouchListener implements View.OnTouchListener {
    private static final int INVALID_POINTER_ID = -1;
    private int mActivePointerId = INVALID_POINTER_ID;
    private float mLastTouchX;
    private float mLastTouchY;

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        final int action = event.getActionMasked();
        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                final int pointerIndex = event.getActionIndex();
                mLastTouchX = event.getX(pointerIndex);
                mLastTouchY = event.getY(pointerIndex);

                mActivePointerId = event.getPointerId(pointerIndex);
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                final int pointerIndex = event.findPointerIndex(mActivePointerId);
                final float x = event.getX(pointerIndex);
                final float y = event.getY(pointerIndex);

                final float dx = x - mLastTouchX;
                final float dy = y - mLastTouchY;

                boolean draggedX = onDragX(dx);
                boolean draggedY = onDragY(dy);

                if (draggedX || draggedY)
                    v.invalidate();

                mLastTouchY = y;
                mLastTouchX = x;
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
                final int pointerIndex = event.getActionIndex();
                final int pointerId = event.getPointerId(pointerIndex);

                if (pointerId == mActivePointerId) {
                    final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
                    mLastTouchX = event.getX(newPointerIndex);
                    mLastTouchY = event.getY(newPointerIndex);
                    mActivePointerId = event.getPointerId(newPointerIndex);
                }
                break;
            }
        }
        return true;
    }

    public abstract boolean onDragX(final float dx);

    public abstract boolean onDragY(final float dy);

    public abstract void onDragCancel();
}
