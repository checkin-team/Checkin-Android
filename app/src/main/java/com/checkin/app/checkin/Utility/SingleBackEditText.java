package com.checkin.app.checkin.Utility;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.EditText;

public class SingleBackEditText extends EditText {

    public SingleBackEditText(Context context) {
        super(context);
    }

    public SingleBackEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SingleBackEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public SingleBackEditText(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Utils.hideSoftKeyboard(getContext());
        }
        return false;
    }
}
