package com.checkin.app.checkin.misc.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import butterknife.ButterKnife
import com.checkin.app.checkin.R
import com.checkin.app.checkin.misc.fragments.QRScannerWrapperFragment
import com.checkin.app.checkin.misc.fragments.QRScannerWrapperInteraction
import com.checkin.app.checkin.utility.inTransaction

class QRScannerActivity : AppCompatActivity(), QRScannerWrapperInteraction {
    private val mScannerFragment: QRScannerWrapperFragment by lazy { QRScannerWrapperFragment() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qrscanner)
        ButterKnife.bind(this)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_back_grey)
            elevation = 10f
        }
        setupFragment()
    }

    private fun setupFragment() {
        supportFragmentManager.inTransaction {
            add(R.id.fragment_scanner, mScannerFragment)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        setResult(Activity.RESULT_CANCELED)
        finish()
        return true
    }

    override fun onScanResult(result: Int, bundle: Intent?) {
        setResult(result, bundle)
        finish()
    }
}