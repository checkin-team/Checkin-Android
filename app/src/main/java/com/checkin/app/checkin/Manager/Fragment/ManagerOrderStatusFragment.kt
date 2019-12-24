package com.checkin.app.checkin.Manager.Fragment

import android.animation.ValueAnimator
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.Transformation
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.checkin.app.checkin.R


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [ManagerOrderStatusFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [ManagerOrderStatusFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ManagerOrderStatusFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var height: Int = 0
    private var listener: OnFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        var v = inflater.inflate(R.layout.item_manager_order_status_confirmed, container, false)
        var bottomArrowBookingStatus = v.findViewById<ImageView>(R.id.im_manager_status_booking_arrow)
        var bookingDetailsView = v.findViewById(R.id.include_manager_status_booking_detail) as View
        val bookingDetailsHeight = bookingDetailsView.layoutParams.height

        height = bookingDetailsView.height
        var j = 0
        bottomArrowBookingStatus.setOnClickListener {
            if (j == 0) {
                collapse(bookingDetailsView);
                j = 1
            } else {
                expand(bookingDetailsView, bookingDetailsHeight)
                j = 0
            }
        }
        var arrowBookingDetails = v.findViewById<ImageView>(R.id.im_fragment_manager_status_details_arrow)
        var orderStatusView = v.findViewById(R.id.includ_fragment_order_status_bill) as View
        val orderStatusViewHeight = orderStatusView.layoutParams.height

        var i = 0
        arrowBookingDetails.setOnClickListener {
            if (i == 0) {

                collapse(orderStatusView);
                i = 1
            } else {
                expand(orderStatusView, orderStatusViewHeight)
                i = 0
            }


        }
        return v
    }

    fun expand(v: View, height: Int) {
        Toast.makeText(context, "Clicked", Toast.LENGTH_LONG).show()
        val anim = ValueAnimator.ofInt(v.getMeasuredHeight(), height)
        anim.addUpdateListener { valueAnimator ->
            val `val` = valueAnimator.animatedValue as Int
            val layoutParams = v.getLayoutParams()
            layoutParams.height = `val`
            v.setLayoutParams(layoutParams)
        }
        anim.duration = 500
        anim.start()
        v.visibility = View.VISIBLE

    }

    // TODO: Rename method, update argument and hook method into UI event
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

    fun collapse(v: View) {
        val initialHeight = v.measuredHeight

        val a = object : Animation() {
            override protected fun applyTransformation(interpolatedTime: Float, t: Transformation) {
                if (interpolatedTime == 1f) {
                    v.visibility = View.GONE
                } else {
                    v.layoutParams.height = initialHeight - (initialHeight * interpolatedTime).toInt()
                    v.requestLayout()
                }
            }

            override fun willChangeBounds(): Boolean {
                return true
            }

            override fun hasEnded(): Boolean {
                return super.hasEnded()
            }
        }

        // Collapse speed of 1dp/ms
        a.duration = (initialHeight / v.context.resources.displayMetrics.density).toInt().toLong()
        v.startAnimation(a)
        if (a.hasEnded()) {
            v.layoutParams.height = 700
            v.requestLayout()
            v.visibility = View.GONE

        }

    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments]
     * (http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */
    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ManagerOrderStatusFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance() =
                ManagerOrderStatusFragment().apply {

                }
    }


}

