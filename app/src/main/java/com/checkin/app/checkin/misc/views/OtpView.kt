package com.checkin.app.checkin.misc.views

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.View
import android.widget.EditText
import android.widget.FrameLayout
import butterknife.BindViews
import butterknife.ButterKnife
import butterknife.OnClick
import com.checkin.app.checkin.R

class OtpView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    @OnClick
    fun onCLick() {
        etArray[0].requestFocus()
    }

    @BindViews(R.id.otp1, R.id.otp2, R.id.otp3, R.id.otp4, R.id.otp5, R.id.otp6)
    internal lateinit var etArray: Array<EditText>

    var otpValue: String
        get() {
            if (!validateOtp()) {
                return ""
            }
            var otp = ""
            for (et in etArray) {
                otp += et.text.toString()
            }
            return otp
        }
        set(value) {
            if (value.isBlank() && value.length != length) {
                return
            }
            value.forEachIndexed { index: Int, c: Char ->
                etArray[index].setText(c.toString())
            }
        }

    init {
        View.inflate(context, R.layout.view_otp, this).apply {
            ButterKnife.bind(this@OtpView)
        }

        //adding
        for (i in 0..5) {
            if (i == 5) {
                etArray[i].addTextChangedListener(GenericTextWatcher(etArray[i], null))
                break
            }
            etArray[i].addTextChangedListener(GenericTextWatcher(etArray[i], etArray[i + 1]))
        }

        //deleting
        for (i in 0..5) {
            if (i == 0) {
                etArray[i].setOnKeyListener(GenericKeyEvent(etArray[i], null))
                continue
            }
            etArray[i].setOnKeyListener(GenericKeyEvent(etArray[i], etArray[i - 1]))
        }
    }


    private fun validateOtp(): Boolean {
        for (et in etArray) {
            if (et.text.isNullOrBlank()) {
                return false
            }
        }
        return true
    }

    companion object {
        const val length = 6
    }

}

class GenericKeyEvent internal constructor(private val currentView: EditText, private val previousView: EditText?) : View.OnKeyListener {
    override fun onKey(p0: View?, keyCode: Int, event: KeyEvent?): Boolean {
        if (event!!.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_DEL && currentView.id != R.id.otp1 && currentView.text.isEmpty()) {
            previousView!!.text = null
            previousView.requestFocus()
            return true
        }
        return false
    }


}

class GenericTextWatcher internal constructor(private val currentView: View, private val nextView: View?) : TextWatcher {
    override fun afterTextChanged(editable: Editable) {
        val text = editable.toString()
        if (text.length == 1) nextView?.requestFocus()
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

    }
}