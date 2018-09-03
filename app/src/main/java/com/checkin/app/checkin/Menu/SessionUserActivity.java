package com.checkin.app.checkin.Menu;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.speech.RecognizerIntent;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.checkin.app.checkin.Menu.MenuUserFragment.STATUS_VALUES;
import com.checkin.app.checkin.R;
import com.checkin.app.checkin.Utility.Constants;
import com.checkin.app.checkin.Utility.EndDrawerToggle;
import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog;
import com.github.javiersantos.materialstyleddialogs.enums.Style;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.checkin.app.checkin.Menu.MenuUserFragment.SESSION_STATUS;

public class SessionUserActivity extends AppCompatActivity implements
        MenuUserFragment.MenuInteractionListener,
        MenuCartAdapter.OnCartInteractionListener
{
    private final String TAG = SessionUserActivity.class.getSimpleName();

    @BindView(R.id.search_view) MaterialSearchView mSearchView;
    @BindView(R.id.action_filter_toggle) ImageButton mFilterToggle;
    @BindView(R.id.filter_container) View mFilterContainer;
    @BindView(R.id.dark_back) View mDarkBack;
    @BindView(R.id.action_search) ImageButton mActionSearch;
    @BindView(R.id.rv_menu_cart) RecyclerView rvCart;
    @BindView(R.id.count_order_items) TextView tvCountItems;
    @BindView(R.id.container_customization) ViewGroup containerCustomization;
    private MenuUserFragment mMenuFragment;
    private MenuViewModel mMenuViewModel;
    private MenuCartAdapter mCartAdapter;

    private MaterialStyledDialog mRemarksDialog;
    private boolean canGoBack = false;
    private STATUS_VALUES mSessionStatus;
    private static final String SESSION_ARG = "session_arg";

    public static void startSession(Context context, long shop_id, int qr_id) {
        Intent intent = new Intent(context, SessionUserActivity.class);
        //TODO: Start session by network request.
        int session_id = 1;
        Bundle args = new Bundle();
        args.putLong(Constants.SHOP_ID, shop_id);
        args.putSerializable(SESSION_STATUS, STATUS_VALUES.STARTED);
        intent.putExtra(SESSION_ARG, args);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefs.edit()
                .putBoolean(Constants.SP_CHECKED_IN, true)
                .putInt(Constants.SESSION_ID, session_id)
                .apply();
        context.startActivity(intent);
    }

    public static void resumeSession(Context context) {
        Intent intent = new Intent(context, SessionUserActivity.class);
        intent.putExtra(SESSION_STATUS, STATUS_VALUES.RESUMED);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session_user);

        ButterKnife.bind(this);

        mSessionStatus = (STATUS_VALUES) getIntent().getBundleExtra(SESSION_ARG).getSerializable(SESSION_STATUS);
        if (mSessionStatus == null)
            return;
        switch (mSessionStatus) {
            case STARTED: {
                showPostCheckin();
                break;
            }

            case RESUMED: {
                break;
            }
        }

        Bundle args = getIntent().getBundleExtra(SESSION_ARG);
        mMenuFragment = new MenuUserFragment();
        mMenuFragment.setArguments(args);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.container_menu, mMenuFragment)
                .commit();

        mMenuViewModel = ViewModelProviders.of(this, new MenuViewModel.Factory(this.getApplication())).get(MenuViewModel.class);

        setupUiStuff();
        setupSearch();
        setupRemarksDialog();
    }

    private void setupRemarksDialog() {
        final View view = LayoutInflater.from(this).inflate(R.layout.view_input_text, null);
        EditText edInput = view.findViewById(R.id.ed_input);
        mRemarksDialog = new MaterialStyledDialog.Builder(this)
                .setStyle(Style.HEADER_WITH_TITLE)
                .setTitle("Enter remarks for ordered item")
                .setPositiveText("Confirm")
                .setNegativeText("Cancel")
                .onPositive((dialog, which) -> mMenuViewModel.setRemarks(edInput.getText().toString()))
                .setCustomView(view)
                .build();
    }

    private void showPostCheckin() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.root_view, PostCheckinFragment.newInstance((userIds, selectedPrivacy) -> {
            Log.e(TAG, "IDS: " + userIds + ", PRIVACY: " + selectedPrivacy.name());
        }));
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void setupUiStuff() {
        Toolbar toolbar = findViewById(R.id.session_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("");
            getSupportActionBar().setElevation(0);
        }

        if (mSessionStatus != STATUS_VALUES.INACTIVE) {
            DrawerLayout drawerLayout = findViewById(R.id.drawer_menu);
            EndDrawerToggle endToggle = new EndDrawerToggle(
                    this, drawerLayout, toolbar, R.string.menu_drawer_open, R.string.menu_drawer_close, R.drawable.ic_cart);
            drawerLayout.addDrawerListener(endToggle);
            endToggle.syncState();
        }

        mDarkBack.setOnTouchListener((view, motionEvent) -> {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN && mFilterContainer.getVisibility() == View.VISIBLE)
                hideFilter();
            else
                onBackPressed();
            return true;
        });
        mFilterToggle.setOnClickListener(view -> {
            if (mDarkBack.getVisibility() == View.VISIBLE)
                hideFilter();
            else
                showFilter();
        });

        rvCart.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mCartAdapter = new MenuCartAdapter(this);
        rvCart.setAdapter(mCartAdapter);
        mMenuViewModel.getOrderedItems().observe(this, mCartAdapter::setOrderedItems);

        mMenuViewModel.getTotalOrderedCount().observe(this, count -> {
            if (count == null)
                return;
            if (count > 0)
                tvCountItems.setText(String.valueOf(count));
            else
                tvCountItems.setText("");
        });
    }

    public List<OrderedItemModel> getOrderedItems() {
        return mCartAdapter.getOrderedItems();
    }

    public void clearPendingItems() {
    }

    public boolean areItemsPending() {
        return false;
    }

    private void showDarkBack() {
        mDarkBack.animate()
                .alpha(0.67f)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        mDarkBack.setVisibility(View.VISIBLE);
                    }
                });
    }

    private void hideDarkBack() {
        mDarkBack.animate()
                .alpha(0.0f)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mDarkBack.setVisibility(View.GONE);
                    }
                });
    }

    private void showFilter() {
        showDarkBack();
        mFilterContainer.animate()
                .rotationBy(-180)
                .alpha(1.0f)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        mFilterContainer.setVisibility(View.VISIBLE);
                    }
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mFilterContainer.setRotation(0);
                    }
                });
    }

    private void hideFilter() {
        hideDarkBack();
        mFilterContainer.animate()
                .rotationBy(-180)
                .alpha(0.0f)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mFilterContainer.setVisibility(View.GONE);
                        mFilterContainer.setRotation(180);
                    }
                });
    }

    private void setupSearch() {
        mSearchView.setVoiceSearch(true);
        mSearchView.setStartFromRight(false);
        mSearchView.setCursorDrawable(R.drawable.color_cursor_white);
        mSearchView.setSuggestions(getResources().getStringArray(R.array.menu_items));

        mSearchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Toast.makeText(getApplicationContext(), "Query: " + query, Toast.LENGTH_SHORT).show();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // TODO: Do some magic!
                return false;
            }

            @Override
            public boolean onQueryClear() {
                return false;
            }
        });
        mSearchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {

            }

            @Override
            public void onSearchViewClosed() {
            }
        });
        ImageButton btn_search = findViewById(R.id.action_search);
        btn_search.setOnClickListener(new ImageButton.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSearchView.showSearch();
            }
        });
    }

    @OnClick(R.id.btn_proceed)
    public void onProceedBtnClicked(View view) {
        Toast.makeText(this, "Processing your order...", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        ItemCustomizationFragment itemCustomizationFragment = ((ItemCustomizationFragment) getSupportFragmentManager().findFragmentById(R.id.container_customization));
        if (getSupportFragmentManager().popBackStackImmediate("info", FragmentManager.POP_BACK_STACK_INCLUSIVE)) {
        } else if (itemCustomizationFragment != null) {
            itemCustomizationFragment.onBackPressed();
        }
        else if (mFilterContainer.getVisibility() == View.VISIBLE)
            hideFilter();
        else if (mSearchView.isSearchOpen())
            mSearchView.closeSearch();
        else if (mDarkBack.getVisibility() == View.VISIBLE) {
            hideDarkBack();
            getSupportFragmentManager().popBackStack();
        }
        else if (mMenuFragment.isGroupExpanded()) {
            mMenuFragment.onBackPressed();
        }
        else if (!canGoBack) {
            new AlertDialog.Builder(this)
                    .setCancelable(false)
                    .setTitle("")
                    .setMessage("Do you want to cancel the current session?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        dialog.dismiss();
                        finish();
                    })
                    .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                    .show();
        }
        else {
            super.onBackPressed();
            canGoBack = false;
        }
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
                    mSearchView.setQuery(searchWrd, false);
                }
            }
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onItemInteraction(MenuItemModel item, int count) {
        if (item.isComplexItem()) {
            Log.e(TAG, "SHOW customizationFragment");
            canGoBack = true;
            showDarkBack();
            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.slide_up, 0, 0, R.anim.slide_down)
                    .replace(containerCustomization.getId(), ItemCustomizationFragment.newInstance(item, new ItemCustomizationFragment.ItemCustomizationInteraction() {
                        @Override
                        public void onCustomizationDone() {
                            mMenuViewModel.orderItem();
                            canGoBack = false;
                            hideDarkBack();
                            getSupportFragmentManager().popBackStack();
                        }

                        @Override
                        public void onCustomizationCancel() {
//                            OrderedItemModel item = mMenuViewModel.getCurrentItem().getValue();
//                            if (item == null)   return;
//                            MenuItemAdapter.ItemViewHolder holder = mMenuViewModel.getCurrentItem().getValue().getHolder();
//                            holder.vQuantityPicker.scrollToPosition(item.getQuantity() - 1);
//                            if (holder.vQuantityPicker.getCurrentItem() == 0)
//                                holder.hideQuantitySelection();
                            mMenuViewModel.changeQuantity(-1);
                            mMenuViewModel.resetItem();
                            canGoBack = false;
                            hideDarkBack();
                            getSupportFragmentManager().popBackStack();
                        }
                    }))
                    .addToBackStack(null)
                    .commit();
        } else {
            mMenuViewModel.orderItem();
        }
    }

    @Override
    public void onItemShowInfo(MenuItemModel item) {
        canGoBack = true;
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.root_view, MenuInfoFragment.newInstance(item))
                .addToBackStack("info")
                .commit();
    }

    @Override
    public void onItemRemoved(OrderedItemModel item) {
        mMenuViewModel.removeItem(item);
    }

    @Override
    public void onItemChanged(OrderedItemModel item, int count) {
        item.setQuantity(count);
        mMenuViewModel.setCurrentItem(item);
        mMenuViewModel.orderItem();
    }

    @Override
    public void onItemRemark(OrderedItemModel item) {
        mRemarksDialog.show();
    }
}
