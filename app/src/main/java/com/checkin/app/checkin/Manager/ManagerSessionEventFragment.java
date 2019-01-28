package com.checkin.app.checkin.Manager;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.checkin.app.checkin.R;
import com.checkin.app.checkin.Session.Model.SessionBriefModel;
import com.checkin.app.checkin.Utility.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class ManagerSessionEventFragment extends Fragment implements ManagerSessionEventAdapter.SessionEventInteraction {
    private Unbinder unbinder;
    @BindView(R.id.rv_ms_events)
    RecyclerView rvMSEvent;
    @BindView(R.id.tv_ms_customer_count)
    TextView tvCustomerCount;
    @BindView(R.id.tv_ms_event_session_time)
    TextView tvSessionTime;

    private ManagerSessionViewModel mViewModel;
    private ManagerSessionEventAdapter mAdapter;

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
        setupUi();
        getData();
    }

    private void setupUi() {
        rvMSEvent.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        mAdapter = new ManagerSessionEventAdapter(this);
        rvMSEvent.setAdapter(mAdapter);
        rvMSEvent.setNestedScrollingEnabled(false);
    }

    private void getData() {
        mViewModel = ViewModelProviders.of(requireActivity()).get(ManagerSessionViewModel.class);
        mViewModel.fetchSessionEvents();
        mViewModel.getSessionBriefData().observe(this, resource -> {
            if (resource == null) return;
            SessionBriefModel data = resource.data;
            switch (resource.status) {
                case SUCCESS: {
                    if (data == null)
                        return;
                    setupData(data);
                }
                case LOADING: {
                    break;
                }
                default: {
                    Log.e(resource.status.name(), resource.message == null ? "Null" : resource.message);
                }
            }
        });

        mViewModel.getSessionEventData().observe(this, listResource -> {
            if (listResource == null || listResource.data == null)
                return;
            switch (listResource.status) {
                case SUCCESS:
                    mAdapter.setData(listResource.data);
                    break;
                case LOADING:
                    break;
                default: {
                    Utils.toast(requireContext(), listResource.message);
                }
            }
        });
    }

    private void setupData(SessionBriefModel data){
        tvCustomerCount.setText(data.formatCustomerCount());
        tvSessionTime.setText(data.formatTimeDuration());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    @Override
    public void onOrderStatusChange() {

    }
}
