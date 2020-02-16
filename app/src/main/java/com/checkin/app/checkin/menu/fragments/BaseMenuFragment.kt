package com.checkin.app.checkin.menu.fragments

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Spinner
import android.widget.TextView
import androidx.annotation.CallSuper
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import butterknife.BindView
import butterknife.OnClick
import com.checkin.app.checkin.R
import com.checkin.app.checkin.data.resource.Resource
import com.checkin.app.checkin.menu.listeners.MenuItemInteraction
import com.checkin.app.checkin.menu.models.MenuItemModel
import com.checkin.app.checkin.menu.viewmodels.BaseCartViewModel
import com.checkin.app.checkin.menu.viewmodels.BaseMenuViewModel
import com.checkin.app.checkin.misc.BlockingNetworkViewModel
import com.checkin.app.checkin.misc.fragments.BaseFragment
import com.checkin.app.checkin.utility.parentActivityDelegate

abstract class BaseMenuFragment :
        BaseFragment(), MenuItemInteraction, ItemCustomizationBottomSheetFragment.ItemCustomizationInteraction {
    override val rootLayout = R.layout.fragment_user_menu

    @BindView(R.id.container_as_menu)
    internal lateinit var containerMenuGroups: ViewGroup
    @BindView(R.id.tv_as_menu_drinks)
    internal lateinit var tvDrinkCategory: TextView
    @BindView(R.id.tv_as_menu_food)
    internal lateinit var tvFoodCategory: TextView
    @BindView(R.id.tv_as_menu_dessert)
    internal lateinit var tvDessertCategory: TextView
    @BindView(R.id.tv_as_menu_specials)
    internal lateinit var tvSpecialCategory: TextView
    @BindView(R.id.spinner_menu_meal_slots)
    internal lateinit var spinnerMealSlots: Spinner

    private val networkViewModel: BlockingNetworkViewModel by activityViewModels()
    abstract val menuViewModel: BaseMenuViewModel
    abstract val cartViewModel: BaseCartViewModel

    private val screenListener: MenuGroupScreenInteraction by parentActivityDelegate()

    @CallSuper
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        spinnerMealSlots.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            val mealSlots = listOf(MenuItemModel.AVAILABLE_MEAL.BREAKFAST, MenuItemModel.AVAILABLE_MEAL.LUNCH, MenuItemModel.AVAILABLE_MEAL.DINNER)

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (position == 0) menuViewModel.clearFilters()
                else menuViewModel.filterMenuGroups(mealSlots[position - 1])
            }
        }
        setupObservers()
    }

    private fun setupObservers() {
        val restaurantId = arguments!!.getLong(KEY_RESTAURANT_ID)
        menuViewModel.fetchAvailableMenu(restaurantId)
        menuViewModel.originalMenuGroups.observe(this, Observer {
            networkViewModel.updateStatusForOnlyError(it, LOAD_MENU_DATA)
            if (it?.status == Resource.Status.SUCCESS) onMenuFetched()
        })
        networkViewModel.shouldTryAgain {
            if (it == LOAD_MENU_DATA) menuViewModel.fetchAvailableMenu(restaurantId)
        }
    }

    protected open fun onMenuFetched() {

    }

    @OnClick(R.id.tv_as_menu_drinks, R.id.tv_as_menu_food, R.id.tv_as_menu_dessert, R.id.tv_as_menu_specials)
    fun onClickOfCategories(v: View) {
        val oldValue = v.isActivated
        resetCategoriesView()
        v.isActivated = !oldValue
        if (v.isActivated) {
            when (v.id) {
                R.id.tv_as_menu_drinks -> menuViewModel.filterMenuCategories("Drinks")
                R.id.tv_as_menu_food -> menuViewModel.filterMenuCategories("Food")
                R.id.tv_as_menu_dessert -> menuViewModel.filterMenuCategories("Dessert")
                R.id.tv_as_menu_specials -> {
                    tvSpecialCategory.setTextColor(ContextCompat.getColor(requireContext(), R.color.greenish_teal))
                    menuViewModel.filterMenuCategories("Specials")
                }
            }
        } else menuViewModel.resetMenuGroups()
    }

    @OnClick(R.id.btn_user_menu_search)
    fun onSearch() = screenListener.onOpenSearch()

    private fun resetCategoriesView() {
        tvDrinkCategory.isActivated = false
        tvFoodCategory.isActivated = false
        tvDessertCategory.isActivated = false
        tvSpecialCategory.isActivated = false
        tvSpecialCategory.setTextColor(ContextCompat.getColor(requireContext(), R.color.brownish_grey))
    }

    override fun onCustomizationCancel() {
        menuViewModel.cancelItem()
    }

    override fun onCustomizationDone() {
        order()
    }

    private fun order() {
        cartViewModel.orderItem(menuViewModel.currentItem.value!!)
        menuViewModel.resetItem()
    }

    override fun onMenuItemAdded(item: MenuItemModel) = run {
        menuViewModel.newOrderedItem(item)
        onItemInteraction(item)
        true
    }

    override fun onMenuItemChanged(item: MenuItemModel, count: Int): Boolean = cartViewModel.updateOrderedItem(item, count)?.let {
        menuViewModel.setCurrentItem(it)
        onItemInteraction(item)
        true
    } ?: onMenuItemAdded(item)

    override fun onMenuItemShowInfo(item: MenuItemModel) {

    }

    protected open fun onItemInteraction(item: MenuItemModel) {
        if (item.isComplexItem) ItemCustomizationBottomSheetFragment.newInstance().show(childFragmentManager, ItemCustomizationBottomSheetFragment.FRAGMENT_TAG)
        else order()
    }

    override fun updateScreen() {
        super.updateScreen()
        menuViewModel.updateResults()
    }

    companion object {
        const val KEY_RESTAURANT_ID = "menu.restaurant_id"
        const val LOAD_MENU_DATA = "load.data.menu"
    }
}