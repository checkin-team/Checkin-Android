package com.checkin.app.checkin.Cook

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import butterknife.BindView
import butterknife.ButterKnife
import co.zsmb.materialdrawerkt.builders.DrawerBuilderKt
import com.checkin.app.checkin.Cook.Fragment.CookTablesFragment
import com.checkin.app.checkin.R
import com.checkin.app.checkin.Utility.Utils
import com.checkin.app.checkin.accounts.ACCOUNT_TYPE
import com.checkin.app.checkin.accounts.BaseAccountActivity
import com.checkin.app.checkin.data.resource.Resource

class CookWorkActivity : BaseAccountActivity() {
    @BindView(R.id.toolbar_cook_work)
    internal lateinit var toolbar: Toolbar

    private val mViewModel: CookWorkViewModel by viewModels()

    override val toolbarView: Toolbar?
        get() = toolbar

    override val accountTypes: Array<ACCOUNT_TYPE> = arrayOf(ACCOUNT_TYPE.RESTAURANT_COOK)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cook_work)
        ButterKnife.bind(this)
        setupUi()

        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            elevation = resources.getDimension(R.dimen.card_elevation)
            setDisplayHomeAsUpEnabled(true)
        }
        initRefreshScreen(R.id.sr_cook_work)
        setupObservers(intent.getLongExtra(KEY_RESTAURANT_PK, 0L))
        supportFragmentManager.beginTransaction()
                .add(R.id.container_cook_tables, CookTablesFragment.newInstance())
                .commit()
    }

    private fun setupObservers(shopId: Long) {
        mViewModel.fetchActiveTables(shopId)
        mViewModel.activeTables.observe(this, Observer { input ->
            if (input.status === Resource.Status.SUCCESS && input.data != null) {
                stopRefreshing()
            } else if (input.status === Resource.Status.LOADING) startRefreshing() else {
                stopRefreshing()
                Utils.toast(this, input.message)
            }
        })
    }

    override fun setupDrawerItems(builder: DrawerBuilderKt) {

    }

    override fun updateScreen() {
        super.updateScreen()
        accountViewModel.updateResults()
        mViewModel.updateResults()
    }

    companion object {
        const val KEY_RESTAURANT_PK = "cook.restaurant_pk"
    }
}