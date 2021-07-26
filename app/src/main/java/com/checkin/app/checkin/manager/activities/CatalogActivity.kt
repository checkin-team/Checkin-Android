package com.checkin.app.checkin.manager.activities

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBar
import com.checkin.app.checkin.R
import com.checkin.app.checkin.databinding.ActivityCatalogBinding
import com.checkin.app.checkin.manager.fragments.CatalogDetailsFragment
import com.checkin.app.checkin.misc.activities.BaseActivity

class CatalogActivity : BaseActivity() {

    private var _binding: ActivityCatalogBinding? = null
    val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityCatalogBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbarCatalogActivity)
        val actionBar: ActionBar? = supportActionBar
        actionBar?.setHomeAsUpIndicator(R.drawable.ic_shape)
        actionBar?.setDisplayHomeAsUpEnabled(true)

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container_catalog, CatalogDetailsFragment.newInstance())
            .commit()
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
}