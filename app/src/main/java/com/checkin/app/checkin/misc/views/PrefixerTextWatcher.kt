package com.checkin.app.checkin.misc.views

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText

class PrefixerTextWatcher(private val prefix: String, private val editText: EditText) : TextWatcher {
    private var fix: String? = null

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        fix = s?.toString()?.replace(prefix, "");
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        val string = s?.toString() ?: return
        val fl = prefix.length
        val sl = string.length
        if (sl < fl) {
            val `in` = prefix
            editText.setText(`in`)
            editText.setSelection(`in`.length)
        } else {
            val cek = string.substring(0, fl)
            if (cek != prefix) {
                if (string.matches(rubah(prefix))) {
                    val `in` = prefix + string.replace(prefix, "")
                    editText.setText(`in`)
                    editText.setSelection(`in`.length)
                } else {
                    val `in` = prefix + fix
                    editText.setText(`in`)
                    editText.setSelection(`in`.length)
                }
            }
        }
    }

    override fun afterTextChanged(s: Editable?) {
    }

    private fun rubah(s: String): Regex = s
            .replace("+", "\\+")
            .replace("$", "\\$")
            .replace("^", "\\^")
            .replace("*", "\\*")
            .replace("?", "\\?")
            .toRegex()
}