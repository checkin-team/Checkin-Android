package com.checkin.app.checkin.Home

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife

import com.checkin.app.checkin.R




class BookRestaurant : Fragment() {

    @BindView(R.id.im_book_restaurant_people_count_sub)
    lateinit var  ivPeopleSub: ImageView

    @BindView(R.id.im_book_restaurant_people_count_add)
    lateinit var ivPeopleAdd:ImageView;

    @BindView(R.id.tv_book_restaurant_people_count)
    lateinit var tvPeopleCount:TextView

    @BindView(R.id.container_book_restaurant_date_picker)
    lateinit var llDatePicker: LinearLayout


    private var listener: OnFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val v: View= inflater.inflate(R.layout.fragment_book_restaurant, container, false);
        ButterKnife.bind(this,v);

        var i=0
        ivPeopleAdd.setOnClickListener(){
            i=i+1
            tvPeopleCount.text=""+i
        }
        ivPeopleSub.setOnClickListener(){
            i=i-1
            tvPeopleCount.text=""+i
        }



        var dates=GetDayDate.getFiveDate()
        var days=GetDayDate.getFiveDays()
        for(x in 0 until dates.size){
            var bookRestaurantViewModel=BookRestaurantDateHolder(context);

            bookRestaurantViewModel.bindView(dates[x],days[x])
            val lp = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT,1.0f)
            var view:View=bookRestaurantViewModel.getViewArrayList()
            view.layoutParams=lp

            llDatePicker.addView(view)
        }






        return v;
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
                BookRestaurant().apply {
                    arguments = Bundle().apply {

                    }
                }
    }
}
