package com.checkin.app.checkin.payment.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.WebView
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.navArgs
import butterknife.BindView
import com.checkin.app.checkin.R
import com.checkin.app.checkin.misc.fragments.BaseFragment
import com.checkin.app.checkin.payment.activities.PaymentActivity
import com.checkin.app.checkin.payment.model.PaymentCardModel
import com.checkin.app.checkin.payment.model.PaymentNetBankingModel
import com.checkin.app.checkin.payment.model.PaymentUPIModel
import com.razorpay.BaseRazorpay
import com.razorpay.BaseRazorpay.ValidationListener
import com.razorpay.PaymentResultListener
import com.razorpay.Razorpay
import org.json.JSONObject


class PaymentSubmitFragment : BaseFragment() {

    override val rootLayout = R.layout.fragment_payment_submit

    private val razorpay: Razorpay by lazy {
        Razorpay(activity)
    }

    private val args: PaymentSubmitFragmentArgs by navArgs()

    @BindView(R.id.wv_payment_submit)
    internal lateinit var webview: WebView


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val callback = activity?.onBackPressedDispatcher?.addCallback {
            //TODO actually nothing to do or figure out a way to disable back click option
        }
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)
        (activity as AppCompatActivity).supportActionBar?.setDisplayShowHomeEnabled(false)

        razorpay.setWebView(webview)
        initRazorPayCallBacks()
        startPayment()

    }

    private fun initRazorPayCallBacks() {
        razorpay.getPaymentMethods(object : BaseRazorpay.PaymentMethodsCallback {
            override fun onPaymentMethodsReceived(result: String) {
                Log.i(TAG, result)
            }

            override fun onError(error: String) {
                Log.d("Get Payment error", error)
                val returnIntent = Intent()
                activity?.setResult(PaymentActivity.PAYMENT_FAILED, returnIntent)
                activity?.finish()
            }
        })
    }

    private fun startPayment() {
        val amount = processRazorPayAmount(args.amount)
        val payload = createPayLoad(args.type, amount)
        sendRequest(payload)
    }

    private fun createPayLoad(type: PAYMENT_TYPE, amount: Int): JSONObject {
        val payload = JSONObject().apply {
            put("currency", args.data.currency)
            put("contact", args.data.contact)
            put("email", args.data.email)
            put("amount", amount)
            put("method", args.data.method)
        }


        when (type) {
            PAYMENT_TYPE.CARD -> {
                val model = args.data as PaymentCardModel
                payload.apply {
                    put("card[name]", model.name)
                    put("card[number]", model.number)
                    put("card[expiry_month]", model.expiryMonth)
                    put("card[expiry_year]", model.expiryYear)
                    put("card[cvv]", model.cvv)
                }
                Log.d(TAG, "$payload")
            }
            PAYMENT_TYPE.WALLET -> {
                //TODO(1) have to implement paytm google pay and phone pay wallet

            }
            PAYMENT_TYPE.UPI -> {
                val model = args.data as PaymentUPIModel
                payload.apply {
                    put("vpa", model.vpa)
                }
            }
            PAYMENT_TYPE.NET_BANKING -> {
                val model = args.data as PaymentNetBankingModel
                payload.apply {
                    put("bank", model.bank)
                }
            }
        }
        return payload
    }

    private fun sendRequest(payload: JSONObject) {
        razorpay.validateFields(payload, object : ValidationListener {
            override fun onValidationSuccess() {
                try {
                    webview.visibility = View.VISIBLE
                    razorpay.submit(payload, object : PaymentResultListener {
                        override fun onPaymentError(p0: Int, p1: String?) {
                            Toast.makeText(context, "Payment Failed $p1", Toast.LENGTH_SHORT).show()
                            val returnIntent = Intent()
                            activity?.setResult(PaymentActivity.PAYMENT_FAILED, returnIntent)
                            activity?.finish()
                        }

                        override fun onPaymentSuccess(p0: String?) {
                            Toast.makeText(context, "Payment Successful", Toast.LENGTH_SHORT).show()
                            val returnIntent = Intent()
                            activity?.setResult(PaymentActivity.PAYMENT_SUCESSFULL, returnIntent)
                            activity?.finish()

                        }
                    })
                } catch (e: Exception) {
                    Log.e(TAG, "Exception: ", e)
                }
            }

            override fun onValidationError(error: Map<String, String>) {
                Log.d(TAG, "Validation failed: " + error["field"] + " " + error["description"])
                Toast.makeText(context, "Validation: " + error["field"] + " " + error["description"], Toast.LENGTH_SHORT).show()
            }
        })

    }


    enum class PAYMENT_TYPE {
        CARD, WALLET, UPI, NET_BANKING
    }

    companion object {
        var TAG: String? = PaymentSelectFragment::class.simpleName
    }

    private fun processRazorPayAmount(amount: Float): Int = (amount * 100).toInt()
}