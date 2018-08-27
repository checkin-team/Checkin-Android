package com.checkin.app.checkin.Session;

import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.checkin.app.checkin.R;


/**
 * Created by Bhavik Patel on 10/08/2018.
 */

public class ItemCustomizationFieldHolder {

    public static final int TYPE_VEG = R.drawable.vegetarian;
    public static final int TYPE_NONVEG = R.drawable.vegetarian; //todo change

    private ViewGroup viewGroup;
    private ImageView type;
    private TextView name;
    private TextView price;
    private CheckBox checkbox;
    private SelectableExtraHolder selectableExtraHolder;

    private ItemCustomizationField extra;
    private ItemCustFieldHolderCompat itemCustFieldHolderCompat;

    /*public ItemCustomizationFieldHolder(View view, SelectableExtraHolder selectableExtraHolder) {
        this(view,null,selectableExtraHolder);
    }*/
    public ItemCustomizationFieldHolder(ItemCustFieldHolderCompat compat,View view, ItemCustomizationField extra, final SelectableExtraHolder selectableExtraHolder) {
        if (view != null){
            this.viewGroup = (ViewGroup) view;
            type = view.findViewById(R.id.type);
            name = view.findViewById(R.id.name);
            price = view.findViewById(R.id.cost);
            checkbox = view.findViewById(R.id.checkbox);
            viewGroup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkbox.performClick();
                }
            });
            checkbox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(checkbox.isChecked())selectableExtraHolder.onSelect(ItemCustomizationFieldHolder.this);else selectableExtraHolder.onUnselect(ItemCustomizationFieldHolder.this);
                }
            });

        }
        itemCustFieldHolderCompat = compat;
        if(selectableExtraHolder!=null)this.selectableExtraHolder = selectableExtraHolder;
        setExtra(extra);


    }

    public void setExtra(ItemCustomizationField extra){
        if(extra == null) return;
        this.extra = extra;
        type.setImageResource(extra.isVegetarian() == true ? TYPE_VEG : TYPE_NONVEG);
        name.setText(extra.getName());
        price.setText(String.valueOf(extra.getCost()));
        checkbox.setChecked(extra.isSelected());
    }
    public void isSelected(boolean isSelected){
        itemCustFieldHolderCompat.onClick(extra,isSelected);
        checkbox.setChecked(isSelected);
    }


    public ImageView getType() {
        return type;
    }

    public void setType(ImageView type) {
        this.type = type;
    }

    public TextView getName() {
        return name;
    }

    public void setName(TextView name) {
        this.name = name;
    }

    public TextView getPrice() {
        return price;
    }

    public void setPrice(TextView price) {
        this.price = price;
    }

    public CheckBox getCheckbox() {
        return checkbox;
    }

    public void setCheckbox(CheckBox checkbox) {
        this.checkbox = checkbox;
    }

    public ItemCustomizationField getExtra() {
        return extra;
    }

    public ViewGroup getViewGroup() {
        return viewGroup;
    }

    public interface SelectableExtraHolder{
        void onSelect(ItemCustomizationFieldHolder itemCustomizationFieldHolder);
        void onUnselect(ItemCustomizationFieldHolder itemCustomizationFieldHolder);
    }


    public interface ItemCustFieldHolderCompat{
        void onClick(ItemCustomizationField itemCustomizationField,boolean isSelected);
    }


}
