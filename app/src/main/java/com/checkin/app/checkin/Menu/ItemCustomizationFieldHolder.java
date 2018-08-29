package com.checkin.app.checkin.Menu;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.checkin.app.checkin.R;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by Bhavik Patel on 10/08/2018.
 */

public class ItemCustomizationFieldHolder {

    private static final int TYPE_VEG = R.drawable.ic_veg;
    private static final int TYPE_NONVEG = R.drawable.ic_veg; //todo change

    @BindView(R.id.im_field_type) ImageView imFieldType;
    @BindView(R.id.tv_field_name) TextView tvFieldName;
    @BindView(R.id.tv_field_cost) TextView tvFieldCost;
    @BindView(R.id.checkbox) CheckBox checkBox;

    private final ViewGroup mView;
    private final ItemCustomizationFieldModel mField;

    ItemCustomizationFieldHolder(@NonNull ItemCustomizationFieldModel customizationField, Context context, @NonNull CustomizationFieldInteraction interactionListener) {
        mField = customizationField;
        mView = ((ViewGroup) LayoutInflater.from(context).inflate(R.layout.item_customization_field, null, false));
        ButterKnife.bind(this, mView);
        tvFieldCost.setText(String.valueOf(mField.getCost()));
        tvFieldName.setText(mField.getName());
        imFieldType.setImageResource(mField.isVegetarian() ? TYPE_VEG : TYPE_NONVEG);
        mView.setOnClickListener(v -> checkBox.performClick());
        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) interactionListener.onSelect(this);
            else interactionListener.onDeselect(this);
        });
    }

    public View getView() {
        return mView;
    }

    public void setChecked(boolean selected) {
        checkBox.setChecked(selected);
    }

    public ItemCustomizationFieldModel getField() {
        return mField;
    }

    public interface CustomizationFieldInteraction {
        void onSelect(ItemCustomizationFieldHolder holder);
        void onDeselect(ItemCustomizationFieldHolder holder);
    }
}
