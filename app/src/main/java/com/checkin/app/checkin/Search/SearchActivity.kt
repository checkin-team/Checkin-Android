package com.checkin.app.checkin.Search

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.annotation.IntDef
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import butterknife.BindView
import butterknife.ButterKnife
import com.checkin.app.checkin.R
import com.checkin.app.checkin.Utility.Utils
import com.checkin.app.checkin.misc.activities.BaseActivity
import com.checkin.app.checkin.restaurant.activities.openPublicRestaurantProfile
import com.google.android.material.tabs.TabLayout
import com.miguelcatalan.materialsearchview.MaterialSearchView
import com.miguelcatalan.materialsearchview.MaterialSearchView.SearchViewListener
import java.util.*

class SearchActivity : BaseActivity(), SearchResultInteraction {
    @BindView(R.id.pager_search_type)
    internal lateinit var vPagerSearchType: ViewPager
    @BindView(R.id.search_view)
    internal lateinit var vSearch: MaterialSearchView
    @BindView(R.id.tabs_search)
    internal lateinit var vTabs: TabLayout
    @BindView(R.id.toolbar_search)
    internal lateinit var toolbar: Toolbar

    private val mResultTypeAdapter: ResultTypePagerAdapter by lazy {
        ResultTypePagerAdapter(supportFragmentManager)
    }
    private val mViewModel: SearchViewModel by viewModels()
    private val mSearchMode by lazy {
        intent.getIntExtra(KEY_SEARCH_MODE, MODE_SEARCH)
    }
    private val mSearchType by lazy {
        intent.getIntExtra(KEY_SEARCH_TYPE, TYPE_ALL)
    }
    private val mSearchFilter by lazy {
        intent.getIntExtra(KEY_SEARCH_FILTER, FILTER_ALL)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        ButterKnife.bind(this)

        setSupportActionBar(toolbar)
        supportActionBar?.run {
            title = ""
            setDisplayHomeAsUpEnabled(true)
        }
        when (mSearchType) {
            TYPE_PEOPLE -> mViewModel.setup(true, false)
            TYPE_RESTAURANT -> mViewModel.setup(false, true)
            else -> mViewModel.setup(true, true)
        }
        setupSearch()
        setupUi()
    }

    private fun setupUi() {
        vSearch.showSearch(false)
        setupSearchTabs()
        vPagerSearchType.adapter = mResultTypeAdapter
        vTabs.setupWithViewPager(vPagerSearchType)
    }

    private fun setupSearchTabs() {
        var fragmentPeopleResult: PeopleResultFragment? = null
        var fragmentRestaurantResult: RestaurantResultFragment? = null
        var fragmentAllResult: AllResultFragment? = null
        when {
            showPeopleResults() -> {
                fragmentPeopleResult = PeopleResultFragment.newInstance(this)
            }
            showRestaurantResults() -> {
                fragmentRestaurantResult = RestaurantResultFragment.newInstance(this)
            }
            showAllResults() -> {
                fragmentPeopleResult = PeopleResultFragment.newInstance(this)
                fragmentRestaurantResult = RestaurantResultFragment.newInstance(this)
                fragmentAllResult = AllResultFragment.newInstance(this)
                showTabs()
            }
        }
        if (fragmentAllResult != null) mResultTypeAdapter.addFragment(fragmentAllResult, "All")
        if (fragmentPeopleResult != null) mResultTypeAdapter.addFragment(fragmentPeopleResult, "People")
        if (fragmentRestaurantResult != null) mResultTypeAdapter.addFragment(fragmentRestaurantResult, "Restaurant")
        mResultTypeAdapter.notifyDataSetChanged()
    }

    private fun setupSearch() {
        vSearch.setStartFromRight(false)
        vSearch.setBackIconGone()
        vSearch.setOnQueryTextListener(object : MaterialSearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                mViewModel.getSearchResults(query)
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                mViewModel.getSearchResults(newText)
                return true
            }

            override fun onQueryClear(): Boolean {
                mViewModel.resetResults()
                return true
            }
        })
        vSearch.setOnSearchViewListener(object : SearchViewListener {
            override fun onSearchViewShown() {}
            override fun onSearchViewClosed() {
                resetUi()
            }
        })
    }

    private fun resetUi() {
        onCancelSearch()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    private fun showTabs() {
        vTabs.visibility = View.VISIBLE
    }

    private fun shouldReturnResult(): Boolean {
        return mSearchMode == MODE_SELECT
    }

    private fun showAllResults(): Boolean {
        return mSearchType == TYPE_ALL
    }

    private fun showPeopleResults(): Boolean {
        return mSearchType == TYPE_PEOPLE
    }

    private fun showRestaurantResults(): Boolean {
        return mSearchType == TYPE_RESTAURANT
    }

    override fun onBackPressed() {
        when {
            vSearch.isSearchOpen -> {
                vSearch.closeSearch()
            }
            shouldReturnResult() -> {
                onCancelSearch()
            }
            else -> {
                super.onBackPressed()
            }
        }
    }

    private fun onCancelSearch() {
        setResult(Activity.RESULT_CANCELED)
        finish()
    }

    override fun onClickResult(searchItem: SearchResultModel) {
        if (shouldReturnResult()) {
            val data = Intent()
                    .putExtra(KEY_RESULT_NAME, searchItem.name)
                    .putExtra(KEY_RESULT_IMAGE, searchItem.imageUrl)
                    .putExtra(KEY_RESULT_PK, searchItem.pk)
                    .putExtra(KEY_RESULT_TYPE, searchItem.type)
            setResult(Activity.RESULT_OK, data)
            finish()
        } else {
            showInfo(searchItem)
        }
    }

    override fun onLongClickResult(searchItem: SearchResultModel): Boolean {
        if (shouldReturnResult()) {
            showInfo(searchItem)
            return true
        }
        return false
    }

    override fun onClickFollowResult(searchItem: SearchResultModel) {
        Utils.toast(this, "Unsupported operation.")
    }

    private fun showInfo(searchItem: SearchResultModel) {
        if (searchItem.isTypeRestaurant) showRestaurantInfo(searchItem.pk)
        else if (searchItem.isTypePeople) showUserInfo(searchItem.pk)
    }

    private fun showUserInfo(pk: Long) {}
    private fun showRestaurantInfo(pk: Long) {
        openPublicRestaurantProfile(pk)
    }

    private class ResultTypePagerAdapter internal constructor(fm: FragmentManager?) : FragmentPagerAdapter(fm!!) {
        private val mFragments: MutableList<Fragment> = ArrayList()
        private val mFragmentTitles: MutableList<String> = ArrayList()
        fun addFragment(fragment: Fragment, title: String) {
            mFragments.add(fragment)
            mFragmentTitles.add(title)
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return mFragmentTitles[position]
        }

        override fun getItem(position: Int): Fragment {
            return mFragments[position]
        }

        override fun getCount(): Int {
            return mFragments.size
        }
    }

    companion object {
        const val KEY_RESULT_PK = "result.pk"
        const val KEY_RESULT_NAME = "result.name"
        const val KEY_RESULT_IMAGE = "result.image"
        const val KEY_RESULT_TYPE = "result.type"
        const val KEY_SEARCH_MODE = "search.mode"
        const val MODE_SELECT = 1
        const val MODE_SEARCH = 2
        const val KEY_SEARCH_TYPE = "search.type"
        const val TYPE_ALL = 0
        const val TYPE_PEOPLE = 1
        const val TYPE_RESTAURANT = 2
        // TODO: Apply search filters!
        const val KEY_SEARCH_FILTER = "search.filter"
        const val FILTER_ALL = 0
        const val FILTER_CONNECTED = 1
        const val FILTER_NOT_CONNECTED = 2

        @IntDef(TYPE_ALL, TYPE_PEOPLE, TYPE_RESTAURANT)
        @Retention(AnnotationRetention.SOURCE)
        annotation class SearchType

        @IntDef(MODE_SEARCH, MODE_SELECT)
        @Retention(AnnotationRetention.SOURCE)
        annotation class SearchMode

        fun withIntent(context: Context, @SearchType searchType: Int = TYPE_ALL, @SearchMode searchMode: Int = MODE_SEARCH) = Intent(context, SearchActivity::class.java).apply {
            putExtra(KEY_SEARCH_TYPE, searchType)
            putExtra(KEY_SEARCH_MODE, searchMode)
        }
    }
}