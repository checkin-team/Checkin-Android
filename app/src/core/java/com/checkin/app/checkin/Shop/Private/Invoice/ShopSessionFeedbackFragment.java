package com.checkin.app.checkin.Shop.Private.Invoice;

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

public class ShopSessionFeedbackFragment extends Fragment {
    private Unbinder unbinder;

    private ShopSessionViewModel mViewModel;
    private ShopSessionFeedbackAdapter mAdapter;

    public static ShopSessionFeedbackFragment newInstance() {
        return new ShopSessionFeedbackFragment();
    }

    public ShopSessionFeedbackFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shop_session_feedback, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
