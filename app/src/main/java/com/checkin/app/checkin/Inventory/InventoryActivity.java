package com.checkin.app.checkin.Inventory;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProviders;

import com.checkin.app.checkin.Inventory.Adapter.InventoryItemAdapter;
import com.checkin.app.checkin.Inventory.Fragment.InventoryGroupsFragment;
import com.checkin.app.checkin.Inventory.Model.InventoryItemModel;
import com.checkin.app.checkin.R;
import com.checkin.app.checkin.menu.fragments.MenuInfoFragment;
import com.checkin.app.checkin.misc.activities.BaseActivity;

import java.util.Collections;

import butterknife.ButterKnife;

public class InventoryActivity extends BaseActivity implements InventoryItemAdapter.OnItemInteractionListener {
    public static final String KEY_INVENTORY_RESTAURANT_PK = "inventory.restaurant_pk";

    private InventoryGroupsFragment mInventoryFragment;
    private InventoryViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);
        ButterKnife.bind(this);

        setupUiStuff();
        setupObserver();
        setupMenuFragment();
    }

    private void setupObserver() {
        mViewModel = ViewModelProviders.of(this).get(InventoryViewModel.class);
        long restaurantPk = getIntent().getLongExtra(KEY_INVENTORY_RESTAURANT_PK, 0);
        mViewModel.fetchAvailableMenuDetail(restaurantPk);
    }

    private void setupMenuFragment() {
        mInventoryFragment = InventoryGroupsFragment.newInstance(this);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.container_inventory, mInventoryFragment)
                .commit();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setupUiStuff() {
        Toolbar toolbar = findViewById(R.id.toolbar_inventory);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("");
            getSupportActionBar().setElevation(0);
        }
    }

    @Override
    public boolean onMenuItemShowInfo(InventoryItemModel item) {
        getSupportFragmentManager().beginTransaction()
                .add(R.id.container_inventory_fragment, MenuInfoFragment.newInstance(item), "item_info")
                .commit();
        return true;
    }

    @Override
    public void onClickItemAvailability(InventoryItemModel mItem, boolean isChecked) {
        mViewModel.confirmMenuItemAvailability(Collections.singletonList(mItem), isChecked);
    }
}
