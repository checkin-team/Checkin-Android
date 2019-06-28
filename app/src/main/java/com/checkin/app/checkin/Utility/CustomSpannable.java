package com.checkin.app.checkin.Utility;

import android.graphics.Color;
        import android.text.TextPaint;
        import android.text.style.ClickableSpan;
        import android.view.View;

public class CustomSpannable extends ClickableSpan {

    private boolean isUnderline = true;

    public CustomSpannable(boolean isUnderline) {
        this.isUnderline = isUnderline;
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        ds.setUnderlineText(isUnderline);
        ds.setColor(Color.parseColor("#af1014"));
    }

    @Override
    public void onClick(View widget) {


    }
}