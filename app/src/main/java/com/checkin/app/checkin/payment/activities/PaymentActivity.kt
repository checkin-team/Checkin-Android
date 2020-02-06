package com.checkin.app.checkin.payment.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import butterknife.BindView
import butterknife.ButterKnife
import com.checkin.app.checkin.R
import com.checkin.app.checkin.misc.activities.BaseActivity


class PaymentActivity : BaseActivity() {

    @BindView(R.id.tv_payment_toolbar_amount)
    internal lateinit var tvAmountToolbar: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)
        ButterKnife.bind(this)
        val toolbar: Toolbar = findViewById(R.id.toolbar_payment)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        val amount = intent.getFloatExtra(paymentAmountKey, 0F)

        tvAmountToolbar.text = amount.toString()

        if (amount == 0f) {
            val returnIntent = Intent()
            setResult(PAYMENT_AMOUNT_ZERO, returnIntent)
            finish()
        }

        var bundle = bundleOf("amount" to amount)

        findNavController(R.id.nav_host_fragment).setGraph(R.navigation.nav_graph, bundle)
    }

    override fun onBackPressed() {
        val returnIntent = Intent()
        setResult(PAYMENT_CANCELLED, returnIntent)
        finish()
    }

    companion object {
        const val PAYMENT_SUCESSFULL = 1
        const val PAYMENT_AMOUNT_ZERO = 2
        const val PAYMENT_CANCELLED = 3
        const val PAYMENT_FAILED = 4

        const val paymentAmountKey = "payment.amount.key"

        fun startPaymentIntent(context: Context, amount: Float): Intent = Intent(context, PaymentActivity::class.java).apply {
            putExtra(paymentAmountKey, amount)
        }
    }
}