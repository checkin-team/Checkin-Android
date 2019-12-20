package com.checkin.app.checkin.misc.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.annotation.LayoutRes
import butterknife.ButterKnife
import butterknife.Unbinder
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

abstract class BaseBottomSheetFragment : BottomSheetDialogFragment() {
    private var unbinder: Unbinder? = null

    @get:LayoutRes
    protected abstract val rootLayout: Int

    @CallSuper
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(rootLayout, container, false)
        unbinder = ButterKnife.bind(this, view)
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        unbinder?.unbind()
    }
}
