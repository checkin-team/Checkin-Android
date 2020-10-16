package com.checkin.app.checkin.menu.views

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.airbnb.epoxy.EpoxyRecyclerView
import com.checkin.app.checkin.R
import com.checkin.app.checkin.menu.controllers.getUniqueId
import com.checkin.app.checkin.menu.holders.ActiveSessionMenuCartModelHolder
import com.checkin.app.checkin.menu.holders.activeSessionMenuCartModelHolder
import com.checkin.app.checkin.menu.models.OrderedItemModel
import com.checkin.app.checkin.menu.viewmodels.ActiveSessionCartViewModel
import com.checkin.app.checkin.utility.LockableBottomSheetBehavior
import com.checkin.app.checkin.utility.Utils
import com.checkin.app.checkin.utility.isNotEmpty
import com.google.android.material.bottomsheet.BottomSheetBehavior
import org.koin.core.KoinComponent

class ActiveSessionCartView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr), ActiveSessionMenuCartModelHolder.MenuCartInteraction, KoinComponent {
    @BindView(R.id.epoxy_rv_as_menu_cart)
    internal lateinit var epoxyRvItems: EpoxyRecyclerView
    @BindView(R.id.tv_as_cart_header_amount)
    internal lateinit var tvCartPrice: TextView
    @BindView(R.id.tv_as_cart_header_item_count)
    internal lateinit var tvItemCount: TextView

    private val activity by lazy { context as FragmentActivity }
    private val bottomSheetBehavior: LockableBottomSheetBehavior<View> by lazy {
        BottomSheetBehavior.from(this) as LockableBottomSheetBehavior<View>
    }
    private val cartViewModel: ActiveSessionCartViewModel by activity.viewModels()

    init {
        View.inflate(context, R.layout.fragment_active_session_cart, this).apply {
            setBackgroundColor(ContextCompat.getColor(context, R.color.white))
            ButterKnife.bind(this@ActiveSessionCartView)
        }
        setupObservers()
    }

    private fun setupObservers() {
        epoxyRvItems.withModels {
            cartViewModel.orderedItems.value.takeIf { it.isNotEmpty() }?.forEach {
                activeSessionMenuCartModelHolder {
                    id(it.getUniqueId())
                    orderData(it)
                    listener(this@ActiveSessionCartView)
                }
            }
        }

        cartViewModel.totalOrderedCount.observe(activity, Observer {
            if (it > 0) {
                tvItemCount.text = "$it Item${if (it > 1) "s" else ""}"
                if (bottomSheetBehavior.peekHeight == 0) bottomSheetBehavior.setPeekHeight(resources.getDimension(R.dimen.height_cart_header_topbar).toInt(), true)
            } else bottomSheetBehavior.setPeekHeight(0, true)
        })
        cartViewModel.orderedSubTotal.observe(activity, Observer {
            tvCartPrice.text = Utils.formatCurrencyAmount(context, it)
        })
        cartViewModel.orderedItems.observe(activity, Observer { epoxyRvItems.requestModelBuild() })
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        setupUi()
    }

    @OnClick(R.id.container_as_cart_header_topbar)
    fun onClickedTopbar() {
        bottomSheetBehavior.state = if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) BottomSheetBehavior.STATE_COLLAPSED else BottomSheetBehavior.STATE_EXPANDED
    }

    fun show() {
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    fun dismiss() {
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
    }

    private fun setupUi() {
        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) = when (newState) {
                BottomSheetBehavior.STATE_EXPANDED -> {
                    bottomSheetBehavior.swipeEnabled = false
                }
                else -> {
                    bottomSheetBehavior.swipeEnabled = true
                }
            }
        })
    }

    @OnClick(R.id.btn_as_menu_cart_proceed)
    fun onProceed() {
        cartViewModel.confirmOrder()
    }

    override fun onOrderedItemRemark(item: OrderedItemModel, s: String) {
        cartViewModel.updateCart(item.updateRemarks(s))
    }

    override fun onOrderedItemChanged(item: OrderedItemModel, count: Int) {
        cartViewModel.orderItem(item.updateQuantity(count))
    }
}
