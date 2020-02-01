package com.checkin.app.checkin.Auth

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import butterknife.BindView
import butterknife.OnClick
import com.checkin.app.checkin.R
import com.checkin.app.checkin.misc.fragments.BaseFragment
import com.checkin.app.checkin.user.models.UserModel.GENDER
import com.checkin.app.checkin.utility.DebouncedOnClickListener
import com.checkin.app.checkin.utility.ParentActivityDelegate

class SignupUserInfoFragment : BaseFragment() {
    @BindView(R.id.ed_firstname)
    internal lateinit var edFirstName: EditText
    @BindView(R.id.ed_lastname)
    internal lateinit var edLastName: EditText
    @BindView(R.id.im_male)
    internal lateinit var imMale: FrameLayout
    @BindView(R.id.im_female)
    internal lateinit var imFemale: FrameLayout
    @BindView(R.id.ed_Username)
    internal lateinit var edUsername: EditText
    @BindView(R.id.btn_enter)
    internal lateinit var btnEnter: Button

    private val fragmentInteraction: AuthFragmentInteraction by ParentActivityDelegate(this)
    private var genderChosen: GENDER? = null

    override val rootLayout = R.layout.fragment_signup_user_info

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val name = arguments?.getString(KEY_NAME) ?: ""
        name.indexOf(" ").takeIf { it > 0 }?.let {
            edFirstName.setText(name.substring(0, it))
            edLastName.setText(name.substring(name.lastIndexOf(" ")))
        } ?: kotlin.run {
            edFirstName.setText(name)
        }

        val viewModel = ViewModelProviders.of(requireActivity()).get(AuthViewModel::class.java)
        viewModel.errors.observe(this, Observer {
            it?.let { jsonNodes ->
                if (jsonNodes.has("username")) {
                    val userNode = jsonNodes.get("username")
                    val msg = userNode.get(0).asText()
                    edUsername.error = msg
                }
            }
        })

        btnEnter.setOnClickListener(DebouncedOnClickListener { v ->
            onProceedClicked()
            Unit
        })
    }

    @OnClick(R.id.im_female, R.id.im_male)
    fun onGenderIconClicked(icon: View) {
        icon.isSelected = true
        genderChosen = if (icon.id == R.id.im_male) GENDER.MALE else GENDER.FEMALE
        if (genderChosen == GENDER.MALE)
            imFemale.isSelected = false
        else
            imMale.isSelected = false
    }

    private fun onProceedClicked() {
        val firstname = edFirstName.text.toString()
        val lastname = edLastName.text.toString()
        val username = edUsername.text.toString()
        if (username.isEmpty()) {
            edUsername.error = "This field cannot be empty"
            return
        }
        if (firstname.isEmpty()) {
            edFirstName.error = "This field cannot be empty"
            return
        }
        if (genderChosen == null) {
            Toast.makeText(context, "Please select gender", Toast.LENGTH_SHORT).show()
            return
        }
        fragmentInteraction.onUserInfoProcess(firstname, lastname, username, genderChosen)
    }

    companion object {
        val KEY_NAME = "name"

        fun newInstance() = SignupUserInfoFragment()
    }
}
