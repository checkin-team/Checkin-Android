package com.checkin.app.checkin.data.config

import com.checkin.app.checkin.R
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
            setDefaultsAsync(R.xml.remote_config_defaults)
        }
    }

    operator fun get(key: String) = instance[key]

    inline fun <reified T> getData(key: String) = instance[key].asString().let {
        Converters.getObjectFromJson(it, object : TypeReference<T>() {})
    }

    inline fun <reified T> getListData(key: String) = instance[key].asString().let {
        Converters.getObjectFromJson<List<T>>(it, TypeFactory.defaultInstance().constructCollectionType(List::class.java, T::class.java))
    }

    fun refresh() = instance.fetch()

    fun activate() = instance.activate()

    fun refreshAndActivate() = instance.fetchAndActivate()

    object Constants {
        const val SUPPORT_PHONE_NO = "checkin_support_phone_no"
        const val SUPPORT_EMAIL_ADDRESS = "checkin_support_email"
        const val SUPPORT_FACEBOOK_PAGE_ID = "checkin_support_facebook_page_id"
        const val SUPPORT_INSTAGRAM_PAGE_ID = "checkin_support_instagram_page_id"
        const val SUPPORT_YOUTUBE_CHANNEL_ID = "checkin_support_youtube_channel_id"
        const val HOME_TOP_BANNERS_AD = "checkin_home_banners_top"
        const val KEY_RAZORPAY = "checkin_key_razorpay"
        const val ENABLED_REWARDS =  "checkin_feature_rewards_enabled"
    }
}