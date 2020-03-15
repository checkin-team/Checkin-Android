package com.checkin.app.checkin.home.fragments

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import butterknife.BindView
import com.checkin.app.checkin.R
import com.checkin.app.checkin.data.resource.Resource
import com.checkin.app.checkin.home.model.ClosedSessionDetailsModel
import com.checkin.app.checkin.home.viewmodels.ClosedSessionViewModel
import com.checkin.app.checkin.misc.fragments.BaseFragment
import com.checkin.app.checkin.misc.views.CollapsibleSectionView
import com.checkin.app.checkin.utility.Utils

class ClosedSessionDetailFragment : BaseFragment() {
    override val rootLayout: Int = R.layout.fragment_closed_session

    @BindView(R.id.csv_closed_session_billing)
    internal lateinit var csvBilling: CollapsibleSectionView

    @BindView(R.id.csv_closed_session_order)
    internal lateinit var csvOrders: CollapsibleSectionView

    @BindView(R.id.tv_serving_time)
    internal lateinit var tvServingTime: TextView

    @BindView(R.id.tv_session_time)
    internal lateinit var tvSessionTime: TextView

    val viewModel: ClosedSessionViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        csvBilling.attachFragment(childFragmentManager, ClosedOrderDetailsModel())
        csvOrders.attachFragment(childFragmentManager, CommonClosedOrderDetailsFragment())

        viewModel.sessionData.observe(viewLifecycleOwner, Observer {
            if (it.status == Resource.Status.SUCCESS && it.data != null) {
                setupData(it.data)
            }
        })
    }

    private fun setupData(data: ClosedSessionDetailsModel) {
        tvServingTime.text = Utils.formatDueTime(data.servingTime)
        tvSessionTime.text = Utils.formatDueTime(data.sessionTime)
    }
}