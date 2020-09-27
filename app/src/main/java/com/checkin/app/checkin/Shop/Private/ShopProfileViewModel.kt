package com.checkin.app.checkin.Shop.Private

import android.app.Application
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.checkin.app.checkin.Shop.RestaurantModel
import com.checkin.app.checkin.Shop.ShopModel.PAYMENT_MODE
import com.checkin.app.checkin.Shop.ShopRepository
import com.checkin.app.checkin.data.BaseViewModel
import com.checkin.app.checkin.data.Converters.objectMapper
import com.checkin.app.checkin.data.network.RetrofitCallAsyncTask
import com.checkin.app.checkin.data.notifications.MessageUtils.NotificationUpdate
import com.checkin.app.checkin.data.notifications.MessageUtils.createUploadNotification
import com.checkin.app.checkin.data.resource.Resource
import com.checkin.app.checkin.data.resource.Resource.Companion.success
import com.checkin.app.checkin.misc.models.GenericDetailModel
import com.checkin.app.checkin.utility.ProgressRequestBody.UploadCallbacks
import com.fasterxml.jackson.databind.JsonNode
import java.io.File

/**
 * Created by Bhavik Patel on 24/08/2018.
 */
class ShopProfileViewModel(application: Application) : BaseViewModel(application) {
    private val mRepository: ShopRepository = ShopRepository.getInstance(application)

    private val mShopData = createNetworkLiveData<RestaurantModel>()
    private val mCollectAspectData = MutableLiveData<Boolean>(false)
    private val mCollectBasicData = MutableLiveData<Boolean>(false)
    private val mErrors = MutableLiveData<JsonNode>()

    var shopPk: Long = 0
    val shopData: LiveData<Resource<RestaurantModel>> = mShopData
    val errors: LiveData<JsonNode> = mErrors

    fun updateAspectData(
            cuisines: Array<CharSequence>, categories: Array<CharSequence>, paymentModes: Array<PAYMENT_MODE>,
            hasNonVeg: Boolean, hasAlcohol: Boolean, hasHomeDelivery: Boolean, extraData: List<String>): RestaurantModel {
        val shop = mShopData.value?.data ?: RestaurantModel(shopPk)
        shop.setCuisines(*cuisines)
        shop.setCategories(*categories)
        shop.setPaymentModes(*paymentModes)
        shop.setHasAlcohol(hasAlcohol)
        shop.setHasHomeDelivery(hasHomeDelivery)
        shop.setHasNonveg(hasNonVeg)
        shop.setExtraData(*extraData.toTypedArray())
        mShopData.value = success(shop)
        return shop
    }

    fun updateBasicData(
            name: String, website: String, tagLine: String,
            nonWorkingDays: Array<CharSequence>, openingTime: Long, closingTime: Long): RestaurantModel {
        val shop = mShopData.value?.data ?: RestaurantModel(shopPk)
        shop.name = name
        shop.website = website
        shop.tagline = tagLine
        shop.nonWorkingDays = nonWorkingDays
        shop.openingHour = openingTime
        shop.closingHour = if (closingTime < openingTime) closingTime + 24 * 60.toLong() else closingTime
        mShopData.value = success(shop)
        return shop
    }

    fun shouldCollectAspectData(): LiveData<Boolean> {
        return mCollectAspectData
    }

    fun shouldCollectBasicData(): LiveData<Boolean> {
        return mCollectBasicData
    }

    fun updateShopContact(phoneToken: String?, email: String?) {
        val data = objectMapper.createObjectNode().apply {
            if (phoneToken != null) put("phone_token", phoneToken)
            if (email != null) put("email", email)
        }
        mData.addSource(mRepository.updateShopContact(shopPk, data), mData::setValue)
    }

    fun updateShop(shop: RestaurantModel) = mData.addSource(mRepository.updateShopDetails(shop), mData::setValue)

    fun collectData() {
        collectBasicData()
        collectAspectData()
    }

    fun collectBasicData() {
        mCollectBasicData.value = true
    }

    fun collectAspectData() {
        mCollectAspectData.value = true
    }

    fun showError(data: JsonNode) {
        mErrors.value = data
    }

    fun fetchShopDetails(shopPk: Long) {
        this.shopPk = shopPk
        mShopData.addSource(mRepository.getShopModel(shopPk)) { value: Resource<RestaurantModel> -> mShopData.setValue(value) }
    }

    fun fetchShopManage(shopPk: Long) {
        this.shopPk = shopPk
        mShopData.addSource(mRepository.getShopManageModel(shopPk), mShopData::setValue)
    }

    fun useShop(restaurantModel: RestaurantModel?) {
        mShopData.value = success(restaurantModel)
    }

    fun removeCoverImage(index: Int) {
        mData.addSource(mRepository.deleteRestaurantCover(shopPk, index), mData::setValue)
    }

    override fun updateResults() {
        fetchShopDetails(shopPk)
    }

    fun updateCoverPic(pictureFile: File, context: Context?, index: Int) {
        val builder = createUploadNotification(context!!)
        doUploadImage(pictureFile, NotificationUpdate(context, builder), index)
    }

    private fun doUploadImage(pictureFile: File, listener: UploadCallbacks<GenericDetailModel>, index: Int) {
        if (index == -1) RetrofitCallAsyncTask(listener).execute(mRepository.postRestaurantLogo(shopPk, pictureFile, listener))
        else RetrofitCallAsyncTask(listener).execute(mRepository.postRestaurantCover(shopPk, index, pictureFile, listener))
    }
}