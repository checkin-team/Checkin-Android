package com.checkin.app.checkin.Utility;

import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.view.Gravity;
import android.view.View;
import androidx.core.content.ContextCompat;


public class BackgroundShadow {

    public static Drawable generateViewShadow(View view, int backgroundColor,
                                              int cornerRadius,
                                              int shadowColor,
                                              int elevation,
                                              int shadowGravity) {
        float cornerRadiusValue = view.getContext().getResources().getDimension(cornerRadius);
        int elevationValue = (int) view.getContext().getResources().getDimension(elevation);
        int shadowColorValue = ContextCompat.getColor(view.getContext(), shadowColor);
        int backgroundColorValue = ContextCompat.getColor(view.getContext(), backgroundColor);

        float[] outerRadius = {cornerRadiusValue, cornerRadiusValue, cornerRadiusValue,
                cornerRadiusValue, cornerRadiusValue, cornerRadiusValue, cornerRadiusValue,
                cornerRadiusValue};

        Paint backgroundPaint = new Paint();
        backgroundPaint.setStyle(Paint.Style.FILL);
        backgroundPaint.setShadowLayer(cornerRadiusValue, 0, 0, 0);

        Rect shapeDrawablePadding = new Rect();
        shapeDrawablePadding.left = elevationValue;
        shapeDrawablePadding.right = elevationValue;

        int DY;
        switch (shadowGravity) {
            case Gravity.CENTER:
                shapeDrawablePadding.top = elevationValue;
                shapeDrawablePadding.bottom = elevationValue;
                DY = 0;
                break;
            case Gravity.TOP:
                shapeDrawablePadding.top = elevationValue * 2;
                shapeDrawablePadding.bottom = elevationValue;
                DY = -1 * elevationValue / 3;
                break;
            default:
            case Gravity.BOTTOM:
                shapeDrawablePadding.top = elevationValue;
                shapeDrawablePadding.bottom = elevationValue * 2;
                DY = elevationValue / 3;
                break;
        }

        ShapeDrawable shapeDrawable = new ShapeDrawable();
        shapeDrawable.setPadding(shapeDrawablePadding);

        shapeDrawable.getPaint().setColor(backgroundColorValue);
        shapeDrawable.getPaint().setShadowLayer(cornerRadiusValue / 3, 0, DY, shadowColorValue);

        view.setLayerType(View.LAYER_TYPE_SOFTWARE, shapeDrawable.getPaint());

        shapeDrawable.setShape(new RoundRectShape(outerRadius, null, null));

        LayerDrawable drawable = new LayerDrawable(new Drawable[]{shapeDrawable});
        drawable.setLayerInset(0, elevationValue, elevationValue * 2, elevationValue, elevationValue * 2);

        return drawable;

    }
}