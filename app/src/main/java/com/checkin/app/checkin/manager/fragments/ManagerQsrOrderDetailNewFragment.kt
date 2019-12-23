package com.checkin.app.checkin.manager.fragments

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import butterknife.BindView
import butterknife.OnClick
import com.checkin.app.checkin.Data.Resource
import com.checkin.app.checkin.R
import com.checkin.app.checkin.Utility.Utils
import com.checkin.app.checkin.Utility.inTransaction
import com.checkin.app.checkin.manager.models.PreparationTimeModel
import com.checkin.app.checkin.manager.viewmodels.ManagerLiveScheduledViewModel
import com.checkin.app.checkin.misc.fragments.BaseFragment

class ManagerQsrOrderDetailNewFragment : BaseFragment() {
    override val rootLayout: Int = R.layout.fragment_manager_qsr_detail_new

    @BindView(R.id.tv_manager_qsr_detail_heading)
    internal lateinit var tvHeading: TextView
    @BindView(R.id.tv_manager_qsr_preparation_time)
    internal lateinit var tvPreparationTime: TextView

    val commonFragment = CommonPreOrderDetailFragment()
    val viewModel: ManagerLiveScheduledViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        childFragmentManager.inTransaction {
            add(R.id.frg_container_manager_qsr_detail, commonFragment)
        }
        tvHeading.text = getString(R.string.label_scheduled_wait_for_confirmation)
        setupObservers()
    }

    private fun setupObservers() {
        viewModel.preparationTimeData.observe(this, Observer {
            if (it != null) tvPreparationTime.text = "${it.preparationTime} minutes"
        })
        viewModel.acceptData.observe(this, Observer {
            if (it?.status == Resource.Status.SUCCESS) {
                Utils.toast(requireContext(), "Accepted Session!")
            }
        })
    }

    @OnClick(R.id.btn_manager_qsr_session_accept)
    fun onAcceptSession() {
        viewModel.acceptSession(viewModel.sessionPk)
    }

    @OnClick(R.id.btn_manager_qsr_session_cancel)
    fun onCancelSession() {
        ManagerScheduledSessionCancelReasonBottomSheetFragment().show(childFragmentManager, null)
    }

    @OnClick(R.id.btn_manager_qsr_preparation_time_decrement, R.id.btn_manager_qsr_preparation_time_increment)
    fun onChangePreparationTime(v: View) {
        if (v.id == R.id.btn_manager_qsr_preparation_time_decrement)
            viewModel.preparationTimeData.value = viewModel.preparationTimeData.value?.let { PreparationTimeModel(it.preparationTime - 2) }
        else
            viewModel.preparationTimeData.value = viewModel.preparationTimeData.value?.let { PreparationTimeModel(it.preparationTime + 2) }
    }
}