package com.checkin.app.checkin.misc.fragments

import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import butterknife.BindView
import butterknife.OnClick
import com.checkin.app.checkin.R
import com.checkin.app.checkin.data.resource.Resource
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
                    Resource.Status.ERROR_DISCONNECTED,
                    Resource.Status.ERROR_UNKNOWN,
                    Resource.Status.ERROR_NOT_FOUND_CACHED -> handleError(resource)
                    Resource.Status.LOADING -> handleLoading()
                    else -> handleSuccess()
                }
            } ?: handleSuccess()
        })
    }

    private fun handleSuccess() {
        hideProgressBar()
        view?.visibility = View.GONE
        containerError.visibility = View.GONE
        containerLoading.visibility = View.GONE
    }

    private fun handleLoading() {
        if (arguments?.getBoolean(KEY_BLOCKING_LOADER, false) == true) {
            visibleProgressBar()
            view?.visibility = View.VISIBLE
            view?.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.translucent_white))
            containerLoading.visibility = View.VISIBLE
            containerError.visibility = View.GONE
        } else {
            view?.visibility = View.GONE
            view?.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.transparent))
        }
    }

    private fun handleError(resource: Resource<Any?>) {
        hideProgressBar()
        view?.visibility = View.VISIBLE
        view?.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white))
        containerError.visibility = View.VISIBLE
        containerLoading.visibility = View.GONE
        if (resource.status == Resource.Status.ERROR_DISCONNECTED || resource.status == Resource.Status.ERROR_NOT_FOUND_CACHED)
            tvErrorMessage.setText(R.string.text_error_network_disconnected)
        else
            tvErrorMessage.setText(R.string.text_error_network_unknown)
    }

    @OnClick(R.id.btn_blocking_error_try_again)
    fun onClickTryAgain() {
        viewModel.tryAgain()
    }

    companion object {
        const val FRAGMENT_TAG = "network_blocking"
        const val KEY_BLOCKING_LOADER = "block_loader"

        fun withBlockingLoader() = NetworkBlockingFragment().apply {
            arguments = bundleOf(KEY_BLOCKING_LOADER to true)
        }
    }
}