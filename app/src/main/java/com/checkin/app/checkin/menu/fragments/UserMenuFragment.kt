package com.checkin.app.checkin.menu.fragments

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import butterknife.BindView
import butterknife.OnClick
import com.checkin.app.checkin.Menu.MenuItemInteraction
import com.checkin.app.checkin.Menu.Model.MenuItemModel
import com.checkin.app.checkin.R
import com.checkin.app.checkin.Utility.Utils
import com.checkin.app.checkin.Utility.inTransaction
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
    @BindView(R.id.tv_menu_count_ordered_items)
    internal lateinit var tvCountOrderedItems: TextView
    @BindView(R.id.tv_as_menu_cart_item_price)
    internal lateinit var tvCartSubtotal: TextView
    @BindView(R.id.container_as_menu_cart)
    internal lateinit var menuCart: ViewGroup

    val viewModel: UserMenuViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        childFragmentManager.inTransaction {
            add(R.id.container_as_menu, UserMenuGroupsFragment.newInstance())
        }
        menuCart.visibility = View.GONE

        setupObservers()
    }

    private fun setupObservers() {
        val restaurantId = arguments!!.getLong(KEY_RESTAURANT_ID)
        viewModel.fetchAvailableMenu(restaurantId)
        viewModel.fetchRecommendedItems(restaurantId)

        viewModel.getOriginalMenuGroups().observe(this, Observer { })
        viewModel.totalOrderedCount.observe(this, Observer {
            it?.let { count ->
                if (count > 0) {
                    tvCountOrderedItems.text = Utils.formatCount(count.toLong())
                    tvCountOrderedItems.visibility = View.VISIBLE
                } else {
                    menuCart.visibility = View.GONE
                    tvCountOrderedItems.visibility = View.GONE
                }
            }
        })
        viewModel.orderedSubTotal.observe(this, Observer {
            it?.let { subtotal ->
                if (subtotal <= 0.0) {
                    menuCart.visibility = View.GONE
                } else {
                    menuCart.visibility = View.VISIBLE
                    tvCartSubtotal.text = Utils.formatCurrencyAmount(requireContext(), subtotal)
                }
            }
        })
    }

    @OnClick(R.id.tv_as_menu_drinks, R.id.tv_as_menu_food, R.id.tv_as_menu_dessert, R.id.tv_as_menu_specials)
    fun onClickOfCategories(v: View) {
        resetCategoriesView()
        v.isActivated = !v.isActivated
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

    override fun onMenuItemChanged(item: MenuItemModel, count: Int): Boolean = if (viewModel.updateOrderedItem(item, count)) {
        this.onItemInteraction(item)
        true
    } else onMenuItemAdded(item)

    override fun onMenuItemShowInfo(item: MenuItemModel) {

    }

    private fun onItemInteraction(item: MenuItemModel) {
        if (item.isComplexItem) ItemCustomizationBottomSheetFragment.newInstance(item).show(childFragmentManager, "menu.item_customization")
        else viewModel.orderItem()
    }

    override fun getItemOrderedCount(item: MenuItemModel) = viewModel.getOrderedCount(item)

    override fun onCustomizationCancel() {
        viewModel.cancelItem()
    }

    override fun onCustomizationDone() {
        viewModel.orderItem()
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
