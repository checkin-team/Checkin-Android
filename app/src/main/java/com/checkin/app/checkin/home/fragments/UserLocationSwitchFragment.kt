package com.checkin.app.checkin.home.fragments

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.View
import android.widget.EditText
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import butterknife.BindView
import butterknife.OnTextChanged
import com.airbnb.epoxy.EpoxyRecyclerView
import com.checkin.app.checkin.R
import com.checkin.app.checkin.data.resource.Resource
import com.checkin.app.checkin.home.epoxy.cityLocationModelHolder
import com.checkin.app.checkin.home.viewmodels.UserLocationViewModel
import com.checkin.app.checkin.misc.fragments.BaseFragment
import com.checkin.app.checkin.misc.holders.textModelHolder
import com.checkin.app.checkin.utility.Constants

class UserLocationSwitchFragment : BaseFragment() {
    override val rootLayout = R.layout.fragment_user_location_switch
    val viewModel: UserLocationViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        epoxyUserLocation.withModels {
            if (viewModel.locationData.value?.inError == true) {
                textModelHolder {
                    id("not.available")
                    text("Sorry not available in your city yet")
                }
            }
            viewModel.locationData.value?.data?.forEachIndexed { index, model ->
                cityLocationModelHolder {
                    id(id)
                    data(model)
                    listener { v ->
                        val preferences = requireActivity().getSharedPreferences(Constants.LOCATION_CITY_ID, Context.MODE_PRIVATE)
                        with(preferences.edit()) {
                            putString(Constants.LOCATION_CITY_ID, model.id)
                        }
                        requireActivity().supportFragmentManager.popBackStack()
                    }
                }
            }
        }

        viewModel.locationData.observe(viewLifecycleOwner, Observer {
            Log.d("BRUH", "okay in fragment and ${it.data} and ${it.status}")

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


    @BindView(R.id.et_user_location)
    internal lateinit var etUserLocation: EditText

    @BindView(R.id.epoxy_rv_user_location)
    internal lateinit var epoxyUserLocation: EpoxyRecyclerView

    @OnTextChanged(R.id.et_user_location, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    fun onTextChanged(et: Editable?) {
        viewModel.searchCities(et.toString())
    }

    @BindView(R.id.t)
    override fun onDestroy() {
        super.onDestroy()

        Log.d(TAG, "Destroyed I guess")
    }

    companion object {
        val TAG = UserLocationSwitchFragment::class.simpleName
    }

}