package com.checkin.app.checkin.Manager.Fragment

import android.app.Application
import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.checkin.app.checkin.Data.Resource

import com.checkin.app.checkin.R
import com.checkin.app.checkin.Utility.pass
import com.checkin.app.checkin.session.activesession.ActiveSessionViewModel
import com.checkin.app.checkin.session.model.SessionOrderedItemModel
import java.util.ArrayList


class ManagerScheduledOrderPending : Fragment(),ManagerScheduledOrderAdapter.SessionOrdersInteraction {
    override fun onCancelOrder(orderedItem: SessionOrderedItemModel?) {
    }

    private  lateinit var mAdapter: ManagerScheduledOrderAdapter

    override fun onCreate(savedInstanceState:  Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
          }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        var v:View=LayoutInflater.from(context).inflate(R.layout.fragment_manager_scheduled_pending_conformation,null,false)
        var rv:RecyclerView=v.findViewById(R.id.rv)
        val sessionOrderedItemModels = ArrayList<SessionOrderedItemModel>()
        val activeSessionViewModel= ViewModelProviders.of(requireActivity()).get(ActiveSessionViewModel::class.java)
        activeSessionViewModel.fetchActiveSessionDetail()

        sessionOrderedItemModels.add(SessionOrderedItemModel())
        sessionOrderedItemModels.add(SessionOrderedItemModel())

        sessionOrderedItemModels.add(SessionOrderedItemModel())
        mAdapter= ManagerScheduledOrderAdapter( null , this);

        activeSessionViewModel.sessionOrdersData.observe(this, Observer {
            it?.let { listResource ->
                when (listResource.status) {
                    Resource.Status.SUCCESS -> listResource.data?.let(mAdapter::setData)
                    else -> pass
                }
            }
        })
        rv.layoutManager=LinearLayoutManager(context)
        rv.adapter=mAdapter
        return v;
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
    }


    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }

    companion object {
         @JvmStatic
         fun newInstance() = ManagerScheduledOrderPending()


    }
}
