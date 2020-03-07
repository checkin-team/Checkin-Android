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
import com.checkin.app.checkin.home.viewmodels.HomeViewModel
import com.checkin.app.checkin.home.viewmodels.UserLocationViewModel
import com.checkin.app.checkin.misc.fragments.BaseFragment
import com.checkin.app.checkin.misc.holders.textModelHolder
import com.checkin.app.checkin.utility.Constants

class UserLocationFragment : BaseFragment() {
    override val rootLayout = R.layout.fragment_user_location_switch
    val viewModel: UserLocationViewModel by viewModels()
    val homeViewModel: HomeViewModel by activityViewModels()
    @BindView(R.id.et_user_location)
    internal lateinit var etUserLocation: EditText

    @BindView(R.id.epoxy_rv_user_location)
    internal lateinit var epoxyUserLocation: EpoxyRecyclerView

    @BindView(R.id.im_user_location_cross)
    internal lateinit var imUserCross: ImageView

    @OnTextChanged(R.id.et_user_location, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    fun onTextChanged(et: Editable?) {
        if (et.toString().isEmpty()) {
            imUserCross.visibility = View.INVISIBLE
        } else {
            imUserCross.visibility = View.VISIBLE
        }
        viewModel.searchCities(et.toString())
    }


    @OnClick(R.id.im_user_location_cross)
    fun onTextCrossed() {
        etUserLocation.text.clear()
    }

    @OnClick(R.id.im_user_location_back)
    fun onBack() {
        requireActivity().supportFragmentManager.popBackStack()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        epoxyUserLocation.withModels {
            if (viewModel.locationData.value?.inError == true) {
                textModelHolder {
                    id("not.available")
                    text("Sorry not available in your city.")
                }
            }


            viewModel.locationData.value?.data?.forEachIndexed { index, model ->
                cityLocationModelHolder {
                    id(model.id)
                    data(model)
                    cityListener { _ ->
                        val preferences = requireActivity().getSharedPreferences(Constants.LOCATION_CITY_FILE, Context.MODE_PRIVATE)
                        with(preferences.edit()) {
                            putInt(Constants.LOCATION_CITY_ID, model.id)
                            putString(Constants.LOCATION_CITY_NAME, model.name)
                            commit()
                        }
                        homeViewModel.setCityId(model.id)
                        requireActivity().supportFragmentManager.popBackStack()
                    }
                }
            }

            currentLocationModelHolder {
                id("present.location")
                currentListener { _ ->
                    val preferences = requireActivity().getSharedPreferences(Constants.LOCATION_CITY_FILE, Context.MODE_PRIVATE)
                    with(preferences.edit()) {
                        putInt(Constants.LOCATION_CITY_ID, 0)
                        putString(Constants.LOCATION_CITY_NAME, "Current Location")
                        commit()
                    }
                    homeViewModel.setCityId(0)
                    requireActivity().supportFragmentManager.popBackStack()
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

}