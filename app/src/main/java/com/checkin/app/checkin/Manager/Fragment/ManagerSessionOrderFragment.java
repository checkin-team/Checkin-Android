package com.checkin.app.checkin.Manager.Fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.checkin.app.checkin.Manager.Adapter.ManagerSessionOrderAdapter;
import com.checkin.app.checkin.Manager.ManagerSessionViewModel;
import com.checkin.app.checkin.Misc.BaseFragment;
import com.checkin.app.checkin.R;
import com.checkin.app.checkin.Session.ActiveSession.Chat.SessionChatModel;
import com.checkin.app.checkin.Session.Model.SessionOrderedItemModel;
import com.checkin.app.checkin.Utility.Utils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;

public class ManagerSessionOrderFragment extends BaseFragment implements ManagerSessionOrderAdapter.SessionOrdersInteraction {
    @BindView(R.id.rv_ms_orders_accepted)
    RecyclerView rvOrdersAccepted;
    @BindView(R.id.rv_ms_orders_new)
    RecyclerView rvOrdersNew;
    @BindView(R.id.rv_ms_orders_delivered)
    RecyclerView rvOrdersDelivered;
    @BindView(R.id.title_ms_new)
    TextView titleNewHeader;
    @BindView(R.id.title_ms_in_progress)
    TextView titleInProgressHeader;
    @BindView(R.id.title_ms_delivered)
    TextView titleDeliveredHeader;
    @BindView(R.id.nested_sv_ms_order)
    NestedScrollView nestedSVOrder;


    private ManagerOrdersInteraction mListener;

    private ManagerSessionViewModel mViewModel;
    private ManagerSessionOrderAdapter mAdapterNew;
    private ManagerSessionOrderAdapter mAdapterAccepted;
    private ManagerSessionOrderAdapter mAdapterDeliveredRejected;


    public static ManagerSessionOrderFragment newInstance(ManagerOrdersInteraction listener) {
        ManagerSessionOrderFragment fragment = new ManagerSessionOrderFragment();
        fragment.mListener = listener;
        return fragment;
    }

    @Override
    protected int getRootLayout() {
        return R.layout.fragment_manager_session_order;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        setupUi();
        getData();
        view.startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.slide_up));
    }

    private void setupUi() {
        rvOrdersNew.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        mAdapterNew = new ManagerSessionOrderAdapter(this);
        rvOrdersNew.setAdapter(mAdapterNew);

        rvOrdersAccepted.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        mAdapterAccepted = new ManagerSessionOrderAdapter(this);
        rvOrdersAccepted.setAdapter(mAdapterAccepted);

        rvOrdersDelivered.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        mAdapterDeliveredRejected = new ManagerSessionOrderAdapter(this);
        rvOrdersDelivered.setAdapter(mAdapterDeliveredRejected);

        initRefreshScreen(R.id.sr_manager_session_orders);
    }

    private void getData() {
        mViewModel = ViewModelProviders.of(requireActivity()).get(ManagerSessionViewModel.class);

        mViewModel.getOpenOrders().observe(this, listResource -> {
            if (listResource == null || listResource.data == null)
                return;
            switch (listResource.status) {
                case SUCCESS:
                    if (listResource.data.size() > 0) {
                        mAdapterNew.setData(listResource.data);
                        titleNewHeader.setVisibility(View.VISIBLE);
                        rvOrdersNew.setVisibility(View.VISIBLE);
                        nestedSVOrder.scrollTo(0, 0);
                    }
                    break;
                default: {
                    Utils.toast(requireContext(), listResource.message);
                }
            }
        });

        mViewModel.getAcceptedOrders().observe(this, listResource -> {
            if (listResource == null || listResource.data == null)
                return;
            switch (listResource.status) {
                case SUCCESS:
                    if(listResource.data.size()>0){
                        mAdapterAccepted.setData(listResource.data);
                        titleInProgressHeader.setVisibility(View.VISIBLE);
                        rvOrdersAccepted.setVisibility(View.VISIBLE);
                    }

                    stopRefreshing();
                    nestedSVOrder.scrollTo(0, 0);
                    break;
                case LOADING:
                    startRefreshing();
                    break;
                default: {
                    Utils.toast(requireContext(), listResource.message);
                    stopRefreshing();
                }
            }
        });

        mViewModel.getDeliveredRejectedOrders().observe(this, listResource -> {
            if (listResource == null || listResource.data == null)
                return;
            switch (listResource.status) {
                case SUCCESS:
                    if (listResource.data.size() > 0) {
                        titleDeliveredHeader.setVisibility(View.VISIBLE);
                        rvOrdersDelivered.setVisibility(View.VISIBLE);
                        mAdapterDeliveredRejected.setData(listResource.data);
                    }

                    stopRefreshing();
                    nestedSVOrder.scrollTo(0, 0);
                    break;
                case LOADING:
                    startRefreshing();
                    break;
                default: {
                    Utils.toast(requireContext(), listResource.message);
                    stopRefreshing();
                }
            }
        });

        mViewModel.getOrderStatusData().observe(this, resource -> {
            if (resource == null)
                return;
            switch (resource.status) {
                case SUCCESS: {
                    mViewModel.updateUiOrderStatus(resource.data);
                    nestedSVOrder.scrollTo(0, 0);
                    break;
                }
                case LOADING:
                    break;
                default: {
                    Utils.toast(requireContext(), resource.message);
                }
            }
        });
    }

    @Override
    protected void updateScreen() {
        mViewModel.updateResults();
    }

    public boolean onBackPressed() {
        if (getFragmentManager() != null) {
            if (getView() != null)
                getView().startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.slide_down));
            getFragmentManager().beginTransaction()
                    .remove(this)
                    .commit();
            return true;
        }
        return false;
    }

    @Override
    public void onOrderStatusChange(SessionOrderedItemModel orderedItem, SessionChatModel.CHAT_STATUS_TYPE statusType) {
        mViewModel.updateOrderStatus(orderedItem.getPk(), statusType.tag);
    }

    public interface ManagerOrdersInteraction {
    }
}

