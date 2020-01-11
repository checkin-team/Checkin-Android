package com.checkin.app.checkin.restaurant.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import butterknife.BindView
import com.airbnb.epoxy.EpoxyRecyclerView
import com.checkin.app.checkin.Data.Resource
import com.checkin.app.checkin.R
import com.checkin.app.checkin.Utility.isNotEmpty
import com.checkin.app.checkin.misc.fragments.BaseFragment
import com.checkin.app.checkin.restaurant.epoxy.publicRestaurantInsider
import com.checkin.app.checkin.restaurant.models.RestaurantModel
import com.checkin.app.checkin.restaurant.viewmodels.RestaurantPublicViewModel
import java.util.*


class PublicRestaurantInfoFragment : BaseFragment() {

    companion object {
        fun newInstance(restaurantId: Long) =
                PublicRestaurantInfoFragment()
    }

    @BindView(R.id.tv_public_restaurant_address)
    internal lateinit var tvAddress: TextView

    @BindView(R.id.tv_public_restaurant_phone)
    internal lateinit var tvPhone: TextView

    @BindView(R.id.epoxy_rv_public_restaurant_insider)
    internal lateinit var epoxyRvInsider: EpoxyRecyclerView

    @BindView(R.id.epoxy_rv_public_restaurant_features)
    internal lateinit var epoxyRvFeatures: EpoxyRecyclerView

    @BindView(R.id.im_public_restaurant_address_btn)
    internal lateinit var imAddress: ImageView

    @BindView(R.id.im_public_restaurant_phone_btn)
    internal lateinit var imPhone: ImageView


    val viewModel: RestaurantPublicViewModel by activityViewModels()


    override val rootLayout: Int = R.layout.fragment_public_restaurant_info


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        epoxyRvInsider.withModels {


            viewModel.restaurantData.value?.data?.extraData?.takeIf { it.isNotEmpty() }?.forEachIndexed { index, s ->
                publicRestaurantInsider {
                    id(index)
                    text(s)
                }
            }
        }
        epoxyRvFeatures.withModels {
            viewModel.restaurantData.value?.data?.extraData?.takeIf { it.isNotEmpty() }?.forEachIndexed { index, s ->
                publicRestaurantInsider {
                    id(index)
                    text(s)
                }
            }
        }


        viewModel.restaurantData.observe(viewLifecycleOwner, Observer {
            if (it.status == Resource.Status.SUCCESS && it.data != null) {
                setupInfo(it.data)
                epoxyRvFeatures.requestModelBuild()
                epoxyRvInsider.requestModelBuild()
            }
        })
    }

    private fun setupInfo(data: RestaurantModel) {
        tvPhone.text = data.phone
        tvAddress.text = data.location?.address

        imAddress.setOnClickListener {
            val uri = String.format(Locale.ENGLISH, "geo:%f,%f", data.location?.latitude, data.location?.longitude)
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
            startActivity(intent)
        }

        imPhone.setOnClickListener {
            val uri = "tel: ${data.phone}"
            val intent = Intent(Intent.ACTION_CALL, Uri.parse(uri))
            startActivity(intent)
        }
    }

}