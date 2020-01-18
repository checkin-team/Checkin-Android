package com.checkin.app.checkin.data.config

import com.checkin.app.checkin.data.Converters
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.type.TypeFactory
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.get
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings

object RemoteConfig {
    val instance by lazy {
        Firebase.remoteConfig.apply {
            setConfigSettingsAsync(remoteConfigSettings {
                minimumFetchIntervalInSeconds = 360
            })
        }
    }

    inline fun <reified T> getData(key: String) = instance[key].asString().let {
        Converters.getObjectFromJson(it, object : TypeReference<T>() {})
    }

    inline fun <reified T> getListData(key: String) = instance[key].asString().let {
        Converters.getObjectFromJson<List<T>>(it, TypeFactory.defaultInstance().constructCollectionType(List::class.java, T::class.java))
    }

    fun refresh() = instance.fetch()

    fun activate() = instance.activate()

    object Constants {
        const val SUPPORT_PHONE_NO = "checkin_support_phone_no"
        const val HOME_TOP_BANNERS_AD = "checkin_home_banners_top"
    }
}