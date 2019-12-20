package com.checkin.app.checkin.session.scheduled

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.airbnb.epoxy.EpoxyRecyclerView
import com.checkin.app.checkin.Data.Resource
import com.checkin.app.checkin.Menu.Model.OrderedItemModel
import com.checkin.app.checkin.R
import com.checkin.app.checkin.Utility.Utils
import com.checkin.app.checkin.Utility.pass
import com.checkin.app.checkin.menu.controllers.CartOrderedItemController
import com.checkin.app.checkin.menu.holders.CartOrderInteraction
import com.checkin.app.checkin.menu.viewmodels.CartViewModel
import com.checkin.app.checkin.misc.BillHolder
import com.google.android.material.bottomsheet.BottomSheetBehavior

class ScheduledSessionCartView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : CoordinatorLayout(context, attrs, defStyleAttr), CartOrderInteraction {
    @BindView(R.id.container_cart_header_topbar)
    internal lateinit var containerCartTopbar: ViewGroup
    @BindView(R.id.tv_cart_header_planned_time)
    internal lateinit var tvHeaderPlannedTime: TextView
    @BindView(R.id.container_cart_header_time_switcher)
    internal lateinit var containerTimeSwitcher: ViewGroup
    @BindView(R.id.epoxy_rv_scheduled_cart_orders)
    internal lateinit var epoxyRvCartOrders: EpoxyRecyclerView
    @BindView(R.id.tv_cart_guest_detail_name)
    internal lateinit var tvGuestName: TextView
    @BindView(R.id.tv_cart_footer_pay_total)
    internal lateinit var tvPayTotal: TextView

    lateinit var activity: FragmentActivity
    lateinit var viewModel: CartViewModel
    lateinit var listener: ScheduledSessionInteraction

    private val ordersController = CartOrderedItemController(this)
    private val billHolder: BillHolder
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>

    init {
        View.inflate(context, R.layout.fragment_scheduled_session_cart, this).apply {
            setBackgroundColor(ContextCompat.getColor(context, R.color.white_two))
            ButterKnife.bind(this@ScheduledSessionCartView)
        }
        billHolder = BillHolder(this)
        setupUi()
    }

    private fun setupUi() {
        epoxyRvCartOrders.setControllerAndBuildModels(ordersController)
    }

    private fun showScheduler() {
        listener.onCreateNewScheduledSession()
    }

    fun dismiss() {
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
    }

    @OnClick(R.id.container_cart_header_topbar)
    fun onClickedTopbar() {
        bottomSheetBehavior.state = if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) BottomSheetBehavior.STATE_COLLAPSED else BottomSheetBehavior.STATE_EXPANDED
    }

    fun setup(activity: FragmentActivity) {
        this.activity = activity
        this.listener = activity as ScheduledSessionInteraction
        this.viewModel = ViewModelProviders.of(activity).get(CartViewModel::class.java)

        bottomSheetBehavior = BottomSheetBehavior.from(this)
        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) = when (newState) {
                BottomSheetBehavior.STATE_EXPANDED -> if (viewModel.sessionPk == 0L) showScheduler() else pass
                else -> pass
            }
        })

        setupObservers()
    }

    private fun setupObservers() {
        viewModel.serverPendingData.observe(activity, Observer {
            it?.let { listResource ->
                when (listResource.status) {
                    Resource.Status.ERROR_DISCONNECTED, Resource.Status.ERROR_UNKNOWN -> handleNoInternet(listResource)
                    Resource.Status.LOADING -> blockScreen()
                    Resource.Status.SUCCESS -> {
                        viewModel.fetchCartBill()
                        unblockScreen()
                    }
                }
            }
        })
        viewModel.orderedItems.observe(activity, Observer {
            it?.let {
                ordersController.orderedItems = it
            }
        })
        viewModel.cartBillData.observe(activity, Observer {
            it?.let { resource ->
                when (resource.status) {
                    Resource.Status.SUCCESS -> resource.data?.also { billHolder.bind(it.bill) }
                    Resource.Status.LOADING -> billHolder.showLoading()
                    else -> pass
                }
            }
        })
    }

    private fun unblockScreen() {
        Utils.toast(context, "Unblock")
    }

    private fun blockScreen() {
        Utils.toast(context, "Blocked")
    }

    private fun handleNoInternet(resource: Resource<Any?>) {
        println(resource.message)
        Utils.toast(context, "No Internet!")
    }

    override fun onItemChange(orderedItemModel: OrderedItemModel, newCount: Int) {
        orderedItemModel.quantity = newCount
        viewModel.orderItem(orderedItemModel)
    }

    companion object {
        const val KEY_SESSION_ID = "session.new.id"
    }
}

interface ScheduledSessionInteraction {
    fun onCreateNewScheduledSession()
}