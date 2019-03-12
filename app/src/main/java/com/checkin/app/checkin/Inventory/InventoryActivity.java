package com.checkin.app.checkin.Inventory;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.text.TextUtils;
import android.view.View;

import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.Inventory.Fragment.InventoryGroupsFragment;
import com.checkin.app.checkin.Inventory.Model.InventoryItemModel;
import com.checkin.app.checkin.Menu.Adapter.MenuCartAdapter;
import com.checkin.app.checkin.Menu.Fragment.ItemCustomizationFragment;
import com.checkin.app.checkin.Menu.Fragment.MenuFilterFragment;
import com.checkin.app.checkin.Menu.Fragment.MenuGroupsFragment;
import com.checkin.app.checkin.Menu.Fragment.MenuInfoFragment;
import com.checkin.app.checkin.Menu.Fragment.MenuItemSearchFragment;
import com.checkin.app.checkin.Menu.MenuItemInteraction;
import com.checkin.app.checkin.Menu.MenuViewModel;
import com.checkin.app.checkin.Menu.Model.MenuItemModel;
import com.checkin.app.checkin.Menu.SessionMenuActivity;
import com.checkin.app.checkin.Misc.BaseActivity;
import com.checkin.app.checkin.R;
import com.checkin.app.checkin.Shop.Private.ShopProfileViewModel;
import com.checkin.app.checkin.Utility.Utils;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.ArrayList;
import java.util.Collections;

import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProviders;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.checkin.app.checkin.Menu.Fragment.MenuGroupsFragment.KEY_SESSION_STATUS;

public class InventoryActivity extends BaseActivity implements InventoryMenuItemInteraction, MenuFilterFragment.MenuFilterInteraction,
        ItemCustomizationFragment.ItemCustomizationInteraction {
    public static final String KEY_INVENTORY_RESTAURANT_PK = "inventory.restaurant_pk";
    private static final String SESSION_ARG = "session_arg";
    @BindView(R.id.view_inventory_search)
    MaterialSearchView vMenuSearch;

    private InventoryGroupsFragment mInventoryFragment;
    private MenuFilterFragment mFilterFragment;
    private InventoryViewModel mViewModel;
    private MenuItemSearchFragment mSearchFragment;
    private InventoryGroupsFragment.SESSION_STATUS mSessionStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);
        ButterKnife.bind(this);

        setupUiStuff();
        setupObserver();
        setupMenuFragment();


//        setupFilter();
//        setupSearch();
    }

    private void setupFilter() {
        mFilterFragment = MenuFilterFragment.newInstance(this);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.container_menu_fragment, mFilterFragment)
                .commit();
    }

    private void setupObserver() {
        mViewModel = ViewModelProviders.of(this).get(InventoryViewModel.class);
        long restaurantPk = getIntent().getLongExtra(KEY_INVENTORY_RESTAURANT_PK, 0);
        mViewModel.fetchAvailableMenuDetail(restaurantPk);

        mViewModel.getMenuItemAvailabilityData().observe(this, listResource -> {
            if (listResource == null)
                return;
            if (listResource.status == Resource.Status.SUCCESS) {
                mViewModel.updateResults();
            } else if (listResource.status == Resource.Status.LOADING) {
            }
        });
    }

    private void setupMenuFragment() {
        mInventoryFragment = InventoryGroupsFragment.newInstance(InventoryGroupsFragment.SESSION_STATUS.INACTIVE, this);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.container_inventory, mInventoryFragment)
                .commit();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setupUiStuff() {
        Toolbar toolbar = findViewById(R.id.toolbar_menu);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("");
            getSupportActionBar().setElevation(0);
        }
    }

    /*private void setupSearch() {
        vInventorySearch.setVoiceSearch(true);
        vInventorySearch.setStartFromRight(false);
        vInventorySearch.setCursorDrawable(R.drawable.color_cursor_white);

        vInventorySearch.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mViewModel.searchMenuItems(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                mViewModel.searchMenuItems(query);
                return true;
            }

            @Override
            public boolean onQueryClear() {
                mViewModel.resetMenuItems();
                return true;
            }
        });
        vInventorySearch.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {
                mViewModel.resetMenuItems();
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.container_menu_fragment, mSearchFragment, "search")
                        .commit();
            }

            @Override
            public void onSearchViewClosed() {
                getSupportFragmentManager().beginTransaction()
                        .remove(mSearchFragment)
                        .commit();
            }
        });
    }*/

    /*@OnClick(R.id.btn_inventory_search)
    public void onSearchClicked(View view) {
        vInventorySearch.showSearch();
    }*/

    /*@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MaterialSearchView.REQUEST_VOICE && resultCode == RESULT_OK) {
            ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (matches != null && matches.size() > 0) {
                String searchWrd = matches.get(0);
                if (!TextUtils.isEmpty(searchWrd)) {
                    vInventorySearch.setQuery(searchWrd, true);
                }
            }
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }*/

    /*private void onItemInteraction(MenuItemModel item, int count) {
        if (item.isComplexItem()) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container_menu_fragment, ItemCustomizationFragment.newInstance(item, this), "item_customization")
                    .commit();
        } else {
            mViewModel.orderItem();
        }
    }*/

    @Override
    public boolean onMenuItemAdded(InventoryItemModel item) {
        return false;
    }

    @Override
    public boolean onMenuItemChanged(InventoryItemModel item, int count) {
        return false;
    }

    @Override
    public void onMenuItemShowInfo(InventoryItemModel item) {
        getSupportFragmentManager().beginTransaction()
                .add(R.id.container_menu_fragment, MenuInfoFragment.newInstanceInventory(item), "item_info")
                .commit();
    }

    @Override
    public int getItemOrderedCount(InventoryItemModel item) {
        return 0;
    }

    @Override
    public void onClickItemAvailability(InventoryItemModel mItem, boolean isChecked) {
        mViewModel.confirmMenuItemAvailability(Collections.singletonList(mItem), isChecked);
    }

    @Override
    public void onShowFilter() {

    }

    @Override
    public void onHideFilter() {

    }

    @Override
    public void filterByCategory(String category) {

    }

    @Override
    public void sortItems() {

    }

    @Override
    public void resetFilters() {

    }

    @Override
    public void onCustomizationDone() {

    }

    @Override
    public void onCustomizationCancel() {

    }
}
