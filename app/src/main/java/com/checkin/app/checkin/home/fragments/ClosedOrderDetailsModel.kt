package com.checkin.app.checkin.home.fragments

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import butterknife.BindView
import com.checkin.app.checkin.R
import com.checkin.app.checkin.accounts.AccountUtil
import com.checkin.app.checkin.data.resource.Resource
import com.checkin.app.checkin.home.viewmodels.ClosedSessionViewModel
import com.checkin.app.checkin.misc.fragments.BaseFragment

class ClosedOrderDetailsModel : BaseFragment() {
    override val rootLayout: Int = R.layout.fragment_closed_order_details

    @BindView(R.id.tv_closed_order_confirmation_guest_name)
    internal lateinit var tvGuestName: TextView
    @BindView(R.id.tv_closed_order_confirmation_due)
    internal lateinit var tvConfirmationDate: TextView
    @BindView(R.id.tv_closed_order_confirmation_order_id)
    internal lateinit var tvOrderId: TextView

    val model: ClosedSessionViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        model.sessionData.observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Resource.Status.SUCCESS -> {
                    tvGuestName.text = AccountUtil.getUsername(requireContext())
                    it.data.let {
                        tvConfirmationDate.text = it?.formatPlannedDate
                        tvOrderId.text = it?.formatId
                    }
                }
            }
        })
    }
}