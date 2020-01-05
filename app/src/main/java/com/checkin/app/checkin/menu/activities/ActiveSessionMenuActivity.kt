package com.checkin.app.checkin.menu.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import butterknife.ButterKnife
import com.checkin.app.checkin.R
import com.checkin.app.checkin.Utility.inTransaction
import com.checkin.app.checkin.menu.fragments.MenuFragment
import com.checkin.app.checkin.menu.viewmodels.ActiveSessionCartViewModel
import com.checkin.app.checkin.misc.activities.BaseActivity

class ActiveSessionMenuActivity : BaseActivity() {
    private val cartViewModel: ActiveSessionCartViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_active_session_menu)
        ButterKnife.bind(this)

        supportActionBar?.title = "Menu"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        initUi()
    }

    private fun initUi() {
        val restaurantId = intent.getLongExtra(KEY_RESTAURANT_ID, 0L)
        supportFragmentManager.inTransaction {
            add(R.id.frg_container_activity, MenuFragment.newInstanceForActiveSession(restaurantId), null)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    companion object {
        const val KEY_RESTAURANT_ID = "as.menu.restaurant_id"

        fun openMenu(context: Context, restaurantId: Long) = Intent(context, ActiveSessionMenuActivity::class.java).apply {
            putExtra(KEY_RESTAURANT_ID, restaurantId)
            context.startActivity(this)
        }
    }
}