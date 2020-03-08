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
import com.checkin.app.checkin.home.epoxy.pastTransactionModelHolder
import com.checkin.app.checkin.home.viewmodels.PastTransactionViewModel
import com.checkin.app.checkin.misc.activities.BaseActivity


class PaymentTransactionActivity : BaseActivity() {

    @BindView(R.id.epoxy_transaction_details)
    internal lateinit var epoxyTransactionDetails: EpoxyRecyclerView

    val model: PastTransactionViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment_transaction)
        ButterKnife.bind(this)
        initRefreshScreen(R.id.sr_transaction)

        val toolbar: Toolbar = findViewById(R.id.toolbar_transaction)

        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            title = null
            setDisplayHomeAsUpEnabled(true)
        }


        epoxyTransactionDetails.withModels {
            model.customerPastTransaction.value?.data?.forEachIndexed { index, item ->
                pastTransactionModelHolder {
                    id(index)
                    data(item)
                }
            }
        }

        setupObservers()
    }


    private fun setupObservers() {
        model.customerPastTransaction.observe(this, Observer {
            when (it.status) {
                Resource.Status.SUCCESS -> {
                    epoxyTransactionDetails.requestModelBuild()
                    stopRefreshing()
                }

            }
        })

        model.fetchCustomerTransaction()

    }

    companion object {
        fun withIntent(context: Context) = Intent(context, PaymentTransactionActivity::class.java)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    override fun updateScreen() {
        super.updateScreen()
        model.updateResults()
    }
}
