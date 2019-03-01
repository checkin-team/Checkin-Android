package com.checkin.app.checkin.Misc;

import android.content.Context;
import android.text.Html;

import java.io.IOException;
import java.io.InputStream;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

public class EulaDialog {
    private String message;
    private AlertDialog alertDialog;

    public EulaDialog(@NonNull Context context, EulaListener listener) {
        buildDialog(context, listener);
    }

    private void buildDialog(Context context, EulaListener listener) {
        try {
            InputStream inputStream = context.getAssets().open("eula.txt");
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();
            message = new String(buffer);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (message != null) {
            alertDialog = new AlertDialog.Builder(context)
                    .setTitle("EULA")
                    .setMessage(Html.fromHtml(message))
                    .setPositiveButton("Agree", ((dialogInterface, i) -> {
                        listener.onChoose(true);
                        dialogInterface.dismiss();
                    }))
                    .setNegativeButton("Disagree", ((dialogInterface, i) -> {
                        listener.onChoose(false);
                        dialogInterface.dismiss();
                    })).create();
        }
    }

    public void show() {
        if (alertDialog != null)
            alertDialog.show();
    }

    public interface EulaListener {
        void onChoose(boolean isAccepted);
    }
}
