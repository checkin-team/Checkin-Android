package com.checkin.app.checkin.user.viewmodels

import android.app.Application
import android.content.Context
import androidx.lifecycle.LiveData
import com.checkin.app.checkin.data.BaseViewModel
import com.checkin.app.checkin.data.Converters.objectMapper
import com.checkin.app.checkin.data.network.ApiResponse
import com.checkin.app.checkin.data.network.RetrofitCallAsyncTask
import com.checkin.app.checkin.data.notifications.MessageUtils
import com.checkin.app.checkin.data.notifications.MessageUtils.NotificationUpdate
import com.checkin.app.checkin.data.resource.Resource
import com.checkin.app.checkin.data.resource.Resource.Companion.error
import com.checkin.app.checkin.data.resource.Resource.Companion.loading
import com.checkin.app.checkin.data.resource.Resource.Companion.success
import com.checkin.app.checkin.misc.models.GenericDetailModel
import com.checkin.app.checkin.user.UserRepository
import com.checkin.app.checkin.user.models.ShopCustomerModel
import com.checkin.app.checkin.user.models.UserModel
import com.checkin.app.checkin.utility.ProgressRequestBody.UploadCallbacks
import com.fasterxml.jackson.databind.node.ObjectNode
import java.io.File

class UserViewModel(application: Application) : BaseViewModel(application) {
    private val mRepository: UserRepository = UserRepository.getInstance(application)

    private val mUserData = createNetworkLiveData<UserModel>()
    private val mUserRecentCheckinsData = createNetworkLiveData<List<ShopCustomerModel>>()
    private val mImageUploadResult = createNetworkLiveData<Void>()

    private var mLastData: ObjectNode? = null

    val hasRequestedUpdate: Boolean
        get() = mLastData != null

    override fun updateResults() {
        fetchUserData()
    }

    fun fetchUserData() {
        mUserData.addSource(mRepository.getUser(0), mUserData::setValue)
    }

    fun fetchUserRecentCheckinsData() {
        mUserRecentCheckinsData.addSource(mRepository.userRecentCheckins, mUserRecentCheckinsData::setValue)
    }

    val userData: LiveData<Resource<UserModel>>
        get() = mUserData

    val userRecentCheckinsData: LiveData<Resource<List<ShopCustomerModel>>>
        get() = mUserRecentCheckinsData

    val imageUploadResult: LiveData<Resource<Void>>
        get() = mImageUploadResult

    fun updateProfilePic(pictureFile: File, context: Context) {
        val builder = MessageUtils.createUploadNotification(context)
        val notificationUpdate = object : NotificationUpdate<GenericDetailModel>(context, builder) {
            override fun onProgressUpdate(percentage: Int) {
                super.onProgressUpdate(percentage)
                mImageUploadResult.postValue(loading<Void>(null))
            }

            override fun onSuccess(response: ApiResponse<GenericDetailModel>) {
                super.onSuccess(response)
                mImageUploadResult.postValue(success<Void>(null))
            }

            override fun onFailure(response: ApiResponse<GenericDetailModel>) {
                super.onFailure(response)
                mImageUploadResult.postValue(error<Void>("Unable to upload image", null))
            }
        }
        doUploadImage(pictureFile, notificationUpdate)
    }

    private fun doUploadImage(pictureFile: File, listener: UploadCallbacks<GenericDetailModel>) {
        RetrofitCallAsyncTask(listener)
                .execute(mRepository.postUserProfilePic(pictureFile, listener))
    }

    fun postUserData(firstName: String?, lastName: String?, phoneToken: String?, bio: String?) {
        val data = objectMapper.createObjectNode()
        data.put("first_name", firstName)
        data.put("last_name", lastName)
        data.put("bio", bio)
        data.put("is_public", true)
        if (!phoneToken.isNullOrEmpty()) data.put("phone_token", phoneToken)
        mLastData = data
        mUserData.addSource(mRepository.postUserData(data), mUserData::setValue)
    }

    fun patchUserPhone(phoneToken: String) {
        val data = objectMapper.createObjectNode()
                .put("phone_token", phoneToken)
        mLastData = data
        mUserData.addSource(mRepository.postUserData(data), mUserData::setValue)
    }

    fun postReferralCode(referralCode: String) {
        val data = objectMapper.createObjectNode()
        data.put("code", referralCode)
        mUserData.addSource(mRepository.postUserReferralCode(data), mUserData::setValue)
    }

    fun retryUpdateProfile() = mLastData?.let {
        mUserData.addSource(mRepository.postUserData(it), mUserData::setValue)
    }
}