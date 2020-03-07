package com.checkin.app.checkin.home.viewmodels

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.checkin.app.checkin.data.resource.Resource
import com.checkin.app.checkin.home.HomeRepository
import com.checkin.app.checkin.home.model.CityLocationModel
import com.checkin.app.checkin.menu.viewmodels.BaseMenuViewModel

class UserLocationViewModel(application: Application) : BaseMenuViewModel(application) {

    val repository = HomeRepository.getInstance(application)


    private val mAllLocationsData =
            createNetworkLiveData<List<CityLocationModel>>()


    private val allLocationsData: LiveData<Resource<List<CityLocationModel>>> = mAllLocationsData

    private val mLocationData = createNetworkLiveData<List<CityLocationModel>>()

    val locationData: LiveData<Resource<List<CityLocationModel>>> = mLocationData

    fun fetchData() {
        mAllLocationsData.addSource(repository.getAllCities, mAllLocationsData::setValue)
        mLocationData.addSource(allLocationsData, mLocationData::setValue)
    }

    fun searchCities(query: String) {
        internalSearchCities(query)

    }

    private fun internalSearchCities(query: String) {
        val resourceLiveData = Transformations.map(allLocationsData) { input ->
            val items = input.data?.filter {
                it.name.contains(query, ignoreCase = true)
            }
            if (items?.isEmpty() == true) return@map Resource.errorNotFound<List<CityLocationModel>>("No Such City Found.")
            Resource.cloneResource(input, items)
        }
        mLocationData.addSource(resourceLiveData) {
            mLocationData.value = it
        }
    }
}