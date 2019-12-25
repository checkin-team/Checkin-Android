package com.checkin.app.checkin.session.scheduled.activities

import android.os.Bundle
import butterknife.ButterKnife
import com.checkin.app.checkin.R
import com.checkin.app.checkin.Utility.inTransaction
import com.checkin.app.checkin.misc.activities.BaseActivity
import com.checkin.app.checkin.session.scheduled.fragments.QsrFoodReadyFragment

class QSRFoodReadyActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qsr_food_ready)
        ButterKnife.bind(this)

        supportFragmentManager.inTransaction {
            add(R.id.frg_container_activity, QsrFoodReadyFragment.newInstance(intent.getLongExtra(KEY_SESSION_ID, 0L)))
        }
    }

    companion object {
        const val KEY_SESSION_ID = "qsr.session_id"
    }
}