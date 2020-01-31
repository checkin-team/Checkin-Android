package com.checkin.app.checkin.restaurant.fragments

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import butterknife.BindView
import butterknife.OnClick
import com.airbnb.epoxy.EpoxyRecyclerView
import com.checkin.app.checkin.R
import com.checkin.app.checkin.data.resource.Resource
import com.checkin.app.checkin.misc.fragments.BaseFragment
import com.checkin.app.checkin.misc.holders.textModelHolder
import com.checkin.app.checkin.restaurant.models.RestaurantModel
import com.checkin.app.checkin.restaurant.viewmodels.RestaurantPublicViewModel
import com.checkin.app.checkin.utility.callPhoneNumber
import com.checkin.app.checkin.utility.isNotEmpty
import com.checkin.app.checkin.utility.navigateToLocation

class PublicRestaurantInfoFragment : BaseFragment() {
    override val rootLayout: Int = R.layout.fragment_public_restaurant_info

    @BindView(R.id.tv_restaurant_info_address)
    internal lateinit var tvAddress: TextView
    @BindView(R.id.tv_restaurant_info_phone)
    internal lateinit var tvPhone: TextView
    @BindView(R.id.epoxy_rv_public_restaurant_insider)
    internal lateinit var epoxyRvInsider: EpoxyRecyclerView

    val viewModel: RestaurantPublicViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        epoxyRvInsider.withModels {
            viewModel.restaurantData.value?.data?.extraData.takeIf { it.isNotEmpty() }?.forEachIndexed { index, text ->
                textModelHolder {
                    withBulletListLayout()
                            .id(index)
                            .text(text)
                }
            }
        }

        viewModel.restaurantData.observe(this, Observer {
            it?.let {
                if (it.status == Resource.Status.SUCCESS && it.data != null) {
                    setupInfo(it.data)
                    epoxyRvInsider.requestModelBuild()
                }
            }
        })
    }

    private fun setupInfo(data: RestaurantModel) {
        tvPhone.text = data.phone
        tvAddress.text = data.location?.address
    }

    @OnClick(R.id.container_restaurant_info_address)
    fun onClickAddress() {
        viewModel.restaurantData.value?.data?.location?.navigateToLocation(requireContext())
    }

    @OnClick(R.id.container_restaurant_info_phone)
    fun onClickPhone() {
        viewModel.restaurantData.value?.data?.phone?.callPhoneNumber(requireContext())
    }

    companion object {
        fun newInstance() = PublicRestaurantInfoFragment()
    }
}