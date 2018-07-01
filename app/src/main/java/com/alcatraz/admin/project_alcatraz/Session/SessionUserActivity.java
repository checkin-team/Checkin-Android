package com.alcatraz.admin.project_alcatraz.Session;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.speech.RecognizerIntent;
import android.support.design.widget.TabLayout;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.alcatraz.admin.project_alcatraz.R;
import com.alcatraz.admin.project_alcatraz.Utility.Constants;
import com.alcatraz.admin.project_alcatraz.Utility.DynamicSwipableViewPager;
import com.alcatraz.admin.project_alcatraz.Utility.Util;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SessionUserActivity extends AppCompatActivity implements MenuUserFragment.OnMenuFragmentInteractionListener {
    private final String TAG = SessionUserActivity.class.getSimpleName();
    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;
    @BindView(R.id.container) DynamicSwipableViewPager mViewPager;
    private MaterialSearchView mSearchView;
    @BindView(R.id.action_filter_toggle) ImageButton mFilterToggle;
    @BindView(R.id.filter_container) View mFilterContainer;
    @BindView(R.id.dark_back) View mDarkBack;
    @BindView(R.id.tabs) TabLayout mTabLayout;
    @BindView(R.id.action_finish) ImageButton mActionFinish;
    @BindView(R.id.action_search) ImageButton mActionSearch;
    private MenuUserFragment mMenuFragment;
    private SessionDetailFragment mDetailsFragment;

    private int mSessionStatus;

    private static final String SESSION_STATUS = "status";
    private static final int SESSION_STARTED = 1;
    private static final int SESSION_RESUMED = 2;
    private static final int MENU_TAB = 0;
    private static final int DETAIL_TAB = 1;

    public static void startSession(Context context, int shop_id, int qr_id) {
        Intent intent = new Intent(context, SessionUserActivity.class);
        //TODO: Start session by network request.
        int session_id = 1;
        intent.putExtra(Constants.SHOP_ID, shop_id);
        intent.putExtra(SESSION_STATUS, SESSION_STARTED);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefs.edit()
                .putBoolean(Constants.SP_CHECKED_IN, true)
                .putInt(Constants.SESSION_ID, session_id)
                .apply();
        context.startActivity(intent);
    }

    public static void resumeSession(Context context) {
        Intent intent = new Intent(context, SessionUserActivity.class);
        intent.putExtra(SESSION_STATUS, SESSION_RESUMED);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session_user);

        Toolbar toolbar = findViewById(R.id.session_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("");
            getSupportActionBar().setElevation(0);
        }
        ButterKnife.bind(this);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mSectionsPagerAdapter);

        mDarkBack.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN)
                    hideFilter();
                return true;
            }
        });
        mFilterToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mDarkBack.getVisibility() == View.VISIBLE)
                    hideFilter();
                else
                    showFilter();
            }
        });

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));
        final TabLayout.ViewPagerOnTabSelectedListener tabSelectedListener = new TabLayout.ViewPagerOnTabSelectedListener(mViewPager) {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                super.onTabSelected(tab);
                switch (tab.getPosition()) {
                    case 0:
                        mActionFinish.setVisibility(View.VISIBLE);
                        mActionSearch.setVisibility(View.VISIBLE);
                        mFilterToggle.setVisibility(View.VISIBLE);
                        break;
                    case 1:
                        mActionFinish.setVisibility(View.INVISIBLE);
                        mActionSearch.setVisibility(View.INVISIBLE);
                        mFilterToggle.setVisibility(View.GONE);
                        break;
                }
            }
        };
        mTabLayout.addOnTabSelectedListener(tabSelectedListener);
        Typeface tf = ResourcesCompat.getFont(this, R.font.arial_rounded_mt_bold);
        Util.setTabsFont(mTabLayout, tf);
        setupSearch();
        mMenuFragment = (MenuUserFragment) mSectionsPagerAdapter.getItem(MENU_TAB);
        mDetailsFragment = (SessionDetailFragment) mSectionsPagerAdapter.getItem(DETAIL_TAB);

        mSessionStatus = getIntent().getIntExtra(SESSION_STATUS, SESSION_STARTED);
        switch (mSessionStatus) {
            case SESSION_STARTED: {
                mViewPager.setCurrentItem(DETAIL_TAB, false);
                break;
            }

            case SESSION_RESUMED: {
                break;
            }
        }
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
        mSearchView = findViewById(R.id.search_view);
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
                mTabLayout.animate()
                        .alpha(1.0f)
                        .setDuration(100)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationStart(Animator animation) {
                                mTabLayout.setVisibility(View.VISIBLE);
                            }
                        });
                mViewPager.setEnabled(true);
            }
        });
        ImageButton btn_search = findViewById(R.id.action_search);
        btn_search.setOnClickListener(new ImageButton.OnClickListener() {
            @Override
            public void onClick(View view) {
                mTabLayout.animate()
                        .alpha(0.0f)
                        .setDuration(100)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                mTabLayout.setVisibility(View.GONE);
                            }
                        });
                mViewPager.setEnabled(false);
                mSearchView.showSearch();
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (mSearchView.isSearchOpen())
            mSearchView.closeSearch();
        else if (mDarkBack.getVisibility() == View.VISIBLE)
            hideFilter();
        else if (mViewPager.getCurrentItem() == MENU_TAB && mMenuFragment.isGroupExpanded()) {
            mMenuFragment.onBackPressed();
        }
        else
            super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }

    @OnClick(R.id.action_finish)
    public void orderItems(View view) {
        if (!mMenuFragment.areItemsPending()) {
            Toast.makeText(this, "Add some items before ordering them.", Toast.LENGTH_SHORT).show();
            return;
        }
        mDetailsFragment.orderAndGetDetails(mMenuFragment.getOrderedItems());
        mMenuFragment.clearPendingItems();
        mViewPager.setCurrentItem(DETAIL_TAB, true);
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
    public void onItemOrderInteraction(int count) {
        Log.e(TAG, "ItemOrderInteraction, count: " + count);
        if (count > 0) {
            mDetailsFragment.setCanCheckout(false);
        } else {
            mDetailsFragment.setCanCheckout(true);
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        Fragment[] fragments;
        SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
            fragments = new Fragment[2];
            fragments[MENU_TAB] = MenuUserFragment.newInstance();
            fragments[DETAIL_TAB] = SessionDetailFragment.newInstance(mSessionStatus == SESSION_STARTED);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            if (position >= getCount())
                return null;
            return fragments[position];
        }

        @Override
        public int getCount() {
            return fragments.length;
        }
    }

}
