package com.checkin.app.checkin.payment.models

import androidx.annotation.DrawableRes
import com.checkin.app.checkin.R
import com.checkin.app.checkin.utility.EnumConverter
import com.checkin.app.checkin.utility.EnumStringGetter
import com.checkin.app.checkin.utility.EnumStringType
import io.objectbox.annotation.Convert
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.annotation.Unique
import java.io.Serializable
import java.util.*

sealed class PaymentOptionModel(val type: PAYMENT_TYPE) : Serializable {
    abstract var id: Long
    var lastUsed: Date? = null
}

@Entity
data class CardPaymentOptionModel(
        val name: String,
        @Unique val cardNumber: String,
        val expiryMonth: String,
        val expiryYear: String,
        val cvv: String,
        var bankCode: String? = null,
        @Convert(converter = CARD_PROVIDER.Companion.Converter::class, dbType = String::class)
        var channel: CARD_PROVIDER? = null,
        @Convert(converter = CARD_TYPE.Companion.TypeConverter::class, dbType = String::class)
        var cardType: CARD_TYPE? = null
) : PaymentOptionModel(PAYMENT_TYPE.CARD) {
    @Id
    override var id: Long = 0

    val formatNumber: String = cardNumber

    enum class CARD_PROVIDER(override val value: String, @DrawableRes val drawable: Int) : EnumStringType {
        VISA("visa", R.drawable.ic_payment_card_visa),
        MASTER_CARD("mastercard", R.drawable.ic_payment_card_mastercard),
        AMEX("amex", R.drawable.ic_payment_card_amex),
        MAESTRO("maestro", R.drawable.ic_payment_card_maestro),
        RUPAY("rupay", R.drawable.ic_payment_card_rupay);

        companion object : EnumStringGetter<CARD_PROVIDER>() {
            override fun getByValue(value: String?): CARD_PROVIDER? = value?.let { EnumStringType.getByValue(it) }

            class Converter : EnumConverter<CARD_PROVIDER, String>(this)
        }
    }

    enum class CARD_TYPE(override val value: String) : EnumStringType {
        DEBIT("DEBIT_CARD"), CREDIT("CREDIT_CARD");

        companion object : EnumStringGetter<CARD_TYPE>() {
            override fun getByValue(value: String?): CARD_TYPE? = value?.let { EnumStringType.getByValue(it) }

            class TypeConverter : EnumConverter<CARD_TYPE, String>(this)
        }
    }
}

sealed class UPIPaymentOptionModel : PaymentOptionModel(PAYMENT_TYPE.UPI)

@Entity
data class UPIPushPaymentOptionModel(
        @Unique val appName: String,
        val iconUrl: String,
        val packageName: String
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
        @DrawableRes val iconRes: Int = 0
) : PaymentOptionModel(PAYMENT_TYPE.NET_BANKING) {
    @Id
    override var id: Long = 0
}

enum class PAYMENT_TYPE {
    CARD, WALLET, UPI, NET_BANKING
}