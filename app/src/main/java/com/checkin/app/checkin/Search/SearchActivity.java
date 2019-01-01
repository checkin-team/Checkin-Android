package com.checkin.app.checkin.Search;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.checkin.app.checkin.R;
import com.checkin.app.checkin.Shop.ShopPublicProfile.ShopActivity;
import com.checkin.app.checkin.User.NonPersonalProfile.UserProfileActivity;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SearchActivity extends AppCompatActivity implements SearchResultInteraction {
    private static final String TAG = SearchActivity.class.getSimpleName();

    public static final String KEY_RESULT_PK = "result.pk";
    public static final String KEY_RESULT_NAME = "result.name";
    public static final String KEY_RESULT_IMAGE = "result.image";
    public static final String KEY_RESULT_TYPE = "result.type";

    public static final String KEY_SEARCH_MODE = "search.mode";
    public static final int MODE_SELECT = 1;
    public static final int MODE_SEARCH = 2;

    public static final String KEY_SEARCH_TYPE = "search.type";
    public static final int TYPE_ALL = 0;
    public static final int TYPE_PEOPLE = 1;
    public static final int TYPE_RESTAURANT = 2;

    // TODO: Apply search filters!
    public static final String KEY_SEARCH_FILTER = "search.filter";
    public static final int FILTER_ALL = 0;
    public static final int FILTER_CONNECTED = 1;
    public static final int FILTER_NOT_CONNECTED = 2;

    @BindView(R.id.pager_search_type) ViewPager vPagerSearchType;
    @BindView(R.id.search_view) MaterialSearchView vSearch;
    @BindView(R.id.tabs_search) TabLayout vTabs;

    private ResultTypePagerAdapter mResultTypeAdapter;
    private SearchViewModel mViewModel;
    private SuggestionsFragment mSuggestionsFragment;

    private int mSearchMode = MODE_SEARCH;
    private int mSearchType = TYPE_ALL;
    private int mSearchFilter = FILTER_ALL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        ButterKnife.bind(this);

        mSearchMode = getIntent().getIntExtra(KEY_SEARCH_MODE, MODE_SEARCH);
        mSearchType = getIntent().getIntExtra(KEY_SEARCH_TYPE, TYPE_ALL);
        mSearchFilter = getIntent().getIntExtra(KEY_SEARCH_FILTER, FILTER_ALL);

        mViewModel = ViewModelProviders.of(this).get(SearchViewModel.class);

        setupSearch();
        setupUi();
    }

    private void setupUi() {
        if (shouldShowSuggestions()) {
            mSuggestionsFragment = SuggestionsFragment.newInstance();
            showSuggestions();
        }
        else {
            vSearch.showSearch(false);
        }
        setupSearchTabs();
        vPagerSearchType.setAdapter(mResultTypeAdapter);
        vTabs.setupWithViewPager(vPagerSearchType);
    }

    private void setupSearchTabs() {
        mResultTypeAdapter = new ResultTypePagerAdapter(getSupportFragmentManager());

        PeopleResultFragment fragmentPeopleResult = null;
        RestaurantResultFragment fragmentRestaurantResult = null;
        AllResultFragment fragmentAllResult = null;

        if (showPeopleResults()) {
            fragmentPeopleResult = PeopleResultFragment.newInstance(this);
        } else if (showRestaurantResults()) {
            fragmentRestaurantResult = RestaurantResultFragment.newInstance(this);
        } else if (showAllResults()) {
            fragmentPeopleResult = PeopleResultFragment.newInstance(this);
            fragmentRestaurantResult = RestaurantResultFragment.newInstance(this);
            fragmentAllResult = AllResultFragment.newInstance(this);
            showTabs();
        }

        if (fragmentAllResult != null)
            mResultTypeAdapter.addFragment(fragmentAllResult, "All");
        if (fragmentPeopleResult != null)
            mResultTypeAdapter.addFragment(fragmentPeopleResult, "People");
        if (fragmentRestaurantResult != null)
            mResultTypeAdapter.addFragment(fragmentRestaurantResult, "Restaurant");
    }

    private void setupSearch() {
        vSearch.setStartFromRight(false);
        vSearch.setBackIcon(getDrawable(R.drawable.ic_appbar_back));
        vSearch.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mViewModel.getSearchResults(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mViewModel.getSearchResults(newText);
                return true;
            }

            @Override
            public boolean onQueryClear() {
                return false;
            }
        });

        vSearch.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {
            }

            @Override
            public void onSearchViewClosed() {
                resetUi();
            }
        });
    }

    private void showSuggestions() {
        getSupportFragmentManager().beginTransaction()
                .add(R.id.container_search_suggestions, mSuggestionsFragment)
                .commit();
    }

    private void removeSuggestions() {
        getSupportFragmentManager().beginTransaction()
                .remove(mSuggestionsFragment)
                .commit();
    }

    private void resetUi() {
        if (shouldShowSuggestions()) {
            showSuggestions();
        } else {
            onCancelSearch();
        }
    }

    @OnClick(R.id.tv_search)
    public void onClickSearch(View v) {
        if (shouldShowSuggestions()) {
            removeSuggestions();
        }
        vSearch.showSearch(true);
    }

    @OnClick(R.id.btn_back)
    public void onClickBack(View v) {
        onBackPressed();
    }

    private void showTabs() {
        vTabs.setVisibility(View.VISIBLE);
    }

    private boolean shouldShowSuggestions() {
        return mSearchMode == MODE_SEARCH;
    }

    private boolean shouldReturnResult() {
        return mSearchMode == MODE_SELECT;
    }

    private boolean showAllResults() {
        return mSearchType == TYPE_ALL;
    }

    private boolean showPeopleResults() {
        return mSearchType == TYPE_PEOPLE;
    }

    private boolean showRestaurantResults() {
        return mSearchType == TYPE_RESTAURANT;
    }

    @Override
    public void onBackPressed() {
        if (vSearch.isSearchOpen()) {
            vSearch.closeSearch();
        } else if (shouldReturnResult()) {
            onCancelSearch();
        } else {
            super.onBackPressed();
        }
    }

    private void onCancelSearch() {
        setResult(RESULT_CANCELED);
        finish();
    }

    @Override
    public void onClickResult(SearchResultModel searchItem) {
        if (shouldReturnResult()) {
            Intent data = new Intent()
                    .putExtra(KEY_RESULT_NAME, searchItem.getName())
                    .putExtra(KEY_RESULT_IMAGE, searchItem.getImageUrl())
                    .putExtra(KEY_RESULT_PK, searchItem.getPk())
                    .putExtra(KEY_RESULT_TYPE, searchItem.getType().type);
            setResult(RESULT_OK, data);
            finish();
        } else {
            showInfo(searchItem);
        }
    }

    @Override
    public boolean onLongClickResult(SearchResultModel searchItem) {
        if (shouldReturnResult()) {
            showInfo(searchItem);
            return true;
        }
        return false;
    }

    private void showInfo(SearchResultModel searchItem) {
        if (searchItem.isTypeRestaurant())
            showRestaurantInfo(searchItem.getPk());
        else if (searchItem.isTypePeople())
            showUserInfo(searchItem.getPk());
    }

    private void showUserInfo(String pk) {
        Intent intent = new Intent(this, UserProfileActivity.class);
        intent.putExtra(UserProfileActivity.KEY_PROFILE_USER_ID, Long.valueOf(pk));
        startActivity(intent);
    }

    private void showRestaurantInfo(String pk) {
        Intent intent = new Intent(this, ShopActivity.class);
        intent.putExtra(ShopActivity.KEY_SHOP_PK, pk);
        startActivity(intent);
    }

    private static class ResultTypePagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragments = new ArrayList<>();
        private final List<String> mFragmentTitles = new ArrayList<>();

        ResultTypePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        void addFragment(Fragment fragment, String title) {
            mFragments.add(fragment);
            mFragmentTitles.add(title);
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitles.get(position);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }
    }
}
