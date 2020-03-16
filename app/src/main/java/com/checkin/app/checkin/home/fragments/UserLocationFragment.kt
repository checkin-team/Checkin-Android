package com.checkin.app.checkin.home.fragments

import android.os.Bundle
import android.preference.PreferenceManager
import android.text.Editable
import android.view.View
import android.widget.EditText
import androidx.fragment.app.activityViewModels
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

    val viewModel: UserLocationViewModel by activityViewModels()
    val homeViewModel: HomeViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        epoxyUserLocation.withModels {
            if (etUserLocation.text.isEmpty() || viewModel.locationData.value?.inError == true) {
                currentLocationModelHolder {
                    id("present.location")
                    listener(this@UserLocationFragment)
                }
            }

            viewModel.locationData.value?.data?.forEachIndexed { _, model ->
                cityLocationModelHolder {
                    id(model.id)
                    data(model)
                    listener(this@UserLocationFragment)
                }
            }
        }

        viewModel.locationData.observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Resource.Status.SUCCESS, Resource.Status.ERROR_NOT_FOUND -> {
                    epoxyUserLocation.requestModelBuild()
                }
            }
        })
        viewModel.fetchData()
    }

    @OnTextChanged(R.id.et_user_location, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    fun onTextChanged(et: Editable?) {
        if (et != null) viewModel.searchCities(et.toString())
    }

    @OnClick(R.id.im_user_location_back)
    fun onClickBack() {
        onBackPressed()
    }

    override fun onBackPressed(): Boolean {
        viewModel.resetResults()
        parentFragmentManager.popBackStack()
        return true
    }

    override fun onLocationSelected(data: CityLocationModel?) {
        var id = 0
        var name = "Current Location"

        if (data != null) {
            id = data.id
            name = data.name
        }

        with(PreferenceManager.getDefaultSharedPreferences(context).edit()) {
            putInt(Constants.LOCATION_CITY_ID, id)
            putString(Constants.LOCATION_CITY_NAME, name)
            apply()
        }

        homeViewModel.setCityId(id)
        onBackPressed()
    }

    companion object {
        val TAG = UserLocationFragment::class.simpleName
    }
}