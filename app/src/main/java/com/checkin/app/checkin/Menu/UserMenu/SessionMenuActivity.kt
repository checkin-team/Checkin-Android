package com.checkin.app.checkin.Menu.UserMenu

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.text.TextUtils
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.Observer
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.checkin.app.checkin.Menu.MenuItemInteraction
import com.checkin.app.checkin.Menu.Model.MenuItemModel
import com.checkin.app.checkin.Menu.UserMenu.Fragment.*
import com.checkin.app.checkin.R
import com.checkin.app.checkin.Utility.OnBoardingUtils
import com.checkin.app.checkin.Utility.Utils
import com.checkin.app.checkin.misc.activities.BaseActivity
import com.checkin.app.checkin.session.activesession.ActiveSessionActivity.KEY_INTERACT_WITH_US
import com.miguelcatalan.materialsearchview.MaterialSearchView

class SessionMenuActivity : BaseActivity(), MenuItemInteraction, ItemCustomizationFragment.ItemCustomizationInteraction, MenuFilterFragment.MenuFilterInteraction {

    @BindView(R.id.view_as_menu_search)
    internal lateinit var vMenuSearch: MaterialSearchView
    @BindView(R.id.tv_as_menu_drinks)
    internal lateinit var drinksCategory: TextView
    @BindView(R.id.tv_as_menu_food)
    internal lateinit var foodCategory: TextView
    @BindView(R.id.tv_as_menu_dessert)
    internal lateinit var dessertCategory: TextView
    @BindView(R.id.tv_as_menu_specials)
    internal lateinit var specialCategory: TextView
    @BindView(R.id.tv_menu_count_ordered_items)
    internal lateinit var tvCountOrderedItems: TextView
    @BindView(R.id.tv_as_menu_cart_item_price)
    internal lateinit var tvCartSubtotal: TextView
    @BindView(R.id.container_as_menu_cart)
    internal lateinit var menuCart: ViewGroup
    @BindView(R.id.btn_as_menu_search)
    internal lateinit var btnMenuSearch: ImageView
    @BindView(R.id.btn_as_menu_filter)
    internal lateinit var btnMenuFilter: ImageView
    @BindView(R.id.tv_as_menu_title)
    internal lateinit var tvMenuTitle: TextView

    private lateinit var mSessionStatus: MenuGroupsFragment.SESSION_STATUS

    private var mMenuFragment: MenuGroupsFragment? = null
    private var mCartFragment: MenuCartFragment? = null
    private var mSearchFragment: MenuItemSearchFragment? = null
    private var mFilterFragment: MenuFilterFragment? = null
    private lateinit var mViewModel: MenuViewModel

    private val isSessionActive: Boolean
        get() = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_session_menu)
        ButterKnife.bind(this)

//        val args = intent.getBundleExtra(SESSION_ARG)
//        mSessionStatus = args.getSerializable(KEY_SESSION_STATUS) as MenuGroupsFragment.SESSION_STATUS
//        mViewModel = ViewModelProviders.of(this).get(MenuViewModel::class.java)
//        mViewModel.fetchRecommendedItems(args.getLong(KEY_RESTAURANT_PK))
//        mViewModel.fetchAvailableMenu(args.getLong(KEY_RESTAURANT_PK))
//        val sessionPk = args.getLong(KEY_SESSION_PK, 0L)
//        if (sessionPk > 0L) mViewModel.manageSession(sessionPk)

        OnBoardingUtils.setOnBoardingIsShown(this, KEY_INTERACT_WITH_US, true)

        mSearchFragment = MenuItemSearchFragment.newInstance(this@SessionMenuActivity, true)

        init(R.id.container_as_menu_fragment, true)
        setupUiStuff()
        setUpObserver()
        setupMenuFragment()
        setupSearch()

//        val itemPk = args.getLong(KEY_SESSION_TRENDING_ITEM, 0L)
//        if (itemPk > 0L) {
//            lifecycleScope.launchWhenStarted {
//                // Busy waiting for data to come.. Since it's on another thread no issues.
//                while (mViewModel.getOriginalMenuGroups().value?.data?.isEmpty() != false) {
//                }
//                mViewModel.getMenuItemById(itemPk)?.let {
//                    onMenuItemAdded(it)
//                    if (!it.isComplexItem)
//                        onCartClick()
//                }
//            }
//        } else {
//            if (isSessionActive) explainMenu()
//        }
    }

    @OnClick(R.id.tv_as_menu_drinks, R.id.tv_as_menu_food, R.id.tv_as_menu_dessert, R.id.tv_as_menu_specials)
    fun onClickOfCategories(v: View) {
        showAllCategories()
        when (v.id) {
            R.id.tv_as_menu_drinks -> {
                drinksCategory.background = resources.getDrawable(R.drawable.rectangle_red_stroke_white_fill)
                mViewModel.filterMenuCategories("Drinks")
            }
            R.id.tv_as_menu_food -> {
                foodCategory.background = resources.getDrawable(R.drawable.rectangle_red_stroke_white_fill)
                mViewModel.filterMenuCategories("Food")
            }
            R.id.tv_as_menu_dessert -> {
                dessertCategory.background = resources.getDrawable(R.drawable.rectangle_red_stroke_white_fill)
                mViewModel.filterMenuCategories("Dessert")
            }
            R.id.tv_as_menu_specials -> {
                specialCategory.background = resources.getDrawable(R.drawable.rectangle_red_stroke_white_fill)
                mViewModel.filterMenuCategories("Specials")
            }
            else -> mViewModel.resetMenuGroups()
        }
    }

    private fun setUpObserver() {
        mViewModel.fetchTrendingItem()

        mViewModel.totalOrderedCount.observe(this, Observer {
            it?.let { count ->
                if (count > 0) {
                    tvCountOrderedItems.text = Utils.formatCount(count.toLong())
                    tvCountOrderedItems.visibility = View.VISIBLE
                    explainCartMenu()
                } else {
                    menuCart.visibility = View.GONE
                    tvCountOrderedItems.visibility = View.GONE
                }
            }
        })

        mViewModel.orderedSubTotal.observe(this, Observer {
            it?.let { subtotal ->
                if (subtotal <= 0.0) {
                    menuCart.visibility = View.GONE
                } else {
                    menuCart.visibility = View.VISIBLE
                    tvCartSubtotal.text = Utils.formatCurrencyAmount(this, subtotal)
                }
            }
        })

        mViewModel.getOriginalMenuGroups().observe(this, Observer { })

        mViewModel.filteredString.observe(this, Observer { tvMenuTitle.text = it ?: "Menu" })
    }

    private fun setupMenuFragment() {
        mMenuFragment = MenuGroupsFragment.newInstance(mSessionStatus, this)
        supportFragmentManager.beginTransaction()
                .replace(R.id.container_as_menu, mMenuFragment!!)
                .commit()

        mViewModel.getSelectedCategory().observe(this, Observer { s ->
            if (s.equals("default", ignoreCase = true)) {
                btnMenuFilter.visibility = View.VISIBLE
                showAllCategories()
            } else {
                btnMenuFilter.visibility = View.GONE
            }
        })
    }

    private fun showAllCategories() {
        drinksCategory.background = null
        foodCategory.background = null
        dessertCategory.background = null
        specialCategory.background = null
    }

    private fun setupFilter() {
        mFilterFragment = MenuFilterFragment.newInstance(this, btnMenuFilter)
        supportFragmentManager.beginTransaction()
                .add(R.id.container_as_menu_fragment, mFilterFragment!!, "filter")
                .commit()
    }

    @OnClick(R.id.btn_as_menu_filter)
    fun showFilter() {
        setupFilter()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupUiStuff() {
//        val toolbar = findViewById<Toolbar>(R.id.toolbar_menu)
//        setSupportActionBar(toolbar)
//        if (supportActionBar != null) {
//            supportActionBar!!.title = ""
//            supportActionBar!!.elevation = 0f
//        }
//        mCartFragment = MenuCartFragment.newInstance(this)
//        menuCart.visibility = View.GONE
    }

    private fun explainMenu() {
        OnBoardingUtils.conditionalOnBoarding(this, SP_MENU_SEARCH, true, OnBoardingUtils.OnBoardingModel("Search for food item here.", btnMenuSearch, true))
    }

    private fun setupCart() {
        supportFragmentManager.beginTransaction()
                .add(R.id.container_as_menu_fragment, mCartFragment!!, "cart")
                .addToBackStack(null)
                .commit()
    }

    @OnClick(R.id.container_as_menu_cart)
    fun onCartClick() {
        setupCart()
    }

    private fun explainCartMenu() {
        OnBoardingUtils.conditionalOnBoarding(this, SP_MENU_CART, true, OnBoardingUtils.OnBoardingModel("Checkout your order here.", menuCart, false))
    }

    private fun setupSearch() {
        vMenuSearch.setVoiceSearch(true)
        vMenuSearch.setStartFromRight(false)
        vMenuSearch.setCursorDrawable(R.drawable.color_cursor_white)
        vMenuSearch.setBackIconGone()

        vMenuSearch.setOnQueryTextListener(object : MaterialSearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                mViewModel.searchMenuItems(query)
                return true
            }

            override fun onQueryTextChange(query: String): Boolean {
                mViewModel.searchMenuItems(query)
                return true
            }

            override fun onQueryClear(): Boolean {
                mViewModel.resetMenuItems()
                return true
            }
        })
        vMenuSearch.setOnSearchViewListener(object : MaterialSearchView.SearchViewListener {
            override fun onSearchViewShown() {
                mViewModel.resetMenuItems()
                openSearchItems()
            }

            override fun onSearchViewClosed() {
                mSearchFragment!!.onBackPressed()
            }
        })
    }

    private fun openSearchItems() {
        if (!mSearchFragment!!.isVisible)
            supportFragmentManager.beginTransaction()
                    .add(R.id.container_as_menu_fragment, mSearchFragment!!, "search")
                    .commit()
    }

    @OnClick(R.id.btn_as_menu_search, R.id.container_as_menu_search)
    fun onSearchClicked(view: View) {
        vMenuSearch.showSearch()
    }

    override fun onBackPressed() {
        if (mViewModel.getSelectedCategory().value != null && !mViewModel.getSelectedCategory().value!!.equals("default", ignoreCase = true)) {
            mViewModel.resetMenuGroups()
            showAllCategories()
        } else if (vMenuSearch.isSearchOpen) {
            closeSearch()
        } else if (mSearchFragment!!.isVisible) {
            mSearchFragment!!.onBackPressed()
            mViewModel.clearFilters()
        } else if (mCartFragment!!.isVisible) {
            mCartFragment!!.onBackPressed()
        } else if (mFilterFragment != null && mFilterFragment!!.onBackPressed()) {
        } else if (!mMenuFragment!!.onBackPressed()) {
            super.onBackPressed()
        }
    }

    @OnClick(R.id.im_as_menu_back)
    fun onBackClick() {
        onBackPressed()
    }

    fun closeSearch() {
        vMenuSearch.closeSearch()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        return false
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == MaterialSearchView.REQUEST_VOICE && resultCode == Activity.RESULT_OK) {
            val matches = data!!.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            if (matches != null && matches.size > 0) {
                val searchWrd = matches[0]
                if (!TextUtils.isEmpty(searchWrd)) {
                    vMenuSearch.setQuery(searchWrd, true)
                }
            }
            return
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun onItemInteraction(item: MenuItemModel, count: Int) {
        if (item.isComplexItem) {
            supportFragmentManager.beginTransaction()
                    .add(R.id.container_as_menu_fragment, ItemCustomizationFragment.newInstance(item, this), "item_customization")
                    .commit()
        } else {
            mViewModel.orderItem()
        }
    }


    override fun onMenuItemAdded(item: MenuItemModel): Boolean {
        if (isSessionActive) {
            mViewModel.newOrderedItem(item)
            this.onItemInteraction(item, 1)
            return true
        }
        return false
    }

    override fun onMenuItemChanged(item: MenuItemModel, count: Int): Boolean {
        if (isSessionActive) {
            if (mViewModel.updateOrderedItem(item, count)) {
                this.onItemInteraction(item, count)
            }
            return true
        }
        return false
    }

    override fun onMenuItemShowInfo(item: MenuItemModel) {
        supportFragmentManager.beginTransaction()
                .add(R.id.container_as_menu_fragment, MenuInfoFragment.newInstance(item), "item_info")
                .commit()
    }

    override fun getItemOrderedCount(item: MenuItemModel): Int {
        return mViewModel.getOrderedCount(item)
    }

    override fun onCustomizationDone() {
        mViewModel.orderItem()
    }

    override fun onCustomizationCancel() {
        mViewModel.cancelItem()
    }

    override fun onShowFilter() {

    }

    override fun onHideFilter() {

    }

    override fun filterByCategory(category: String) {
        mMenuFragment!!.scrollToGroup(category)
    }

    override fun sortItems(low2high: Boolean) {
        mViewModel.searchMenuItems("")
        openSearchItems()
    }

    override fun resetFilters() {
        vMenuSearch.closeSearch()
    }

    override fun filterByAvailableMeals() {}

    companion object {
        val SP_MENU_SEARCH = "sp.menu.search"
        val SP_MENU_CART = "sp.menu.cart"

        fun startWithSession(context: Context, restaurantPk: Long?, sessionPk: Long?, itemModel: Long?) {
            context.startActivity(withSession(context, restaurantPk, sessionPk, itemModel))
        }

        fun withSession(context: Context, restaurantPk: Long?, sessionPk: Long?, itemModel: Long?): Intent = Intent(context, SessionMenuActivity::class.java)
//            val args = Bundle()
//            args.putSerializable(KEY_SESSION_STATUS, MenuGroupsFragment.SESSION_STATUS.ACTIVE)
//            args.putLong(KEY_RESTAURANT_PK, restaurantPk!!)
//            if (sessionPk != null)
//                args.putLong(KEY_SESSION_PK, sessionPk)
//            if (itemModel != null)
//                args.putLong(KEY_SESSION_TRENDING_ITEM, itemModel)
//            intent.putExtra(SESSION_ARG, args)
//            return intent
    }
}
