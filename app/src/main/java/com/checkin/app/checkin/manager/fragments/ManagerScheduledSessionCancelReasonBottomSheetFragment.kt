package com.checkin.app.checkin.manager.fragments

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import butterknife.BindView
import butterknife.OnClick
import com.checkin.app.checkin.R
import com.checkin.app.checkin.data.resource.Resource
import com.checkin.app.checkin.manager.models.ShopScheduledSessionDetailModel
import com.checkin.app.checkin.manager.viewmodels.ManagerLiveScheduledViewModel
import com.checkin.app.checkin.misc.fragments.BaseBottomSheetFragment
import com.checkin.app.checkin.session.models.ScheduledSessionStatus
import com.checkin.app.checkin.utility.Utils
import com.google.android.material.textfield.TextInputEditText

class ManagerScheduledSessionCancelReasonBottomSheetFragment : BaseBottomSheetFragment() {
    override val rootLayout: Int = R.layout.fragment_manager_scheduled_cancel

    @BindView(R.id.tet_manager_scheduled_cancel_reason)
    internal lateinit var tetReasonOther: TextInputEditText
    @BindView(R.id.til_manager_scheduled_cancel_reason)
    internal lateinit var tilReasonBox: ViewGroup
    @BindView(R.id.rg_manager_scheduled_cancel_reason)
    internal lateinit var rgCancelReason: RadioGroup

    private val viewModel: ManagerLiveScheduledViewModel by activityViewModels()
    private val extraReasons = listOf(R.id.rb_manager_scheduled_cancel_reason_customer_delayed, R.id.rb_manager_scheduled_cancel_reason_food_delayed)
    var reasonType = -1

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        rgCancelReason.setOnCheckedChangeListener { _, checkedId -> onChooseReason(checkedId) }
        viewModel.sessionData.observe(this, Observer {
            if (it?.status == Resource.Status.SUCCESS && it.data != null) {
                updateWithStatus(it.data)
            }
        })
        updateReason(-1)
    }

    private fun updateWithStatus(data: ShopScheduledSessionDetailModel) {
        if (data.scheduled.status == ScheduledSessionStatus.PENDING) updateVisibility(View.GONE)
        else updateVisibility(View.VISIBLE)
    }

    private fun updateVisibility(visibilty: Int) {
        extraReasons.forEach { view?.findViewById<View>(it)?.visibility = visibilty }
    }

    private fun onChooseReason(checkedId: Int) {
        when (checkedId) {
            R.id.rb_manager_scheduled_cancel_reason_customer_delayed -> updateReason(1)
            R.id.rb_manager_scheduled_cancel_reason_food_delayed -> updateReason(2)
            R.id.rb_manager_scheduled_cancel_reason_item_unavailable -> updateReason(3)
            R.id.rb_manager_scheduled_cancel_reason_other -> updateReason(0)
        }
    }

    private fun updateReason(type: Int) {
        reasonType = type
        if (type == 0) tilReasonBox.visibility = View.VISIBLE
        else tilReasonBox.visibility = View.GONE
    }

    @OnClick(R.id.btn_manager_scheduled_cancel_no)
    fun onCancel() = dismiss()

    @OnClick(R.id.btn_manager_scheduled_cancel_yes)
    fun onConfirm() {
        if (reasonType < 0) {
            Utils.toast(requireContext(), "Select a cancellation reason!")
            return
        }
        viewModel.rejectSession(viewModel.sessionPk, reasonType, tetReasonOther.text?.toString()?.takeIf { reasonType == 0 })
    }
}