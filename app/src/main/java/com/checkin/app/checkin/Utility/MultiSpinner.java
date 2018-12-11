package com.checkin.app.checkin.Utility;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatSpinner;
import android.util.AttributeSet;
import android.widget.ArrayAdapter;

import com.checkin.app.checkin.R;

import java.util.ArrayList;
import java.util.List;

public class MultiSpinner extends AppCompatSpinner implements
        DialogInterface.OnMultiChoiceClickListener, DialogInterface.OnCancelListener {

    private CharSequence[] items;
    private CharSequence[] values;
    private boolean[] selected;
    private boolean hasHintText;
    private String defaultText;
    private MultiSpinnerListener listener;

    public MultiSpinner(Context context) {
        super(context);
    }

    public MultiSpinner(Context context, int mode) {
        super(context, mode);
    }

    public MultiSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.MultiSpinner);
        items = a.getTextArray(R.styleable.MultiSpinner_entries);
        values = a.getTextArray(R.styleable.MultiSpinner_values);
        if (values == null)
            values = items;
        defaultText = a.getString(R.styleable.MultiSpinner_hintText);
        hasHintText = a.getBoolean(R.styleable.MultiSpinner_staticText, true);
        a.recycle();
        if (values != null) {
            selected = new boolean[values.length]; // false-filled by default
        }
        if (defaultText != null) {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                    android.R.layout.simple_spinner_item, new String[]{defaultText});
            setAdapter(adapter);
        }
    }

    private List<CharSequence> getSelectedValuesInternal() {
        List<CharSequence> selectedList = new ArrayList<>();
        for (int i = 0; i < values.length; i++)
            if (selected[i])
                selectedList.add(values[i]);
        return selectedList;
    }

    public CharSequence[] getSelectedValues() {
        return getSelectedValuesInternal().toArray(new CharSequence[] {});
    }

    public void selectValues(Object[] selectedValues) {
        if (selectedValues == null)
            return;
        resetSelection();
        for (Object selectedValue : selectedValues) {
            for (int j = 0; j < values.length; j++) {
                if (selectedValue.toString().equals(values[j].toString())) {
                    selected[j] = true;
                    break;
                }
            }
        }
        refreshSpinnerText();
    }

    public void resetSelection() {
        for (int i = 0; i < selected.length; i++)
            selected[i] = false;
    }

    public void setListener(MultiSpinnerListener listener) {
        this.listener = listener;
    }

    @Override
    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
        selected[which] = isChecked;
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        refreshSpinnerText();
        if (listener != null)
            listener.onItemsSelected(selected);
    }

    public void refreshSpinnerText() {
        String spinnerText;
        if (!hasHintText) {
            // refresh text on spinner
            spinnerText = getSpinnerText();
        } else {
            // Keep hint Text
            spinnerText = defaultText;
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item,
                new String[]{spinnerText});
        setAdapter(adapter);
    }

    public String getSpinnerText() {
        StringBuilder spinnerBuffer = new StringBuilder();
        boolean someUnselected = false;
        for (int i = 0; i < items.length; i++) {
            if (selected[i]) {
                spinnerBuffer.append(items[i]);
                spinnerBuffer.append(", ");
            } else {
                someUnselected = true;
            }
        }
        if (someUnselected) {
            String spinnerText = spinnerBuffer.toString();
            if (spinnerText.length() > 2)
                spinnerText = spinnerText.substring(0, spinnerText.length() - 2);
            return spinnerText;
        } else {
            return defaultText;
        }
    }

    @Override
    public boolean performClick() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMultiChoiceItems(items, selected, this);
        builder.setPositiveButton(android.R.string.ok,
                (dialog, which) -> dialog.cancel());
        builder.setOnCancelListener(this);
        builder.show();
        return true;
    }

    public void setItems(CharSequence[] items, @Nullable int[] selectedPositions, String allText,
                         MultiSpinnerListener listener) {
        this.items = items;
        this.defaultText = allText;
        this.listener = listener;

        // all selected by default
        selected = new boolean[items.length];
        for (int i = 0; i < selected.length; i++)
            selected[i] = false;

        for (int i = 0; selectedPositions != null && i < selectedPositions.length; i++) {
            if (selectedPositions[i] >= selected.length)
                continue; // Skip over invalid positions.
            selected[selectedPositions[i]] = true;
        }

        // all text on the spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item, new String[]{allText});
        setAdapter(adapter);
    }

    public interface MultiSpinnerListener {
        void onItemsSelected(boolean[] selected);
    }
}