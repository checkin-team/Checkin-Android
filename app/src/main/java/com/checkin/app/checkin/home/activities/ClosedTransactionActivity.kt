package com.checkin.app.checkin.home.activities


import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import butterknife.BindView
import butterknife.ButterKnife
import com.airbnb.epoxy.EpoxyRecyclerView
import com.checkin.app.checkin.R
import com.checkin.app.checkin.data.resource.Resource
import com.checkin.app.checkin.home.epoxy.closedTransactionModelHolder
import com.checkin.app.checkin.home.viewmodels.ClosedSessionViewModel
import com.checkin.app.checkin.misc.activities.BaseActivity


class ClosedTransactionActivity : BaseActivity() {

    @BindView(R.id.epoxy_closed_transaction)
    internal lateinit var epoxyTransactionDetails: EpoxyRecyclerView
    @BindView(R.id.toolbar_closed)
    internal lateinit var toolbar: Toolbar

    val viewModel: ClosedSessionViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_closed_transaction)
        ButterKnife.bind(this)

        initUI()
        setupObservers()
    }

    private fun initUI() {

        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            title = null
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
            when (it.status) {
                Resource.Status.SUCCESS -> {
                    epoxyTransactionDetails.requestModelBuild()
                    stopRefreshing()
                }

            }
        })

        viewModel.fetchCustomerTransaction()

    }

    companion object {
        fun withIntent(context: Context) = Intent(context, ClosedTransactionActivity::class.java)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
