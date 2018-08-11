package com.alcatraz.admin.project_alcatraz.Session;

import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;


import com.alcatraz.admin.project_alcatraz.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bhavik Patel on 10/08/2018.
 */

public class MenuItemHolder implements View.OnClickListener{

    ViewGroup viewGroup;
    ViewGroup foodExtraRoot;
    private TextView name;
    private RadioGroup radioGroup;
    private RadioButton radioButton1;
    private RadioButton radioButton2;
    private RadioButton radioButton3;
    private TextView radioText1;
    private TextView radioText2;
    private TextView radioText3;
    private List<ItemCustomizationGroupHolder> itemCustomizationGroupHolderList = new ArrayList<>();

    private MenuItem menuItem;

    public MenuItemHolder(View view){
        this(view,null,null);
    }
    public MenuItemHolder(View view, MenuItem menuItem){
        this(view,null, menuItem);
    }
    public MenuItemHolder(View view, List<ItemCustomizationGroupHolder> itemCustomizationGroupHolderList) {
        this(view, itemCustomizationGroupHolderList,null);
    }
    public MenuItemHolder(View view, List<ItemCustomizationGroupHolder> itemCustomizationGroupHolderList, MenuItem menuItem) {
        if(view != null){
            this.viewGroup = (ViewGroup) view;
            foodExtraRoot = view.findViewById(R.id.foodExtrasLL);
            name = view.findViewById(R.id.name);
            radioGroup = view.findViewById(R.id.rg);
            radioButton1 = view.findViewById(R.id.rb1);
            radioButton2 = view.findViewById(R.id.rb2);
            radioButton3 = view.findViewById(R.id.rb3);
            radioText1 = view.findViewById(R.id.rt1);
            radioText2 = view.findViewById(R.id.rt2);
            radioText3 = view.findViewById(R.id.rt3);
            radioButton1.setOnClickListener(this);
            radioButton2.setOnClickListener(this);
            radioButton3.setOnClickListener(this);
        }
        if(itemCustomizationGroupHolderList != null){
            for(ItemCustomizationGroupHolder itemCustomizationGroupHolder : itemCustomizationGroupHolderList){
                addFoodExtraHolder(itemCustomizationGroupHolder);
            }
        }
        setMenuItem(menuItem);
    }


    @Override
    public void onClick(View v) {
        radioButton1.setSelected(false);
        radioButton2.setSelected(false);
        radioButton3.setSelected(false);
        switch (v.getId()){
            case R.id.rb1:
                selectQuantity(0);
                break;
            case R.id.rb2:
                selectQuantity(1);
                break;
            case R.id.rb3:
                if(menuItem.getTypeCost().size() == 3) selectQuantity(2);
                else if(menuItem.getTypeCost().size() == 2) selectQuantity(1);
                break;
            default: selectQuantity(0);
        }
    }

    public void addFoodExtraHolder(ItemCustomizationGroupHolder itemCustomizationGroupHolder){
        if(itemCustomizationGroupHolderList.size() == 0){
            itemCustomizationGroupHolder.divider.setVisibility(View.GONE);
        }
        this.itemCustomizationGroupHolderList.add(itemCustomizationGroupHolder);
        foodExtraRoot.addView(itemCustomizationGroupHolder.viewGroup);


    }

    public void setMenuItem(MenuItem menuItem){
        if(menuItem == null) return;
        this.menuItem = menuItem;
        name.setText(menuItem.getName());
        setQuantities(menuItem.getTypeName());
        selectQuantity(menuItem.getBaseTypeIndex());

        /*if(menuItem.getFoodExtraHolderList().size() == foodExtraHolderList.size()){
            for(int i = 0;i< foodExtraHolderList.size();i++){
                foodExtraHolderList.get(i).setFoodExtra(menuItem.getFoodExtraHolderList().get(i));
            }
        }*/

    }

    public void selectQuantity(int position){
        menuItem.setBaseType(position);
        if(menuItem.getTypeCost().size() == 3){
            switch (position){
                case 0:
                    radioGroup.check(radioButton1.getId());
                    break;
                case 1:
                    radioGroup.check(radioButton2.getId());
                    break;
                case 2:
                    radioGroup.check(radioButton3.getId());
                    break;
                default:
                    radioGroup.check(radioButton1.getId());
                    break;
            }
        }
        else if(menuItem.getTypeCost().size() == 2) {
            switch (position){
                case 0:
                    radioGroup.check(radioButton1.getId());
                    break;
                case 1:
                    radioGroup.check(radioButton3.getId());
                    break;
                default:
                    radioGroup.check(radioButton1.getId());
                    break;
            }
        }

    }
    public void setQuantities(List<String> typeNameList){
        if(typeNameList == null) return;
        if(typeNameList.size() == 3){
            radioText1.setText(typeNameList.get(0));
            radioText2.setText(typeNameList.get(1));
            radioText3.setText(typeNameList.get(2));
        }
        if(typeNameList.size() < 3){
            radioButton2.setVisibility(View.GONE);
            radioText2.setVisibility(View.GONE);
            if(typeNameList.size() == 2){
                radioText1.setText(typeNameList.get(0));
                radioText3.setText(typeNameList.get(1));
            }
            if(typeNameList.size() < 2){
                radioButton1.setVisibility(View.GONE);
                radioButton3.setVisibility(View.GONE);
                radioText1.setVisibility(View.GONE);
                radioText3.setVisibility(View.GONE);
            }
        }

    }

    public TextView getName() {
        return name;
    }

    public void setName(TextView name) {
        this.name = name;
    }

    public RadioGroup getRadioGroup() {
        return radioGroup;
    }

    public void setRadioGroup(RadioGroup radioGroup) {
        this.radioGroup = radioGroup;
    }

    public RadioButton getRadioButton1() {
        return radioButton1;
    }

    public void setRadioButton1(RadioButton radioButton1) {
        this.radioButton1 = radioButton1;
    }

    public RadioButton getRadioButton2() {
        return radioButton2;
    }

    public void setRadioButton2(RadioButton radioButton2) {
        this.radioButton2 = radioButton2;
    }

    public RadioButton getRadioButton3() {
        return radioButton3;
    }

    public void setRadioButton3(RadioButton radioButton3) {
        this.radioButton3 = radioButton3;
    }

    public TextView getRadioText1() {
        return radioText1;
    }

    public void setRadioText1(TextView radioText1) {
        this.radioText1 = radioText1;
    }

    public TextView getRadioText2() {
        return radioText2;
    }

    public void setRadioText2(TextView radioText2) {
        this.radioText2 = radioText2;
    }

    public TextView getRadioText3() {
        return radioText3;
    }

    public void setRadioText3(TextView radioText3) {
        this.radioText3 = radioText3;
    }

    public List<ItemCustomizationGroupHolder> getItemCustomizationGroupHolderList() {
        return itemCustomizationGroupHolderList;
    }

    public void setItemCustomizationGroupHolderList(List<ItemCustomizationGroupHolder> itemCustomizationGroupHolderList) {
        if(itemCustomizationGroupHolderList != null)
            this.itemCustomizationGroupHolderList = itemCustomizationGroupHolderList;
    }

    public MenuItem getMenuItem() {
        return menuItem;
    }
}
