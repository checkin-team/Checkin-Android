package com.checkin.app.checkin.menu.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import butterknife.BindView
import butterknife.ButterKnife
import com.checkin.app.checkin.Data.Resource
import com.checkin.app.checkin.R
import com.checkin.app.checkin.Utility.Utils
import com.checkin.app.checkin.Utility.coroutineLifecycleScope
import com.checkin.app.checkin.Utility.inTransaction
import com.checkin.app.checkin.menu.fragments.MenuFragment
import com.checkin.app.checkin.menu.viewmodels.ActiveSessionCartViewModel
import com.checkin.app.checkin.menu.views.ActiveSessionCartView
import com.checkin.app.checkin.misc.BlockingNetworkViewModel
import com.checkin.app.checkin.misc.activities.BaseActivity
import com.checkin.app.checkin.misc.fragments.NetworkBlockingFragment
import org.koin.androidx.fragment.android.setupKoinFragmentFactory
import org.koin.androidx.scope.lifecycleScope

class ActiveSessionMenuActivity : BaseActivity() {
    @BindView(R.id.view_as_menu_cart)
    internal lateinit var cartView: ActiveSessionCartView

    private lateinit var menuFragment: MenuFragment

    private val cartViewModel: ActiveSessionCartViewModel by viewModels()
    private val networkViewModel: BlockingNetworkViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        setupKoinFragmentFactory(lifecycleScope)
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_active_session_menu)
        ButterKnife.bind(this)

        supportActionBar?.title = "Menu"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setupObservers()
        initUi()
    }

    private fun setupObservers() {
        cartViewModel.serverOrders.observe(this, Observer {
            it?.let { resource ->
                networkViewModel.updateStatus(resource)
                if (resource.status == Resource.Status.SUCCESS) {
                    Utils.toast(this, "Confirmed orders!")
                    finish()
                }
            }
        })
        networkViewModel.shouldTryAgain.observe(this, Observer {
            cartViewModel.confirmOrder()
        })
    }

    private fun initUi() {
        val restaurantId = intent.getLongExtra(KEY_RESTAURANT_ID, 0L)
        menuFragment = MenuFragment.newInstanceForActiveSession(restaurantId)

        supportFragmentManager.inTransaction {
            add(R.id.frg_container_activity, menuFragment, null)
            add(android.R.id.content, NetworkBlockingFragment.withBlockingLoader(), NetworkBlockingFragment.FRAGMENT_TAG)
        }

        val itemPk = intent.getLongExtra(KEY_PRE_SELECTED_ITEM, 0L)
        if (itemPk > 0L) {
            coroutineLifecycleScope.launchWhenStarted {
                // Busy waiting for data to come... Since it's on another thread no issues.
                while (menuFragment.menuViewModel.originalMenuGroups.value?.data?.isEmpty() != false) {
                }
                menuFragment.menuViewModel.getMenuItemById(itemPk)?.let {
                    menuFragment.onMenuItemAdded(it)
                    if (!it.isComplexItem)
                        cartView.show()
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    companion object {
        private const val KEY_RESTAURANT_ID = "as.menu.restaurant_id"
        private const val KEY_PRE_SELECTED_ITEM = "as.menu.pre_selected.item_id"

        @JvmOverloads
        fun openMenu(context: Context, restaurantId: Long, itemPk: Long = 0L) = Intent(context, ActiveSessionMenuActivity::class.java).apply {
            putExtra(KEY_RESTAURANT_ID, restaurantId)
            putExtra(KEY_PRE_SELECTED_ITEM, itemPk)
            context.startActivity(this)
        }
    }
}