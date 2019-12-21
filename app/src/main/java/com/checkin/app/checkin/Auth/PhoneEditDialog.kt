package com.checkin.app.checkin.Auth

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.checkin.app.checkin.R

class PhoneEditDialog(context: Context, val listener: PhoneInteraction) : AlertDialog(context) {
    @BindView(R.id.btn_proceed)
    internal lateinit var btnProceed: Button
    @BindView(R.id.et_dialog_phone)
    internal lateinit var etPhone: EditText

    init {
        val contentView = layoutInflater.inflate(R.layout.fragment_edit_phone_dialog, null) as ViewGroup
        ButterKnife.bind(this, contentView)

        init()
    }

    private fun init() {
        btnProceed.isActivated = false
        etPhone.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                btnProceed.isActivated = s.length > 12
            }
        })
    }

    @OnClick(R.id.btn_proceed)
    fun onConfirm(v: EditText) {
        listener.onPhoneSubmit(v.text.toString())
        v.setText("")
    }

    override fun dismiss() {
        super.dismiss()
        listener.onPhoneCancel()
    }
}

interface PhoneInteraction {
    fun onPhoneSubmit(phone: String)
    fun onPhoneCancel()
}