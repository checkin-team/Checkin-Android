package com.checkin.app.checkin.manager.fragments

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import butterknife.BindView
import butterknife.OnClick
import com.checkin.app.checkin.R
import com.checkin.app.checkin.Utility.Utils
import com.checkin.app.checkin.Utility.inTransaction
import com.checkin.app.checkin.manager.viewmodels.ManagerLiveScheduledViewModel
import com.checkin.app.checkin.misc.fragments.BaseFragment

class ManagerQsrOrderDetailEndFragment : BaseFragment() {
    override val rootLayout: Int = R.layout.fragment_manager_qsr_detail_end

    @BindView(R.id.tv_manager_qsr_detail_heading)
    internal lateinit var tvHeading: TextView
    @BindView(R.id.tv_manager_qsr_serving_time)
    internal lateinit var tvServingTime: TextView

    val commonFragment = CommonPreOrderDetailFragment()
    val viewModel: ManagerLiveScheduledViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        childFragmentManager.inTransaction {
            add(R.id.frg_container_manager_qsr_detail, commonFragment)
        }
        tvHeading.text = getString(R.string.label_qsr_order_summary)
        setupObservers()
    }

    private fun setupObservers() {
        viewModel.sessionData.observe(this, Observer {
            it?.data?.let {
                tvServingTime.text = Utils.formatDueTime(it.scheduled.orderTime!!, it.scheduled.modified)
            }
        })
    }

    @OnClick(R.id.btn_manager_qsr_session_end)
    fun onEndSession() {
        viewModel.markSessionDone(viewModel.sessionPk)
    }
}