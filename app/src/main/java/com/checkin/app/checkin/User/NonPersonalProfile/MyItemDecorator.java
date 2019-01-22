package com.checkin.app.checkin.User.NonPersonalProfile;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class MyItemDecorator extends RecyclerView.ItemDecoration {

    private int size;

    public MyItemDecorator(int size){
        this.size = size;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);

        outRect.top = size;
        outRect.bottom = size;
        outRect.left = size;
        outRect.right = size;
    }
}
