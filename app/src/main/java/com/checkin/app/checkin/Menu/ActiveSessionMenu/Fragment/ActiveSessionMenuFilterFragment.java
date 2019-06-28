package com.checkin.app.checkin.Menu.ActiveSessionMenu.Fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.checkin.app.checkin.Menu.MenuViewModel;
import com.checkin.app.checkin.Menu.Model.MenuItemModel.AVAILABLE_MEAL;
import com.checkin.app.checkin.Menu.ActiveSessionMenu.Adapter.FilterGroupAdapter;
import com.checkin.app.checkin.R;
import com.checkin.app.checkin.Utility.Utils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class ActiveSessionMenuFilterFragment extends Fragment {
    private static final String TAG = ActiveSessionMenuFilterFragment.class.getSimpleName();
    @BindView(R.id.dark_back_as_menu_filter)
    ViewGroup vDarkBack;
    @BindView(R.id.container_as_menu_filter)
    ViewGroup containerFilter;
    @BindView(R.id.rv_as_menu_groups)
    RecyclerView rvFilterCategories;
    @BindView(R.id.container_as_menu_filter_clear)
    ViewGroup tvFilterClear;
    @BindView(R.id.im_as_menu_filter_breakfast)
    ImageView imBreakfast;
    @BindView(R.id.im_as_menu_filter_lunch)
    ImageView imLunch;
    @BindView(R.id.im_as_menu_filter_dinner)
    ImageView imDinner;
    @BindView(R.id.tv_as_menu_filter_breakfast)
    TextView tvBreakfast;
    @BindView(R.id.tv_as_menu_filter_lunch)
    TextView tvLunch;
    @BindView(R.id.tv_as_menu_filter_dinner)
    TextView tvDinner;
    @BindView(R.id.tv_as_menu_low)
    TextView tvLow;
    @BindView(R.id.tv_as_menu_high)
    TextView tvHigh;
    private Unbinder unbinder;
    private MenuViewModel mViewModel;
    private FilterGroupAdapter mAdapter;
    private boolean isFilterShown = false;
    private MenuFilterInteraction mListener;

    public static ActiveSessionMenuFilterFragment newInstance(MenuFilterInteraction listener) {
        ActiveSessionMenuFilterFragment fragment = new ActiveSessionMenuFilterFragment();
        fragment.mListener = listener;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_as_menu_filter, container, false);
        unbinder = ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        vDarkBack.setOnClickListener(v -> {
            hideFilter();

        });

        mAdapter = new FilterGroupAdapter(null, category -> {
            mListener.filterByCategory(category);
            hideFilter();
        });
        rvFilterCategories.setLayoutManager(new LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false));
        rvFilterCategories.setAdapter(mAdapter);



        mViewModel = ViewModelProviders.of(requireActivity()).get(MenuViewModel.class);
        mViewModel.getGroupName().observe(this, listResource -> {
            mAdapter.setCategories(listResource);
        });
        mViewModel.getFilteredString().observe(this, value -> {
            if (value != null) {
                if(value.equalsIgnoreCase("Breakfast")){
                    tvBreakfast.setTextColor(getResources().getColor(R.color.primary_red));
                    imBreakfast.setImageDrawable(getResources().getDrawable(R.drawable.ic_menu_filter_breakfast_rouge));
                }else if(value.equalsIgnoreCase("Lunch")){
                    tvLunch.setTextColor(getResources().getColor(R.color.primary_red));
                    imLunch.setImageDrawable(getResources().getDrawable(R.drawable.ic_menu_filter_lunch_rouge));
                }else if(value.equalsIgnoreCase("Dinner")){
                    tvDinner.setTextColor(getResources().getColor(R.color.primary_red));
                    imDinner.setImageDrawable(getResources().getDrawable(R.drawable.ic_menu_filter_dinner_rouge));
                }else if(value.equalsIgnoreCase("Low-High")){
                    tvLow.setTextColor(getResources().getColor(R.color.brownish_grey));
                    tvHigh.setTextColor(getResources().getColor(R.color.primary_red));
                }else if(value.equalsIgnoreCase("High-Low")){
                    tvHigh.setTextColor(getResources().getColor(R.color.brownish_grey));
                    tvLow.setTextColor(getResources().getColor(R.color.primary_red));
                }

            }
        });

        resetFilterContainer();
        showFilter();
    }

    private void resetFilterContainer() {
        containerFilter.measure(0, 0);
        containerFilter.setPivotY(Utils.dpToPx(374));
        containerFilter.setPivotX(0.5f * containerFilter.getMeasuredWidth());
        containerFilter.setVisibility(View.GONE);
        containerFilter.setRotation(180);
    }

    @OnClick({R.id.container_as_menu_filter_breakfast, R.id.container_as_menu_filter_lunch, R.id.container_as_menu_filter_dinner})
    public void filterByAvailableMeal(View v) {
        mListener.sortItems();
        switch (v.getId()) {
            case R.id.container_as_menu_filter_breakfast:
                mViewModel.filterMenuGroups(AVAILABLE_MEAL.BREAKFAST);
                break;
            case R.id.container_as_menu_filter_lunch:
                mViewModel.filterMenuGroups(AVAILABLE_MEAL.LUNCH);
                break;
            case R.id.container_as_menu_filter_dinner:
                mViewModel.filterMenuGroups(AVAILABLE_MEAL.DINNER);
                break;
        }

        hideFilter();
    }

    @OnClick(R.id.container_as_menu_filter_clear)
    public void resetFilter() {
        mViewModel.clearFilters();
        mListener.resetFilters();
    }

    @OnClick({R.id.tv_as_menu_high, R.id.tv_as_menu_low})
    public void sortMenuItems(View v) {
        switch (v.getId()) {
            case R.id.tv_as_menu_high:
                mListener.sortItems();
                mViewModel.sortMenuItems(false);
                break;
            case R.id.tv_as_menu_low:
                mListener.sortItems();
                mViewModel.sortMenuItems(true);
                break;
        }
        hideFilter();
    }

    public boolean onBackPressed() {
        if (isFilterShown) {
            isFilterShown = false;
            return true;
        }
        return false;
    }

    private void showFilter() {
        isFilterShown = true;
        mListener.onShowFilter();

        showDarkBack();
        containerFilter.animate()
                .rotationBy(-180)
                .alpha(1.0f)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        containerFilter.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        containerFilter.setRotation(0);
                    }
                });
    }

    private void hideFilter() {
        isFilterShown = false;
        mListener.onHideFilter();

        hideDarkBack();
        containerFilter.animate()
                .rotationBy(-180)
                .alpha(0.0f)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        containerFilter.setVisibility(View.GONE);
                        containerFilter.setRotation(180);
                    }
                });

        if (getFragmentManager() != null) {
            getFragmentManager().popBackStack();
        }
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
                .start();
    }

    private void hideDarkBack() {
        vDarkBack.animate()
                .alpha(0.0f)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        vDarkBack.setVisibility(View.GONE);
                    }
                })
                .start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    public interface MenuFilterInteraction {
        void onShowFilter();

        void onHideFilter();

        void filterByCategory(String category);

        void sortItems();

        void resetFilters();

        void filterByAvailableMeals();
    }
}
