package com.checkin.app.checkin.Shop.Private

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.checkin.app.checkin.R
import com.checkin.app.checkin.Shop.Private.ShopCoverAdapter.ShopCoverInteraction
import com.checkin.app.checkin.data.resource.Resource
import com.checkin.app.checkin.misc.activities.BaseActivity
import com.checkin.app.checkin.misc.activities.SelectCropImageActivity
import com.checkin.app.checkin.utility.Utils
import java.io.File

class LogoCoverActivity : BaseActivity(), ShopCoverInteraction {
    @BindView(R.id.im_sc_logo)
    internal lateinit var imLogo: ImageView
    @BindView(R.id.rv_sc_covers)
    internal lateinit var rvCovers: RecyclerView

    private val mViewModel: ShopProfileViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shop_logo_cover)
        ButterKnife.bind(this)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_back_grey)
            elevation = 10f
        }
        setupUi()
        mViewModel.observableData.observe(this, Observer {
            it?.let { resource ->
                if (resource.status === Resource.Status.SUCCESS && resource.data != null) {
                    Utils.toast(this, resource.data["detail"].asText())
                } else if (resource.status !== Resource.Status.LOADING) {
                    Utils.toast(this, resource.message)
                }
            }
        })
    }

    private fun setupUi() {
        val coverUrls = intent.getStringArrayExtra(KEY_SHOP_COVERS) ?: emptyArray()
        val logoUrl = intent.getStringExtra(KEY_SHOP_LOGO)
        val shopPk = intent.getLongExtra(KEY_SHOP_PK, 0)
        mViewModel.shopPk = shopPk
        val adapter = ShopCoverAdapter(coverUrls, this)
        rvCovers.adapter = adapter
        rvCovers.layoutManager = GridLayoutManager(this, 2, RecyclerView.VERTICAL, false)
        Utils.loadImageOrDefault(imLogo, logoUrl, R.drawable.card_image_add)
    }

    override fun onCoverRemove(index: Int) {
        mViewModel.removeCoverImage(index)
    }

    @OnClick(R.id.im_sc_logo)
    fun onLogoClick() {
        val intent = Intent(this, SelectCropImageActivity::class.java)
        startActivityForResult(intent, RC_LOGO)
    }

    override fun onCoverChange(index: Int) {
        val intent = Intent(this, SelectCropImageActivity::class.java)
        startActivityForResult(intent, RC_COVER_BASE + index)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            data?.extras?.let {
                val image = it[SelectCropImageActivity.KEY_IMAGE] as File
                if (requestCode == RC_LOGO) mViewModel.updateCoverPic(image, this, -1)
                else mViewModel.updateCoverPic(image, this, requestCode - RC_COVER_BASE)
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    companion object {
        const val KEY_SHOP_PK = "shop.pk"
        const val KEY_SHOP_COVERS = "shop.covers"
        const val KEY_SHOP_LOGO = "shop.logo"
        private const val RC_COVER_BASE = 100
        private const val RC_LOGO = 50
    }
}