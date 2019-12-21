package com.checkin.app.checkin.misc.paytm

import android.os.Bundle
import androidx.lifecycle.LiveData
import com.checkin.app.checkin.Data.IBaseViewModel
import com.checkin.app.checkin.Data.Resource
import com.fasterxml.jackson.databind.node.ObjectNode

interface IPaytmViewModel {
    fun postPaytmCallback(bundle: Bundle)

    fun requestPaytmDetails()

    val paytmCallbackData: LiveData<Resource<ObjectNode>>

    val paytmDetails: LiveData<Resource<PaytmModel>>
}

/*
class PaytmImplViewModel: IPaytmViewModel, IBaseViewModel {
    private val mPaytmData = createNetworkLiveData<PaytmModel>()

    override fun postPaytmCallback(bundle: Bundle) {

    }

    override fun requestPaytmDetails() {
    }

    override val paytmCallbackData: LiveData<Resource<ObjectNode>>
    override val paytmDetails: LiveData<Resource<PaytmModel>> = mPaytmData

}*/
