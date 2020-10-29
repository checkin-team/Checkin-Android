package com.checkin.app.checkin.misc.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.checkin.app.checkin.R
import com.checkin.app.checkin.utility.RealPathUtil.getRealPath
import com.checkin.app.checkin.utility.Utils
import com.checkin.app.checkin.utility.log
import com.checkin.app.checkin.utility.toast
import com.lyft.android.scissors.CropView
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class SelectCropImageActivity : AppCompatActivity() {
    @BindView(R.id.crop_view)
    internal lateinit var cropView: CropView
    @BindView(R.id.btn_image_crop_done)
    internal lateinit var imageCropDone: ImageView

    private var mRectangleFile: File? = null
    private var maxFileSizeInBytes: Long = 0

    private val imageIntent: Intent
        get() = Intent().apply { putExtra(KEY_IMAGE, mRectangleFile) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_crop_image)
        ButterKnife.bind(this)
        val fileSizeInMB = intent.getLongExtra(KEY_FILE_SIZE_IN_MB, 0)
        maxFileSizeInBytes = fileSizeInMB * ONE_BYTES_IN_MB
        val viewportRatio = intent.getFloatExtra(KEY_CROP_ASPECT_RATIO, 0.81f)
        checkPermissionExternalStorage()
        cropView.viewportRatio = viewportRatio
    }

    private fun checkPermissionExternalStorage() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), PERMISSION_REQUEST_READ_EXTERNAL)
        } else init()
    }

    private fun init() {
        requestImage()
        mRectangleFile = File(cacheDir, "cropped.png").also { it.delete() }
    }

    private fun requestImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), RC_PICK_IMAGE)
    }

    @OnClick(R.id.btn_image_crop_done)
    fun doneCropImage() {
        val bitmap = cropView.crop()
        if (bitmap == null) {
            Log.e(TAG, "Cropped bitmap is null!")
            return
        }
        try {
            FileOutputStream(mRectangleFile).use { out -> bitmap.compress(Bitmap.CompressFormat.PNG, 100, out) }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        val cropFileSizeInByte = mRectangleFile!!.length()
        if (maxFileSizeInBytes == 0L || cropFileSizeInByte <= maxFileSizeInBytes) {
            setResult(Activity.RESULT_OK, imageIntent)
            finish()
        } else {
            Utils.toast(this, "File size is larger than " + maxFileSizeInBytes / ONE_BYTES_IN_MB + "MB")
            init()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data == null) {
            finish()
            return
        }
        if (requestCode == RC_PICK_IMAGE) {
            val uri = data.data
            if (uri == null) {
                finish()
                return
            }
            try {
                // Setting null value to setImageBitmap to remove OOM Error.
                if (cropView.imageBitmap != null) cropView.imageBitmap = null
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)
                getRealPath(this, uri)?.let {
                    imageCropDone.visibility = View.VISIBLE
                    cropView.imageBitmap = Utils.modifyOrientation(bitmap, it)
                } ?: kotlin.run {
                    toast("Unable to locate the image. Use another one")
                    requestImage()
                }
            } catch (e: IOException) {
                e.log(TAG, "Unable to set image bitmap")
                toast(getString(R.string.text_error_network_unknown))
                finish()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        GlobalScope.launch {
            cacheDir.deleteRecursively()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSION_REQUEST_READ_EXTERNAL -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    init() else finish()
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    companion object {
        const val KEY_IMAGE = "select_crop.image"
        const val KEY_CROP_ASPECT_RATIO = "aspect_ratio"
        const val KEY_FILE_SIZE_IN_MB = "file_size_in_mb"
        const val RC_CROP_IMAGE = 1000
        private val TAG = SelectCropImageActivity::class.java.simpleName
        private const val RC_PICK_IMAGE = 100
        private const val PERMISSION_REQUEST_READ_EXTERNAL = 201
        private const val ONE_BYTES_IN_MB = 1024 * 1024.toLong()
    }
}