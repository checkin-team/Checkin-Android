package com.checkin.app.checkin.Inventory.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.checkin.app.checkin.Inventory.Adapter.InventoryItemAdapter;
import com.checkin.app.checkin.Inventory.Model.InventoryItemModel;
import com.checkin.app.checkin.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class InventoryItemsFragment extends Fragment {
    @BindView(R.id.rv_menu_items)
    RecyclerView rvMenuItems;
    private Unbinder unbinder;
    private InventoryItemAdapter mAdapter;
    @Nullable
    private InventoryItemAdapter.OnItemInteractionListener mListener;

    public InventoryItemsFragment() {
    }

    public static InventoryItemsFragment newInstance(List<InventoryItemModel> menuItems, InventoryItemAdapter.OnItemInteractionListener listener, boolean isSessionActive) {
        InventoryItemsFragment fragment = new InventoryItemsFragment();
        fragment.mListener = listener;
        fragment.mAdapter = new InventoryItemAdapter(menuItems, listener, isSessionActive);
        return fragment;
    }

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
        rvMenuItems.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}
