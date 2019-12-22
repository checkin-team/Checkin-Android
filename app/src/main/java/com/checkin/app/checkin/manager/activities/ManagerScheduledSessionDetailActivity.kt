package com.checkin.app.checkin.manager.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.checkin.app.checkin.Data.Resource
import com.checkin.app.checkin.R
import com.checkin.app.checkin.Utility.Utils
import com.checkin.app.checkin.Utility.callPhoneNumber
import com.checkin.app.checkin.Utility.inTransaction
import com.checkin.app.checkin.Utility.pass
import com.checkin.app.checkin.manager.fragments.ManagerNewPreOrderDetailFragment
import com.checkin.app.checkin.manager.fragments.ManagerPreparationPreOrderDetailFragment
import com.checkin.app.checkin.manager.fragments.ManagerUpcomingPreOrderDetailFragment
import com.checkin.app.checkin.manager.models.PreparationTimeModel
import com.checkin.app.checkin.manager.viewmodels.ManagerLivePreOrdersViewModel
import com.checkin.app.checkin.misc.BlockingNetworkViewModel
import com.checkin.app.checkin.misc.activities.BaseActivity
import com.checkin.app.checkin.misc.fragments.NetworkBlockingFragment
import com.checkin.app.checkin.session.models.ScheduledSessionStatus
import java.util.concurrent.TimeUnit

class ManagerScheduledSessionDetailActivity : BaseActivity() {
    @BindView(R.id.tv_manager_scheduled_session_customer_name)
    internal lateinit var tvCustomerName: TextView
    @BindView(R.id.tv_manager_scheduled_session_customer_visits)
    internal lateinit var tvCustomerVisits: TextView
    @BindView(R.id.tv_manager_scheduled_session_order_id)
    internal lateinit var tvOrderId: TextView
    @BindView(R.id.toolbar_manager_scheduled_session_detail)
    internal lateinit var toolbar: Toolbar

    private val networkFragment = NetworkBlockingFragment()
    private lateinit var mainFragment: Fragment
    val networkViewModel: BlockingNetworkViewModel by viewModels()
    val viewModel: ManagerLivePreOrdersViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manager_scheduled_session_detail)
        ButterKnife.bind(this)

        initUI()
        setupObservers()
    }

    private fun initUI() {
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        supportFragmentManager.inTransaction {
            add(android.R.id.content, networkFragment, NetworkBlockingFragment.FRAGMENT_TAG)
        }

        val sessionId = intent.getLongExtra(KEY_SESSION_ID, 0L)
        viewModel.fetchSessionData(sessionId)
    }

    fun setupObservers() {
        viewModel.sessionData.observe(this, Observer {
            it?.let {
                networkViewModel.updateStatus(it)
                when (it.status) {
                    Resource.Status.SUCCESS -> it.data?.let {
                        tvCustomerName.text = it.customer.displayName
                        tvCustomerVisits.text = it.formatVisitFrequency
                        tvOrderId.text = "Order ID: ${it.hashId}"
                        addDetailFragment(it.scheduled.status, it.pk)
                        if (it.scheduled.preparationTime != null) {
                            val diff = Utils.getTimeDifference(it.scheduled.preparationTime, it.scheduled.plannedDatetime).let {
                                TimeUnit.MINUTES.convert(it.second, it.first)
                            }
                            viewModel.preparationTimeData.value = PreparationTimeModel(diff)
                        }
                    }
                    else -> pass
                }
            }
        })
    }

    private fun addDetailFragment(status: ScheduledSessionStatus, pk: Long) {
        mainFragment = when (status) {
            ScheduledSessionStatus.PENDING -> ManagerNewPreOrderDetailFragment()
            ScheduledSessionStatus.PREPARATION -> ManagerPreparationPreOrderDetailFragment()
            ScheduledSessionStatus.ACCEPTED -> ManagerUpcomingPreOrderDetailFragment()
            else -> {
                // Impossible to encounter such a case
                finish()
                return
            }
        }
        supportFragmentManager.inTransaction {
            add(R.id.frg_container_activity, mainFragment, "main")
        }
    }

    @OnClick(R.id.tv_manager_scheduled_call)
    fun onCall() {
        viewModel.sessionData.value?.data?.customer?.phoneNo?.callPhoneNumber(this)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    companion object {
        private const val KEY_SESSION_ID = "manager.scheduled.session_id"

        fun startScheduledSessionDetailActivity(context: Context, sessionId: Long) = Intent(context, ManagerScheduledSessionDetailActivity::class.java).apply {
            putExtra(KEY_SESSION_ID, sessionId)
            context.startActivity(this)
        }
    }
}