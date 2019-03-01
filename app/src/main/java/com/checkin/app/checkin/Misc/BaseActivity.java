package com.checkin.app.checkin.Misc;

import android.annotation.SuppressLint;
import androidx.annotation.IdRes;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentTransaction;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.checkin.app.checkin.Data.Resource.Status;

@SuppressLint("Registered")
public class BaseActivity extends AppCompatActivity {
    private static final String TAG = BaseActivity.class.getSimpleName();
    private DataStatusFragment mFragment;
    private boolean isFragmentShown = false;

    @IdRes
    private int vGroupId;

    @Nullable
    private SwipeRefreshLayout swipeRefreshLayout;
    @Nullable
    private ProgressBar progressBar;

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

    protected void initRefreshScreen(@IdRes int viewId) {
        swipeRefreshLayout = findViewById(viewId);
        swipeRefreshLayout.setOnRefreshListener(this::updateScreen);
    }

    protected void updateScreen() {}

    protected void startRefreshing() {
        if (swipeRefreshLayout != null)
            swipeRefreshLayout.setRefreshing(true);
    }

    protected void stopRefreshing() {
        if (swipeRefreshLayout != null)
            swipeRefreshLayout.setRefreshing(false);
    }

    protected void initProgressBar(@IdRes int viewId){
        progressBar = findViewById(viewId);
        progressBar.setVisibility(View.GONE);
    }

    protected void visibleProgressBar(){
        progressBar.setVisibility(View.VISIBLE);
    }

    protected void hideProgressBar(){
        progressBar.setVisibility(View.GONE);
    }
}
