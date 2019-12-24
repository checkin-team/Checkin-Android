package com.checkin.app.checkin.misc.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.checkin.app.checkin.R;
import com.checkin.app.checkin.Utility.Utils;
import com.lyft.android.scissors.CropView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class SelectCropImageActivity extends AppCompatActivity {
    public static final String KEY_IMAGE = "select_crop.image";
    public static final String KEY_CROP_ASPECT_RATIO = "aspect_ratio";
    public static final String KEY_FILE_SIZE_IN_MB = "file_size_in_mb";
    public static final int RC_CROP_IMAGE = 1000;
    private static final String TAG = SelectCropImageActivity.class.getSimpleName();
    private static final int RC_PICK_IMAGE = 100;
    private static final int PERMISSION_REQUEST_READ_EXTERNAL = 201;


    private static final long ONE_BYTES_IN_MB = 1024 * 1024;
    @BindView(R.id.crop_view)
    CropView cropView;
    @BindView(R.id.btn_image_crop_done)
    ImageView imageCropDone;

    private File mRectangleFile;
    private long maxFileSizeInBytes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_crop_image);
        ButterKnife.bind(this);

        long fileSizeInMB = getIntent().getLongExtra(KEY_FILE_SIZE_IN_MB, 0);
        maxFileSizeInBytes = fileSizeInMB * ONE_BYTES_IN_MB;
        float viewportRatio = getIntent().getFloatExtra(KEY_CROP_ASPECT_RATIO, 0.81f);
        checkPermissionExternalStorage();
        cropView.setViewportRatio(viewportRatio);

    }

    private void checkPermissionExternalStorage() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_READ_EXTERNAL);
        }else {
            init();
        }
    }

    private void init() {
        requestImage();
        mRectangleFile = new File(getCacheDir(), "cropped.png");
        mRectangleFile.delete();
    }

    private void requestImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), RC_PICK_IMAGE);
    }

    @OnClick(R.id.btn_image_crop_done)
    public void doneCropImage() {
        Bitmap bitmap = cropView.crop();
        if (bitmap == null) {
            Log.e(TAG, "Cropped bitmap is null!");
            return;
        }
        try (FileOutputStream out = new FileOutputStream(mRectangleFile)) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
        } catch (IOException e) {
            e.printStackTrace();
        }

        long cropFileSizeInByte = mRectangleFile.length();

        if (maxFileSizeInBytes == 0 || cropFileSizeInByte <= maxFileSizeInBytes) {
            setResult(Activity.RESULT_OK, getImageIntent());
            finish();
        } else {
            Utils.toast(this, "File size is larger than " + maxFileSizeInBytes / ONE_BYTES_IN_MB + "MB");
            init();
        }
    }

    private Intent getImageIntent() {
        Intent intent = new Intent();
        intent.putExtra(KEY_IMAGE, mRectangleFile);
        return intent;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) {
            finish();
            return;
        }
        if (requestCode == RC_PICK_IMAGE) {
            Uri uri = data.getData();
            try {
                /*Setting null value to setImageBitmap to remove OOM Error.*/
                if (cropView.getImageBitmap() != null) {
                    cropView.setImageBitmap(null);
                }
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                String imageAbsolutePath = Utils.getPath(this, uri);
                Bitmap mFinalBitmap = Utils.modifyOrientation(bitmap, imageAbsolutePath);
                imageCropDone.setVisibility(View.VISIBLE);
                cropView.setImageBitmap(mFinalBitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_READ_EXTERNAL: {
                if (grantResults[0] == PERMISSION_GRANTED)
                    init();
                else
                    finish();
            }
            break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
