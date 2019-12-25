package com.checkin.app.checkin.session.scheduled.fragments

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import butterknife.BindView
import com.checkin.app.checkin.Account.AccountUtil
import com.checkin.app.checkin.R
import com.checkin.app.checkin.misc.fragments.BaseFragment
import com.checkin.app.checkin.session.scheduled.viewmodels.ScheduledSessionDetailViewModel

class QSRBillingDetailFragment : BaseFragment() {
    override val rootLayout: Int = R.layout.fragment_user_qsr_detail_billing

    @BindView(R.id.tv_qsr_confirmation_guest_name)
    internal lateinit var tvGuestName: TextView
    @BindView(R.id.tv_qsr_confirmation_order_time)
    internal lateinit var tvOrderTime: TextView
    @BindView(R.id.tv_qsr_confirmation_order_id)
    internal lateinit var tvOrderId: TextView

    val viewModel: ScheduledSessionDetailViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        tvGuestName.text = AccountUtil.getUsername(requireContext())
        viewModel.sessionData.observe(this, Observer {
            it.data?.let {
                tvOrderId.text = it.hashId
                tvOrderTime.text = it.scheduled.formatOrderTime
            }
        })
    }
}