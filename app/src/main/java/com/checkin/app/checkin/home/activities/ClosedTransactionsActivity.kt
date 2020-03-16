package com.checkin.app.checkin.home.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import butterknife.BindView
import butterknife.ButterKnife
import com.airbnb.epoxy.EpoxyRecyclerView
import com.checkin.app.checkin.R
import com.checkin.app.checkin.data.resource.Resource
import com.checkin.app.checkin.home.epoxy.closedTransactionModelHolder
import com.checkin.app.checkin.home.viewmodels.ClosedSessionViewModel
import com.checkin.app.checkin.misc.activities.BaseActivity

class ClosedTransactionsActivity : BaseActivity() {
    @BindView(R.id.epoxy_closed_transaction)
    internal lateinit var epoxyTransactionDetails: EpoxyRecyclerView

    val viewModel: ClosedSessionViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_closed_transaction)
        ButterKnife.bind(this)

        initUI()
        setupObservers()
    }

    private fun initUI() {
        initRefreshScreen(R.id.sr_closed_sessions)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
        }

        epoxyTransactionDetails.withModels {
            viewModel.customerClosedList.value?.data?.forEachIndexed { index, item ->
                closedTransactionModelHolder {
                    id(index)
                    data(item)
                }
            }
        }
    }

    private fun setupObservers() {
        viewModel.customerClosedList.observe(this, Observer {
            it?.let { resource ->
                handleLoadingRefresh(resource)
                when (resource.status) {
                    Resource.Status.SUCCESS -> {
                        epoxyTransactionDetails.requestModelBuild()
                    }
                }
            }
        })
        viewModel.fetchClosedSessions()
    }

    override fun updateScreen() {
        super.updateScreen()
        viewModel.fetchClosedSessions()
    }

    override fun onResume() {
        super.onResume()
        viewModel.fetchMissing()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    companion object {
        fun withIntent(context: Context) = Intent(context, ClosedTransactionsActivity::class.java)
    }
}
