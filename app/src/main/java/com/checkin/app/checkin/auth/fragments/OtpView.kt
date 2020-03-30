package com.checkin.app.checkin.auth.fragments

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

    @BindViews(R.id.otp1, R.id.otp2, R.id.otp3, R.id.otp4)
    internal lateinit var etArray: Array<EditText>

    var otpValue: String?
        get() {
            if (!validateOtp()) {
                return null
            }
            val otp = ""
            for (et in etArray) {
                otp.plus(et.text.toString())
            }
            return otp
        }
        set(value) {
            if (value.isNullOrBlank() && value?.length!! != 4) {
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
        etArray[0].addTextChangedListener(GenericTextWatcher(etArray[0], etArray[1]))
        etArray[1].addTextChangedListener(GenericTextWatcher(etArray[1], etArray[2]))
        etArray[2].addTextChangedListener(GenericTextWatcher(etArray[2], etArray[3]))
        etArray[3].addTextChangedListener(GenericTextWatcher(etArray[3], null))

//GenericKeyEvent here works for deleting the element and to switch back to previous EditText
//first parameter is the current EditText and second parameter is previous EditText
        etArray[0].setOnKeyListener(GenericKeyEvent(etArray[0], null))
        etArray[1].setOnKeyListener(GenericKeyEvent(etArray[1], etArray[0]))
        etArray[2].setOnKeyListener(GenericKeyEvent(etArray[2], etArray[1]))
        etArray[3].setOnKeyListener(GenericKeyEvent(etArray[3], etArray[2]))
    }


    private fun validateOtp(): Boolean {
        for (et in etArray) {
            if (et.text.isNullOrBlank()) {
                return false
            }
        }
        return true
    }

}

class GenericKeyEvent internal constructor(private val currentView: EditText, private val previousView: EditText?) : View.OnKeyListener {
    override fun onKey(p0: View?, keyCode: Int, event: KeyEvent?): Boolean {
        if (event!!.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_DEL && currentView.id != R.id.otp1 && currentView.text.isEmpty()) {
            //If current is empty then previous EditText's number will also be deleted
            previousView!!.text = null
            previousView.requestFocus()
            return true
        }
        return false
    }


}

class GenericTextWatcher internal constructor(private val currentView: View, private val nextView: View?) : TextWatcher {
    override fun afterTextChanged(editable: Editable) { // TODO Auto-generated method stub
        val text = editable.toString()
        when (currentView.id) {
            R.id.otp1 -> if (text.length == 1) nextView!!.requestFocus()
            R.id.otp2 -> if (text.length == 1) nextView!!.requestFocus()
            R.id.otp3 -> if (text.length == 1) nextView!!.requestFocus()
            //You can use EditText4 same as above to hide the keyboard
        }
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

    }
}