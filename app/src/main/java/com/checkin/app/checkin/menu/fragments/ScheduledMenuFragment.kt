package com.checkin.app.checkin.menu.fragments

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.checkin.app.checkin.R
import com.checkin.app.checkin.Utility.inTransaction
import com.checkin.app.checkin.menu.viewmodels.ScheduledCartViewModel
import com.checkin.app.checkin.menu.viewmodels.UserMenuViewModel

class ScheduledMenuFragment : BaseMenuFragment() {
    override val menuViewModel: UserMenuViewModel by viewModels()
    override val cartViewModel: ScheduledCartViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        childFragmentManager.inTransaction {
            add(R.id.container_as_menu, UserMenuGroupsFragment.withScheduledCart(), null)
        }

        menuViewModel.fetchRecommendedItems()
    }

    companion object {
        fun newInstance(restaurantId: Long) = ScheduledMenuFragment().apply {
            arguments = bundleOf(KEY_RESTAURANT_ID to restaurantId)
        }
    }
}
