package com.checkin.app.checkin.session.scheduled.fragments

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import butterknife.BindView
import com.checkin.app.checkin.R
import com.checkin.app.checkin.Utility.Utils
import com.checkin.app.checkin.data.resource.Resource
import com.checkin.app.checkin.misc.fragments.BaseFragment
import com.checkin.app.checkin.misc.views.CollapsibleSectionView
import com.checkin.app.checkin.session.models.CustomerScheduledSessionDetailModel
import com.checkin.app.checkin.session.models.ScheduledSessionStatus
import com.checkin.app.checkin.session.scheduled.viewmodels.ScheduledSessionDetailViewModel
import java.util.*

class QSRDetailFragment : BaseFragment(), CollapsibleSectionView.SectionListener {
    override val rootLayout: Int = R.layout.fragment_user_qsr_detail

    @BindView(R.id.tv_qsr_detail_status)
    internal lateinit var tvStatus: TextView
    @BindView(R.id.tv_qsr_detail_status_text)
    internal lateinit var tvStatusInfo: TextView
    @BindView(R.id.im_qsr_detail_status_icon)
    internal lateinit var imStatusIcon: ImageView
    @BindView(R.id.csv_qsr_detail_billing)
    internal lateinit var csvBilling: CollapsibleSectionView
    @BindView(R.id.csv_qsr_detail_order)
    internal lateinit var csvOrders: CollapsibleSectionView
    @BindView(R.id.tv_qsr_detail_preparation_time)
    internal lateinit var tvPreparationTime: TextView
    @BindView(R.id.container_qsr_preparation_time)
    internal lateinit var containerPreparationTime: ViewGroup

    val viewModel: ScheduledSessionDetailViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        csvBilling.attachFragment(childFragmentManager, QSRBillingDetailFragment())
        csvOrders.attachFragment(childFragmentManager, CommonOrderDetailFragment())

        viewModel.sessionData.observe(this, Observer {
            if (it.status == Resource.Status.SUCCESS && it.data != null) setupData(it.data)
        })
    }

    private fun setupData(data: CustomerScheduledSessionDetailModel) {
        when (data.scheduled.status) {
            ScheduledSessionStatus.PENDING -> {
                tvStatus.text = getString(R.string.text_preorder_scheduled_status_pending)
                tvStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color.orange_red))
                tvStatusInfo.text = getString(R.string.text_preorder_scheduled_status_info_pending)
                imStatusIcon.setImageResource(R.drawable.ic_hourglass_orange)
                containerPreparationTime.visibility = View.GONE
            }
            ScheduledSessionStatus.ACCEPTED, ScheduledSessionStatus.PREPARATION -> {
                tvStatus.text = getString(R.string.text_preorder_scheduled_status_accepted)
                tvStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color.greenish_teal))
                tvStatusInfo.text = getString(R.string.text_preorder_scheduled_status_info_accepted)
                imStatusIcon.setImageResource(R.drawable.ic_accepted_tick)
                containerPreparationTime.visibility = View.VISIBLE
            }
            else -> requireActivity().finish()
        }
        val currTime = Calendar.getInstance().time
        tvPreparationTime.text = if (data.scheduled.plannedDatetime != null && currTime < data.scheduled.plannedDatetime)
            Utils.formatDueTime(currTime, data.scheduled.plannedDatetime) else "Ready soon"
    }

    override fun onExpand(view: CollapsibleSectionView) {
        when (view.id) {
            R.id.csv_qsr_detail_order -> csvBilling.collapse()
            R.id.csv_qsr_detail_billing -> csvOrders.collapse()
        }
    }

    override fun onCollapse(view: CollapsibleSectionView) {}
}