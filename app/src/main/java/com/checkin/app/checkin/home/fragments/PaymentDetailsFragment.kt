package com.checkin.app.checkin.home.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import butterknife.BindView
import com.checkin.app.checkin.R
import com.checkin.app.checkin.home.viewmodels.PaymentDetailsViewModel
import com.checkin.app.checkin.misc.fragments.BaseFragment
import com.checkin.app.checkin.misc.views.CollapsibleSectionView
import com.checkin.app.checkin.session.scheduled.fragments.CommonOrderDetailFragment
import com.checkin.app.checkin.session.scheduled.fragments.PreorderBillingDetailFragment

class PaymentDetailsFragment : BaseFragment() {
    override val rootLayout: Int = R.layout.fragment_payment_details

    @BindView(R.id.csv_preorder_detail_billing)
    internal lateinit var csvBilling: CollapsibleSectionView
    @BindView(R.id.csv_preorder_detail_order)
    internal lateinit var csvOrders: CollapsibleSectionView
    val model: PaymentDetailsViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        csvBilling.attachFragment(childFragmentManager, ClosedOrderedDetailsModel())
        csvOrders.attachFragment(childFragmentManager, ClosedOrderDetailsFragment())

    }
}