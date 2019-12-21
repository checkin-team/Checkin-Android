package com.checkin.app.checkin.misc.activities

import android.annotation.SuppressLint
import android.util.Log
import android.view.View
import android.widget.ProgressBar

import com.checkin.app.checkin.Data.Resource.Status

import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.checkin.app.checkin.misc.fragments.DataStatusFragment

@SuppressLint("Registered")
open class BaseActivity : AppCompatActivity() {
    private var mFragment: DataStatusFragment? = null
    private var isFragmentShown = false

    @IdRes
    private var vGroupId: Int = 0

    private var swipeRefreshLayout: SwipeRefreshLayout? = null
    private var progressBar: ProgressBar? = null

    fun init(@IdRes groupId: Int, isNetworkRequired: Boolean) {
        if (mFragment != null) {
            Log.e(TAG, "init called with existing fragment!")
            return
        }
        vGroupId = groupId
        mFragment = DataStatusFragment.newInstance(isNetworkRequired)
    }

    fun initLoad() {
        if (!isFragmentShown) {
            showFragment()
        }
    }

    fun doneLoad() {
        hideFragment()
    }

    fun alertStatus(status: Status, msg: String) {
        if (!isFragmentShown)
            showFragment()
        mFragment!!.showErrorStatus(status, msg)
    }

    private fun showFragment() {
        val ft = supportFragmentManager.beginTransaction()
        ft.add(vGroupId, mFragment!!, null)
        ft.commit()
        isFragmentShown = true
    }

    private fun hideFragment() {
        val ft = supportFragmentManager.beginTransaction()
        ft.remove(mFragment!!)
        ft.commit()
        isFragmentShown = false
    }

    protected fun initRefreshScreen(@IdRes viewId: Int) {
        swipeRefreshLayout = findViewById(viewId)
        swipeRefreshLayout?.setOnRefreshListener { this.updateScreen() }
    }

    protected open fun updateScreen() {}

    protected fun startRefreshing() {
        swipeRefreshLayout?.isRefreshing = true
    }

    protected fun stopRefreshing() {
        swipeRefreshLayout?.isRefreshing = false
    }

    protected fun initProgressBar(@IdRes viewId: Int) {
        progressBar = findViewById(viewId)
        progressBar?.visibility = View.GONE
    }

    protected fun visibleProgressBar() {
        progressBar?.visibility = View.VISIBLE
    }

    protected fun hideProgressBar() {
        progressBar?.visibility = View.GONE
    }

    companion object {
        private val TAG = BaseActivity::class.java.simpleName
    }
}
