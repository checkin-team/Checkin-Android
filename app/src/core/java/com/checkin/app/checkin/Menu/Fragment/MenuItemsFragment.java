package com.checkin.app.checkin.Menu.Fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.checkin.app.checkin.Menu.Adapter.MenuItemAdapter;
import com.checkin.app.checkin.Menu.MenuItemInteraction;
import com.checkin.app.checkin.Menu.Model.MenuItemModel;
import com.checkin.app.checkin.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class MenuItemsFragment extends Fragment implements MenuItemAdapter.OnItemInteractionListener {
    private Unbinder unbinder;

    @BindView(R.id.rv_menu_items) RecyclerView rvMenuItems;

    private MenuItemAdapter mAdapter;
    @Nullable
    private MenuItemInteraction mListener;

    public static MenuItemsFragment newInstance(List<MenuItemModel> menuItems, MenuItemInteraction listener, boolean isSessionActive) {
        MenuItemsFragment fragment = new MenuItemsFragment();
        fragment.mAdapter = new MenuItemAdapter(menuItems, fragment, isSessionActive);
        fragment.mListener = listener;
        return fragment;
    }

    public MenuItemsFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_menu_items, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        rvMenuItems.setAdapter(mAdapter);
        rvMenuItems.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    @Override
    public boolean onItemAdded(MenuItemModel item) {
        if (mListener != null) {
            return mListener.onMenuItemAdded(item);
        }
        return false;
    }

    @Override
    public boolean onItemLongPress(MenuItemModel item) {
        if (mListener != null) {
            mListener.onMenuItemShowInfo(item);
        }
        return true;
    }

    @Override
    public boolean onItemChanged(MenuItemModel item, int count) {
        if (mListener != null) {
            return mListener.onMenuItemChanged(item, count);
        }
        return false;
    }

    @Override
    public int orderedItemCount(MenuItemModel item) {
        if (mListener != null) {
            return mListener.getItemOrderedCount(item);
        }
        return 0;
    }
}
