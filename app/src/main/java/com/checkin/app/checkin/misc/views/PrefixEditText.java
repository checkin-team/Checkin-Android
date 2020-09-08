package com.checkin.app.checkin.misc.views;

import android.content.Context;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatEditText;

/**
 * @author Ican Bachors
 * @version 1.1
 * Issues: https://github.com/bachors/Android-Prefix-Input/issues
 */
public class PrefixEditText extends AppCompatEditText {
    public PrefixEditText(Context context) {
        super(context);
        init(this.getText().toString().trim());
    }

    public PrefixEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(this.getText().toString().trim());
    }

    public PrefixEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(this.getText().toString().trim());
    }

    private void init(String prefix) {
        addTextChangedListener(new PrefixerTextWatcher(prefix, this));
    }
}