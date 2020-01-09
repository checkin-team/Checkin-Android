package com.checkin.app.checkin.misc.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.annotation.CallSuper

import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import butterknife.ButterKnife
import butterknife.Unbinder
import com.checkin.app.checkin.Data.Resource
import com.checkin.app.checkin.misc.activities.BaseActivity

abstract class BaseFragment : Fragment() {
    private var unbinder: Unbinder? = null

    private var swipeRefreshLayout: SwipeRefreshLayout? = null
    private var progressBar: ProgressBar? = null

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

    open fun updateScreen() {
        (activity as? BaseActivity)?.updateScreen()
    }

    protected fun startRefreshing() {
        swipeRefreshLayout?.isRefreshing = true
    }

    protected fun stopRefreshing() {
        swipeRefreshLayout?.isRefreshing = false
    }

    protected fun enableDisableSwipeRefresh(enable: Boolean) {
        swipeRefreshLayout?.isEnabled = enable
    }

    protected fun initProgressBar(@IdRes viewId: Int) {
        progressBar = view?.findViewById(viewId)
        progressBar?.visibility = View.GONE
    }

    protected fun visibleProgressBar() {
        progressBar?.visibility = View.VISIBLE
    }

    protected fun hideProgressBar() {
        progressBar?.visibility = View.GONE
    }

    protected fun handleLoadingRefresh(resource: Resource<*>) {
        when (resource.status) {
            Resource.Status.LOADING -> startRefreshing()
            else -> stopRefreshing()
        }
    }

    open fun onBackPressed(): Boolean = false

    override fun onDestroyView() {
        super.onDestroyView()
        unbinder?.unbind()
    }
}
