package com.checkin.app.checkin.manager.fragments

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import butterknife.BindView
import butterknife.OnClick
import com.checkin.app.checkin.data.resource.Resource
import com.checkin.app.checkin.R
import com.checkin.app.checkin.Utility.Utils
import com.checkin.app.checkin.Utility.inTransaction
import com.checkin.app.checkin.manager.viewmodels.ManagerLiveScheduledViewModel
import com.checkin.app.checkin.misc.fragments.BaseFragment

class ManagerPreOrderDetailPreparationFragment : BaseFragment() {
    override val rootLayout: Int = R.layout.fragment_manager_preparation_scheduled_detail

    @BindView(R.id.tv_manager_scheduled_detail_heading)
    internal lateinit var tvHeading: TextView
    @BindView(R.id.tv_manager_scheduled_preparation_time)
    internal lateinit var tvPreparationTime: TextView

    private val commonFragment = CommonPreOrderDetailFragment()
    private val viewModel: ManagerLiveScheduledViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        childFragmentManager.inTransaction {
            add(R.id.frg_container_manager_scheduled_detail, commonFragment)
        }
        tvHeading.text = Utils.fromHtml(getString(R.string.format_label_scheduled_preparation_food_guest_due, "0 minutes"))
        setupObservers()
    }

    private fun setupObservers() {
        viewModel.preparationTimeData.observe(this, Observer {
            if (it != null) tvPreparationTime.text = "${it.preparationTime} minutes"
        })
        viewModel.sessionData.observe(this, Observer {
            if (it?.data != null) tvHeading.text = Utils.fromHtml(it.data.customerDueReachTime?.let { getString(R.string.format_label_scheduled_preparation_food_guest_due, it) }
                    ?: getString(R.string.format_label_scheduled_preparation_food_guest_late, it.data.customerLateTime!!))
        })
        viewModel.doneData.observe(this, Observer {
            if (it?.status == Resource.Status.SUCCESS) {
                Utils.toast(requireContext(), "Closed Session!")
            }
        })
    }

    @OnClick(R.id.container_manager_scheduled_session_done)
    fun onDoneSession() {
        viewModel.markSessionDone(viewModel.sessionPk)
        requireActivity().setResult(FragmentActivity.RESULT_OK)
    }

    @OnClick(R.id.btn_manager_scheduled_session_cancel)
    fun onCancelSession() {
        ManagerScheduledSessionCancelReasonBottomSheetFragment().show(childFragmentManager, null)
        requireActivity().setResult(FragmentActivity.RESULT_OK)
    }
}