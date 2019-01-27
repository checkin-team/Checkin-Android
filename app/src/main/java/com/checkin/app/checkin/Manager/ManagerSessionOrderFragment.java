package com.checkin.app.checkin.Manager;

import android.annotation.SuppressLint;
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
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.checkin.app.checkin.R;
import com.checkin.app.checkin.Session.ActiveSession.Chat.SessionChatModel;
import com.checkin.app.checkin.Session.Model.SessionOrderedItemModel;
import com.checkin.app.checkin.Utility.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class ManagerSessionOrderFragment extends Fragment implements ManagerSessionOrderAdapter.SessionOrdersInteraction {
    private Unbinder unbinder;

    @BindView(R.id.rv_ms_orders_accepted)
    RecyclerView rvOrdersAccepted;
    @BindView(R.id.rv_ms_orders_new)
    RecyclerView rvOrdersNew;
    @BindView(R.id.refresh_ms_orders)
    SwipeRefreshLayout refreshLayout;
    @BindView(R.id.title_ms_new)
    TextView titleNewHeader;

    private ManagerOrdersInteraction mListener;

    private ManagerSessionViewModel mViewModel;
    private ManagerSessionOrderAdapter mAdapterNew;
    private ManagerSessionOrderAdapter mAdapterAccepted;


    public static ManagerSessionOrderFragment newInstance(ManagerOrdersInteraction listener) {
        ManagerSessionOrderFragment fragment = new ManagerSessionOrderFragment();
        fragment.mListener = listener;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_manager_session_order, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        setupUi();
        getData();
        view.startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.slide_up));
    }

    private void setupUi() {
        rvOrdersNew.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        mAdapterNew = new ManagerSessionOrderAdapter(this);
        rvOrdersNew.setAdapter(mAdapterNew);

        rvOrdersAccepted.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        mAdapterAccepted = new ManagerSessionOrderAdapter(this);
        rvOrdersAccepted.setAdapter(mAdapterAccepted);

        refreshLayout.setOnRefreshListener(() -> mViewModel.updateResults());
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
                        refreshLayout.setRefreshing(false);
                        titleNewHeader.setVisibility(View.VISIBLE);
                        rvOrdersNew.setVisibility(View.VISIBLE);
                    }
                    break;
                case LOADING:
                    refreshLayout.setRefreshing(true);
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
                    mAdapterAccepted.setData(listResource.data);
                    refreshLayout.setRefreshing(false);
                    break;
                case LOADING:
                    refreshLayout.setRefreshing(true);
                    break;
                default: {
                    Utils.toast(requireContext(), listResource.message);
                }
            }
        });
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
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    @Override
    public void onOrderStatusChange(SessionOrderedItemModel orderedItem, SessionChatModel.CHAT_STATUS_TYPE statusType) {
        mViewModel.updateOrderStatus(orderedItem.getPk(), statusType.tag);
    }

    public interface ManagerOrdersInteraction {}
}

