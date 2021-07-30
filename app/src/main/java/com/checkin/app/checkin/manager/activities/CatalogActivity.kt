package com.checkin.app.checkin.manager.activities

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBar
import com.checkin.app.checkin.R
import com.checkin.app.checkin.databinding.ActivityCatalogBinding
import com.checkin.app.checkin.manager.fragments.CatalogDetailsFragment
import com.checkin.app.checkin.manager.viewmodels.CatalogViewModel
import com.checkin.app.checkin.misc.activities.BaseActivity

class CatalogActivity : BaseActivity() {

    private var _binding: ActivityCatalogBinding? = null
    val binding get() = _binding!!
    private val mViewModel: CatalogViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityCatalogBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbarCatalogActivity)
        val actionBar: ActionBar? = supportActionBar
        actionBar?.setHomeAsUpIndicator(R.drawable.ic_shape)
        actionBar?.setDisplayHomeAsUpEnabled(true)
        setUpObservers(intent.getLongExtra(ManagerWorkActivity.KEY_RESTAURANT_PK, 0L))
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container_catalog, CatalogDetailsFragment.newInstance())
            .commit()
    }

    private fun setUpObservers(shopPk: Long) {
        mViewModel.shopPk = 12
        Log.d("CatalogActivity", 12.toString())
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        const val KEY_RESTAURANT_PK = "catalog.restaurant_pk"
    }
}