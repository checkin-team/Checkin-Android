package com.checkin.app.checkin.home.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import butterknife.BindView
import com.checkin.app.checkin.R
import com.checkin.app.checkin.home.viewmodels.ClosedSessionViewModel
import com.checkin.app.checkin.misc.fragments.BaseFragment
import com.checkin.app.checkin.misc.views.CollapsibleSectionView

class ClosedSessionDetailFragment : BaseFragment() {
    override val rootLayout: Int = R.layout.fragment_closed_session

    @BindView(R.id.csv_closed_session_billing)
    internal lateinit var csvBilling: CollapsibleSectionView
    @BindView(R.id.csv_closed_session_order)
    internal lateinit var csvOrders: CollapsibleSectionView
    val model: ClosedSessionViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        csvBilling.attachFragment(childFragmentManager, ClosedOrderDetailsModel())
        csvOrders.attachFragment(childFragmentManager, CommonClosedOrderDetailsFragment())

    }
}