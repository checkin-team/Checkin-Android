package com.checkin.app.checkin.Menu;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.text.InputType;
import android.text.TextUtils;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.checkin.app.checkin.Data.Resource.Status;
import com.checkin.app.checkin.Menu.Adapter.MenuCartAdapter;
import com.checkin.app.checkin.Menu.Fragment.ItemCustomizationFragment;
import com.checkin.app.checkin.Menu.Fragment.MenuCartFragment;
import com.checkin.app.checkin.Menu.Fragment.MenuFilterFragment;
import com.checkin.app.checkin.Menu.Fragment.MenuGroupsFragment;
import com.checkin.app.checkin.Menu.Fragment.MenuGroupsFragment.SESSION_STATUS;
import com.checkin.app.checkin.Menu.Fragment.MenuInfoFragment;
import com.checkin.app.checkin.Menu.Fragment.MenuItemSearchFragment;
import com.checkin.app.checkin.Menu.Model.MenuItemModel;
import com.checkin.app.checkin.Menu.Model.OrderedItemModel;
import com.checkin.app.checkin.Misc.BaseActivity;
import com.checkin.app.checkin.R;
import com.checkin.app.checkin.Utility.EndDrawerToggle;
import com.checkin.app.checkin.Utility.OnBoardingUtils;
import com.checkin.app.checkin.Utility.OnBoardingUtils.OnBoardingModel;
import com.checkin.app.checkin.Utility.Utils;
import com.google.android.material.navigation.NavigationView;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.ArrayList;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.checkin.app.checkin.Menu.Fragment.MenuGroupsFragment.KEY_SESSION_STATUS;
import static com.checkin.app.checkin.session.activesession.ActiveSessionActivity.KEY_INTERACT_WITH_US;

public class SessionMenuActivity extends BaseActivity implements
        MenuItemInteraction, ItemCustomizationFragment.ItemCustomizationInteraction, MenuFilterFragment.MenuFilterInteraction {
    public static final String SP_MENU_SEARCH = "sp.menu.search";
    public static final String SP_MENU_CART = "sp.menu.cart";

    public static final String KEY_RESTAURANT_PK = "menu.shop_pk";
    public static final String KEY_SESSION_PK = "menu.session_pk";
    public static final String KEY_SESSION_TRENDING_ITEM = "menu.session_trending_item_pk";
    public static final String SESSION_ARG = "session_arg";

    @BindView(R.id.view_menu_search)
    MaterialSearchView vMenuSearch;

    @BindView(R.id.btn_menu_search)
    ImageButton btnMenuSearch;

    private MenuGroupsFragment mMenuFragment;
    private MenuCartFragment mCartFragment;
    private MenuItemSearchFragment mSearchFragment;
    private MenuFilterFragment mFilterFragment;
    private MenuViewModel mViewModel;

    private SESSION_STATUS mSessionStatus;

    public static void startWithSession(Context context, Long restaurantPk, @Nullable Long sessionPk, @Nullable Long itemModel) {
        context.startActivity(withSession(context, restaurantPk, sessionPk, itemModel));
    }

    public static Intent withSession(Context context, Long restaurantPk, @Nullable Long sessionPk, @Nullable Long itemModel) {
        Intent intent = new Intent(context, SessionMenuActivity.class);
        Bundle args = new Bundle();
        args.putSerializable(KEY_SESSION_STATUS, SESSION_STATUS.ACTIVE);
        args.putLong(KEY_RESTAURANT_PK, restaurantPk);
        if (sessionPk != null)
            args.putLong(KEY_SESSION_PK, sessionPk);
        if (itemModel != null)
            args.putLong(KEY_SESSION_TRENDING_ITEM, itemModel);
        intent.putExtra(SESSION_ARG, args);
        return intent;
    }

    public static void withoutSession(Context context, long restaurantPk) {
        Intent intent = new Intent(context, SessionMenuActivity.class);
        Bundle args = new Bundle();
        args.putSerializable(KEY_SESSION_STATUS, SESSION_STATUS.INACTIVE);
        args.putLong(KEY_RESTAURANT_PK, restaurantPk);
        intent.putExtra(SESSION_ARG, args);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session_menu);
        ButterKnife.bind(this);

        OnBoardingUtils.setOnBoardingIsShown(this, KEY_INTERACT_WITH_US, true);

        Bundle args = getIntent().getBundleExtra(SESSION_ARG);
        mSessionStatus = (SESSION_STATUS) args.getSerializable(KEY_SESSION_STATUS);
        mViewModel = ViewModelProviders.of(this).get(MenuViewModel.class);
        mViewModel.fetchAvailableMenu(args.getLong(KEY_RESTAURANT_PK));
        long sessionPk = args.getLong(KEY_SESSION_PK, 0L);
        if (sessionPk > 0L)
            mViewModel.manageSession(sessionPk);

        mSearchFragment = MenuItemSearchFragment.newInstance(SessionMenuActivity.this, isSessionActive());

        init(R.id.container_menu_fragment, true);
        setupUiStuff();
        setupMenuFragment();
        setupFilter();
        setupSearch();

        if(isSessionActive())
            mViewModel.fetchTrendingItem();

        long itemPk = args.getLong(KEY_SESSION_TRENDING_ITEM, 0L);
        if (itemPk > 0L) {
            mViewModel.searchMenuItemById(itemPk);
            mViewModel.getMenuItem().observe(this, itemModel -> {
                if (itemModel != null) {
                    if (itemModel.isComplexItem()) {
                        mViewModel.newOrderedItem(itemModel);
                        onItemInteraction(itemModel, 1);
                    } else {
                        onMenuItemAdded(itemModel);
                    }
                }
            });
        } else {
            if (isSessionActive())
                explainMenu();
        }
    }

    private void setupFilter() {
        mFilterFragment = MenuFilterFragment.newInstance(this);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.container_menu_fragment, mFilterFragment)
                .commit();
    }

    private void setupMenuFragment() {
        mMenuFragment = MenuGroupsFragment.newInstance(mSessionStatus, this);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.container_menu, mMenuFragment)
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
        mCartFragment = MenuCartFragment.newInstance(this);
        /*if (isSessionActive()) {
            DrawerLayout drawerLayout = findViewById(R.id.drawer_menu);
            endToggle = new EndDrawerToggle(
                    this, drawerLayout, toolbar, R.string.menu_drawer_open, R.string.menu_drawer_close, R.drawable.ic_cart_white) {

                public void onDrawerClosed(View view) {
                    super.onDrawerClosed(view);
                    explainMenu();
                }
            };
            drawerLayout.addDrawerListener(endToggle);
            endToggle.syncState();
        } else {
            findViewById(R.id.nav_menu_cart).setVisibility(View.GONE);
        }*/
    }

    private void explainMenu() {
        OnBoardingUtils.conditionalOnBoarding(this, SP_MENU_SEARCH, true, new OnBoardingModel("Search for food item here.", btnMenuSearch, true));
    }

    private void setupCart() {
        getSupportFragmentManager().beginTransaction()
                .add(R.id.container_menu_fragment, mCartFragment)
                .addToBackStack(null)
                .commit();
    }

    @OnClick(R.id.show_cart)
    public void onCartClick() {
        if (isSessionActive()) {
            setupCart();
        }
    }

//    private void explainCartMenu(EndDrawerToggle toggle) {
//        View cartButton = toggle.getToggleButton();
//        OnBoardingUtils.conditionalOnBoarding(this, SP_MENU_CART, true, new OnBoardingModel("Checkout your order here.", cartButton, true));
//    }

    private void setupSearch() {
        vMenuSearch.setVoiceSearch(true);
        vMenuSearch.setStartFromRight(false);
        vMenuSearch.setCursorDrawable(R.drawable.color_cursor_white);

        vMenuSearch.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
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
        vMenuSearch.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
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
    }

    @OnClick(R.id.btn_menu_search)
    public void onSearchClicked(View view) {
        vMenuSearch.showSearch();
    }

    @Override
    public void onBackPressed() {
        Fragment customizationFragment = getSupportFragmentManager().findFragmentByTag("item_customization");
        Fragment infoFragment = getSupportFragmentManager().findFragmentByTag("item_info");
        if (infoFragment != null) {
            ((MenuInfoFragment) infoFragment).onBackPressed();
        } else if (customizationFragment != null) {
            ((ItemCustomizationFragment) customizationFragment).onBackPressed();
        } else if (vMenuSearch.isSearchOpen()) {
            closeSearch();
        } else if(mCartFragment.isVisible()){
            mCartFragment.onBackPressed();
            return;
        } else if (!mFilterFragment.onBackPressed() && !mMenuFragment.onBackPressed() ) {
            super.onBackPressed();
        }
        mViewModel.clearFilters();
    }

    public void closeSearch() {
        vMenuSearch.closeSearch();
    }

    private boolean isSessionActive() {
        return mSessionStatus == SESSION_STATUS.ACTIVE;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MaterialSearchView.REQUEST_VOICE && resultCode == RESULT_OK) {
            ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (matches != null && matches.size() > 0) {
                String searchWrd = matches.get(0);
                if (!TextUtils.isEmpty(searchWrd)) {
                    vMenuSearch.setQuery(searchWrd, true);
                }
            }
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void onItemInteraction(MenuItemModel item, int count) {
        if (item.isComplexItem()) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container_menu_fragment, ItemCustomizationFragment.newInstance(item, this), "item_customization")
                    .commit();
        } else {
            mViewModel.orderItem();
        }
    }


    @Override
    public boolean onMenuItemAdded(MenuItemModel item) {
        if (isSessionActive()) {
            mViewModel.newOrderedItem(item);
            this.onItemInteraction(item, 1);
            return true;
        }
        return false;
    }

    @Override
    public boolean onMenuItemChanged(MenuItemModel item, int count) {
        if (isSessionActive()) {
            if (mViewModel.updateOrderedItem(item, count)) {
                this.onItemInteraction(item, count);
            }
            return true;
        }
        return false;
    }

    @Override
    public void onMenuItemShowInfo(MenuItemModel item) {
        getSupportFragmentManager().beginTransaction()
                .add(R.id.container_menu_fragment, MenuInfoFragment.newInstance(item), "item_info")
                .commit();
    }

    @Override
    public int getItemOrderedCount(MenuItemModel item) {
        return mViewModel.getOrderedCount(item);
    }

    @Override
    public void onCustomizationDone() {
        mViewModel.orderItem();
    }

    @Override
    public void onCustomizationCancel() {
        mViewModel.cancelItem();
    }

    @Override
    public void onShowFilter() {

    }

    @Override
    public void onHideFilter() {

    }

    @Override
    public void filterByCategory(String category) {
        mMenuFragment.scrollToCategory(category);
    }

    @Override
    public void sortItems() {
        vMenuSearch.showSearch(true);
    }

    @Override
    public void resetFilters() {
        vMenuSearch.closeSearch();
    }
}
