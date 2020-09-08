package com.checkin.app.checkin.user

import android.app.Application
import android.content.Context
import androidx.lifecycle.LiveData
import com.checkin.app.checkin.data.BaseRepository
import com.checkin.app.checkin.data.network.ApiClient.Companion.getApiService
import com.checkin.app.checkin.data.network.ApiResponse
import com.checkin.app.checkin.data.network.RetrofitLiveData
import com.checkin.app.checkin.data.network.WebApiService
import com.checkin.app.checkin.data.resource.NetworkBoundResource
import com.checkin.app.checkin.data.resource.Resource
import com.checkin.app.checkin.misc.models.GenericDetailModel
import com.checkin.app.checkin.user.models.ShopCustomerModel
import com.checkin.app.checkin.user.models.UserLocationModel
import com.checkin.app.checkin.user.models.UserModel
import com.checkin.app.checkin.utility.ProgressRequestBody
import com.checkin.app.checkin.utility.ProgressRequestBody.UploadCallbacks
import com.checkin.app.checkin.utility.SingletonHolder
import com.fasterxml.jackson.databind.node.ObjectNode
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import java.io.File

class UserRepository private constructor(context: Context) : BaseRepository() {
    private val mWebService: WebApiService = getApiService(context)

    val userRecentCheckins: LiveData<Resource<List<ShopCustomerModel>>>
        get() = object : NetworkBoundResource<List<ShopCustomerModel>, List<ShopCustomerModel>>() {
            override fun shouldUseLocalDb(): Boolean = false

            override fun createCall(): LiveData<ApiResponse<List<ShopCustomerModel>>> {
                return RetrofitLiveData(mWebService.userRecentCheckins)
            }
        }.asLiveData

    val allUsers: LiveData<Resource<List<UserModel>>>
        get() = object : NetworkBoundResource<List<UserModel>, List<UserModel>>() {
            override fun shouldUseLocalDb(): Boolean = false

            override fun createCall(): LiveData<ApiResponse<List<UserModel>>> {
                return RetrofitLiveData(mWebService.users)
            }
        }.asLiveData

    /**
     * @param userPk - 0 for self, non-zero for others.
     * @return
     */
    fun getUser(userPk: Long): LiveData<Resource<UserModel>> {
        return object : NetworkBoundResource<UserModel, UserModel>() {
            override fun shouldUseLocalDb(): Boolean = false

            override fun createCall(): LiveData<ApiResponse<UserModel>> {
                return if (userPk == 0L) {
                    RetrofitLiveData(mWebService.personalUser)
                } else {
                    RetrofitLiveData(mWebService.getNonPersonalUser(userPk))
                }
            }
        }.asLiveData
    }


    fun postUserData(objectNode: ObjectNode): LiveData<Resource<UserModel>> {
        return object : NetworkBoundResource<UserModel, UserModel>() {
            override fun shouldUseLocalDb(): Boolean = false

            override fun createCall(): LiveData<ApiResponse<UserModel>> {
                return RetrofitLiveData(mWebService.postUserData(objectNode))
            }
        }.asLiveData
    }

    fun postUserProfilePic(pic: File, listener: UploadCallbacks<GenericDetailModel>): Call<GenericDetailModel> {
        val requestFile = RequestBody.create(MediaType.parse("image/jpeg"), pic)
        val requestBody = ProgressRequestBody(requestFile, listener)
        val body = MultipartBody.Part.createFormData("profile_pic", "profile.jpg", requestBody)
        return mWebService.postUserProfilePic(body)
    }

    fun postUserCurrentLocation(data: UserLocationModel): Call<UserLocationModel> {
        return mWebService.postUserLocation(data)
    }

    fun postUserReferralCode(data: ObjectNode): LiveData<Resource<UserModel>> {
        return object : NetworkBoundResource<UserModel, UserModel>() {
            override fun createCall(): LiveData<ApiResponse<UserModel>>? {
                return RetrofitLiveData(mWebService.postUserReferralCode(data))
            }
        }.asLiveData
    }

    companion object : SingletonHolder<UserRepository, Application>({ UserRepository(it.applicationContext) })
}