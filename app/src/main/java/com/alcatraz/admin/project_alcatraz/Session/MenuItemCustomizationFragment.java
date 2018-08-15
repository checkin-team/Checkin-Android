package com.alcatraz.admin.project_alcatraz.Session;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alcatraz.admin.project_alcatraz.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by Bhavik Patel on 12/08/2018.
 */

public class MenuItemCustomizationFragment extends Fragment implements MenuItemHolder.MenuItemHolderCompat,ItemCustomizationFieldHolder.ItemCustFieldHolderCompat{
    public static final String TAG = MenuItemCustomizationFragment.class.getSimpleName();

    @BindView(R.id.customizationRoot) ViewGroup customizationRoot;
    @BindView(R.id.done)TextView done;
    @BindView(R.id.bill)TextView bill;

    private Unbinder unbinder;
    private MenuItem item;
    private ItemCustFragCompat itemCustFragCompat;
    private List<ItemCustomizationField> selectedItemCustFieldList;
    private int selectedType;
    private double selectedCost;

    public MenuItemCustomizationFragment() {
        // Required empty public constructor
    }

    public static MenuItemCustomizationFragment newInstance(MenuItem item) {
        MenuItemCustomizationFragment fragment = new MenuItemCustomizationFragment();
        fragment.item = item;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(getContext() instanceof ItemCustFragCompat){
            itemCustFragCompat = (ItemCustFragCompat) getContext();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        itemCustFragCompat = null;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.layout_item_customization, container, false);

        unbinder = ButterKnife.bind(this, rootView);
        if(item != null){
            MenuItemHolder menuItemHolder = new MenuItemHolder(this,customizationRoot,getFoodExtraHolderList(item.getCustomizationGroups()),item);
            selectedType = item.getBaseTypeIndex();
            item.getTypeCost().get(selectedType);
        }
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemCustFragCompat.onItemCustDone(selectedType,selectedItemCustFieldList);
            }
        });


        setBill();
        return rootView;
    }

    public List<ItemCustomizationGroupHolder> getFoodExtraHolderList(List<ItemCustomizationGroup> foodExtraList){
        List<ItemCustomizationGroupHolder> foodExtraHolderList = new ArrayList<>();
        for(ItemCustomizationGroup foodExtra : foodExtraList){
            View view = LayoutInflater.from(getContext()).inflate(R.layout.item_customization_group,null,false);
            ItemCustomizationGroupHolder foodExtraHolder = new ItemCustomizationGroupHolder(view,null,foodExtra);

            foodExtraHolder.setItemCustomizationFieldHolderList(getExtraHolder(foodExtra.getItemCustomizationFieldList(),foodExtraHolder));
            foodExtraHolderList.add(foodExtraHolder);
        }
        return foodExtraHolderList;
    }
    public List<ItemCustomizationFieldHolder> getExtraHolder(List<ItemCustomizationField> extraList, ItemCustomizationFieldHolder.SelectableExtraHolder selectableExtraHolder){
        List<ItemCustomizationFieldHolder> extraHolderList = new ArrayList<>();
        for(ItemCustomizationField extra : extraList){
            View view = LayoutInflater.from(getContext()).inflate(R.layout.item_customization_field,null,false);
            ItemCustomizationFieldHolder extraHolder = new ItemCustomizationFieldHolder(this,view, extra,selectableExtraHolder);
            extraHolderList.add(extraHolder);
        }
        return extraHolderList;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();

    }


    @Override
    public void onClick(ItemCustomizationField itemCustomizationField, boolean isSelected) {
        if(selectedItemCustFieldList != null) {
            if (isSelected) selectedItemCustFieldList.add(itemCustomizationField);
            else {
                selectedItemCustFieldList.remove(itemCustomizationField);
            }
        }else if(isSelected){
            selectedItemCustFieldList = new ArrayList<>();
            selectedItemCustFieldList.add(itemCustomizationField);
        }
        setBill();
    }


    @Override
    public void onSelectType(int position) {
        selectedType = position;
        selectedCost = item.getTypeCost().get(selectedType);
        setBill();
    }
    private void setBill(){
        double totalBill = 0;
        totalBill += selectedCost;
        if(selectedItemCustFieldList != null)
        for( ItemCustomizationField itemCustomizationField: selectedItemCustFieldList){
            totalBill += itemCustomizationField.getPrice();
        }
        bill.setText(String.valueOf(totalBill));
    }


    public interface ItemCustFragCompat{
        void onItemCustDone(int selectedType, List<ItemCustomizationField> selectedItemCustFields);
    }

}
