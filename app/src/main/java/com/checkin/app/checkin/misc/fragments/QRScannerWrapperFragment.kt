package com.checkin.app.checkin.misc.fragments

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.core.app.ActivityCompat
import butterknife.BindView
import butterknife.OnClick
import com.checkin.app.checkin.R
import com.checkin.app.checkin.utility.inTransaction
import com.checkin.app.checkin.utility.parentActivityDelegate
import com.google.zxing.Result

class QRScannerWrapperFragment : BaseFragment(), QRScannerFragment.QRScannerInteraction {
    override val rootLayout: Int = R.layout.activity_qrscanner

    @BindView(R.id.btn_flash_toggle)
    internal lateinit var btnFlash: ImageView

    private val mScannerFragment = QRScannerFragment.newInstance()
    private val listener: QRScannerWrapperInteraction by parentActivityDelegate()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        checkValidCamera()
        setupFragment()
    }

    private fun checkValidCamera() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.CAMERA), PERMISSION_REQUEST_CAMERA)
        }
    }

    @OnClick(R.id.btn_flash_toggle)
    fun onFlashToggle(v: View?) {
        val value = !btnFlash.isActivated
        mScannerFragment!!.setFlash(value)
        btnFlash.isActivated = value
    }

    private fun setupFragment() {
        childFragmentManager.inTransaction { add(R.id.fragment_scanner, mScannerFragment) }
    }

    override fun onScannedResult(result: Result) {
        val data = Intent()
        data.putExtra(KEY_QR_RESULT, result.text)
        listener.onScanResult(Activity.RESULT_OK, data)
        onBackPressed()
    }

    override fun updateFlashState(isActivated: Boolean) {
        btnFlash.isActivated = isActivated
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSION_REQUEST_CAMERA -> {
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    listener.onScanResult(Activity.RESULT_CANCELED, null)
                    onBackPressed()
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    companion object {
        const val KEY_QR_RESULT = "qr.result"
        private const val PERMISSION_REQUEST_CAMERA = 101

        const val FRAGMENT_TAG = "qrscanner"
    }
}

interface QRScannerWrapperInteraction {
    fun onScanResult(result: Int, bundle: Intent?)
}