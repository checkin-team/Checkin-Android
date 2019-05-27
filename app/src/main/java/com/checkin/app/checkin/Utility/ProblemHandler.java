package com.checkin.app.checkin.Utility;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;

import com.checkin.app.checkin.Data.ProblemModel;
import com.checkin.app.checkin.Data.ProblemModel.ERROR_CODE;
import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.R;

/**
 * Created by shivanshs9 on 27/5/19.
 */

public final class ProblemHandler {
    public static boolean handleDeprecatedAPIUse(Context context, Resource<?> resource) {
        ProblemModel problemModel = ProblemModel.fromResource(resource);
        if (problemModel != null) {
            if (problemModel.getErrorCode() == ERROR_CODE.DEPRECATED_VERSION || problemModel.getErrorCode() == ERROR_CODE.INVALID_VERSION) {
                new AlertDialog.Builder(context)
                        .setTitle(R.string.app_old_version_dialog_title)
                        .setMessage(R.string.app_old_version_dialog_message)
                        .setPositiveButton("Ok", (dialogInterface, i) -> {
                            dialogInterface.dismiss();
                            context.startActivity(new Intent(Intent.ACTION_VIEW, Constants.PLAY_STORE_URI));
                        })
                        .setNegativeButton("Cancel", ((dialogInterface, i) -> dialogInterface.dismiss()))
                        .show();
                return true;
            }
        }
        return false;
    }

    public static boolean handleProblems(Context context, Resource<?> resource) {
        return handleDeprecatedAPIUse(context, resource);
    }
}
