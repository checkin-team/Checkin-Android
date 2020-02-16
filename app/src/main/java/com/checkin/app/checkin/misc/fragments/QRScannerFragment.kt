package com.checkin.app.checkin.misc.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.checkin.app.checkin.utility.Utils
import com.checkin.app.checkin.utility.parentActivityDelegate
import com.google.zxing.BarcodeFormat
import com.google.zxing.Result
import me.dm7.barcodescanner.zxing.ZXingScannerView
import me.dm7.barcodescanner.zxing.ZXingScannerView.ResultHandler

class QRScannerFragment : Fragment(), ResultHandler {
    private lateinit var mScannerView: ZXingScannerView
    private val mListener: QRScannerInteraction by parentActivityDelegate()
    private var mFlash = false
    private var mAutoFocus = false
    private var mCameraId = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mScannerView = ZXingScannerView(requireContext())
        return mScannerView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if (savedInstanceState != null) {
            mFlash = savedInstanceState.getBoolean(STATE_FLASH, false)
            mAutoFocus = savedInstanceState.getBoolean(STATE_AUTO_FOCUS, true)
            mCameraId = savedInstanceState.getInt(CAMERA_ID, -1)
        } else {
            mFlash = false
            mAutoFocus = true
            mCameraId = -1
        }
        setupFormats()
    }

    override fun onResume() {
        super.onResume()
        mScannerView.runCatching {
            setResultHandler(this@QRScannerFragment)
            startCamera(mCameraId)
            setAutoFocus(mAutoFocus)
        }.onFailure { Utils.logErrors(TAG, it) }
        setFlash(mFlash)
        mListener.updateFlashState(mFlash)
    }

    fun setFlash(value: Boolean) {
        mFlash = value
        mScannerView.runCatching { flash = mFlash }.onFailure { Utils.logErrors(TAG, it) }
    }

    override fun onPause() {
        super.onPause()
        mScannerView.runCatching { stopCamera() }.onFailure { Utils.logErrors(TAG, it) }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(STATE_FLASH, mFlash)
        outState.putBoolean(STATE_AUTO_FOCUS, mAutoFocus)
        outState.putInt(CAMERA_ID, mCameraId)
    }

    private fun setupFormats() {
        mScannerView.setFormats(listOf(BarcodeFormat.QR_CODE))
    }

    override fun handleResult(result: Result) {
        mListener.onScannedResult(result)
    }

    interface QRScannerInteraction {
        fun onScannedResult(result: Result)
        fun updateFlashState(isActivated: Boolean)
    }

    companion object {
        private val TAG = QRScannerFragment::class.simpleName
        private const val STATE_FLASH = "FLASH_STATE"
        private const val STATE_AUTO_FOCUS = "AUTO_FOCUS_STATE"
        private const val CAMERA_ID = "CAMERA_ID"

        @JvmStatic
        fun newInstance() = QRScannerFragment()
    }
}