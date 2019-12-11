package com.checkin.app.checkin.Shop

import android.app.Application
import android.content.Context
import androidx.lifecycle.LiveData
import com.checkin.app.checkin.Data.*
import com.checkin.app.checkin.Home.model.NearbyRestaurantModel
import com.checkin.app.checkin.Misc.GenericDetailModel
import com.checkin.app.checkin.Shop.Private.MemberModel
import com.checkin.app.checkin.Shop.ShopJoin.ShopJoinModel
import com.checkin.app.checkin.Utility.ProgressRequestBody
import com.checkin.app.checkin.Utility.SingletonHolder
import com.fasterxml.jackson.databind.node.ObjectNode
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import java.io.File


class ShopRepository private constructor(context: Context) : BaseRepository() {
    private val mWebService: WebApiService = ApiClient.getApiService(context)

    val nearbyRestaurants: LiveData<Resource<List<NearbyRestaurantModel>>>
        get() = object : NetworkBoundResource<List<NearbyRestaurantModel>, List<NearbyRestaurantModel>>() {
            override fun shouldUseLocalDb(): Boolean {
                return false
            }

            override fun createCall(): LiveData<ApiResponse<List<NearbyRestaurantModel>>> {
                return RetrofitLiveData(mWebService.nearbyRestaurants)
            }

            override fun saveCallResult(data: List<NearbyRestaurantModel>) {}
        }.asLiveData

    fun registerShop(model: ShopJoinModel): LiveData<Resource<GenericDetailModel>> {
        return object : NetworkBoundResource<GenericDetailModel, GenericDetailModel>() {
            override fun shouldUseLocalDb(): Boolean {
                return false
            }

            override fun createCall(): LiveData<ApiResponse<GenericDetailModel>> {
                return RetrofitLiveData(mWebService.postRegisterShop(model))
            }

            override fun saveCallResult(data: GenericDetailModel) {

            }
        }.asLiveData
    }

    fun updateShopDetails(restaurantModel: RestaurantModel): LiveData<Resource<ObjectNode>> {
        return object : NetworkBoundResource<ObjectNode, ObjectNode>() {
            override fun shouldUseLocalDb(): Boolean {
                return false
            }

            override fun createCall(): LiveData<ApiResponse<ObjectNode>> {
                return RetrofitLiveData(mWebService.putRestaurantManageDetails(restaurantModel.getPk(), restaurantModel))
            }

            override fun saveCallResult(data: ObjectNode) {

            }
        }.asLiveData
    }

    fun updateShopContact(shopId: Long, data: ObjectNode): LiveData<Resource<ObjectNode>> {
        return object : NetworkBoundResource<ObjectNode, ObjectNode>() {
            override fun shouldUseLocalDb(): Boolean {
                return false
            }

            override fun createCall(): LiveData<ApiResponse<ObjectNode>> {
                return RetrofitLiveData(mWebService.putRestaurantContactVerify(shopId, data))
            }

            override fun saveCallResult(data: ObjectNode) {

            }
        }.asLiveData
    }

    fun getShopModel(shopId: Long): LiveData<Resource<RestaurantModel>> {
        return object : NetworkBoundResource<RestaurantModel, RestaurantModel>() {

            override fun shouldUseLocalDb(): Boolean {
                return false
            }

            override fun createCall(): LiveData<ApiResponse<RestaurantModel>> {
                return RetrofitLiveData(mWebService.getRestaurantDetails(shopId))
            }

            override fun saveCallResult(data: RestaurantModel) {}
        }.asLiveData
    }

    fun getShopManageModel(shopId: Long): LiveData<Resource<RestaurantModel>> {
        return object : NetworkBoundResource<RestaurantModel, RestaurantModel>() {

            override fun shouldUseLocalDb(): Boolean {
                return false
            }

            override fun createCall(): LiveData<ApiResponse<RestaurantModel>> {
                return RetrofitLiveData(mWebService.getRestaurantManageDetails(shopId))
            }

            override fun saveCallResult(data: RestaurantModel) {}
        }.asLiveData
    }

    fun getRestaurantMembers(shopId: Long): LiveData<Resource<List<MemberModel>>> {
        return object : NetworkBoundResource<List<MemberModel>, List<MemberModel>>() {
            override fun shouldUseLocalDb(): Boolean {
                return false
            }

            override fun createCall(): LiveData<ApiResponse<List<MemberModel>>> {
                return RetrofitLiveData(mWebService.getRestaurantMembers(shopId))
            }

            override fun saveCallResult(data: List<MemberModel>) {

            }
        }.asLiveData
    }

    fun addRestaurantMember(shopId: Long, data: MemberModel): LiveData<Resource<ObjectNode>> {
        return object : NetworkBoundResource<ObjectNode, ObjectNode>() {
            override fun shouldUseLocalDb(): Boolean {
                return false
            }

            override fun createCall(): LiveData<ApiResponse<ObjectNode>> {
                return RetrofitLiveData(mWebService.postRestaurantMember(shopId, data))
            }

            override fun saveCallResult(data: ObjectNode) {}
        }.asLiveData
    }

    fun updateRestaurantMember(shopId: Long, shopMember: MemberModel): LiveData<Resource<ObjectNode>> {
        return object : NetworkBoundResource<ObjectNode, ObjectNode>() {
            override fun shouldUseLocalDb(): Boolean {
                return false
            }

            override fun createCall(): LiveData<ApiResponse<ObjectNode>> {
                return RetrofitLiveData(mWebService.putRestaurantMember(shopId, shopMember.userId, shopMember))
            }

            override fun saveCallResult(data: ObjectNode) {}
        }.asLiveData
    }

    fun removeRestaurantMember(shopId: Long, userId: Long): LiveData<Resource<ObjectNode>> {
        return object : NetworkBoundResource<ObjectNode, ObjectNode>() {
            override fun shouldUseLocalDb(): Boolean {
                return false
            }

            override fun createCall(): LiveData<ApiResponse<ObjectNode>> {
                return RetrofitLiveData(mWebService.deleteRestaurantMember(shopId, userId))
            }

            override fun saveCallResult(data: ObjectNode) {}
        }.asLiveData
    }

    fun deleteRestaurantCover(shopId: Long, index: Int): LiveData<Resource<ObjectNode>> {
        return object : NetworkBoundResource<ObjectNode, ObjectNode>() {
            override fun shouldUseLocalDb(): Boolean {
                return false
            }

            override fun createCall(): LiveData<ApiResponse<ObjectNode>> {
                return RetrofitLiveData(mWebService.deleteRestaurantCover(shopId, index))
            }

            override fun saveCallResult(data: ObjectNode) {

            }
        }.asLiveData
    }

    fun postRestaurantLogo(mShopPk: Long, pic: File, listener: ProgressRequestBody.UploadCallbacks): Call<GenericDetailModel> {
        val requestFile = RequestBody.create(MediaType.parse("image/jpeg"), pic)
        val requestBody = ProgressRequestBody(requestFile, listener)
        val body = MultipartBody.Part.createFormData("logo", "cover.jpg", requestBody)
        return mWebService.postRestaurantLogo(mShopPk, body)
    }

    fun postRestaurantCover(mShopPk: Long, index: Int, pic: File, listener: ProgressRequestBody.UploadCallbacks): Call<GenericDetailModel> {
        val requestFile = RequestBody.create(MediaType.parse("image/jpeg"), pic)
        val requestBody = ProgressRequestBody(requestFile, listener)
        val body = MultipartBody.Part.createFormData("image", "cover.jpg", requestBody)
        return mWebService.postRestaurantCover(mShopPk, index, body)
    }

    companion object : SingletonHolder<ShopRepository, Application>({ ShopRepository(it.applicationContext) })
}
