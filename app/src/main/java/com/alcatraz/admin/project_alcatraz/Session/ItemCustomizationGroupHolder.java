package com.alcatraz.admin.project_alcatraz.Session;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;


import com.alcatraz.admin.project_alcatraz.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bhavik Patel on 10/08/2018.
 */

public class ItemCustomizationGroupHolder implements ItemCustomizationFieldHolder.SelectableExtraHolder{

    ViewGroup viewGroup;
    TextView name;
    List<ItemCustomizationFieldHolder> itemCustomizationFieldHolderList = new ArrayList<>();
    View divider;

    ItemCustomizationGroup foodExtra;

    public ItemCustomizationGroupHolder(View view){
        this(view,null,null);
    }
    public ItemCustomizationGroupHolder(View view, ItemCustomizationGroup foodExtra){
        this(view,null,foodExtra);
    }
    public ItemCustomizationGroupHolder(View view, List<ItemCustomizationFieldHolder> itemCustomizationFieldHolderList) {
        this(view, itemCustomizationFieldHolderList,null);
    }
    public ItemCustomizationGroupHolder(View view, List<ItemCustomizationFieldHolder> itemCustomizationFieldHolderList, ItemCustomizationGroup foodExtra) {
        if (view != null){
            this.viewGroup = (ViewGroup) view;
            name  = view.findViewById(R.id.name);
            divider = view.findViewById(R.id.divider);
        }
        setItemCustomizationFieldHolderList(itemCustomizationFieldHolderList);
        setFoodExtra(foodExtra);
    }

    public void setItemCustomizationFieldHolderList(List<ItemCustomizationFieldHolder> itemCustomizationFieldHolderList) {
        if(itemCustomizationFieldHolderList != null){
            this.itemCustomizationFieldHolderList = itemCustomizationFieldHolderList;
            for(ItemCustomizationFieldHolder itemCustomizationFieldHolder : itemCustomizationFieldHolderList){
                viewGroup.addView(itemCustomizationFieldHolder.getViewGroup());
            }
        }
    }

    public void setFoodExtra(ItemCustomizationGroup foodExtra){
        if(foodExtra == null) return;
        this.foodExtra = foodExtra;
        name.setText(foodExtra.getName() + "(" + foodExtra.getMinSelection() + "/" + foodExtra.getMaxSelection() + ")");

        /*if(extraHolderList.size() == foodExtra.getExtraList().size()){
            for(int i = 0; i < extraHolderList.size();i++){
                extraHolderList.get(i).setExtra(foodExtra.getExtraList().get(i));
            }
        }*/
    }


    public ItemCustomizationGroupHolder(TextView name) {
        this.name = name;
        this.itemCustomizationFieldHolderList = new ArrayList<>();
    }

    public TextView getName() {
        return name;
    }

    public void setName(TextView name) {
        this.name = name;
    }

    public List<ItemCustomizationFieldHolder> getItemCustomizationFieldHolderList() {
        return itemCustomizationFieldHolderList;
    }



    @Override
    public void onSelect(ItemCustomizationFieldHolder itemCustomizationFieldHolder) {
        List<ItemCustomizationFieldHolder> selectedItemCustomizationFieldHolderList = getTotalSelectedExtra();
        if(selectedItemCustomizationFieldHolderList.size() > foodExtra.getMaxSelection()) {
            if(foodExtra.getMaxSelection() == 1){
                for(ItemCustomizationFieldHolder selectedItemCustomizationFieldHolder : selectedItemCustomizationFieldHolderList){
                    selectedItemCustomizationFieldHolder.isSelected(false);
                }
                itemCustomizationFieldHolder.isSelected(true);
            }
            else {
                itemCustomizationFieldHolder.isSelected(false);
                Toast.makeText(viewGroup.getContext(),"Cannot select more than " + foodExtra.getMaxSelection(), Toast.LENGTH_SHORT).show();
            }
        }else {
            itemCustomizationFieldHolder.isSelected(true);
        }
    }

    @Override
    public void onUnselect(ItemCustomizationFieldHolder itemCustomizationFieldHolder) {
        List<ItemCustomizationFieldHolder> selectedItemCustomizationFieldHolderList = getTotalSelectedExtra();
        if(selectedItemCustomizationFieldHolderList.size() >= foodExtra.getMinSelection()){
            itemCustomizationFieldHolder.isSelected(false);
        }else {
            itemCustomizationFieldHolder.isSelected(true);
            Toast.makeText(viewGroup.getContext(),"Cannot select less than " + foodExtra.getMinSelection(), Toast.LENGTH_SHORT).show();
        }
    }

    public List<ItemCustomizationFieldHolder> getTotalSelectedExtra(){
        List<ItemCustomizationFieldHolder> selectedItemCustomizationFieldHolderList = new ArrayList<>();
        for(ItemCustomizationFieldHolder itemCustomizationFieldHolder : itemCustomizationFieldHolderList){
            if(itemCustomizationFieldHolder.getCheckbox().isChecked()){
                selectedItemCustomizationFieldHolderList.add(itemCustomizationFieldHolder);

            }
        }
        return selectedItemCustomizationFieldHolderList;
    }
}
