package com.checkin.app.checkin.Utility;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.ImageView;


@SuppressLint("AppCompatCustomView")
public class FadingImageView extends ImageView {
    private FadeSide mFadeSide;
    private FadeSide nextFadeSide;

    private Context c;

    public FadingImageView(Context c, AttributeSet attrs, int defStyle) {
        super(c, attrs, defStyle);

        this.c = c;

        init();
    }

    public FadingImageView(Context c, AttributeSet attrs) {
        super(c, attrs);

        this.c = c;

        init();
    }

    public FadingImageView(Context c) {
        super(c);

        this.c = c;

        init();
    }

    private void init() {
        this.setHorizontalFadingEdgeEnabled(true);
//        this.setVerticalFadingEdgeEnabled(true);
        this.setEdgeLength(50);
        this.setFadeDirection(FadeSide.RIGHT_SIDE);
        this.setFadeDirection(FadeSide.LEFT_SIDE);
//        BlurMaskFilter filter = new BlurMaskFilter(80, BlurMaskFilter.Blur.OUTER);
    }

    public void setFadeDirection(FadeSide side) {
        this.mFadeSide = side;
        this.nextFadeSide = side;
    }

    public void setEdgeLength(int length) {
        this.setFadingEdgeLength(getPixels(length));
    }

    @Override
    protected float getLeftFadingEdgeStrength() {
        return mFadeSide.equals(FadeSide.LEFT_SIDE) ? 1.0f : 1.0f;
    }

    @Override
    protected float getRightFadingEdgeStrength() {
        return nextFadeSide.equals(FadeSide.RIGHT_SIDE) ? 1.0f : 1.0f;
    }

    @Override
    public boolean hasOverlappingRendering() {
        return true;
    }

    @Override
    public boolean onSetAlpha(int alpha) {
        return false;
    }

    private int getPixels(int dipValue) {
        Resources r = c.getResources();

        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dipValue, r.getDisplayMetrics());
    }

    public enum FadeSide {
        RIGHT_SIDE, LEFT_SIDE
    }
}