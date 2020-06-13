package com.checkin.app.checkin.auth.fragments

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import butterknife.BindView
import com.checkin.app.checkin.R
import com.checkin.app.checkin.data.resource.Resource
import com.checkin.app.checkin.home.activities.HomeActivity
import com.checkin.app.checkin.misc.fragments.BaseFragment
import com.checkin.app.checkin.user.viewmodels.UserViewModel
import com.checkin.app.checkin.utility.toast
import com.google.android.material.textfield.TextInputLayout

class AuthReferralFragment : BaseFragment() {
    override val rootLayout: Int = R.layout.fragment_auth_referral

    @BindView(R.id.til_auth_referral_code)
    internal lateinit var tilAuthReferralCode: TextInputLayout

    @BindView(R.id.btn_auth_referral_skip)
    internal lateinit var btnAuthReferralSkip: Button

    private val userViewModel: UserViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        userViewModel.userData.observe(viewLifecycleOwner, Observer {
            it?.let {
                when (it.status) {
                    Resource.Status.SUCCESS -> {
                        closeAuth()
                    }
                }
            }
        })

        tilAuthReferralCode.setEndIconOnClickListener {
            val code = tilAuthReferralCode.editText?.text.toString()
            if (code.isEmpty()) {
                toast("Invalid code try again !")
                return@setEndIconOnClickListener
            }
            userViewModel.postReferralCode(code)
        }

        btnAuthReferralSkip.setOnClickListener {
            closeAuth()
        }
    }

    private fun closeAuth() {
        requireActivity().apply {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }
    }
}