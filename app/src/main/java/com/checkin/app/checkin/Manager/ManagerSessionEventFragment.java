package com.checkin.app.checkin.Manager;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.checkin.app.checkin.R;

import butterknife.ButterKnife;
import butterknife.Unbinder;

public class ManagerSessionEventFragment extends Fragment {
    private Unbinder unbinder;

    public static ManagerSessionEventFragment newInstance() {
        return new ManagerSessionEventFragment();
    }

    public ManagerSessionEventFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_manager_session_event, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}
