package com.checkin.app.checkin.Misc;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.checkin.app.checkin.R;
import com.google.zxing.Result;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class QRScannerActivity extends AppCompatActivity implements QRScannerFragment.QRScannerInteraction {
    public static final String KEY_QR_RESULT = "qr.result";
    private static final String TAG = QRScannerActivity.class.getSimpleName();
    private static final int PERMISSION_REQUEST_CAMERA = 101;
    @BindView(R.id.btn_flash_toggle)
    ImageView btnFlash;

    private QRScannerFragment mScannerFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrscanner);

        ButterKnife.bind(this);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_back_grey);
        actionBar.setElevation(10);

        checkValidCamera();
        setupFragment();
    }

    private void checkValidCamera() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CAMERA);
        }
    }

    @OnClick(R.id.btn_flash_toggle)
    public void onFlashToggle(View v) {
        boolean value = !btnFlash.isActivated();
        mScannerFragment.setFlash(value);
        btnFlash.setActivated(value);
    }

    private void setupFragment() {
        mScannerFragment = QRScannerFragment.newInstance(this);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_scanner, mScannerFragment)
                .commit();
    }

    @Override
    public void onScannedResult(Result result) {
        Intent data = new Intent();
        data.putExtra(KEY_QR_RESULT, result.getText());
        setResult(RESULT_OK, data);
        finish();
    }

    @Override
    public void updateFlashState(boolean isActivated) {
        btnFlash.setActivated(isActivated);
    }

    @Override
    public boolean onSupportNavigateUp() {
        setResult(RESULT_CANCELED);
        finish();
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CAMERA: {
                if (grantResults[0] != PERMISSION_GRANTED) finish();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
