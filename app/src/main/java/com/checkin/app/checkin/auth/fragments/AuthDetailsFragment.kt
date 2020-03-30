package com.checkin.app.checkin.auth.fragments

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import butterknife.BindView
import butterknife.OnClick
import com.checkin.app.checkin.R
import com.checkin.app.checkin.auth.AuthFragmentInteraction
import com.checkin.app.checkin.auth.AuthViewModel
import com.checkin.app.checkin.misc.fragments.BaseFragment
import com.checkin.app.checkin.user.models.UserModel
import com.checkin.app.checkin.utility.parentActivityDelegate
import com.google.android.material.textfield.TextInputLayout

class AuthDetailsFragment : BaseFragment() {
    override val rootLayout: Int = R.layout.fragment_auth_details

    @BindView(R.id.til_auth_userinfo_firstname)
    internal lateinit var tilFirstName: TextInputLayout
    @BindView(R.id.til_auth_userinfo_lastname)
    internal lateinit var tilLastName: TextInputLayout
    @BindView(R.id.btn_auth_userinfo_proceed)
    internal lateinit var btnProceed: Button

    private val interaction: AuthFragmentInteraction by parentActivityDelegate()
    private val args: AuthDetailsFragmentArgs by navArgs()

    val name: String by lazy {
        args.name
    }

    val viewModel: AuthViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        name.indexOf(" ").takeIf { it > 0 }?.let {
            tilFirstName.editText?.setText(name.substring(0, it))
            tilFirstName.editText?.setText(name.substring(name.lastIndexOf(" ")))
        } ?: kotlin.run {
            tilFirstName.editText?.setText(name)
        }
    }

    @OnClick(R.id.btn_auth_userinfo_proceed)
    fun onProceedClicked() {
        val firstname = tilFirstName.editText?.text.toString()
        val lastname = tilLastName.editText?.text.toString()
        if (firstname.isEmpty()) {
            tilFirstName.error = "This field cannot be empty"
            return
        }
        //FIXME assuming the gender to be male and username to be firstname+lastname
        interaction.onUserInfoProcess(firstname, lastname, "$firstname$lastname",
                UserModel.GENDER.MALE)
    }

    companion object {
        const val KEY_NAME = "name"
        fun newInstance() = AuthDetailsFragment()
    }
}
