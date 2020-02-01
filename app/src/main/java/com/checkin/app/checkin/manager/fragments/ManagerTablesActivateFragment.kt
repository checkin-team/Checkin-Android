package com.checkin.app.checkin.manager.fragments

import android.widget.Button
import butterknife.BindView
import butterknife.OnClick
import com.checkin.app.checkin.R
import com.checkin.app.checkin.misc.fragments.BaseFragment
import com.checkin.app.checkin.utility.parentActivityDelegate

class ManagerTablesActivateFragment : BaseFragment() {
    override val rootLayout: Int = R.layout.fragment_manager_tables_activate

    @BindView(R.id.btn_live_order_activate)
    internal lateinit var btnLiveOrderActivate: Button

    private val mListener: LiveOrdersInteraction by parentActivityDelegate()

    @OnClick(R.id.btn_live_order_activate)
    fun onViewClicked() {
        mListener.setLiveOrdersActivation(true)
    }

    interface LiveOrdersInteraction {
        fun setLiveOrdersActivation(isActivated: Boolean)
    }

    companion object {
        fun newInstance(): ManagerTablesActivateFragment = ManagerTablesActivateFragment()
    }
}