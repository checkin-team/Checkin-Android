package com.checkin.app.checkin.Menu;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.checkin.app.checkin.Menu.Model.ItemCustomizationFieldModel;
import com.checkin.app.checkin.Menu.Model.ItemCustomizationGroupModel;
import com.checkin.app.checkin.R;
import com.golovin.fluentstackbar.FluentSnackbar;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Bhavik Patel on 10/08/2018.
 */

public class ItemCustomizationGroupHolder implements ItemCustomizationFieldHolder.CustomizationFieldInteraction {
    @BindView(R.id.tv_menu_customization_group_name) TextView tvGroupName;
    @BindView(R.id.list_menu_customization_fields) LinearLayout vListFields;

    private ItemCustomizationGroupModel mGroup;
    private final ViewGroup mView;
    private List<ItemCustomizationFieldHolder> selectedFields = new ArrayList<>();
    private CustomizationGroupInteraction mInteractionListener;

    public ItemCustomizationGroupHolder(@NonNull ItemCustomizationGroupModel customizationGroup, Context context, CustomizationGroupInteraction interactionListener) {
        mGroup = customizationGroup;
        mView = ((ViewGroup) LayoutInflater.from(context).inflate(R.layout.item_menu_customization_group, null, false));
        ButterKnife.bind(this, mView);
        mInteractionListener = interactionListener;
        tvGroupName.setText(String.format(Locale.ENGLISH, "%s (%d/%d)", mGroup.getName(), mGroup.getMinSelection(), mGroup.getMaxSelection()));
        for (ItemCustomizationFieldModel field: mGroup.getCustomizationFields()) {
            vListFields.addView(new ItemCustomizationFieldHolder(field, context, this).getView());
        }
    }

    @Override
    public void onSelect(ItemCustomizationFieldHolder holder) {
        if (selectedFields.size() >= mGroup.getMaxSelection()) {
            if(mGroup.getMaxSelection() == 1) {
                selectedFields.get(0).setChecked(false);
                selectedFields.remove(0);
                holder.setChecked(true);
                Log.e("onSelect", "replaced");
                selectedFields.add(holder);
                mInteractionListener.onFieldClick(holder.getField(), true);
            }
            else {
                holder.setChecked(false);
                FluentSnackbar.create(mView)
                        .create("Cannot select more than " + mGroup.getMaxSelection())
                        .warningBackgroundColor()
                        .textColorRes(R.color.brownish_grey)
                        .duration(Snackbar.LENGTH_SHORT)
                        .show();
            }
        } else {
            Log.e("onSelect", "added");
            selectedFields.add(holder);
            holder.setChecked(true);
            mInteractionListener.onFieldClick(holder.getField(), true);
        }
        Log.e("onSelect", "SIZE: " + selectedFields.size());
    }

    @Override
    public void onDeselect(ItemCustomizationFieldHolder holder) {
        selectedFields.remove(holder);
        holder.setChecked(false);
        mInteractionListener.onFieldClick(holder.getField(), false);
        Log.e("onSelect", "removed - " +selectedFields.size());

    }

    public View getView() {
        return mView;
    }

    public interface CustomizationGroupInteraction {
        void onFieldClick(ItemCustomizationFieldModel itemCustomizationField, boolean isSelected);
    }
}
