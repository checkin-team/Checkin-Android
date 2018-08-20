package com.checkin.app.checkin.Session;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

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
    private ActiveSessionViewModel mActiveSessionViewModel;

    public ActiveSessionOrdersFragment() {}

    public static ActiveSessionOrdersFragment newInstance(FragmentActivity activity) {
        ActiveSessionOrdersFragment fragment = new ActiveSessionOrdersFragment();
        fragment.mActiveSessionViewModel = ViewModelProviders.of(activity, new ActiveSessionViewModel.Factory(activity.getApplication())).get(ActiveSessionViewModel.class);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_active_session_orders, container, false);
        unbinder = ButterKnife.bind(this, view);

        view.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
            }
            return true;
        });

        mOrdersAdapter = new ActiveSessionOrdersAdapter(this);
        rvOrders.setAdapter(mOrdersAdapter);
        rvOrders.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        if (getActivity() != null) {
            ActiveSessionViewModel sessionViewModel = ViewModelProviders.of(getActivity(), new ActiveSessionViewModel.Factory(getActivity().getApplication())).get(ActiveSessionViewModel.class);
            sessionViewModel.getOrderedItems().observe(this, data -> mOrdersAdapter.setOrderedItems(data));
        }

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    @Override
    public void onCancelOrder(OrderedItemModel orderedItem) {
        mActiveSessionViewModel.cancelOrders(orderedItem.getId());
    }
}