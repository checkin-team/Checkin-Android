package com.checkin.app.checkin.Utility;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import com.checkin.app.checkin.R;
import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;

public final class OnBoardingUtils {
    private static final Handler sHandler = new Handler(Looper.getMainLooper());
    private static final float ALPHA_TAP_BACKGROUND = 0.67f;

    public static void animateOnBoarding(Activity activity, OnBoardingModel... models) {
        sHandler.post(() -> showOnBoarding(activity, models).start());
    }

    public static TapTargetSequence showOnBoarding(Activity activity, OnBoardingModel... models) {
        return showOnBoarding(activity, null, models);
    }

    public static TapTargetSequence showOnBoarding(Activity activity, TapTargetSequence.Listener listener, OnBoardingModel... models) {
        return showOnBoarding(activity, true, listener, models);
    }

    public static TapTargetSequence showOnBoarding(Activity activity, boolean continueOnCancel, TapTargetSequence.Listener listener, OnBoardingModel... models) {
        TapTarget[] targets = new TapTarget[models.length];
        for (int i = 0; i < models.length; i++)
            targets[i] = tapFocusView(models[i]);
        return new TapTargetSequence(activity)
                .targets(targets)
                .continueOnCancel(continueOnCancel)
                .listener(listener);
    }

    public static TapTarget tapFocusView(OnBoardingModel model) {
        return TapTarget.forView(model.view, model.title, model.description)
                .outerCircleAlpha(ALPHA_TAP_BACKGROUND)
                .outerCircleColor(R.color.primary_red)
                .drawShadow(true)
                .cancelable(model.isCancelable)
                .targetRadius(60);
    }

    public static class OnBoardingModel {
        String title;
        String description;
        View view;
        boolean isCancelable;

        public OnBoardingModel(String title, String description, View view) {
            this(title, description, view, true);
        }

        public OnBoardingModel(String title, View view) {
            this(title, null, view, true);
        }

        public OnBoardingModel(String title, View view, boolean isCancelable) {
            this(title, null, view, isCancelable);
        }

        public OnBoardingModel(String title, String description, View view, boolean isCancelable) {
            this.title = title;
            this.description = description;
            this.view = view;
            this.isCancelable = isCancelable;
        }

        public String getTitle() {
            return title;
        }

        public String getDescription() {
            return description;
        }

        public View getView() {
            return view;
        }
    }
}
