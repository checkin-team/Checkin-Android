package com.checkin.app.checkin.Utility;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import androidx.annotation.Nullable;

import com.checkin.app.checkin.Data.ProblemModel;
import com.checkin.app.checkin.Data.ProblemModel.ERROR_CODE;
import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.R;

/**
 * Created by shivanshs9 on 27/5/19.
 */

public final class ProblemHandler {
    public static Context mContext;

    public ProblemHandler(Context context){
        this.mContext=context;
    }

    public static boolean handleDeprecatedAPIUse(Context context, Resource<?> resource) {
        ProblemModel problemModel = resource.getProblem();
        if (problemModel != null) {
            if (problemModel.getErrorCode() == ERROR_CODE.DEPRECATED_VERSION || problemModel.getErrorCode() == ERROR_CODE.INVALID_VERSION) {
                Utils.navigateBackToHome(mContext);
                startServiceShowDialog();
                return true;
            }
        }
        return false;
    }


    public static void startServiceShowDialog() {
        Intent intent=new Intent(mContext,ShowDialogUpdateApp.class);
        mContext.startService(intent);
    }

    public static boolean handleUnauthenticatedAPIUse(Context context, Resource<?> resource) {
        if (resource.status == Resource.Status.ERROR_UNAUTHORIZED) {
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

    public static class ShowDialogUpdateApp extends Service{

        public ShowDialogUpdateApp(){
        }

        @Nullable
        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }

        @Override
        public void onCreate() {
            super.onCreate();

            WindowManager windowManager = (WindowManager)getSystemService(WINDOW_SERVICE);
            LayoutInflater layoutInflater=(LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view=layoutInflater.inflate(R.layout.custom_dialog_update_app, null);
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                        WindowManager.LayoutParams.MATCH_PARENT,
                        WindowManager.LayoutParams.MATCH_PARENT,
                        WindowManager.LayoutParams.TYPE_PHONE,
                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                                | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                                | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
                                | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                        PixelFormat.TRANSLUCENT);

                params.gravity = Gravity.CENTER ;
                params.x = 0;
                params.y = 0;
                windowManager.addView(view, params);

            } else {
                WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                        WindowManager.LayoutParams.MATCH_PARENT,
                        WindowManager.LayoutParams.MATCH_PARENT,
                        WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                                | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                                | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
                                | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                        PixelFormat.TRANSLUCENT);


                params.gravity = Gravity.CENTER ;
                params.x = 0;
                params.y = 0;
                windowManager.addView(view, params);
            }

            Button btnCancel = view.findViewById(R.id.btn_update_app_cancel);
            Button btnOk = view.findViewById(R.id.btn_update_app_ok);
            btnCancel.setOnClickListener(v -> {
                windowManager.removeView(view);
               Utils.navigateBackToHome(mContext);
            });
            btnOk.setOnClickListener(v -> {new Intent(Intent.ACTION_VIEW, Constants.PLAY_STORE_URI);});

        }
    }
}
