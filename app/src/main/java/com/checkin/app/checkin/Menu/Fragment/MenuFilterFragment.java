package com.checkin.app.checkin.Menu.Fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.checkin.app.checkin.Menu.Adapter.FilterCategoryAdapter;
import com.checkin.app.checkin.Menu.MenuViewModel;
import com.checkin.app.checkin.Menu.Model.MenuItemModel.AVAILABLE_MEAL;
import com.checkin.app.checkin.R;
import com.checkin.app.checkin.Utility.Utils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class MenuFilterFragment extends Fragment {
    private static final String TAG = MenuFilterFragment.class.getSimpleName();
    @BindView(R.id.dark_back_menu_filter)
    ViewGroup vDarkBack;
    @BindView(R.id.container_filter)
    ViewGroup containerFilter;
    @BindView(R.id.rv_menu_categories)
    RecyclerView rvFilterCategories;
    @BindView(R.id.btn_filter_toggle)
    ImageButton btnFilterToggle;
    @BindView(R.id.tv_menu_filter_title)
    TextView tvFilterTitle;
    @BindView(R.id.tv_menu_filter_clear)
    TextView tvFilterClear;
    private Unbinder unbinder;
    private MenuViewModel mViewModel;
    private FilterCategoryAdapter mAdapter;
    private boolean isFilterShown = false;
    private MenuFilterInteraction mListener;

    public static MenuFilterFragment newInstance(MenuFilterInteraction listener) {
        MenuFilterFragment fragment = new MenuFilterFragment();
        fragment.mListener = listener;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_menu_filter, container, false);
        unbinder = ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        vDarkBack.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                btnFilterToggle.performClick();
                return true;
            }
            return false;
        });

        mAdapter = new FilterCategoryAdapter(null, category -> {
            mListener.filterByCategory(category);
            hideFilter();
        });
        GridLayoutManager gridLayoutManager = new GridLayoutManager(
                requireContext(), 2, LinearLayoutManager.HORIZONTAL, false);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (position % 3 == 2)
                    return 2;
                else return 1;
            }
        });
        rvFilterCategories.setAdapter(mAdapter);
        rvFilterCategories.setLayoutManager(gridLayoutManager);

        mViewModel = ViewModelProviders.of(requireActivity()).get(MenuViewModel.class);
        mViewModel.getCategories().observe(this, mAdapter::setCategories);
        mViewModel.getFilteredString().observe(this, value -> {
            if (value == null) {
                tvFilterClear.setVisibility(View.GONE);
                tvFilterTitle.setText("Filters");
                btnFilterToggle.setActivated(false);
            } else {
                tvFilterTitle.setText(value);
                tvFilterClear.setVisibility(View.VISIBLE);
                btnFilterToggle.setActivated(true);
            }
        });

        resetFilterContainer();
        hideDarkBack();
    }

    private void resetFilterContainer() {
        containerFilter.measure(0, 0);
        containerFilter.setPivotY(Utils.dpToPx(374));
        containerFilter.setPivotX(0.5f * containerFilter.getMeasuredWidth());
        containerFilter.setVisibility(View.GONE);
        containerFilter.setRotation(180);
    }

    @OnClick({R.id.btn_filter_breakfast, R.id.btn_filter_lunch, R.id.btn_filter_dinner})
    public void filterByAvailableMeal(View v) {
        switch (v.getId()) {
            case R.id.btn_filter_breakfast:
                mViewModel.filterMenuGroups(AVAILABLE_MEAL.BREAKFAST);
                break;
            case R.id.btn_filter_lunch:
                mViewModel.filterMenuGroups(AVAILABLE_MEAL.LUNCH);
                break;
            case R.id.btn_filter_dinner:
                mViewModel.filterMenuGroups(AVAILABLE_MEAL.DINNER);
                break;
        }
        hideFilter();
    }

    @OnClick(R.id.tv_menu_filter_clear)
    public void resetFilter() {
        mViewModel.clearFilters();
        mListener.resetFilters();
    }

    @OnClick({R.id.btn_sort_high2low, R.id.btn_sort_mainstream, R.id.btn_sort_low2high})
    public void sortMenuItems(View v) {
        switch (v.getId()) {
            case R.id.btn_sort_mainstream:
                Utils.toast(requireContext(), "Unsupported operation.");
                break;
            case R.id.btn_sort_high2low:
                mListener.sortItems();
                mViewModel.sortMenuItems(false);
                break;
            case R.id.btn_sort_low2high:
                mListener.sortItems();
                mViewModel.sortMenuItems(true);
                break;
        }
        hideFilter();
    }

    @OnClick(R.id.btn_filter_toggle)
    public void onToggleFilter() {
        if (isFilterShown)
            hideFilter();
        else
            showFilter();
    }

    public boolean onBackPressed() {
        if (isFilterShown) {
            hideFilter();
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
    }
}
