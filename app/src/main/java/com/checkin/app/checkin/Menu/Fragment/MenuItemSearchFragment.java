package com.checkin.app.checkin.Menu.Fragment;

import android.annotation.SuppressLint;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.checkin.app.checkin.Data.Resource.Status;
import com.checkin.app.checkin.Menu.Adapter.MenuItemAdapter;
import com.checkin.app.checkin.Menu.MenuItemInteraction;
import com.checkin.app.checkin.Menu.MenuViewModel;
import com.checkin.app.checkin.Menu.Model.MenuItemModel;
import com.checkin.app.checkin.Menu.SessionMenuActivity;
import com.checkin.app.checkin.Misc.BaseSearchFragment;
import com.checkin.app.checkin.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class MenuItemSearchFragment extends BaseSearchFragment implements MenuItemAdapter.OnItemInteractionListener {
    private static final String TAG = MenuItemSearchFragment.class.getSimpleName();
    private Unbinder unbinder;

    @BindView(R.id.rv_menu_items) RecyclerView rvMenuItems;

    private MenuViewModel mViewModel;
    private MenuItemAdapter mAdapter;
    private MenuItemInteraction mListener;

    public static MenuItemSearchFragment newInstance(MenuItemInteraction listener, boolean isSessionActive) {
        MenuItemSearchFragment fragment = new MenuItemSearchFragment();
        fragment.mListener = listener;
        fragment.mAdapter = new MenuItemAdapter(null, fragment, isSessionActive);
        return fragment;
    }

    public MenuItemSearchFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_menu_items_search, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    public void resetResults() {
        mAdapter.setMenuItems(null);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        view.setOnTouchListener((v, event) -> {
            ((SessionMenuActivity) requireActivity()).closeSearch();
            return true;
        });

        rvMenuItems.setAdapter(mAdapter);
        rvMenuItems.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        mViewModel = ViewModelProviders.of(requireActivity()).get(MenuViewModel.class);
        mViewModel.getFilteredMenuItems().observe(this, listResource -> {
            if (listResource == null)
                return;
            if (listResource.status == Status.SUCCESS && listResource.data != null) {
                mAdapter.setMenuItems(listResource.data);
                this.hideLoadProgress();
            } else if (listResource.status == Status.LOADING) {
                this.showLoadProgress();
                this.resetResults();
            } else if (listResource.status == Status.ERROR_NOT_FOUND) {
                this.noResultFound();
            } else if (listResource.status == Status.NO_REQUEST) {
                this.resetResults();
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    @Override
    public boolean onItemAdded(MenuItemModel item) {
        return mListener.onMenuItemAdded(item);
    }

    @Override
    public boolean onItemLongPress(MenuItemModel item) {
        mListener.onMenuItemShowInfo(item);
        return true;
    }

    @Override
    public boolean onItemChanged(MenuItemModel item, int count) {
        return mListener.onMenuItemChanged(item, count);
    }

    @Override
    public int orderedItemCount(MenuItemModel item) {
        return mListener.getItemOrderedCount(item);
    }
}
