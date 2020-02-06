package com.checkin.app.checkin.payment.fragments


import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.airbnb.epoxy.EpoxyRecyclerView
import com.checkin.app.checkin.R
import com.checkin.app.checkin.misc.fragments.BaseFragment
import com.checkin.app.checkin.payment.holders.*
import com.checkin.app.checkin.payment.model.PaymentCardModel
import com.checkin.app.checkin.payment.model.PaymentNetBankingModel
import com.checkin.app.checkin.payment.model.PaymentUPIModel


class PaymentSelectFragment : BaseFragment() {


    override val rootLayout: Int = R.layout.fragment_payment_select

    private lateinit var epoxyRecyclerView: EpoxyRecyclerView

    private val args: PaymentSelectFragmentArgs by navArgs()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        epoxyRecyclerView = view as EpoxyRecyclerView
        val amount = args.amount

        var selectedItem: Int = 1

        val cards = arrayOf(
                PaymentCardModel(
                        "jvns67@gmai.com",
                        "+918073298546",
                        "Jeevan",
                        "4111111111111111",
                        "12",
                        "20",
                        "100"
                ),
                PaymentCardModel(
                        "jvns67@gmai.com",
                        "+918073298546",
                        "GauravKumar",
                        "4111111111111111",
                        "12",
                        "20",
                        "100"
                ),
                PaymentCardModel(
                        "jvns67@gmai.com",
                        "+918073298546",
                        "GauravKumar",
                        "4111111111111111",
                        "12",
                        "20",
                        "100"
                ),
                PaymentCardModel(
                        "jvns67@gmai.com",
                        "+918073298546",
                        "GauravKumar",
                        "4111111111111111",
                        "12",
                        "20",
                        "100"
                )

        )


        val upis = arrayOf(
                PaymentUPIModel(
                        "jvns67@gmai.com",
                        "+918073298546",
                        "jvns67@oksbi"),
                PaymentUPIModel(
                        "jvns67@gmai.com",
                        "+918073298546",
                        "jvns67@oksbi"),
                PaymentUPIModel(
                        "jvns67@gmai.com",
                        "+918073298546",
                        "jvns67@oksbi"),
                PaymentUPIModel(
                        "jvns67@gmai.com",
                        "+918073298546",
                        "jvns67@oksbi")
        )

        epoxyRecyclerView.withModels {

            //Recently Used
            paymentHeader {
                id("header")
                header("Recently Used")
            }


            paymentItem {
                val id = 1
                val model = cards[id]
                id(id)
                showPay(id == selectedItem)
                type(PaymentItemModel.PaymentType.CARD)
                image(model.card.image)
                content(model.number)
                payListener { _ ->
                    val action = PaymentSelectFragmentDirections
                            .actionPaymentSelectFragmentToPaymentSubmitFragment(model, amount, PaymentSubmitFragment.PAYMENT_TYPE.CARD)
                    findNavController().navigate(action)
                }
                selectListener { v ->
                    selectedItem = id
                    requestModelBuild()

                }
            }

            paymentItem {
                val id = 2
                id(id)
                showPay(id == selectedItem)
                content("Google pay")
                image(R.drawable.ic_payment_gpay)
                selectListener { v ->
                    selectedItem = id
                    requestModelBuild()

                }

            }

            //Debit and Credit Card
            paymentHeader {
                id("header2")
                header("Debit/Credit Card")
            }
            cards.forEachIndexed { index, model ->
                paymentItem {
                    val id = index + 10
                    id(id)
                    showPay(id == selectedItem)
                    selectListener { v ->
                        selectedItem = id
                        requestModelBuild()

                    }
                    type(PaymentItemModel.PaymentType.CARD)
                    image(model.card.image)
                    content(model.number)
                    payListener { _ ->
                        val action = PaymentSelectFragmentDirections
                                .actionPaymentSelectFragmentToPaymentSubmitFragment(model, amount, PaymentSubmitFragment.PAYMENT_TYPE.CARD)
                        findNavController().navigate(action)
                    }
                }

            }
            paymentAddCard {
                id("add card")
                addListener { _ ->
                    val action = PaymentSelectFragmentDirections.actionPaymentSelectFragmentToPaymentCardFragment()
                    findNavController().navigate(action)
                }
            }

            //Wallet
            paymentHeader {
                id("header3")
                header("Wallet")
            }
            paymentItem {
                val id = 20
                id(id)
                image(R.drawable.ic_payment_paytm)
                content("PayTm")
                type(PaymentItemModel.PaymentType.WALLET)
                showPay(id == selectedItem)
                selectListener { v ->
                    selectedItem = id
                    requestModelBuild()

                }
            }
            paymentItem {
                val id = 21
                id(id)
                image(R.drawable.ic_payment_gpay)
                type(PaymentItemModel.PaymentType.WALLET)
                content("Google Pay")
                showPay(id == selectedItem)
                selectListener { v ->
                    selectedItem = id
                    requestModelBuild()

                }
            }
            paymentItem {
                val id = 22
                id(id)
                image(R.drawable.ic_payment_phonepe)
                content("PhonePe")
                type(PaymentItemModel.PaymentType.WALLET)
                showPay(id == selectedItem)
                selectListener { v ->
                    selectedItem = id
                    requestModelBuild()

                }
            }

            //UPI ID
            paymentHeader {
                id("header4")
                header("UPI ID")
            }
            upis.forEachIndexed { index, model ->
                paymentItem {
                    val id = 30 + index
                    id(id)
                    showPay(id == selectedItem)
                    selectListener { v ->
                        selectedItem = id
                        requestModelBuild()

                    }
                    content(model.vpa)
                    type(PaymentItemModel.PaymentType.UPI)
                    payListener { _ ->
                        val action = PaymentSelectFragmentDirections.actionPaymentSelectFragmentToPaymentSubmitFragment(
                                model, amount, PaymentSubmitFragment.PAYMENT_TYPE.UPI
                        )
                        findNavController().navigate(action)
                    }

                }
            }

            paymentAddItem {
                id("Add UPI")
                content("ADD UPI ID")
                addListener { _ ->
                    val action = PaymentSelectFragmentDirections.actionPaymentSelectFragmentToPaymentUpiFragment()
                    findNavController().navigate(action)
                }
            }

            paymentHeader {
                id("Add NetBanking")
                header("NET Banking")
            }
            paymentBanking {
                id(50)
                listener { view ->
                    when (view.id) {
                        R.id.im_payment_netbanking_sbi -> {
                            val modelNetBanking = PaymentNetBankingModel(
                                    "jvns67@gmai.com",
                                    "+918073298546",
                                    "SBIN")
                            val action = PaymentSelectFragmentDirections.actionPaymentSelectFragmentToPaymentSubmitFragment(
                                    modelNetBanking, amount, PaymentSubmitFragment.PAYMENT_TYPE.NET_BANKING
                            )
                            findNavController().navigate(action)
                        }
                        R.id.im_payment_netbanking_axis -> {
                            val modelNetBanking = PaymentNetBankingModel(
                                    "jvns67@gmai.com",
                                    "+918073298546",
                                    "UTIB")
                            val action = PaymentSelectFragmentDirections.actionPaymentSelectFragmentToPaymentSubmitFragment(
                                    modelNetBanking, amount, PaymentSubmitFragment.PAYMENT_TYPE.NET_BANKING
                            )
                            findNavController().navigate(action)
                        }
                        R.id.im_payment_netbanking_citi -> {
                            val modelNetBanking = PaymentNetBankingModel(
                                    "jvns67@gmail.com",
                                    "+918073298546",
                                    "SBIN") //TODO make it HDFC but first find a logo
                            val action = PaymentSelectFragmentDirections.actionPaymentSelectFragmentToPaymentSubmitFragment(
                                    modelNetBanking, amount, PaymentSubmitFragment.PAYMENT_TYPE.NET_BANKING
                            )
                            findNavController().navigate(action)
                        }
                        R.id.im_payment_netbanking_kotak -> {
                            val modelNetBanking = PaymentNetBankingModel(
                                    "jvns67@gmai.com",
                                    "+918073298546",
                                    "KKBK")
                            val action = PaymentSelectFragmentDirections.actionPaymentSelectFragmentToPaymentSubmitFragment(
                                    modelNetBanking, amount, PaymentSubmitFragment.PAYMENT_TYPE.NET_BANKING
                            )
                            findNavController().navigate(action)
                        }
                    }

                }
            }
            paymentAddItem {
                id(37)
                content("OTHER BANKS")
                addListener { v ->
                    Toast.makeText(context, "Yet To Add", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


}