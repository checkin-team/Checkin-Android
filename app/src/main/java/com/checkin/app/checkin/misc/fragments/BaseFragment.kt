package com.checkin.app.checkin.misc.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper

import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import butterknife.ButterKnife
import butterknife.Unbinder

abstract class BaseFragment : Fragment() {
    private var unbinder: Unbinder? = null

    private var swipeRefreshLayout: SwipeRefreshLayout? = null

    @get:LayoutRes
    protected abstract val rootLayout: Int

    @CallSuper
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(rootLayout, container, false)
        unbinder = ButterKnife.bind(this, view)
        return view
    }

    protected fun initRefreshScreen(@IdRes viewId: Int) = view?.let {
        swipeRefreshLayout = it.findViewById(viewId)
        swipeRefreshLayout?.setOnRefreshListener { this.updateScreen() }
    }

    @CallSuper
    protected open fun updateScreen() {}

    protected fun startRefreshing() {
        swipeRefreshLayout?.isRefreshing = true
    }

    protected fun stopRefreshing() {
        swipeRefreshLayout?.isRefreshing = false
    }

    open fun onBackPressed(): Boolean = false

    override fun onDestroyView() {
        super.onDestroyView()
        unbinder?.unbind()
    }
}

interface FragmentInteraction {
    fun onAddFragment(fragment: Fragment, tag: String? = null)
}