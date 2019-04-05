package com.checkin.app.checkin.Misc;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public abstract class BaseFragment extends Fragment {
    private Unbinder unbinder;

    @Nullable
    private SwipeRefreshLayout swipeRefreshLayout;

    public BaseFragment() {
    }

    @LayoutRes
    protected abstract int getRootLayout();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(getRootLayout(), container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    protected void initRefreshScreen(@IdRes int viewId) {
        if (getView() == null)
            return;
        swipeRefreshLayout = getView().findViewById(viewId);
        swipeRefreshLayout.setOnRefreshListener(this::updateScreen);
    }

    protected void updateScreen() {
    }

    protected void startRefreshing() {
        if (swipeRefreshLayout != null)
            swipeRefreshLayout.setRefreshing(true);
    }

    protected void stopRefreshing() {
        if (swipeRefreshLayout != null)
            swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
