package com.checkin.app.checkin.session.scheduled.fragments

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import butterknife.BindView
import butterknife.OnClick
import com.checkin.app.checkin.Account.AccountUtil
import com.checkin.app.checkin.R
import com.checkin.app.checkin.misc.fragments.BaseFragment
import com.checkin.app.checkin.session.scheduled.viewmodels.ScheduledSessionDetailViewModel

class PreorderBillingDetailFragment : BaseFragment() {
    override val rootLayout: Int = R.layout.fragment_user_preorder_detail_billing

    @BindView(R.id.tv_preorder_confirmation_guest_name)
    internal lateinit var tvGuestName: TextView
    @BindView(R.id.tv_preorder_confirmation_guest_count)
    internal lateinit var tvGuestCount: TextView
    @BindView(R.id.tv_preorder_confirmation_due)
    internal lateinit var tvPlannedTime: TextView
    @BindView(R.id.tv_preorder_confirmation_order_id)
    internal lateinit var tvOrderId: TextView

    val viewModel: ScheduledSessionDetailViewModel by activityViewModels()
    val cancelDialog by lazy {
        AlertDialog.Builder(requireContext())
                .setTitle("Are you sure you want to cancel?")
                .setPositiveButton("Yes") { _, _ -> viewModel.cancelSession() }
                .setNegativeButton("No") { dialog, _ -> dialog.dismiss() }
                .create()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        tvGuestName.text = AccountUtil.getUsername(requireContext())
        viewModel.sessionData.observe(this, Observer {
            it.data?.let {
                tvGuestCount.text = it.scheduled.countPeople.toString()
                tvPlannedTime.text = it.scheduled.formatPlannedDateTime
                tvOrderId.text = it.hashId
            }
        })
    }

    @OnClick(R.id.btn_user_scheduled_cancel)
    fun onCancel() {
        cancelDialog.show()
    }
}