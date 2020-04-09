package com.checkin.app.checkin.auth.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import butterknife.BindView
import butterknife.OnClick
import com.checkin.app.checkin.R
import com.checkin.app.checkin.auth.AuthFragmentInteraction
import com.checkin.app.checkin.auth.AuthViewModel
import com.checkin.app.checkin.data.resource.Resource
import com.checkin.app.checkin.misc.activities.SelectCropImageActivity
import com.checkin.app.checkin.misc.fragments.BaseFragment
import com.checkin.app.checkin.user.models.UserModel
import com.checkin.app.checkin.user.viewmodels.UserViewModel
import com.checkin.app.checkin.utility.Utils
import com.checkin.app.checkin.utility.parentActivityDelegate
import com.checkin.app.checkin.utility.toast
import com.google.android.material.textfield.TextInputLayout
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

    private val interaction: AuthFragmentInteraction by parentActivityDelegate()
    private val args: AuthDetailsFragmentArgs by navArgs()
    private val authViewModel: AuthViewModel by activityViewModels()
    private val userViewModel: UserViewModel by viewModels()

    private val name: List<String> by lazy {
        args.name?.split("\\s+".toRegex()) ?: listOf(defaultFirstName)
    }
    private val mobileNo: String by lazy { args.phone }

    private fun setupObservers() {
        userViewModel.userData.observe(this, Observer<Resource<UserModel>> {
            it.let {
                when (it.status) {
                    Resource.Status.SUCCESS -> {
                        setupData(it.data)
                    }
                }
            }
        })
        authViewModel.authResult.observe(this, Observer {
            when (it.status) {
                Resource.Status.SUCCESS -> userViewModel.fetchUserData()
            }

        })
        userViewModel.imageUploadResult.observe(this, Observer {
            it?.let {
                when (it.status) {
                    Resource.Status.SUCCESS -> userViewModel.updateResults()
                    Resource.Status.LOADING -> {
                    }
                    Resource.Status.ERROR_UNKNOWN -> toast(it.message)
                    else -> toast(it.message)
                }
            }
        })
    }

    private fun setupData(data: UserModel?) {
        Utils.loadImageOrDefault(imProfilePhoto, data?.profilePic, R.drawable.ic_auth_profile)
        toast("${data?.firstName} ${data?.lastName}")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupObservers()
        setupUser()
        name.takeIf { it.size > 1 }?.let {
            val s = it
            tilFirstName.editText?.setText(s[0])
            tilLastName.editText?.setText(s[1])
        }
    }

    fun setupUser() {
        val firstName = name[0]
        //this is an issue cause this immediately calls auth result callback for success which shuts the activity
        // a better logic should fbe figures out.
        interaction.onUserInfoProcess(firstName = firstName, lastName = null, username = mobileNo, gender = defaultGender)
    }

    @OnClick(R.id.btn_auth_userinfo_proceed)
    fun onProceedClicked() {
        if (!tilFirstName.editText?.text.isNullOrEmpty()) {
            val firstName = tilFirstName.editText?.text.toString()
            val lastName = tilLastName.editText?.text.toString()
            userViewModel.postUserData(firstName, lastName, "", null)
        }


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
            if (data?.extras != null) {
                val image = data.extras[SelectCropImageActivity.KEY_IMAGE] as File
                userViewModel.updateProfilePic(image, requireContext())
            }
        } else {
            userViewModel.updateResults()
        }
    }

    companion object {
        val TAG = AuthDetailsFragment::class.simpleName
        val defaultFirstName = "Checkin"
        val defaultGender = UserModel.GENDER.MALE
    }
}
