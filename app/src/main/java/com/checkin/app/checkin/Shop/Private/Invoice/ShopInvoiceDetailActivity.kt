package com.checkin.app.checkin.Shop.Private.Invoice

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.checkin.app.checkin.R
import com.checkin.app.checkin.misc.activities.BaseActivity
import com.checkin.app.checkin.misc.models.BriefModel
import com.google.android.material.tabs.TabLayout
import java.util.*

class ShopInvoiceDetailActivity : BaseActivity() {
    @BindView(R.id.tv_invoice_session_id)
    internal lateinit var tvSessionId: TextView
    @BindView(R.id.tv_invoice_session_date)
    internal lateinit var tvDate: TextView
    @BindView(R.id.tv_invoice_session_item_count)
    internal lateinit var tvItemCount: TextView
    @BindView(R.id.tv_invoice_session_member_count)
    internal lateinit var tvMemberCount: TextView
    @BindView(R.id.tv_invoice_session_waiter)
    internal lateinit var tvWaiter: TextView
    @BindView(R.id.tv_invoice_session_table)
    internal lateinit var tvTable: TextView
    @BindView(R.id.tabs_shop_invoice)
    internal lateinit var tabLayout: TabLayout
    @BindView(R.id.pager_shop_invoice)
    internal lateinit var pagerInvoice: ViewPager

    private val mViewModel: ShopSessionViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shop_invoice_detail)
        ButterKnife.bind(this)
        val data = intent.getSerializableExtra(KEY_SESSION_DATA) as RestaurantSessionModel
        setupAppbarDetails(data)
        setupViewPager()
        setupData(data)
    }

    private fun setupData(data: RestaurantSessionModel) {
        mViewModel.setSessionPk(data.pk)
    }

    private fun setupAppbarDetails(data: RestaurantSessionModel) {
        val host: BriefModel? = data.host
        if (data.isScheduled) {
            tvWaiter.visibility = View.GONE
        } else {
            tvWaiter.visibility = View.VISIBLE
            tvWaiter.text = String.format(Locale.ENGLISH, "Waiter : %s", if (host != null) host.displayName else resources.getString(R.string.waiter_unassigned))
        }
        tvSessionId.text = data.formatHashId
        tvDate.text = data.formattedDate
        tvMemberCount.text = data.countCustomers.toString()
        tvItemCount.text = String.format(Locale.ENGLISH, " | %d item(s)", data.countOrders)
        tvTable.text = data.tableInfo
    }

    private fun setupViewPager() {
        pagerInvoice.adapter = InvoiceFragmentAdapter(supportFragmentManager)
        tabLayout.setupWithViewPager(pagerInvoice)
    }

    @OnClick(R.id.im_shop_invoice_appbar_back)
    fun onBackClicked() {
        onBackPressed()
    }

    class InvoiceFragmentAdapter(fm: FragmentManager?) : FragmentPagerAdapter(fm!!) {
        override fun getItem(position: Int): Fragment = when (position) {
            0 -> ShopSessionDetailFragment.newInstance()
            else -> ShopSessionFeedbackFragment.newInstance()
        }

        override fun getCount(): Int = 2

        override fun getPageTitle(position: Int): CharSequence? = when (position) {
            0 -> "Detail"
            1 -> "Feedbacks"
            else -> null
        }
    }

    companion object {
        const val KEY_SESSION_DATA = "shop.invoice_session"
    }
}