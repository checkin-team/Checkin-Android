package com.checkin.app.checkin.payment.controllers

import com.checkin.app.checkin.misc.epoxy.BaseEpoxyController
import com.checkin.app.checkin.misc.holders.textSectionModelHolder
import com.checkin.app.checkin.payment.holders.PaymentNetBankingModelHolder_
import com.checkin.app.checkin.payment.holders.PaymentOptionInteraction
import com.checkin.app.checkin.payment.holders.paymentAddOptionModelHolder
import com.checkin.app.checkin.payment.holders.paymentOptionModelHolder
import com.checkin.app.checkin.payment.listeners.PaymentOptionSelectListener
import com.checkin.app.checkin.payment.models.*
import com.checkin.app.checkin.utility.carousel
import com.checkin.app.checkin.utility.isNotEmpty

class PaymentOptionsController(val listener: PaymentOptionSelectListener) : BaseEpoxyController(), PaymentOptionInteraction {
    private var selectedOptionID: Long = 0
        set(value) {
            field = value
            requestModelBuild()
        }

    var lastUsedOptions: List<PaymentOptionModel>? = null
        set(value) {
            field = value
            requestModelBuild()
        }

    var cardOptions: List<CardPaymentOptionModel>? = null
        set(value) {
            field = value
            requestModelBuild()
        }

    var upiOptions: List<UPIPaymentOptionModel>? = null
        set(value) {
            field = value
            requestModelBuild()
        }

    var netBankingOptions: List<NetBankingPaymentOptionModel>? = null
        set(value) {
            field = value
            requestModelBuild()
        }

    override fun buildModels() {
        // SECTION: Recently Used
        lastUsedOptions.takeIf { it.isNotEmpty() }?.also {
            textSectionModelHolder {
                withBackgroundLayout()
                        .id("recently used")
                        .heading("Recently Used")
            }
            it.forEachIndexed { index, data ->
                paymentOptionModelHolder {
                    id("recently used", index.toLong())
                    optionData(data)
                    selectedId(selectedOptionID)
                    selectListener(this@PaymentOptionsController)
                    listener(this@PaymentOptionsController.listener)
                }
            }
        }

        // SECTION: Cards
        textSectionModelHolder {
            withBackgroundLayout()
                    .id("card")
                    .heading("Debit/Credit Cards")
        }
        cardOptions.takeIf { it.isNotEmpty() }?.also {
            it.forEachIndexed { index, data ->
                paymentOptionModelHolder {
                    id("card", index.toLong())
                    optionData(data)
                    selectedId(selectedOptionID)
                    selectListener(this@PaymentOptionsController)
                    listener(this@PaymentOptionsController.listener)
                }
            }
        }
        paymentAddOptionModelHolder {
            withCardLayout()
                    .id("card add")
                    .content("Add Card")
                    .optionType(PAYMENT_TYPE.CARD)
                    .listener(this@PaymentOptionsController.listener)
        }

        // SECTION: UPI
        textSectionModelHolder {
            withBackgroundLayout()
                    .id("upi")
                    .heading("UPI Options")
        }
        upiOptions.takeIf { it.isNotEmpty() }?.also {
            it.forEachIndexed { index, data ->
                paymentOptionModelHolder {
                    id("upi ${if (data is UPIPushPaymentOptionModel) "app" else "id"}", index.toLong())
                    optionData(data)
                    selectedId(selectedOptionID)
                    selectListener(this@PaymentOptionsController)
                    listener(this@PaymentOptionsController.listener)
                }
            }
        }
        paymentAddOptionModelHolder {
            id("upi id add")
            content("Add UPI ID")
            optionType(PAYMENT_TYPE.UPI)
            listener(this@PaymentOptionsController.listener)
        }

        // SECTION: NetBanking
        netBankingOptions.takeIf { it.isNotEmpty() }?.also {
            val models = it.mapIndexed { index, data ->
                PaymentNetBankingModelHolder_().apply {
                    id("netbanking", index.toLong())
                    data(data)
                    listener(this@PaymentOptionsController.listener)
                }
            }
            carousel {
                id("netbanking")
                models(models)
            }
        }
        paymentAddOptionModelHolder {
            id("netbanking add")
            content("Other Bank")
            optionType(PAYMENT_TYPE.NET_BANKING)
            listener(this@PaymentOptionsController.listener)
        }
    }

    override fun onSelectOption(id: Long) {
        if (selectedOptionID != id)
            selectedOptionID = id
    }
}