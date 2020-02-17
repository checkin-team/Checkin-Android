package com.checkin.app.checkin.auth.fragments

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
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
    @BindView(R.id.tv_dialog_phone_heading)
    internal lateinit var tvHeading: TextView

    var headingText: String = "Input phone number"
        set(value) {
            field = value
            tvHeading.text = value
        }

    init {
        val contentView = layoutInflater.inflate(R.layout.view_edit_phone_dialog, null) as ViewGroup
        ButterKnife.bind(this, contentView)
        setView(contentView)

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
    fun onConfirm() {
        if (!btnProceed.isActivated) return
        listener.onPhoneSubmit(etPhone.text.toString())
        etPhone.setText("")
        dismiss()
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        savedInstanceState.getString(STATE_HEADING_TEXT, null)?.let {
            headingText = it
        }
    }

    override fun onSaveInstanceState(): Bundle {
        return super.onSaveInstanceState().apply {
            putCharSequence(STATE_HEADING_TEXT, headingText)
        }
    }

    override fun dismiss() {
        super.dismiss()
        listener.onPhoneCancel()
    }

    companion object {
        const val STATE_HEADING_TEXT = "state.heading"
    }
}

interface PhoneInteraction {
    fun onPhoneSubmit(phone: String)
    fun onPhoneCancel()
}