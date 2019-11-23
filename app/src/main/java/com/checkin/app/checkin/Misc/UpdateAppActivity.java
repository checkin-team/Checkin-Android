package com.checkin.app.checkin.Misc;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.checkin.app.checkin.R;
import com.checkin.app.checkin.Utility.Constants;

public class UpdateAppActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        new AlertDialog.Builder(UpdateAppActivity.this)
                .setTitle(R.string.app_old_version_dialog_title)
                .setMessage(R.string.app_old_version_dialog_message)
                .setPositiveButton("Ok", (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                    startActivity(new Intent(Intent.ACTION_VIEW, Constants.INSTANCE.getPLAY_STORE_URI()));
                    finish();
                })
                .setNegativeButton("Cancel", ((dialogInterface, i) -> {
                    dialogInterface.dismiss();
                    finish();
                }))
                .show();
    }
}
