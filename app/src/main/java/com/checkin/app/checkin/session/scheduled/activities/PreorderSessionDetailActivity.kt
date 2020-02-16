package com.checkin.app.checkin.session.scheduled.activities

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.checkin.app.checkin.R
import com.checkin.app.checkin.data.notifications.MESSAGE_TYPE
import com.checkin.app.checkin.data.notifications.MessageUtils
import com.checkin.app.checkin.data.resource.Resource
import com.checkin.app.checkin.misc.BlockingNetworkViewModel
import com.checkin.app.checkin.misc.activities.BaseActivity
import com.checkin.app.checkin.misc.fragments.NetworkBlockingFragment
import com.checkin.app.checkin.session.models.CustomerScheduledSessionDetailModel
import com.checkin.app.checkin.session.models.ScheduledSessionStatus
import com.checkin.app.checkin.session.scheduled.fragments.PreorderDetailFragment
import com.checkin.app.checkin.session.scheduled.viewmodels.ScheduledSessionDetailViewModel
import com.checkin.app.checkin.utility.Utils
import com.checkin.app.checkin.utility.inTransaction
import com.checkin.app.checkin.utility.navigateBackToHome

class PreorderSessionDetailActivity : BaseActivity() {
    @BindView(R.id.toolbar_user_scheduled_session_detail)
    internal lateinit var toolbar: Toolbar
    @BindView(R.id.tv_scheduled_session_header_restaurant)
    internal lateinit var tvRestaurantName: TextView
    @BindView(R.id.tv_scheduled_session_header_address)
    internal lateinit var tvAddress: TextView

    private val networkFragment = NetworkBlockingFragment()
    private val mainFragment = PreorderDetailFragment()

    val networkViewModel: BlockingNetworkViewModel by viewModels()
    val viewModel: ScheduledSessionDetailViewModel by viewModels()

    private val receiver by lazy {
        object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent) {
                val message = MessageUtils.parseMessage(intent) ?: return
                if (message.sessionDetail?.pk == viewModel.sessionId) return
                when (message.type) {
                    MESSAGE_TYPE.USER_SCHEDULED_CBYG_ACCEPTED -> viewModel.updateStatus(ScheduledSessionStatus.ACCEPTED)
                    MESSAGE_TYPE.USER_SCHEDULED_CBYG_PREPARATION -> viewModel.updateStatus(ScheduledSessionStatus.PREPARATION)
                    MESSAGE_TYPE.USER_SCHEDULED_CBYG_CANCELLED, MESSAGE_TYPE.USER_SCHEDULED_CBYG_CHECKOUT -> {
                        Utils.toast(this@PreorderSessionDetailActivity, "Session ended!")
                        finish()
                    }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scheduled_session_detail)
        ButterKnife.bind(this)

        initUI()
    }

    private fun initUI() {
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        supportFragmentManager.inTransaction {
            add(android.R.id.content, networkFragment, NetworkBlockingFragment.FRAGMENT_TAG)
        }
        supportFragmentManager.inTransaction {
            add(R.id.frg_container_activity, mainFragment, "main")
        }

        val sessionId = intent.getLongExtra(KEY_SESSION_ID, 0L)
        viewModel.fetchSessionData(sessionId)

        viewModel.sessionData.observe(this, Observer {
            networkViewModel.updateStatus(it)
            if (it.status == Resource.Status.SUCCESS && it.data != null) setupHeader(it.data)
            else if (it.status == Resource.Status.ERROR_NOT_FOUND) finish()
        })
        viewModel.cancelData.observe(this, Observer {
            if (it.status == Resource.Status.SUCCESS) finish()
        })
    }

    private fun setupHeader(data: CustomerScheduledSessionDetailModel) {
        tvRestaurantName.text = data.restaurant.name
        tvAddress.text = data.restaurant.formatAddress
    }

    @OnClick(R.id.im_scheduled_session_header_share)
    fun onShare() {
        Utils.toast(this, "TODO: SHare!")
    }

    override fun onSupportNavigateUp(): Boolean {
        navigateBackToHome()
        return true
    }

    override fun onResume() {
        super.onResume()
        MessageUtils.registerLocalReceiver(
                this, receiver,
                MESSAGE_TYPE.USER_SCHEDULED_CBYG_ACCEPTED, MESSAGE_TYPE.USER_SCHEDULED_CBYG_PREPARATION,
                MESSAGE_TYPE.USER_SCHEDULED_CBYG_CHECKOUT, MESSAGE_TYPE.USER_SCHEDULED_CBYG_CANCELLED
        )
        viewModel.fetchMissing()
    }

    override fun onPause() {
        super.onPause()
        MessageUtils.unregisterLocalReceiver(this, receiver)
    }

    companion object {
        private const val KEY_SESSION_ID = "session_detail.preorder.id"

        fun withSessionIntent(context: Context, sessionId: Long) = Intent(context, PreorderSessionDetailActivity::class.java).apply {
            putExtra(KEY_SESSION_ID, sessionId)
        }
    }
}