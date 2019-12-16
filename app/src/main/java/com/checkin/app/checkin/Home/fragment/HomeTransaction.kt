package com.checkin.app.checkin.Home.fragment

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.checkin.app.checkin.Misc.BillHolder

import com.checkin.app.checkin.R
import com.checkin.app.checkin.session.activesession.InvoiceOrdersAdapter
import com.checkin.app.checkin.session.model.SessionBillModel


class HomeTransaction : Fragment() {
    private var listener: OnFragmentInteractionListener? = null
    @BindView(R.id.rv_user_transaction_session_orders)
    internal lateinit var rvSessionOrders:RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val v=inflater.inflate(R.layout.fragment_home_transaction, container, false);
        ButterKnife.bind(this,v)

        var mOrdersAdapter = InvoiceOrdersAdapter(null, null)
        rvSessionOrders.setLayoutManager(LinearLayoutManager(context, RecyclerView.VERTICAL, false))
        rvSessionOrders.setAdapter(mOrdersAdapter)
        return v;
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var billholder=BillHolder(view);
        var sessionBillModel=SessionBillModel();
        sessionBillModel.discount=12.6;
        sessionBillModel.discountPercentage=12.7
        sessionBillModel.brownie=13.3
        sessionBillModel.promo="Trail"
        billholder.bind(sessionBillModel)



    }
    fun onButtonPressed(uri: Uri) {
        listener?.onFragmentInteraction(uri)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }
 interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }

    companion object {
       @JvmStatic
        fun newInstance() =
                HomeTransaction().apply {
                    arguments = Bundle().apply {

                    }
                }
    }
}
