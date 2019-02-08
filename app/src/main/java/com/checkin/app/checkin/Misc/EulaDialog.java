package com.checkin.app.checkin.Misc;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.text.Html;

import java.io.IOException;
import java.io.InputStream;

public class EulaDialog {
    private Context mContext;
    private EulaListener mListener;

    public EulaDialog(@NonNull Context context, EulaListener listener) {
        mContext = context;
        mListener = listener;
    }

    public void show() {
        String message = null;
        try {
            InputStream inputStream = mContext.getAssets().open("eula.txt");
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();
            message = new String(buffer);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (message != null) {
            new AlertDialog.Builder(mContext)
                    .setTitle("EULA")
                    .setMessage(Html.fromHtml(message))
                    .setPositiveButton("Agree", ((dialogInterface, i) -> {
                        mListener.onChoose(true);
                        dialogInterface.dismiss();
                    }))
                    .setNegativeButton("Disagree", ((dialogInterface, i) -> {
                        mListener.onChoose(false);
                        dialogInterface.dismiss();
                    }))
                    .show();
        }
    }

    public interface EulaListener {
        void onChoose(boolean isAccepted);
    }
}
