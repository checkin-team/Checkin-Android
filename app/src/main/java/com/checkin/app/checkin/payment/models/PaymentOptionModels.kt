package com.checkin.app.checkin.payment.models

import android.content.pm.ActivityInfo
import android.graphics.drawable.Drawable
import androidx.annotation.DrawableRes
import com.checkin.app.checkin.R
import com.checkin.app.checkin.utility.EnumConverter
import com.checkin.app.checkin.utility.EnumStringGetter
import com.checkin.app.checkin.utility.EnumStringType
import io.objectbox.annotation.*
import java.io.Serializable
import java.util.*

sealed class PaymentOptionModel(val type: PAYMENT_TYPE) : Serializable {
    abstract var id: Long
    var lastUsed: Date? = null
}

@Entity
data class CardPaymentOptionModel(
        val cardNumber: String,
        val expiryMonth: String,
        val expiryYear: String,
        val cvv: String,
        val bankCode: String,
        @Convert(converter = CARD_PROVIDER.Companion.Converter::class, dbType = String::class)
        val channel: CARD_PROVIDER,
        @Convert(converter = CARD_TYPE.Companion.TypeConverter::class, dbType = String::class)
        val cardType: CARD_TYPE
) : PaymentOptionModel(PAYMENT_TYPE.CARD) {
    @Id
    override var id: Long = 0

    val formatNumber: String = cardNumber

    enum class CARD_PROVIDER(override val value: String, @DrawableRes val drawable: Int) : EnumStringType {
        VISA("visa", R.drawable.ic_payment_visa),
        MASTER_CARD("mastercard", R.drawable.ic_payment_mastercard),
        AMEX("amex", R.drawable.ic_payment_amex),
        MAESTRO("maestro", R.drawable.ic_payment_maestro),
        RUPAY("rupay", R.drawable.ic_payment_rupay);

        companion object : EnumStringGetter<CARD_PROVIDER>() {
            override fun getByValue(value: String): CARD_PROVIDER = EnumStringType.getByValue(value)

            class Converter : EnumConverter<CARD_PROVIDER, String>(this)
        }
    }

    enum class CARD_TYPE(override val value: String) : EnumStringType {
        DEBIT("DEBIT_CARD"), CREDIT("CREDIT_CARD");

        companion object : EnumStringGetter<CARD_TYPE>() {
            override fun getByValue(value: String): CARD_TYPE = EnumStringType.getByValue(value)

            class TypeConverter : EnumConverter<CARD_TYPE, String>(this)
        }
    }
}

sealed class UPIPaymentOptionModel : PaymentOptionModel(PAYMENT_TYPE.UPI)

@Entity
data class UPIPushPaymentOptionModel(
        @Unique val appName: String,
        @Transient val iconDrawable: Drawable,
        @Transient val activityInfo: ActivityInfo
) : UPIPaymentOptionModel() {
    @Id
    override var id: Long = 0
}

@Entity
data class UPICollectPaymentOptionModel(
        @Unique val vpa: String
) : UPIPaymentOptionModel() {
    @Id
    override var id: Long = 0
}

@Entity
data class NetBankingPaymentOptionModel(
        @Unique val bankCode: String,
        val bankName: String,
        val iconUrl: String
) : PaymentOptionModel(PAYMENT_TYPE.NET_BANKING) {
    @Id
    override var id: Long = 0
}

enum class PAYMENT_TYPE {
    CARD, WALLET, UPI, NET_BANKING
}