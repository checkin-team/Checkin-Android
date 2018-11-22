package com.checkin.app.checkin;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.zxing.Result;
import com.mikhaellopez.circularimageview.CircularImageView;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class QRScanner extends AppCompatActivity implements ZXingScannerView.ResultHandler{
    private static final String FLASH_STATE = "FLASH_STATE";
    private ZXingScannerView mScannerView;
    Toolbar toolbar;
    CircularImageView flashoff;
    private boolean mFlash;
    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        mScannerView = new ZXingScannerView(this);
        setContentView(R.layout.activity_qrscanner);
        flashoff = (CircularImageView) findViewById(R.id.btnSwitch);
        flashoff.setBackgroundDrawable(null);

        if (state != null) {
            mFlash = state.getBoolean(FLASH_STATE, false);

        } else {
            mFlash = false;

        }
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mTitle.setText(toolbar.getTitle());
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setIcon(getDrawable(R.drawable.ic_chevron_left_black_24dp));
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(true);

        ViewGroup contentFrame = (ViewGroup) findViewById(R.id.content_frame);
        contentFrame.addView(mScannerView);

        flashoff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EnableFlash();
            }
        });
    }
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this);
        mScannerView.startCamera();
        mScannerView.setFlash(mFlash);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(FLASH_STATE, mFlash);
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();
    }

    @Override
    public void handleResult(Result rawResult) {
       /* MainActivity.result_text.setText(rawResult.getText());*/
        onBackPressed();
    }


    private boolean EnableFlash() {
        mFlash = !mFlash;
        if (mFlash) {
            flashoff.setImageResource(R.drawable.ic_flash_on_black_24dp);
        } else {
            flashoff.setImageResource(R.drawable.ic_flash_off_black_24dp);
        }
        mScannerView.setFlash(mFlash);
        return true;
    }

    public void onCameraSelected(int cameraId) {

        mScannerView.setFlash(mFlash);

    }


}
