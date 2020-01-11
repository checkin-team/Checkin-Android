package com.checkin.app.checkin.Waiter.Fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.checkin.app.checkin.data.resource.Resource;
import com.checkin.app.checkin.R;
import com.checkin.app.checkin.Utility.Utils;
import com.checkin.app.checkin.Waiter.Model.WaiterEventModel;
import com.checkin.app.checkin.Waiter.WaiterEventAdapter;
import com.checkin.app.checkin.Waiter.WaiterTableViewModel;
import com.checkin.app.checkin.misc.fragments.BaseFragment;
import com.checkin.app.checkin.session.activesession.chat.SessionChatModel;
import com.checkin.app.checkin.session.models.SessionOrderedItemModel;

import butterknife.BindView;

public class WaiterTableEventFragment extends BaseFragment implements WaiterEventAdapter.WaiterEventInteraction {
    @BindView(R.id.rv_waiter_table_events_active)
    RecyclerView rvEventsActive;
    @BindView(R.id.rv_waiter_table_events_done)
    RecyclerView rvEventsDone;
    @BindView(R.id.title_waiter_delivered)
    TextView tvDelivered;
    @BindView(R.id.nested_sv_ws_event)
    NestedScrollView nestedSVEvent;


    private WaiterEventAdapter mActiveAdapter;
    private WaiterEventAdapter mDoneAdapter;
    private WaiterTableViewModel mViewModel;

    public WaiterTableEventFragment() {
    }

    public static WaiterTableEventFragment newInstance() {
        return new WaiterTableEventFragment();
    }

    @Override
    protected int getRootLayout() {
        return R.layout.fragment_waiter_table_event;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mActiveAdapter = new WaiterEventAdapter(this);
        mDoneAdapter = new WaiterEventAdapter(this);

        rvEventsActive.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        rvEventsDone.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        rvEventsActive.setAdapter(mActiveAdapter);
        rvEventsDone.setAdapter(mDoneAdapter);

        Fragment fragment = getParentFragment();
        if (fragment == null)
            return;
        mViewModel = ViewModelProviders.of(fragment).get(WaiterTableViewModel.class);


        mViewModel.fetchTableEvents();

        setupObservers();
    }

    private void setupObservers() {
        mViewModel.getActiveTableEvents().observe(this, listResource -> {
            if (listResource == null)
                return;
            if (listResource.getStatus() == Resource.Status.SUCCESS && listResource.getData() != null) {
                mActiveAdapter.setData(listResource.getData());
                nestedSVEvent.scrollTo(0, 0);
            }
        });
        mViewModel.getDeliveredTableEvents().observe(this, listResource -> {
            if (listResource == null)
                return;
            if (listResource.getStatus() == Resource.Status.SUCCESS && listResource.getData() != null) {
                if (listResource.getData().size() > 0) {
                    tvDelivered.setVisibility(View.VISIBLE);
                    rvEventsDone.setVisibility(View.VISIBLE);
                    mDoneAdapter.setData(listResource.getData());
                    nestedSVEvent.scrollTo(0, 0);
                }
            }
        });
        mViewModel.getOrderStatus().observe(this, resource -> {
            if (resource == null)
                return;
            if (resource.getStatus() == Resource.Status.SUCCESS && resource.getData() != null)
                mViewModel.updateUiMarkOrderStatus(resource.getData());
            else if (resource.getStatus() != Resource.Status.LOADING)
                Utils.toast(requireContext(), resource.getMessage());
        });
        mViewModel.getEventUpdate().observe(this, resource -> {
            if (resource == null)
                return;
            if (resource.getStatus() == Resource.Status.SUCCESS && resource.getData() != null) {
                mViewModel.updateUiMarkEventDone(Long.valueOf(resource.getData().getPk()));
            } else if (resource.getStatus() != Resource.Status.LOADING)
                Utils.toast(requireContext(), resource.getMessage());
        });

        mViewModel.getOrderListStatusData().observe(this, listResource -> {
            if (listResource == null)
                return;
            switch (listResource.getStatus()) {
                case SUCCESS: {
                    mViewModel.updateUiOrderListStatus(listResource.getData());
                    break;
                }
                case LOADING:
                    break;
                default: {
                    Utils.toast(requireContext(), listResource.getMessage());
                }
            }
        });
    }

    @Override
    public void onEventMarkDone(WaiterEventModel eventModel) {
        mViewModel.markEventDone(eventModel.getPk());
    }

    @Override
    public void onOrderMarkDone(SessionOrderedItemModel orderedItemModel) {
        mViewModel.updateOrderStatus(orderedItemModel.getPk(), SessionChatModel.CHAT_STATUS_TYPE.DONE);
    }

    @Override
    public void onOrderAccept(SessionOrderedItemModel orderedItemModel) {
        mViewModel.updateOrderStatusNew(orderedItemModel.getPk(), SessionChatModel.CHAT_STATUS_TYPE.IN_PROGRESS.tag);
        mViewModel.confirmOrderStatusWaiter();
    }

    @Override
    public void onOrderCancel(SessionOrderedItemModel orderedItemModel) {
        mViewModel.updateOrderStatusNew(orderedItemModel.getPk(), SessionChatModel.CHAT_STATUS_TYPE.CANCELLED.tag);
        mViewModel.confirmOrderStatusWaiter();
    }
}
