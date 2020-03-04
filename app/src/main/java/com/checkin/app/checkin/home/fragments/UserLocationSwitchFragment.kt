package com.checkin.app.checkin.home.fragments

import android.os.Bundle
import android.text.Editable
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
import com.checkin.app.checkin.misc.fragments.BaseBottomSheetFragment
import com.checkin.app.checkin.utility.Utils

class UserLocationSwitchFragment : BaseBottomSheetFragment() {
    override val rootLayout = R.layout.fragment_user_location_switch
    val viewModel: UserLocationViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        epoxyUserLocation.withModels {
            viewModel.locationsData.value?.data?.forEachIndexed { index, model ->
                cityLocationModelHolder {
                    id(id)
                    data(model)
                }
            }
        }

        viewModel.locationsData.observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Resource.Status.SUCCESS -> {
                    epoxyUserLocation.requestModelBuild()
                }
                Resource.Status.LOADING -> {
                    Utils.toast(requireContext(), "Bruh")
                }
            }
        })
    }


    @BindView(R.id.et_user_location)
    internal lateinit var etUserLocation: EditText

    @BindView(R.id.epoxy_rv_user_location)
    internal lateinit var epoxyUserLocation: EpoxyRecyclerView

    @OnTextChanged(R.id.et_user_location, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    fun onTextChanged(et: Editable?) {
        viewModel.searchCities(et.toString())
    }

}