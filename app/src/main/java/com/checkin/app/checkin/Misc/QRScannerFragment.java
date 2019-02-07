package com.checkin.app.checkin.Misc;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;

import java.util.Collections;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class QRScannerFragment extends Fragment implements ZXingScannerView.ResultHandler {
    private static final String STATE_FLASH = "FLASH_STATE";
    private static final String STATE_AUTO_FOCUS = "AUTO_FOCUS_STATE";
    private static final String CAMERA_ID = "CAMERA_ID";

    private ZXingScannerView mScannerView;

    private QRScannerInteraction mListener;
    private boolean mFlash;
    private boolean mAutoFocus;
    private int mCameraId;

    public static QRScannerFragment newInstance(QRScannerInteraction listener) {
        QRScannerFragment fragment = new QRScannerFragment();
        fragment.mListener = listener;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mScannerView = new ZXingScannerView(requireContext());
        return mScannerView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            mFlash = savedInstanceState.getBoolean(STATE_FLASH, false);
            mAutoFocus = savedInstanceState.getBoolean(STATE_AUTO_FOCUS, true);
            mCameraId = savedInstanceState.getInt(CAMERA_ID, -1);
        } else {
            mFlash = false;
            mAutoFocus = true;
            mCameraId = -1;
        }

        setupFormats();
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this);
        mScannerView.startCamera(mCameraId);
        mScannerView.setFlash(mFlash);
        mScannerView.setAutoFocus(mAutoFocus);

        if (mListener != null)
            mListener.updateFlashState(mFlash);
    }

    public void setFlash(boolean value) {
        mFlash = value;
        mScannerView.setFlash(mFlash);
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(STATE_FLASH, mFlash);
        outState.putBoolean(STATE_AUTO_FOCUS, mAutoFocus);
        outState.putInt(CAMERA_ID, mCameraId);
    }

    private void setupFormats() {
        mScannerView.setFormats(Collections.singletonList(BarcodeFormat.QR_CODE));
    }

    @Override
    public void handleResult(Result result) {
        if (mListener != null)
            mListener.onScannedResult(result);
    }

    public interface QRScannerInteraction {
        void onScannedResult(Result result);
        void updateFlashState(boolean isActivated);
    }
}
