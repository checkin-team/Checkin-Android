package com.checkin.app.checkin.misc.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import butterknife.BindView
import butterknife.ButterKnife
import com.checkin.app.checkin.R
import com.checkin.app.checkin.utility.Constants.PLAY_STORE_URI

class UpdateAppActivity : AppCompatActivity() {
    @BindView(R.id.container_blocking_network_error)
    internal lateinit var containerError: ViewGroup
    @BindView(R.id.container_blocking_network_loading)
    internal lateinit var containerLoading: ViewGroup
    @BindView(R.id.tv_blocking_error_message)
    internal lateinit var tvErrorMessage: TextView
    @BindView(R.id.btn_blocking_error_try_again)
    internal lateinit var btnPlayStore: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_blocking_network)
        ButterKnife.bind(this)

        containerLoading.visibility = View.GONE
        containerError.visibility = View.VISIBLE
        tvErrorMessage.text = "${getString(R.string.app_old_version_dialog_title)}\n${getString(R.string.app_old_version_dialog_message)}"
        btnPlayStore.setText(R.string.app_old_version_button_update)
        btnPlayStore.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, PLAY_STORE_URI))
            finish()
        }
    }
}