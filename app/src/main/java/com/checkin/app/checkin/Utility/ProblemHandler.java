package com.checkin.app.checkin.Utility;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.Nullable;

import com.checkin.app.checkin.Data.ProblemModel;
import com.checkin.app.checkin.Data.ProblemModel.ERROR_CODE;
import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.Misc.UpdateAppActivity;
import com.checkin.app.checkin.R;
import com.crashlytics.android.Crashlytics;

/**
 * Created by shivanshs9 on 27/5/19.
 */

public final class ProblemHandler {
    private static final String TAG = ProblemHandler.class.getSimpleName();

    public static boolean handleDeprecatedAPIUse(Context context, Resource<?> resource) {
        ProblemModel problemModel = resource.getProblem();
        if (problemModel != null) {
            if (problemModel.getErrorCode() == ERROR_CODE.DEPRECATED_VERSION || problemModel.getErrorCode() == ERROR_CODE.INVALID_VERSION) {
                Crashlytics.log(Log.ERROR, TAG, "Error Code: " + problemModel.getErrorCode());
                context.startActivity(Intent.makeRestartActivityTask(new ComponentName(context, UpdateAppActivity.class)));
                return true;
            }
        }
        return false;
    }

    public static boolean handleUnauthenticatedAPIUse(Context context, Resource<?> resource) {
        if (resource.getStatus() == Resource.Status.ERROR_UNAUTHORIZED) {
            Utils.logoutFromApp(context);
            Utils.toast(context, R.string.app_force_logged_out);
            return true;
        }
        return false;
    }

    public static boolean handleProblems(Context context, @Nullable Resource<?> resource) {
        if (resource == null)
            return false;
        return handleDeprecatedAPIUse(context, resource) || handleUnauthenticatedAPIUse(context, resource);
    }
}
