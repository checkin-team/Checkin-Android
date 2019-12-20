package com.checkin.app.checkin.misc.fragments

import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import butterknife.BindView
import butterknife.OnClick
import com.checkin.app.checkin.Data.Resource
import com.checkin.app.checkin.R
import com.checkin.app.checkin.misc.BlockingNetworkViewModel

class NetworkBlockingFragment : BaseFragment() {
    override val rootLayout: Int = R.layout.fragment_blocking_network

    @BindView(R.id.container_blocking_network_error)
    internal lateinit var containerError: ViewGroup
    @BindView(R.id.container_blocking_network_loading)
    internal lateinit var containerLoading: ViewGroup
    @BindView(R.id.tv_blocking_error_message)
    internal lateinit var tvErrorMessage: TextView

    val viewModel: BlockingNetworkViewModel by activityViewModels()
    val touchListener = View.OnTouchListener { v, event -> event.action == MotionEvent.ACTION_DOWN }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initProgressBar(R.id.pb_network_blocking_loading)
        view.setOnTouchListener(touchListener)
        handleSuccess()
        viewModel.networkBlockingData.observe(this, Observer {
            it?.let { resource ->
                when (resource.status) {
                    Resource.Status.ERROR_DISCONNECTED, Resource.Status.ERROR_UNKNOWN -> handleError(resource)
                    Resource.Status.LOADING -> handleLoading()
                    Resource.Status.SUCCESS -> handleSuccess()
                }
            }
        })
    }

    private fun handleSuccess() {
        hideProgressBar()
        view?.visibility = View.GONE
        containerError.visibility = View.GONE
        containerLoading.visibility = View.GONE
    }

    private fun handleLoading() {
        visibleProgressBar()
        view?.visibility = View.VISIBLE
        view?.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.translucent_white))
        containerLoading.visibility = View.VISIBLE
        containerError.visibility = View.GONE
    }

    private fun handleError(resource: Resource<Any?>) {
        hideProgressBar()
        view?.visibility = View.VISIBLE
        containerError.visibility = View.GONE
        containerLoading.visibility = View.VISIBLE
    }

    @OnClick(R.id.btn_blocking_error_try_again)
    fun onClickTryAgain() {
        viewModel.tryAgain()
    }

    companion object {
        const val FRAGMENT_TAG = "network_blocking"
    }
}