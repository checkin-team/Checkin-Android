package com.checkin.app.checkin.session.scheduled.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import butterknife.ButterKnife
import com.checkin.app.checkin.R
import com.checkin.app.checkin.misc.activities.BaseActivity
import com.checkin.app.checkin.session.scheduled.fragments.QSRFoodReadyFragment
import com.checkin.app.checkin.utility.inTransaction
import com.checkin.app.checkin.utility.navigateBackToHome

class QSRFoodReadyActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qsr_food_ready)
        ButterKnife.bind(this)

        supportFragmentManager.inTransaction {
            add(R.id.frg_container_activity, QSRFoodReadyFragment.newInstance(intent.getLongExtra(KEY_SESSION_ID, 0L)))
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        navigateBackToHome()
        return true
    }

    companion object {
        private const val KEY_SESSION_ID = "qsr.session_id"

        fun withSessionIntent(context: Context, sessionId: Long) = Intent(context, QSRFoodReadyActivity::class.java).apply {
            putExtra(KEY_SESSION_ID, sessionId)
        }
    }
}