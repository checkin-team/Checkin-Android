package com.checkin.app.checkin.Menu;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.checkin.app.checkin.R;
import com.golovin.fluentstackbar.FluentSnackbar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by Bhavik Patel on 12/08/2018.
 */

public class ItemCustomizationFragment extends Fragment implements ItemCustomizationGroupHolder.CustomizationGroupInteraction {
    private static final String TAG = ItemCustomizationFragment.class.getSimpleName();

    @BindView(R.id.btn_done) Button btnDone;
    @BindView(R.id.tv_bill) TextView tvBill;
    @BindView(R.id.tv_item_name) TextView tvItemName;
    @BindView(R.id.container_radio) RadioGroup groupRadio;
    @BindView(R.id.line_horizontal) View vLineHorizontal;
    @BindView(R.id.radio_1) RadioButton btnRadio1;
    @BindView(R.id.radio_2) RadioButton btnRadio2;
    @BindView(R.id.radio_3) RadioButton btnRadio3;
    @BindView(R.id.tv_radio_1) TextView tvRadioLabel1;
    @BindView(R.id.tv_radio_2) TextView tvRadioLabel2;
    @BindView(R.id.tv_radio_3) TextView tvRadioLabel3;
    @BindView(R.id.list_customizations) LinearLayout listCustomizations;

    private Unbinder unbinder;
    private MenuViewModel mViewModel;
    private MenuItemModel mItem;
    private ItemCustomizationInteraction mInteractionListener;

    public ItemCustomizationFragment() {
        // Required empty public constructor
    }

    public static ItemCustomizationFragment newInstance(MenuItemModel item, ItemCustomizationInteraction listener) {
        ItemCustomizationFragment fragment = new ItemCustomizationFragment();
        fragment.mItem = item;
        fragment.mInteractionListener = listener;
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_item_customization, container, false);

        unbinder = ButterKnife.bind(this, rootView);
        if (getActivity() == null || mItem == null)
            return null;
        mViewModel = ViewModelProviders.of(getActivity(), new MenuViewModel.Factory(getActivity().getApplication())).get(MenuViewModel.class);
        setup();
        return rootView;
    }

    private void setup() {
        tvItemName.setText(mItem.getName());
        mViewModel.getItemCost().observe(this, value -> tvBill.setText(String.valueOf(value)));
        tvRadioLabel1.setText(mItem.getTypeName().get(0));
        switch (mItem.getTypeName().size()) {
            case 3:
                tvRadioLabel2.setText(mItem.getTypeName().get(1));
                tvRadioLabel3.setText(mItem.getTypeName().get(2));
                btnRadio1.performClick();
                break;
            case 2:
                hideViews(btnRadio2, tvRadioLabel2);
                tvRadioLabel3.setText(mItem.getTypeName().get(1));
                btnRadio1.performClick();
                break;
            default:
                hideViews(btnRadio1, btnRadio2, btnRadio3, tvRadioLabel1, tvRadioLabel2, tvRadioLabel3, vLineHorizontal);
                break;
        }
        for (ItemCustomizationGroupModel group: mItem.getCustomizationGroups()) {
            listCustomizations.addView(new ItemCustomizationGroupHolder(group, getContext(), this).getView());
        }
    }

    private void hideViews(View... views) {
        for (View v: views) {
            v.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    @Override
    public void onFieldClick(ItemCustomizationFieldModel field, boolean isSelected) {
        Log.e(TAG, "onFieldClick: " + isSelected);
        if (isSelected) {
            mViewModel.addItemCustomization(field);
        } else {
            mViewModel.removeItemCustomization(field);
        }
    }

    @OnClick(R.id.btn_done)
    public void onDoneClick() {
        if (!mViewModel.canOrder()) {
            FluentSnackbar.create(getView())
                    .create("Not all required customizations are selected.")
                    .warningBackgroundColor()
                    .textColorRes(R.color.brownish_grey)
                    .duration(Snackbar.LENGTH_SHORT)
                    .show();
            return;
        }
        mInteractionListener.onCustomizationDone();
    }

    @OnClick({R.id.radio_1, R.id.radio_2, R.id.radio_3})
    public void onChangeType(View v) {
        btnRadio1.setSelected(false);
        btnRadio2.setSelected(false);
        btnRadio3.setSelected(false);
        switch (v.getId()) {
            case R.id.radio_1:
                selectType(0);
                groupRadio.check(R.id.radio_1);
                break;
            case R.id.radio_2:
                selectType(1);
                groupRadio.check(R.id.radio_2);
                break;
            case R.id.radio_3:
                if (mItem.getTypeCost().size() == 3) selectType(2);
                else if(mItem.getTypeCost().size() == 2) selectType(1);
                groupRadio.check(R.id.radio_3);
                break;
            default: selectType(0);
        }
    }

    public void selectType(int position) {
        mViewModel.setSelectedType(position);
    }

    public void onBackPressed() {
        mInteractionListener.onCustomizationCancel();
    }

    public interface ItemCustomizationInteraction {
        void onCustomizationDone();
        void onCustomizationCancel();
    }
}
