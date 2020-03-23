package com.checkin.app.checkin.utility

import android.widget.Button
import android.widget.EditText
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

fun Button.resetErrors() {
    tag = null
}

fun Button.addError(msg: String?) {
    tag = msg?.let { ToastError(it) }
}

fun Button.addError(view: EditText? = null, @IdRes viewId: Int = 0, msg: String?) {
    val key = view?.id ?: viewId
    val map = (tag as? MutableMap<Int, InputFieldError>) ?: mutableMapOf()
    msg?.also {
        map[key] = InputFieldError(view = view, viewId = viewId, msg = it)
    } ?: also {
        view?.error = null
        map.remove(key)
    }
    tag = map
}

val Button.isValidForm: Boolean
    get() = tag?.let { !(it is FormError || (it is Map<*, *> && it.isNotEmpty())) } ?: true

fun Button.validateField(view: EditText): Boolean {
    (tag as? Map<*, *>)?.also {
        val error = (it[view.id] as? InputFieldError)
        view.error = error?.msg ?: return true
        return false
    }
    return true
}

fun Fragment.validateForm(button: Button): Boolean = kotlin.runCatching {
    if (button.isValidForm) return true
    button.tag.let {
        when (it) {
            is ToastError -> {
                toast(it.msg)
                false
            }
            is Map<*, *> -> {
                it.forEach {
                    val error = it.value as InputFieldError
                    if (error.view != null) error.view.error = error.msg
                    else if (error.viewId != 0) view?.findViewById<EditText>(error.viewId)?.error = error.msg
                }
                false
            }
            else -> true
        }
    }
}.getOrNull() ?: true

fun AppCompatActivity.validateForm(button: Button): Boolean = button.tag?.let {
    when (it) {
        is ToastError -> {
            toast(it.msg)
            false
        }
        is Map<*, *> -> {
            it.forEach {
                val error = it.value as InputFieldError
                if (error.view != null) error.view.error = error.msg
                else if (error.viewId != 0) findViewById<EditText>(error.viewId)?.error = error.msg
            }
            false
        }
        else -> true
    }
} ?: false

sealed class FormError
data class ToastError(val msg: String) : FormError()
data class InputFieldError(@IdRes val viewId: Int = 0, val view: EditText? = null, val msg: String) : FormError()
