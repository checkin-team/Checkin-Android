package com.checkin.app.checkin.menu.fragments

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Spinner
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import butterknife.BindView
import butterknife.OnClick
import com.checkin.app.checkin.Menu.MenuItemInteraction
import com.checkin.app.checkin.Menu.Model.MenuItemModel
import com.checkin.app.checkin.R
import com.checkin.app.checkin.Utility.inTransaction
import com.checkin.app.checkin.Utility.parentActivityDelegate
import com.checkin.app.checkin.menu.viewmodels.CartViewModel
import com.checkin.app.checkin.menu.viewmodels.UserMenuViewModel
import com.checkin.app.checkin.misc.fragments.BaseFragment

class UserMenuFragment : BaseFragment(), MenuItemInteraction, ItemCustomizationBottomSheetFragment.ItemCustomizationInteraction {
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

    val viewModel: UserMenuViewModel by viewModels()
    val cartViewModel: CartViewModel by activityViewModels()
    val menuListener: MenuInteraction by parentActivityDelegate()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        childFragmentManager.inTransaction {
            add(R.id.container_as_menu, UserMenuGroupsFragment.newInstance())
        }
        spinnerMealSlots.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            val mealSlots = listOf(MenuItemModel.AVAILABLE_MEAL.BREAKFAST, MenuItemModel.AVAILABLE_MEAL.LUNCH, MenuItemModel.AVAILABLE_MEAL.DINNER)

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (position == 0)  viewModel.resetMenuGroups()
                else viewModel.filterMenuGroups(mealSlots[position - 1])
            }
        }

        setupObservers()
    }

    private fun setupObservers() {
        val restaurantId = arguments!!.getLong(KEY_RESTAURANT_ID)
        viewModel.fetchAvailableMenu(restaurantId)
        viewModel.fetchRecommendedItems(restaurantId)

        viewModel.getOriginalMenuGroups().observe(this, Observer { })
    }

    @OnClick(R.id.tv_as_menu_drinks, R.id.tv_as_menu_food, R.id.tv_as_menu_dessert, R.id.tv_as_menu_specials)
    fun onClickOfCategories(v: View) {
        val oldValue = v.isActivated
        resetCategoriesView()
        v.isActivated = !oldValue
        if (v.isActivated) {
            when (v.id) {
                R.id.tv_as_menu_drinks -> viewModel.filterMenuCategories("Drinks")
                R.id.tv_as_menu_food -> viewModel.filterMenuCategories("Food")
                R.id.tv_as_menu_dessert -> viewModel.filterMenuCategories("Dessert")
                R.id.tv_as_menu_specials -> {
                    tvSpecialCategory.setTextColor(ContextCompat.getColor(requireContext(), R.color.brownish_grey))
                    viewModel.filterMenuCategories("Specials")
                }
            }
        } else viewModel.resetMenuGroups()
    }

    private fun resetCategoriesView() {
        tvDrinkCategory.isActivated = false
        tvFoodCategory.isActivated = false
        tvDessertCategory.isActivated = false
        tvSpecialCategory.isActivated = false
        tvSpecialCategory.setTextColor(ContextCompat.getColor(requireContext(), R.color.greenish_teal))
    }

    override fun onMenuItemAdded(item: MenuItemModel) = run {
        viewModel.newOrderedItem(item)
        onItemInteraction(item)
        true
    }

    override fun onMenuItemChanged(item: MenuItemModel, count: Int): Boolean = cartViewModel.updateOrderedItem(item, count)?.let {
        viewModel.setCurrentItem(it)
        this.onItemInteraction(item)
        true
    } ?: onMenuItemAdded(item)

    override fun onMenuItemShowInfo(item: MenuItemModel) {

    }

    private fun onItemInteraction(item: MenuItemModel) {
        if (item.isComplexItem) ItemCustomizationBottomSheetFragment.newInstance(item).show(childFragmentManager, "menu.item_customization")
        else order()
    }

    override fun getItemOrderedCount(item: MenuItemModel) = 0

    override fun onCustomizationCancel() {
        viewModel.cancelItem()
    }

    override fun onCustomizationDone() {
        order()
    }

    private fun order() {
        cartViewModel.orderItem(viewModel.currentItem.value!!)
        viewModel.resetItem()
    }

    override fun updateScreen() {
        super.updateScreen()
        viewModel.updateResults()
    }

    companion object {
        private const val KEY_RESTAURANT_ID = "user.menu.restaurant_id"

        fun newInstance(restaurantId: Long) = UserMenuFragment().apply {
            arguments = bundleOf(KEY_RESTAURANT_ID to restaurantId)
        }
    }
}

interface MenuInteraction