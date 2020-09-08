package com.checkin.app.checkin.auth.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import butterknife.BindView
import butterknife.OnClick
import com.checkin.app.checkin.R
import com.checkin.app.checkin.data.resource.Resource
import com.checkin.app.checkin.misc.activities.SelectCropImageActivity
import com.checkin.app.checkin.misc.fragments.BaseFragment
import com.checkin.app.checkin.user.models.UserModel
import com.checkin.app.checkin.user.viewmodels.UserViewModel
import com.checkin.app.checkin.utility.Utils
import com.checkin.app.checkin.utility.toast
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import java.io.File

class AuthDetailsFragment : BaseFragment() {
    override val rootLayout: Int = R.layout.fragment_auth_details

    @BindView(R.id.til_auth_details_firstname)
    internal lateinit var tilFirstName: TextInputLayout
    @BindView(R.id.til_auth_details_lastname)
    internal lateinit var tilLastName: TextInputLayout
    @BindView(R.id.btn_auth_userinfo_proceed)
    internal lateinit var btnProceed: Button
    @BindView(R.id.im_auth_details_profile_photo)
    internal lateinit var imProfilePhoto: ImageView

    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val userViewModel: UserViewModel by activityViewModels()

    private fun setupObservers() {
        userViewModel.userData.observe(viewLifecycleOwner, Observer {
            it.let {
                when (it.status) {
                    Resource.Status.SUCCESS -> {
                        if (userViewModel.hasRequestedUpdate) Utils.navigateBackToHome(context)
                        setupData(it.data!!)
                    }
                    else -> toast(it.message)
                }
            }
        })
        userViewModel.imageUploadResult.observe(viewLifecycleOwner, Observer {
            it?.let {
                when (it.status) {
                    Resource.Status.SUCCESS -> {
                        userViewModel.fetchUserData()
                        toast("Profile picture updated!")
                    }
                    Resource.Status.LOADING -> {
                        Log.d(TAG, "Image Uploading")
                    }
                    Resource.Status.ERROR_UNKNOWN -> toast(it.message)
                    else -> toast(it.message)
                }
            }
        })
    }

    private fun setupData(data: UserModel? = null) {
        (data?.profilePic ?: firebaseAuth.currentUser?.photoUrl?.toString())?.also {
            Utils.loadImageOrDefault(imProfilePhoto, it, R.drawable.ic_auth_profile)
        }

        val fullName = if (data != null) arrayOf(data.firstName, data.lastName) else arrayOf(defaultFirstName, "")
        firebaseAuth.currentUser?.displayName?.also {
            it.split("\\s+".toRegex()).toTypedArray().copyInto(fullName, endIndex = 2)
        }
        tilFirstName.editText?.setText(fullName[0])
        tilLastName.editText?.setText(fullName[1])
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupObservers()
        setupData()
    }

    @OnClick(R.id.btn_auth_userinfo_proceed)
    fun onProceedClicked() {
        val firstName = tilFirstName.editText?.text.takeUnless { it.isNullOrBlank() }?.toString()
                ?: kotlin.run {
                    toast("Atleast provide a first name")
                    return
                }
        val lastName = tilLastName.editText?.text?.toString() ?: ""
        userViewModel.postUserData(firstName, lastName, null, null)
    }

    @OnClick(R.id.im_auth_details_profile_photo)
    fun onAddImage() {
        val intent = Intent(requireContext(), SelectCropImageActivity::class.java)
        intent.putExtra(SelectCropImageActivity.KEY_FILE_SIZE_IN_MB, 4L)
        startActivityForResult(intent, SelectCropImageActivity.RC_CROP_IMAGE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SelectCropImageActivity.RC_CROP_IMAGE && resultCode == Activity.RESULT_OK) {
            val image = data?.extras?.get(SelectCropImageActivity.KEY_IMAGE) as? File
                    ?: kotlin.run {
                        Log.e(TAG, "Nothing in bundle: ${data?.extras}")
                        return
                    }
            context?.also { userViewModel.updateProfilePic(image, it) }
        } else {
            userViewModel.updateResults()
        }
    }

    companion object {
        val TAG = AuthDetailsFragment::class.simpleName

        private const val defaultFirstName = "Anonymous"
    }
}
