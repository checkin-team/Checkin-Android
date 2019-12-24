package com.checkin.app.checkin.manager.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.checkin.app.checkin.R;
import com.checkin.app.checkin.Utility.Utils;
import com.checkin.app.checkin.manager.adapters.ManagerSessionOrderAdapter;
import com.checkin.app.checkin.manager.viewmodels.ManagerSessionViewModel;
import com.checkin.app.checkin.misc.fragments.BaseFragment;
import com.checkin.app.checkin.session.activesession.chat.SessionChatModel;
import com.checkin.app.checkin.session.models.SessionOrderedItemModel;

import butterknife.BindView;
import butterknife.OnClick;

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
    @BindView(R.id.sr_manager_session_orders)
    SwipeRefreshLayout srManagerSessionOrders;
    @BindView(R.id.iv_cash)
    ImageView ivCash;
    @BindView(R.id.iv_generate_bill)
    ImageView ivGenerateBill;
    @BindView(R.id.rl_container_generate_bill)
    RelativeLayout rlContainerGenerateBill;

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
        setHasOptionsMenu(true);
        rvOrdersNew.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        mAdapterNew = new ManagerSessionOrderAdapter(this, true);
        rvOrdersNew.setAdapter(mAdapterNew);

        rvOrdersAccepted.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        mAdapterAccepted = new ManagerSessionOrderAdapter(this, false);
        rvOrdersAccepted.setAdapter(mAdapterAccepted);

        rvOrdersDelivered.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        mAdapterDeliveredRejected = new ManagerSessionOrderAdapter(this, false);
        rvOrdersDelivered.setAdapter(mAdapterDeliveredRejected);

        initRefreshScreen(R.id.sr_manager_session_orders);
    }

    private void getData() {
        mViewModel = ViewModelProviders.of(requireActivity()).get(ManagerSessionViewModel.class);

        mViewModel.getOpenOrders().observe(this, listResource -> {
            if (listResource == null || listResource.getData() == null)
                return;
            switch (listResource.getStatus()) {
                case SUCCESS:
                    if (listResource.getData().size() > 0) {
                        mAdapterNew.setData(listResource.getData());
                        titleNewHeader.setVisibility(View.VISIBLE);
                        rvOrdersNew.setVisibility(View.VISIBLE);
                        nestedSVOrder.scrollTo(0, 0);
                    } else {
                        titleNewHeader.setVisibility(View.GONE);
                        rvOrdersNew.setVisibility(View.GONE);
                    }
                    stopRefreshing();
                    break;
                case LOADING:
                    startRefreshing();
                    break;
                default:
                    stopRefreshing();
                    Utils.toast(requireContext(), listResource.getMessage());
            }
        });

        mViewModel.getAcceptedOrders().observe(this, listResource -> {
            if (listResource == null || listResource.getData() == null)
                return;
            switch (listResource.getStatus()) {
                case SUCCESS:
                    if (listResource.getData().size() > 0) {
                        mAdapterAccepted.setData(listResource.getData());
                        titleInProgressHeader.setVisibility(View.VISIBLE);
                        rvOrdersAccepted.setVisibility(View.VISIBLE);
                    } else {
                        titleInProgressHeader.setVisibility(View.GONE);
                        rvOrdersAccepted.setVisibility(View.GONE);
                    }
                    nestedSVOrder.scrollTo(0, 0);
                    break;
            }
        });

        mViewModel.getDeliveredRejectedOrders().observe(this, listResource -> {
            if (listResource == null || listResource.getData() == null)
                return;
            switch (listResource.getStatus()) {
                case SUCCESS:
                    if (listResource.getData().size() > 0) {
                        titleDeliveredHeader.setVisibility(View.VISIBLE);
                        rvOrdersDelivered.setVisibility(View.VISIBLE);
                        mAdapterDeliveredRejected.setData(listResource.getData());
                    } else {
                        titleDeliveredHeader.setVisibility(View.GONE);
                        rvOrdersDelivered.setVisibility(View.GONE);
                    }
                    stopRefreshing();
                    nestedSVOrder.scrollTo(0, 0);
                    break;
            }
        });

        mViewModel.getOrderStatusData().observe(this, resource -> {
            if (resource == null)
                return;
            switch (resource.getStatus()) {
                case SUCCESS: {
                    mViewModel.updateUiOrderStatus(resource.getData());
                    nestedSVOrder.scrollTo(0, 0);
                    break;
                }
                case LOADING:
                    break;
                default: {
                    Utils.toast(requireContext(), resource.getMessage());
                }
            }
        });

        mViewModel.getOrderListStatusData().observe(this, listResource -> {
            if (listResource == null)
                return;
            switch (listResource.getStatus()) {
                case SUCCESS: {
                    mViewModel.updateUiOrderListStatus(listResource.getData());
                    nestedSVOrder.scrollTo(0, 0);
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
    protected void updateScreen() {
        mViewModel.updateResults();
    }

    public boolean onBackPressed() {
        if (getFragmentManager() != null) {
            if (getView() != null)
                getView().startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.slide_down));
            getFragmentManager().popBackStack();
            return true;
        }
        return false;
    }


    @Override
    public void confirmNewOrders() {
        mViewModel.confirmOrderStatus();
    }

    @Override
    public void onSelectDeselect(SessionOrderedItemModel orderedItem, SessionChatModel.CHAT_STATUS_TYPE statusType) {
        mViewModel.updateOrderStatusNew(orderedItem.getPk(), statusType.tag);
    }

    @Override
    public void onOrderStatusChange(SessionOrderedItemModel orderedItem, SessionChatModel.CHAT_STATUS_TYPE statusType) {
        mViewModel.updateOrderStatus(orderedItem.getPk(), statusType.tag);
    }

    @OnClick({R.id.iv_generate_bill, R.id.rl_container_generate_bill})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_generate_bill:
                mListener.onGenerateBillClick();
                break;
            case R.id.rl_container_generate_bill:
                mListener.onGenerateBillClick();
                break;
        }
    }

    public interface ManagerOrdersInteraction {
        void onGenerateBillClick();
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.clear();
    }
}

