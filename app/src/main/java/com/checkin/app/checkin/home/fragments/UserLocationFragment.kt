package com.checkin.app.checkin.home.fragments

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import butterknife.BindView
import butterknife.OnClick
import butterknife.OnTextChanged
import com.airbnb.epoxy.EpoxyRecyclerView
import com.checkin.app.checkin.R
import com.checkin.app.checkin.data.resource.Resource
import com.checkin.app.checkin.home.epoxy.cityLocationModelHolder
import com.checkin.app.checkin.home.epoxy.currentLocationModelHolder
import com.checkin.app.checkin.home.listeners.LocationSelectedListener
import com.checkin.app.checkin.home.model.CityLocationModel
import com.checkin.app.checkin.home.viewmodels.HomeViewModel
import com.checkin.app.checkin.home.viewmodels.UserLocationViewModel
import com.checkin.app.checkin.misc.fragments.BaseFragment
import com.checkin.app.checkin.utility.Constants

class UserLocationFragment : BaseFragment(), LocationSelectedListener {
    override val rootLayout = R.layout.fragment_user_location_switch

    @BindView(R.id.et_user_location)
    internal lateinit var etUserLocation: EditText

    @BindView(R.id.epoxy_rv_user_location)
    internal lateinit var epoxyUserLocation: EpoxyRecyclerView

    @BindView(R.id.im_user_location_cross)
    internal lateinit var imUserCross: ImageView

    val viewModel: UserLocationViewModel by viewModels()
    val homeViewModel: HomeViewModel by activityViewModels()


    @OnTextChanged(R.id.et_user_location, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    fun onTextChanged(et: Editable?) {
        viewModel.searchCities(et.toString())
    }

    @OnClick(R.id.im_user_location_back)
    fun onBack() {
        requireActivity().supportFragmentManager.popBackStack()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        epoxyUserLocation.withModels {
            if (etUserLocation.text.isEmpty() || viewModel.locationData.value?.inError == true) {
                currentLocationModelHolder {
                    id("present.location")
                    locationSelectedListener(this@UserLocationFragment)
                }
            }


            viewModel.locationData.value?.data?.forEachIndexed { index, model ->
                cityLocationModelHolder {
                    id(model.id)
                    data(model)
                    locationSelectedListener(this@UserLocationFragment)
                }
            }
        }

        viewModel.locationData.observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Resource.Status.SUCCESS -> {
                    epoxyUserLocation.requestModelBuild()
                }
                Resource.Status.ERROR_NOT_FOUND -> {
                    epoxyUserLocation.requestModelBuild()
                }
            }
        })
        viewModel.fetchData()
    }

    companion object {
        val TAG = UserLocationFragment::class.simpleName
    }

    override fun onLocationSelected(data: CityLocationModel?) {
        var id = 0
        var name = "Current Location"

        if (data != null) {
            id = data.id
            name = data.name
        }

        val preferences = requireContext().getSharedPreferences(Constants.LOCATION_CITY_FILE, Context.MODE_PRIVATE)
        with(preferences.edit()) {
            putInt(Constants.LOCATION_CITY_ID, id)
            putString(Constants.LOCATION_CITY_NAME, name)
            commit()
        }

        homeViewModel.setCityId(id)
        requireActivity().supportFragmentManager.popBackStack()
    }

}