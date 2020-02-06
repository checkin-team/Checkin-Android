package com.checkin.app.checkin.payment.model

import androidx.annotation.DrawableRes
import com.checkin.app.checkin.R
import java.io.Serializable

sealed class PaymentModel(open val email: String,
                          open val contact: String,
                          open val method: String) : Serializable {
    val currency: String = "INR"
}

data class PaymentCardModel(
        override val email: String,
        override val contact: String,
        val name: String,
        val number: String,
        val expiryMonth: String,
        val expiryYear: String,
        val cvv: String
) : PaymentModel(email, contact, method = "card") {
    val card = CardProvider.MASTER_CARD

    enum class CardProvider(@DrawableRes val image: Int) {
        VISA(R.drawable.ic_payment_visa),
        MASTER_CARD(R.drawable.ic_payment_mastercard),
        AMEX(R.drawable.ic_payment_amex),
        BAJAJ(R.drawable.ic_payment_mastercard),
        DICL(R.drawable.ic_payment_mastercard),
        MAESTRO(R.drawable.ic_payment_maestro),
        RUPAY(R.drawable.ic_payment_rupay);

    }
}

data class PaymentUPIModel(override val email: String,
                           override val contact: String,
                           val vpa: String) : PaymentModel(email, contact, method = "upi") {
}

data class PaymentNetBankingModel(override val email: String,
                                  override val contact: String,
                                  val bank: String) : PaymentModel(email, contact, method = "netbanking")