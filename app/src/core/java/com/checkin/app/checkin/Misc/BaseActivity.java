package com.checkin.app.checkin.Misc;

import android.annotation.SuppressLint;
import android.support.annotation.IdRes;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.checkin.app.checkin.Data.Resource.Status;

@SuppressLint("Registered")
public class BaseActivity extends AppCompatActivity {
    private static final String TAG = BaseActivity.class.getSimpleName();
    private DataStatusFragment mFragment;
    private boolean isFragmentShown = false;
    @IdRes private int vGroupId;

    public void init(@IdRes int groupId, boolean isNetworkRequired) {
        if (mFragment != null) {
            Log.e(TAG, "init called with existing fragment!");
            return;
        }
        vGroupId = groupId;
        mFragment = DataStatusFragment.newInstance(isNetworkRequired);
    }

    public void initLoad() {
        if (!isFragmentShown) {
            showFragment();
        }
    }

    public void doneLoad() {
        hideFragment();
    }

    public void alertStatus(Status status, String msg) {
        if (!isFragmentShown)
            showFragment();
        mFragment.showErrorStatus(status, msg);
    }

    private void showFragment() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(vGroupId, mFragment, null);
        ft.commit();
        isFragmentShown = true;
    }

    private void hideFragment() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.remove(mFragment);
        ft.commit();
        isFragmentShown = false;
    }
}
