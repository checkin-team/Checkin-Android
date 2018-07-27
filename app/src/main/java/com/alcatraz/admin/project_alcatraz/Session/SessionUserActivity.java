package com.alcatraz.admin.project_alcatraz.Session;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.speech.RecognizerIntent;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.alcatraz.admin.project_alcatraz.R;
import com.alcatraz.admin.project_alcatraz.Session.MenuUserFragment.STATUS_VALUES;
import com.alcatraz.admin.project_alcatraz.Utility.Constants;
import com.alcatraz.admin.project_alcatraz.Utility.EndDrawerToggle;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.alcatraz.admin.project_alcatraz.Session.MenuUserFragment.SESSION_STATUS;

public class SessionUserActivity extends AppCompatActivity implements MenuUserFragment.OnMenuFragmentInteractionListener, MenuCartAdapter.OnCartInteractionListener {
    private final String TAG = SessionUserActivity.class.getSimpleName();

    @BindView(R.id.search_view) MaterialSearchView mSearchView;
    @BindView(R.id.action_filter_toggle) ImageButton mFilterToggle;
    @BindView(R.id.filter_container) View mFilterContainer;
    @BindView(R.id.dark_back) View mDarkBack;
    @BindView(R.id.action_search) ImageButton mActionSearch;
    @BindView(R.id.rv_menu_cart) RecyclerView rvCart;
    @BindView(R.id.count_order_items) TextView tvCountItems;
    private MenuUserFragment mMenuFragment;
    private MenuViewModel mMenuViewModel;
    private MenuCartAdapter mCartAdapter;

    private STATUS_VALUES mSessionStatus;
    private static final String SESSION_ARG = "session_arg";



    public static void startSession(Context context, int shop_id, int qr_id) {
        Intent intent = new Intent(context, SessionUserActivity.class);
        //TODO: Start session by network request.
        int session_id = 1;
        Bundle args = new Bundle();
        args.putInt(Constants.SHOP_ID, shop_id);
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
        switch (mSessionStatus) {
            case STARTED: {
                break;
            }

            case RESUMED: {
                break;
            }
        }

        Bundle args = getIntent().getBundleExtra(SESSION_ARG);
        args.putInt(MenuUserFragment.MENU_KEY, 1);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        mMenuFragment = new MenuUserFragment();
        mMenuFragment.setArguments(args);
        transaction.replace(R.id.fragment_menu, mMenuFragment);
        transaction.commit();

        mMenuViewModel = ViewModelProviders.of(this, new MenuViewModel.Factory(this.getApplication())).get(MenuViewModel.class);

        setupUiStuff();
        setupSearch();
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
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN)
                hideFilter();
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

    public List<OrderedItem> getOrderedItems() {
        return mCartAdapter.getOrderedItems();
    }

    public void clearPendingItems() {
    }

    public boolean areItemsPending() {
        return false;
    }

    private void showFilter() {
        mDarkBack.animate()
                .alpha(0.67f)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        mDarkBack.setVisibility(View.VISIBLE);
                    }
                });
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

        mDarkBack.animate()
                .alpha(0.0f)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mDarkBack.setVisibility(View.GONE);
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
        if (mSearchView.isSearchOpen())
            mSearchView.closeSearch();
        else if (mDarkBack.getVisibility() == View.VISIBLE)
            hideFilter();
        else if (mMenuFragment.isGroupExpanded()) {
            mMenuFragment.onBackPressed();
        }
        else
            super.onBackPressed();
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
    public void onItemOrderInteraction(MenuItem item, int count) {
        mMenuViewModel.orderItem(item, count, item.getBaseType());
    }

    @Override
    public void onItemRemoved(OrderedItem item) {
        mMenuViewModel.removeItem(item);
    }

    @Override
    public void onItemEdited(OrderedItem item) {

    }
}
