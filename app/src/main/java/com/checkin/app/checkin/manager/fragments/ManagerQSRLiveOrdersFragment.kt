package com.checkin.app.checkin.manager.fragments

import com.checkin.app.checkin.R
import com.checkin.app.checkin.misc.fragments.BaseFragment

class ManagerQSRLiveOrdersFragment : BaseFragment() {
    override val rootLayout: Int = R.layout.fragment_manager_qsr_live_orders

    companion object {
        fun newInstance() = ManagerQSRLiveOrdersFragment()
    }
}