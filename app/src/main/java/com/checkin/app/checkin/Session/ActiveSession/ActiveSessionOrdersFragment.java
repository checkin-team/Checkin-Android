package com.checkin.app.checkin.Session.ActiveSession;

import android.annotation.SuppressLint;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.checkin.app.checkin.Menu.Model.OrderedItemModel;
import com.checkin.app.checkin.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by shivanshs9 on 16/8/18.
 */

public class ActiveSessionOrdersFragment extends Fragment implements ActiveSessionOrdersAdapter.SessionOrdersInteraction {
    private final static String TAG = ActiveSessionOrdersFragment.class.getSimpleName();

    @BindView(R.id.rv_session_orders) RecyclerView rvOrders;

    private Unbinder unbinder;
    private ActiveSessionOrdersAdapter mOrdersAdapter;
    private ActiveSessionViewModel mViewModel;

    public ActiveSessionOrdersFragment() {}

    public static ActiveSessionOrdersFragment newInstance() {
        return new ActiveSessionOrdersFragment();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_active_session_orders, container, false);
        unbinder = ButterKnife.bind(this, view);

        view.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                getFragmentManager().popBackStack();
            }
            return true;
        });

        mOrdersAdapter = new ActiveSessionOrdersAdapter(this);
        rvOrders.setAdapter(mOrdersAdapter);
        rvOrders.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        ActiveSessionViewModel sessionViewModel = ViewModelProviders.of(requireActivity()).get(ActiveSessionViewModel.class);
        sessionViewModel.getOrderedItems().observe(this, data -> mOrdersAdapter.setOrderedItems(data));

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    @Override
    public void onCancelOrder(OrderedItemModel orderedItem) {
        mViewModel.cancelOrders(orderedItem.getPk());
    }
}