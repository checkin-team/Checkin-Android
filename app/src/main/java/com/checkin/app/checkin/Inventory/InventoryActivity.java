package com.checkin.app.checkin.Inventory;

import android.annotation.SuppressLint;
import android.os.Bundle;

import com.checkin.app.checkin.Inventory.Adapter.InventoryItemAdapter;
import com.checkin.app.checkin.Inventory.Fragment.InventoryGroupsFragment;
import com.checkin.app.checkin.Inventory.Model.InventoryItemModel;
import com.checkin.app.checkin.Menu.Fragment.MenuInfoFragment;
import com.checkin.app.checkin.Menu.Fragment.MenuItemSearchFragment;
import com.checkin.app.checkin.Misc.BaseActivity;
import com.checkin.app.checkin.R;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.Collections;

import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProviders;
import butterknife.BindView;
import butterknife.ButterKnife;

public class InventoryActivity extends BaseActivity implements InventoryItemAdapter.OnItemInteractionListener {
    public static final String KEY_INVENTORY_RESTAURANT_PK = "inventory.restaurant_pk";
    private static final String SESSION_ARG = "session_arg";
    @BindView(R.id.view_inventory_search)
    MaterialSearchView vMenuSearch;

    private InventoryGroupsFragment mInventoryFragment;
    private InventoryViewModel mViewModel;
    private MenuItemSearchFragment mSearchFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);
        ButterKnife.bind(this);

        setupUiStuff();
        setupObserver();
        setupMenuFragment();

//        setupSearch();
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

    @Override
    public boolean onMenuItemShowInfo(InventoryItemModel item) {
        getSupportFragmentManager().beginTransaction()
                .add(R.id.container_menu_fragment, MenuInfoFragment.newInstanceInventory(item), "item_info")
                .commit();
        return true;
    }

    @Override
    public void onClickItemAvailability(InventoryItemModel mItem, boolean isChecked) {
        mViewModel.confirmMenuItemAvailability(Collections.singletonList(mItem), isChecked);
    }
}
