package com.checkin.app.checkin.menu.fragments

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.checkin.app.checkin.R
import com.checkin.app.checkin.menu.viewmodels.ActiveSessionCartViewModel
import com.checkin.app.checkin.menu.viewmodels.UserMenuViewModel
import com.checkin.app.checkin.utility.inTransaction

class MenuFragment : BaseMenuFragment() {
    override val menuViewModel: UserMenuViewModel by viewModels()
    override val cartViewModel: ActiveSessionCartViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        childFragmentManager.inTransaction {
            add(R.id.container_as_menu, MenuGroupsFragment.withAsCart(), null)
        }

        val isShopMenu = arguments?.getBoolean(KEY_SHOP_MENU) ?: false
        if (!isShopMenu) menuViewModel.fetchRecommendedItems()
        cartViewModel.sessionPk = arguments?.getLong(KEY_SESSION_ID) ?: 0L
    }

    companion object {
        private const val KEY_SHOP_MENU = "menu.is_shop"
        private const val KEY_SESSION_ID = "menu.session_id"

        fun newInstanceForShop(restaurantId: Long, sessionId: Long) = MenuFragment().apply {
            arguments = bundleOf(KEY_RESTAURANT_ID to restaurantId, KEY_SESSION_ID to sessionId, KEY_SHOP_MENU to true)
        }

        fun newInstanceForActiveSession(restaurantId: Long) = MenuFragment().apply {
            arguments = bundleOf(KEY_RESTAURANT_ID to restaurantId)
        }
    }
}