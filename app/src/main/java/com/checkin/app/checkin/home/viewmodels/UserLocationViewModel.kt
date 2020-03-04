package com.checkin.app.checkin.home.viewmodels

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.viewModelScope
import com.checkin.app.checkin.data.resource.Resource
import com.checkin.app.checkin.home.model.CityLocationModel
import com.checkin.app.checkin.menu.viewmodels.BaseMenuViewModel
import com.checkin.app.checkin.user.UserRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UserLocationViewModel(application: Application) : BaseMenuViewModel(application) {

    val repository = UserRepository.getInstance(application)


    val locationsData by lazy {
        createNetworkLiveData<List<CityLocationModel>>()
    }

    fun searchCities(query: String) {
        viewModelScope.launch {
            delay(500)
            val resourceLiveData = repository.getCities(query)
            locationsData.addSource(resourceLiveData) {
                locationsData.removeSource(resourceLiveData)
                locationsData.value = it
            }
        }
    }

    private suspend fun internalSearchCities(query: String) =
            withContext(viewModelScope.coroutineContext) {
                val resourceLiveData: LiveData<Resource<List<CityLocationModel>>> = Transformations.map(locationsData) { input ->
                    if (input?.data == null) {
                        return@map null
                    }
                    val items = input.data.filter {
                        it.name.contains(query, ignoreCase = false)
                    }
                    if (items.isEmpty()) return@map Resource.errorNotFound<List<CityLocationModel>>("No such city")
                    Resource.cloneResource(input, items)
                }
                locationsData.addSource(resourceLiveData) {
                    locationsData.removeSource(resourceLiveData)
                    locationsData.value = it
                }
            }

}