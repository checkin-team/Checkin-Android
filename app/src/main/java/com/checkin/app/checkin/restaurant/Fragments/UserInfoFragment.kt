package com.checkin.app.checkin.restaurant.Fragments

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.GridLayout
import android.widget.Spinner
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.OnClick
import com.checkin.app.checkin.Menu.MenuItemInteraction
import com.checkin.app.checkin.Menu.Model.MenuItemModel
import com.checkin.app.checkin.R
import com.checkin.app.checkin.Utility.inTransaction
import com.checkin.app.checkin.Utility.parentActivityDelegate
import com.checkin.app.checkin.menu.viewmodels.CartViewModel
import com.checkin.app.checkin.menu.viewmodels.UserMenuViewModel
import com.checkin.app.checkin.misc.fragments.BaseFragment
import com.checkin.app.checkin.restaurant.RestaurantFeaturesAdapter

class UserInfoFragment : BaseFragment(){
    @BindView(R.id.rv_restaurant_info_feature)
   lateinit var restaurantFeaturesRecyclerView: RecyclerView
     var restaurantFeaturesAdapter=RestaurantFeaturesAdapter(null)
    lateinit var restarantFeatures:Array<String>

    override val rootLayout: Int=R.layout.fragment_shop_info
         //To change initializer of created properties use File | Settings | File Templates.


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

            restaurantFeaturesAdapter= RestaurantFeaturesAdapter(restarantFeatures)
            restaurantFeaturesRecyclerView.layoutManager=GridLayoutManager(context,2,RecyclerView.VERTICAL,false)
            restaurantFeaturesRecyclerView.adapter=restaurantFeaturesAdapter

        }





    companion object {
        fun newInstance():UserInfoFragment {
            return UserInfoFragment()
        }
    }
    }









