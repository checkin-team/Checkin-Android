package com.checkin.app.checkin.utility;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import com.checkin.app.checkin.R;
import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;

public final class OnBoardingUtils {
    private static final Handler sHandler = new Handler(Looper.getMainLooper());
    private static final float ALPHA_TAP_BACKGROUND = 0.67f;
    private static final String SP_ONBOARDING_TABLE_NAME = "onboarding";

    public static SharedPreferences getPreferences(Context context) {
        return context.getSharedPreferences(SP_ONBOARDING_TABLE_NAME, Context.MODE_PRIVATE);
    }

    public static boolean isOnBoardingShown(Context context, String spKey) {
        return getPreferences(context).getBoolean(spKey, false);
    }

    public static void setOnBoardingIsShown(Context context, String spKey, boolean isShown) {
        getPreferences(context).edit().putBoolean(spKey, isShown).apply();
    }

    public static void conditionalOnBoarding(Activity activity, String spKey, boolean isShown, OnBoardingModel... models) {
        TapTargetSequence.Listener listener = new TapTargetSequence.Listener() {
            int currView = 0;

            @Override
            public void onSequenceFinish() {
                setOnBoardingIsShown(activity, spKey, isShown);
            }

            @Override
            public void onSequenceStep(TapTarget lastTarget, boolean targetClicked) {
                if (targetClicked) models[currView++].view.performClick();
            }

            @Override
            public void onSequenceCanceled(TapTarget lastTarget) {
            }
        };
        if (!isOnBoardingShown(activity.getApplicationContext(), spKey))
            animateOnBoardingListener(activity, listener, models);
    }

    public static void animateOnBoarding(Activity activity, OnBoardingModel... models) {
        sHandler.post(() -> showOnBoarding(activity, models).start());
    }

    public static void animateOnBoardingListener(Activity activity, TapTargetSequence.Listener listener, OnBoardingModel... models) {
        sHandler.post(() -> showOnBoarding(activity, listener, models).start());
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
                .targetRadius(60)
                .tintTarget(model.doTintTarget());
    }

    public static class OnBoardingModel {
        String title;
        String description;
        View view;
        boolean isCancelable;
        boolean doTintTarget;

        public OnBoardingModel(String title, String description, View view) {
            this(title, description, view, true, true);
        }

        public OnBoardingModel(String title, View view, boolean doTintTarget) {
            this(title, view, true, doTintTarget);
        }

        public OnBoardingModel(String title, View view, boolean isCancelable, boolean doTintTarget) {
            this(title, null, view, isCancelable, doTintTarget);
        }

        public OnBoardingModel(String title, String description, View view, boolean isCancelable, boolean doTintTarget) {
            this.title = title;
            this.description = description;
            this.view = view;
            this.isCancelable = isCancelable;
            this.doTintTarget = doTintTarget;
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

        public boolean doTintTarget() {
            return doTintTarget;
        }
    }
}
