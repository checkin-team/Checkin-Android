package com.checkin.app.checkin.Menu.ActiveSessionMenu.Fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.opengl.Visibility;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.checkin.app.checkin.Menu.ItemCustomizationGroupHolder;
import com.checkin.app.checkin.Menu.MenuViewModel;
import com.checkin.app.checkin.Menu.Model.ItemCustomizationFieldModel;
import com.checkin.app.checkin.Menu.Model.ItemCustomizationGroupModel;
import com.checkin.app.checkin.Menu.Model.MenuItemModel;
import com.checkin.app.checkin.R;
import com.checkin.app.checkin.Utility.Utils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import okhttp3.internal.Util;

/**
 * Created by Bhavik Patel on 12/08/2018.
 */

public class ActiveSessionItemCustomizationFragment extends Fragment implements ItemCustomizationGroupHolder.CustomizationGroupInteraction {
    private static final String TAG = ActiveSessionItemCustomizationFragment.class.getSimpleName();
    @BindView(R.id.container_menu_customization)
    ConstraintLayout vMenuCustomizations;
    @BindView(R.id.dark_back_menu_customization)
    ViewGroup vDarkBack;
    @BindView(R.id.btn_menu_customization_done)
    Button btnDone;
    @BindView(R.id.tv_menu_customization_bill)
    TextView tvBill;
    @BindView(R.id.tv_menu_customization_item_name)
    TextView tvItemName;
    @BindView(R.id.rg_menu_customization_type)
    RadioGroup groupRadio;
    @BindView(R.id.line_horizontal)
    View vLineHorizontal;
    @BindView(R.id.line_horizontal_2)
    View vLineHorizontalTwo;
    @BindView(R.id.rb_menu_customization_type_1)
    RadioButton btnRadio1;
    @BindView(R.id.rb_menu_customization_type_2)
    RadioButton btnRadio2;
    @BindView(R.id.rb_menu_customization_type_3)
    RadioButton btnRadio3;
    @BindView(R.id.tv_menu_customization_radio_1)
    TextView tvRadioLabel1;
    @BindView(R.id.tv_menu_customization_radio_2)
    TextView tvRadioLabel2;
    @BindView(R.id.tv_menu_customization_radio_3)
    TextView tvRadioLabel3;
    @BindView(R.id.list_menu_customizations)
    LinearLayout listCustomizations;
    @BindView(R.id.sv_menu_customization)
    ViewGroup svContainerCustomization;
    @BindView(R.id.container_as_radio_buttons)
    ConstraintLayout containerRadioButtons;
    private Unbinder unbinder;
    private MenuViewModel mViewModel;
    private MenuItemModel mItem;
    private ItemCustomizationInteraction mInteractionListener;

    public ActiveSessionItemCustomizationFragment() {
        // Required empty public constructor
    }

    public static ActiveSessionItemCustomizationFragment newInstance(MenuItemModel item, ItemCustomizationInteraction listener) {
        ActiveSessionItemCustomizationFragment fragment = new ActiveSessionItemCustomizationFragment();
        fragment.mItem = item;
        fragment.mInteractionListener = listener;
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_as_menu_item_customization, container, false);
        unbinder = ButterKnife.bind(this, rootView);

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mViewModel = ViewModelProviders.of(requireActivity()).get(MenuViewModel.class);
        setupUi();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setupUi() {
        vDarkBack.setVisibility(View.GONE);
        vDarkBack.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                onBackPressed();
                return true;
            }
            return false;
        });

        tvItemName.setText(mItem.getName());
        mViewModel.getItemCost().observe(this, value -> tvBill.setText("Item Total  " + Utils.formatCurrencyAmount(requireContext(), value)));
        tvRadioLabel1.setText(mItem.getTypeNames().get(0));
        switch (mItem.getTypeNames().size()) {
            case 3:
                tvRadioLabel2.setText(mItem.getTypeNames().get(1));
                tvRadioLabel3.setText(mItem.getTypeNames().get(2));
                btnRadio1.performClick();
                break;
            case 2:
                goneViews(btnRadio3, tvRadioLabel3, vLineHorizontalTwo);
                setViewInCenter();
                tvRadioLabel2.setText(mItem.getTypeNames().get(1));
                btnRadio1.performClick();
                break;
            default:
                hideViews(btnRadio1, btnRadio2, btnRadio3, tvRadioLabel1, tvRadioLabel2, tvRadioLabel3, vLineHorizontal, vLineHorizontalTwo);
                break;
        }
        if (mItem.hasCustomizations()) {
            for (ItemCustomizationGroupModel group : mItem.getCustomizations()) {
                listCustomizations.addView(new ItemCustomizationGroupHolder(group, getContext(), this).getView());
            }
        } else {
            svContainerCustomization.setVisibility(View.GONE);
            ConstraintSet constraintSet = new ConstraintSet();
            constraintSet.clone(vMenuCustomizations);
            constraintSet.connect(R.id.container_as_menu_customization_header, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START);
            constraintSet.connect(R.id.container_as_menu_customization_header, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END);
            constraintSet.connect(R.id.container_as_menu_customization_header, ConstraintSet.BOTTOM, R.id.footer_menu_customization, ConstraintSet.TOP);
            constraintSet.applyTo(vMenuCustomizations);
        }

        vMenuCustomizations.startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.slide_up));
        showDarkBack();
    }

    private void setViewInCenter(){
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(containerRadioButtons);
        constraintSet.connect(R.id.rb_menu_customization_type_1, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START);
        constraintSet.connect(R.id.rb_menu_customization_type_1, ConstraintSet.END, R.id.line_horizontal, ConstraintSet.START);
        constraintSet.setHorizontalChainStyle(R.id.rb_menu_customization_type_1, ConstraintSet.CHAIN_SPREAD);

        constraintSet.connect(R.id.rb_menu_customization_type_2, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END);
        constraintSet.connect(R.id.rb_menu_customization_type_2, ConstraintSet.START, R.id.line_horizontal, ConstraintSet.END);

        constraintSet.connect(R.id.tv_menu_customization_radio_1, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START);
        constraintSet.connect(R.id.tv_menu_customization_radio_1, ConstraintSet.TOP, R.id.rb_menu_customization_type_1, ConstraintSet.BOTTOM);
        constraintSet.connect(R.id.tv_menu_customization_radio_1, ConstraintSet.END ,R.id.line_horizontal, ConstraintSet.START);
        constraintSet.setHorizontalChainStyle(R.id.tv_menu_customization_radio_1, ConstraintSet.HORIZONTAL_GUIDELINE);

        constraintSet.connect(R.id.tv_menu_customization_radio_2, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END);
        constraintSet.connect(R.id.tv_menu_customization_radio_2, ConstraintSet.TOP, R.id.rb_menu_customization_type_2, ConstraintSet.BOTTOM);
        constraintSet.connect(R.id.tv_menu_customization_radio_2, ConstraintSet.START,R.id.line_horizontal, ConstraintSet.END);

        constraintSet.applyTo(containerRadioButtons);
    }

    private void exitFragment() {
        Animation anim = AnimationUtils.loadAnimation(requireContext(), R.anim.slide_down);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (getFragmentManager() != null) {
                    getFragmentManager().beginTransaction()
                            .remove(ActiveSessionItemCustomizationFragment.this)
                            .commit();
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        vMenuCustomizations.startAnimation(anim);
        hideDarkBack();
    }

    private void hideViews(View... views) {
        for (View v : views) {
            v.setVisibility(View.INVISIBLE);
        }
    }

    private void goneViews(View... views) {
        for (View v : views) {
            v.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    @Override
    public void onFieldClick(ItemCustomizationFieldModel field, boolean isSelected) {
        if (isSelected) {
            mViewModel.addItemCustomization(field);
        } else {
            mViewModel.removeItemCustomization(field);
        }
    }

    @OnClick(R.id.btn_menu_customization_done)
    public void onDoneClick() {
        if (!mViewModel.canOrder() && getView() != null) {
            Utils.warningSnack(getView(), "Not all required customizations are selected.");
            return;
        }
        mInteractionListener.onCustomizationDone();
        exitFragment();
    }

    @OnClick({R.id.rb_menu_customization_type_1, R.id.rb_menu_customization_type_2, R.id.rb_menu_customization_type_3})
    public void onChangeType(View v) {
        btnRadio1.setSelected(false);
        btnRadio2.setSelected(false);
        btnRadio3.setSelected(false);
        switch (v.getId()) {
            case R.id.rb_menu_customization_type_1:
                selectType(0);
                groupRadio.check(R.id.rb_menu_customization_type_1);
                break;
            case R.id.rb_menu_customization_type_2:
                selectType(1);
                groupRadio.check(R.id.rb_menu_customization_type_2);
                break;
            case R.id.rb_menu_customization_type_3:
                if (mItem.getTypeCosts().size() == 3) selectType(2);
                else if (mItem.getTypeCosts().size() == 2) selectType(1);
                groupRadio.check(R.id.rb_menu_customization_type_3);
                break;
            default:
                selectType(0);
        }
    }

    public void selectType(int position) {
        mViewModel.setSelectedType(position);
    }

    public void onBackPressed() {
        mInteractionListener.onCustomizationCancel();
        exitFragment();
    }

    private void showDarkBack() {
        vDarkBack.animate()
                .alpha(1.0f)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        vDarkBack.setVisibility(View.VISIBLE);
                    }
                })
                .setDuration(600L)
                .start();
    }

    private void hideDarkBack() {
        vDarkBack.animate()
                .alpha(0.0f)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        if (vDarkBack != null)
                            vDarkBack.setVisibility(View.GONE);
                    }
                })
                .start();
    }

    public interface ItemCustomizationInteraction {
        void onCustomizationDone();

        void onCustomizationCancel();
    }
}
