package com.checkin.app.checkin.Waiter.Fragment;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.checkin.app.checkin.Data.Resource.Status;
import com.checkin.app.checkin.R;
import com.checkin.app.checkin.Session.ActiveSession.Chat.SessionChatModel.CHAT_STATUS_TYPE;
import com.checkin.app.checkin.Session.Model.SessionOrderedItemModel;
import com.checkin.app.checkin.Utility.Utils;
import com.checkin.app.checkin.Waiter.Model.WaiterEventModel;
import com.checkin.app.checkin.Waiter.WaiterEventAdapter;
import com.checkin.app.checkin.Waiter.WaiterTableViewModel;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class WaiterTableEventFragment extends Fragment implements WaiterEventAdapter.WaiterEventInteraction {
    private Unbinder unbinder;

    private static final String KEY_WAITER_TABLE_ID = "waiter.table";

    @BindView(R.id.container_waiter_table_actions) ViewGroup containerActions;
    @BindView(R.id.rv_waiter_table_events_active) RecyclerView rvEventsActive;
    @BindView(R.id.rv_waiter_table_events_done) RecyclerView rvEventsDone;
    @BindView(R.id.title_waiter_delivered) TextView tvDelivered;
    @BindView(R.id.tv_waiter_table_members_count) TextView tvMembersCount;
    @BindView(R.id.refresh_waiter_event) SwipeRefreshLayout refreshEvent;

    private WaiterEventAdapter mActiveAdapter;
    private WaiterEventAdapter mDoneAdapter;
    private WaiterTableViewModel mViewModel;

    public static WaiterTableEventFragment newInstance(long tableNumber) {
        WaiterTableEventFragment fragment = new WaiterTableEventFragment();
        Bundle bundle = new Bundle();
        bundle.putLong(KEY_WAITER_TABLE_ID, tableNumber);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_waiter_table_event, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mActiveAdapter = new WaiterEventAdapter(this);
        mDoneAdapter = new WaiterEventAdapter(this);
        rvEventsActive.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        rvEventsDone.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        rvEventsActive.setAdapter(mActiveAdapter);
        rvEventsDone.setAdapter(mDoneAdapter);

        if (getArguments() == null)
            return;

        mViewModel = ViewModelProviders.of(this).get(WaiterTableViewModel.class);
        mViewModel.fetchSessionDetail(getArguments().getLong(KEY_WAITER_TABLE_ID, 0));
        mViewModel.fetchTableEvents();
        mViewModel.getActiveTableEvents().observe(this, listResource -> {
            if (listResource == null)
                return;
            if (listResource.status == Status.SUCCESS && listResource.data != null) {
                refreshEvent.setRefreshing(false);
                mActiveAdapter.setData(listResource.data);
            } else if (listResource.status == Status.LOADING)
                refreshEvent.setRefreshing(true);
        });
        mViewModel.getDeliveredTableEvents().observe(this, listResource -> {
            if (listResource == null)
                return;
            if (listResource.status == Status.SUCCESS && listResource.data != null) {
                if (listResource.data.size() > 0) {
                    tvDelivered.setVisibility(View.VISIBLE);
                    rvEventsDone.setVisibility(View.VISIBLE);
                    mDoneAdapter.setData(listResource.data);
                }
            } else if (listResource.status == Status.LOADING)
                refreshEvent.setRefreshing(true);
        });
        mViewModel.getSessionDetail().observe(this, resource -> {
            if (resource == null)
                return;
            if (resource.status == Status.SUCCESS && resource.data != null) {
                tvMembersCount.setText(resource.data.formatCustomerCount());
            }
        });
        mViewModel.getObservableData().observe(this, resource -> {
            if (resource == null)
                return;
            if (resource.status == Status.SUCCESS && resource.data != null)
                Utils.toast(requireContext(), "Done!");
            else if (resource.status != Status.LOADING)
                Utils.toast(requireContext(), resource.message);
        });
        mViewModel.getEventUpdate().observe(this, resource -> {
            if (resource == null)
                return;
            if (resource.status == Status.SUCCESS && resource.data != null)
                Utils.toast(requireContext(), resource.data.getDetail());
            else if (resource.status != Status.LOADING)
                Utils.toast(requireContext(), resource.message);
        });

        refreshEvent.setOnRefreshListener(() -> mViewModel.updateResults());
    }

    @Override
    public void onEventMarkDone(WaiterEventModel eventModel) {
        mViewModel.markEventDone(eventModel.getPk());
    }

    @Override
    public void onOrderMarkDone(SessionOrderedItemModel orderedItemModel) {
        mViewModel.updateOrderStatus(orderedItemModel.getPk(), CHAT_STATUS_TYPE.DONE);
    }

    @Override
    public void onOrderAccept(SessionOrderedItemModel orderedItemModel) {
        mViewModel.updateOrderStatus(orderedItemModel.getPk(), CHAT_STATUS_TYPE.IN_PROGRESS);
    }

    @Override
    public void onOrderCancel(SessionOrderedItemModel orderedItemModel) {
        mViewModel.updateOrderStatus(orderedItemModel.getPk(), CHAT_STATUS_TYPE.CANCELLED);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
