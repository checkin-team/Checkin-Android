package com.checkin.app.checkin.manager.fragments

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import com.checkin.app.checkin.R
import com.checkin.app.checkin.Shop.Private.Invoice.RestaurantSessionModel
import com.checkin.app.checkin.Shop.Private.Invoice.ShopInvoiceDetailActivity
import com.checkin.app.checkin.Shop.Private.Invoice.ShopInvoiceSessionAdapter
import com.checkin.app.checkin.Shop.Private.Invoice.ShopInvoiceSessionAdapter.ShopInvoiceInteraction
import com.checkin.app.checkin.Shop.Private.Invoice.ShopInvoiceViewModel
import com.checkin.app.checkin.data.resource.Resource
import com.checkin.app.checkin.misc.fragments.BaseFragment
import com.checkin.app.checkin.utility.Utils

class ManagerInvoiceFragment : BaseFragment(), ShopInvoiceInteraction {
    override val rootLayout = R.layout.fragment_manager_invoice

    @BindView(R.id.rv_shop_invoice_sessions)
    internal lateinit var rvSessions: RecyclerView

    private val mAdapter: ShopInvoiceSessionAdapter by lazy {
        ShopInvoiceSessionAdapter(this)
    }
    private val mViewModel: ShopInvoiceViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initRefreshScreen(R.id.sr_manager_invoice)
        setupUi()
        mViewModel.restaurantSessions.observe(viewLifecycleOwner, Observer {
            it?.let { input ->
                handleLoadingRefresh(input)
                if (input.status === Resource.Status.SUCCESS && input.data != null) {
                    mAdapter.setSessionData(input.data)
                }
            }
        })
    }

    private fun setupUi() {
        rvSessions.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        rvSessions.adapter = mAdapter
    }

    override fun updateScreen() {
        super.updateScreen()
        Utils.getCurrentFormattedDateInvoice().also { currDate ->
            mViewModel.filterFrom(currDate, doFetch = false)
            mViewModel.filterTo(currDate, doFetch = false)
        }
        mViewModel.fetchShopSessions()
    }

    override fun onClickSession(data: RestaurantSessionModel?) {
        context?.let { ctx ->
            Intent(ctx, ShopInvoiceDetailActivity::class.java).apply {
                putExtra(ShopInvoiceDetailActivity.KEY_SESSION_DATA, data)
            }.also { startActivity(it) }
        }
    }

    companion object {
        fun newInstance() = ManagerInvoiceFragment()
    }
}
