package com.checkin.app.checkin.Menu.UserMenu.Fragment

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.OnClick
import com.checkin.app.checkin.Data.Resource
import com.checkin.app.checkin.Menu.MenuItemInteraction
import com.checkin.app.checkin.menu.models.OrderedItemModel
import com.checkin.app.checkin.Menu.UserMenu.Adapter.MenuCartAdapter
import com.checkin.app.checkin.Menu.UserMenu.Adapter.MenuTreatYourselfAdapter
import com.checkin.app.checkin.Menu.UserMenu.MenuViewModel
import com.checkin.app.checkin.R
import com.checkin.app.checkin.Utility.Utils
import com.checkin.app.checkin.misc.fragments.BaseFragment
import com.checkin.app.checkin.session.models.TrendingDishModel
import kotlinx.coroutines.launch

class MenuCartFragment : BaseFragment(), MenuCartAdapter.MenuCartInteraction, MenuTreatYourselfAdapter.TreatYourselfInteraction {
    @BindView(R.id.rv_menu_cart)
    internal lateinit var rvCart: RecyclerView
    @BindView(R.id.rv_menu_treat_yourself)
    internal lateinit var rvTreatYourself: RecyclerView
    @BindView(R.id.tv_menu_count_ordered_items)
    internal lateinit var tvCountItems: TextView
    @BindView(R.id.tv_menu_subtotal)
    internal lateinit var tvCartSubtotal: TextView
    @BindView(R.id.btn_menu_cart_proceed)
    internal lateinit var btnCartProceed: Button
    private var mListener: MenuItemInteraction? = null

    private lateinit var mViewModel: MenuViewModel
    private var mCartAdapter: MenuCartAdapter? = null
    private var mTreatYourselfAdapter: MenuTreatYourselfAdapter? = null

    override val rootLayout: Int
        get() = R.layout.fragment_menu_cart

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mViewModel = ViewModelProviders.of(requireActivity()).get(MenuViewModel::class.java)

        mCartAdapter = MenuCartAdapter(this)
        rvCart.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        rvCart.adapter = mCartAdapter

        mTreatYourselfAdapter = MenuTreatYourselfAdapter(this)
        rvTreatYourself.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
        rvTreatYourself.adapter = mTreatYourselfAdapter

        mViewModel.orderedItems.observe(this, Observer<List<OrderedItemModel>> { mCartAdapter!!.setOrderedItems(it) })
        mViewModel.totalOrderedCount.observe(this, Observer {
            it?.let { count ->
                tvCountItems.text = String.format("%s Items", Utils.formatCount(count.toLong()))
            }
        })
        mViewModel.orderedSubTotal.observe(this, Observer {
            it?.let { subtotal ->
                tvCartSubtotal.text = Utils.formatCurrencyAmount(requireContext(), subtotal)
            }
        })

        mViewModel.serverOrderedItems.observe(this, Observer {
            it?.let { resource ->
                when {
                    resource.status === Resource.Status.SUCCESS -> {
                        Utils.toast(requireContext(), "Confirmed orders!")
                        requireActivity().finish()
                    }
                    resource.status === Resource.Status.LOADING -> {
                    }
                    else -> {
                        Utils.toast(requireContext(), resource.message)
                        btnCartProceed.isEnabled = true
                    }
                }
            }
        })

        mViewModel.menuTrendingItems.observe(this, Observer {
            it?.let { inventoryItemModels ->
                if (inventoryItemModels.status === Resource.Status.SUCCESS && inventoryItemModels.data != null)
                    mTreatYourselfAdapter!!.setData(inventoryItemModels.data!!)
            }
        })
    }

    @OnClick(R.id.btn_menu_cart_proceed)
    fun onProceedBtnClicked(view: View) {
        if (mCartAdapter!!.itemCount > 0) {
            btnCartProceed.isEnabled = false
            mViewModel.confirmOrder()
        } else {
            Utils.toast(requireContext(), "Order something before proceeding!")
            btnCartProceed.isEnabled = true
        }
    }

    override fun onOrderedItemChanged(item: OrderedItemModel?, count: Int) {
        mViewModel.setCurrentItem(item!!.updateQuantity(count))
        mViewModel.orderItem()
    }

    override fun onOrderedItemRemark(item: OrderedItemModel, s: String) {
//        if (s.length > 0) {
//            item.re
//        }
    }

    @OnClick(R.id.im_menu_cart_back)
    fun onBack() {
        onBackPressed()
    }

    override fun onBackPressed(): Boolean {
        if (fragmentManager != null) {
            fragmentManager!!.beginTransaction()
                    .remove(this)
                    .commit()
            return true
        }
        return false
    }

    override fun onTreatYourselfItemClick(itemModel: TrendingDishModel?) {
        itemModel?.let {
            lifecycleScope.launch {
                mViewModel.getMenuItemById(itemModel.pk)?.let {
                    mListener?.onMenuItemAdded(it)
                }
            }
        }
    }

    companion object {

        fun newInstance(listener: MenuItemInteraction): MenuCartFragment {
            val fragment = MenuCartFragment()
            fragment.mListener = listener
            return fragment
        }
    }
}

