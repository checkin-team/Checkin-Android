package com.checkin.app.checkin.manager.fragments

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import butterknife.BindView
import com.checkin.app.checkin.R
import com.checkin.app.checkin.Utility.inTransaction
import com.checkin.app.checkin.manager.viewmodels.ManagerLiveScheduledViewModel
import com.checkin.app.checkin.misc.fragments.BaseFragment

class ManagerPreOrderDetailUpcomingFragment : BaseFragment() {
    override val rootLayout: Int = R.layout.fragment_manager_upcoming_scheduled_detail

    @BindView(R.id.tv_manager_scheduled_detail_heading)
    internal lateinit var tvHeading: TextView
    @BindView(R.id.tv_manager_scheduled_preparation_time)
    internal lateinit var tvPreparationTime: TextView

    val commonFragment = CommonPreOrderDetailFragment()
    val viewModel: ManagerLiveScheduledViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        childFragmentManager.inTransaction {
            add(R.id.frg_container_manager_scheduled_detail, commonFragment)
        }
        tvHeading.text = getString(R.string.label_scheduled_accepted_request)
        setupObservers()
    }

    private fun setupObservers() {
        viewModel.preparationTimeData.observe(this, Observer {
            if (it != null) tvPreparationTime.text = "${it.preparationTime} minutes"
        })
    }
}