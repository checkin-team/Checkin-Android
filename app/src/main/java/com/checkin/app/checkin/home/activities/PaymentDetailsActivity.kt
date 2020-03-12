package com.checkin.app.checkin.home.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import butterknife.BindView
import butterknife.ButterKnife
import com.checkin.app.checkin.R
import com.checkin.app.checkin.data.resource.Resource
import com.checkin.app.checkin.home.fragments.PaymentDetailsFragment
import com.checkin.app.checkin.home.model.PastSessionDetailsModel
import com.checkin.app.checkin.home.viewmodels.PaymentDetailsViewModel
import com.checkin.app.checkin.misc.BlockingNetworkViewModel
import com.checkin.app.checkin.misc.activities.BaseActivity
import com.checkin.app.checkin.misc.fragments.NetworkBlockingFragment
import com.checkin.app.checkin.utility.inTransaction
import com.checkin.app.checkin.utility.navigateBackToHome

class PaymentDetailsActivity : BaseActivity() {
    @BindView(R.id.toolbar_payment_details)
    internal lateinit var toolbar: Toolbar
    @BindView(R.id.tv_payment_details_header_restaurant)
    internal lateinit var tvRestaurantName: TextView
    @BindView(R.id.tv_payment_details_header_address)
    internal lateinit var tvAddress: TextView

    private val networkFragment = NetworkBlockingFragment()
    private val mainFragment = PaymentDetailsFragment()

    val networkViewModel: BlockingNetworkViewModel by viewModels()
    val viewModel: PaymentDetailsViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment_details)
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
    }

    override fun onSupportNavigateUp(): Boolean {
        navigateBackToHome()
        return true
    }

    private fun setupHeader(data: PastSessionDetailsModel) {
        tvRestaurantName.text = data.restaurant.name
        tvAddress.text = data.restaurant.formatAddress
    }

    companion object {
        private const val KEY_SESSION_ID = "payment.details.session.id"

        fun withSessionIntent(context: Context, sessionId: Long) = Intent(context, PaymentDetailsActivity::class.java).apply {
            putExtra(KEY_SESSION_ID, sessionId)
        }
    }


}
