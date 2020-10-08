package com.checkin.app.checkin.Waiter.Fragment

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import com.checkin.app.checkin.R
import com.checkin.app.checkin.Waiter.Model.WaiterEventModel
import com.checkin.app.checkin.Waiter.WaiterEventAdapter
import com.checkin.app.checkin.Waiter.WaiterEventAdapter.WaiterEventInteraction
import com.checkin.app.checkin.Waiter.WaiterTableViewModel
import com.checkin.app.checkin.data.resource.Resource
import com.checkin.app.checkin.misc.fragments.BaseFragment
import com.checkin.app.checkin.session.activesession.chat.SessionChatModel
import com.checkin.app.checkin.session.models.SessionOrderedItemModel
import com.checkin.app.checkin.utility.parentViewModels
import com.checkin.app.checkin.utility.toast

class WaiterTableEventFragment : BaseFragment(), WaiterEventInteraction {
    override val rootLayout: Int = R.layout.fragment_waiter_table_event

    @BindView(R.id.rv_waiter_table_events_active)
    lateinit var rvEventsActive: RecyclerView

    @BindView(R.id.rv_waiter_table_events_done)
    lateinit var rvEventsDone: RecyclerView

    @BindView(R.id.title_waiter_delivered)
    lateinit var tvDelivered: TextView

    @BindView(R.id.nested_sv_ws_event)
    lateinit var nestedSVEvent: NestedScrollView

    private val mActiveAdapter = WaiterEventAdapter(this)
    private val mDoneAdapter = WaiterEventAdapter(this)
    private val mViewModel: WaiterTableViewModel by parentViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        rvEventsActive.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        rvEventsDone.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        rvEventsActive.adapter = mActiveAdapter
        rvEventsDone.adapter = mDoneAdapter

        mViewModel.fetchTableEvents()
        setupObservers()
    }

    private fun setupObservers() {
        mViewModel.activeTableEvents.observe(viewLifecycleOwner, Observer { listResource ->
            if (listResource?.status === Resource.Status.SUCCESS && listResource.data != null) {
                mActiveAdapter.setData(listResource.data)
                nestedSVEvent.scrollTo(0, 0)
            }
        })
        mViewModel.deliveredTableEvents.observe(viewLifecycleOwner, Observer { listResource ->
            if (listResource?.status === Resource.Status.SUCCESS && listResource.data != null) {
                if (listResource.data.size > 0) {
                    tvDelivered.visibility = View.VISIBLE
                    rvEventsDone.visibility = View.VISIBLE
                    mDoneAdapter.setData(listResource.data)
                    nestedSVEvent.scrollTo(0, 0)
                }
            }
        })
        mViewModel.orderStatus.observe(viewLifecycleOwner, Observer {
            it?.let { resource ->
                if (resource.status === Resource.Status.SUCCESS && resource.data != null) mViewModel.updateUiMarkOrderStatus(resource.data)
                else if (resource.status !== Resource.Status.LOADING) toast(resource.message)
            }
        })
        mViewModel.eventUpdate.observe(viewLifecycleOwner, Observer {
            it?.let { resource ->
                if (resource.status === Resource.Status.SUCCESS && resource.data != null) {
                    mViewModel.updateUiMarkEventDone(java.lang.Long.valueOf(resource.data.pk))
                } else if (resource.status !== Resource.Status.LOADING) toast(resource.message)
            }
        })
        mViewModel.orderListStatusData.observe(viewLifecycleOwner, Observer {
            it?.let { listResource ->
                when (listResource.status) {
                    Resource.Status.SUCCESS -> mViewModel.updateUiOrderListStatus(listResource.data)
                    Resource.Status.LOADING -> {
                    }
                    else -> toast(listResource.message)
                }
            }
        })
    }

    override fun onEventMarkDone(eventModel: WaiterEventModel) {
        mViewModel.markEventDone(eventModel.pk)
    }

    override fun onOrderMarkDone(orderedItemModel: SessionOrderedItemModel) {
        mViewModel.updateOrderStatus(orderedItemModel.pk.toLong(), SessionChatModel.CHAT_STATUS_TYPE.DONE)
    }

    override fun onOrderAccept(orderedItemModel: SessionOrderedItemModel) {
        mViewModel.updateOrderStatusNew(orderedItemModel.pk, SessionChatModel.CHAT_STATUS_TYPE.IN_PROGRESS.tag)
        mViewModel.confirmOrderStatusWaiter()
    }

    override fun onOrderCancel(orderedItemModel: SessionOrderedItemModel) {
        mViewModel.updateOrderStatusNew(orderedItemModel.pk, SessionChatModel.CHAT_STATUS_TYPE.CANCELLED.tag)
        mViewModel.confirmOrderStatusWaiter()
    }

    companion object {
        fun newInstance(): WaiterTableEventFragment = WaiterTableEventFragment()
    }
}