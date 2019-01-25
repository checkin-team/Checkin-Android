package com.checkin.app.checkin.ManagerOrders;

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
import android.widget.Toast;

import com.checkin.app.checkin.R;
import com.checkin.app.checkin.Session.ActiveSession.Chat.SessionChatModel;
import com.checkin.app.checkin.Session.Model.SessionOrderedItemModel;
import com.checkin.app.checkin.Utility.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class ManagerOrdersFragment extends Fragment implements ManagerOrdersNewAdapter.SessionOrdersInteraction {
    @BindView(R.id.rv_orders_accepted)
    RecyclerView rvOrdersAccepted;
    @BindView(R.id.rv_orders_new)
    RecyclerView rvOrdersNew;
    @BindView(R.id.refresh_orders)
    SwipeRefreshLayout refreshLayout;
    private Unbinder unbinder;
    private ManagerOrdersInteraction mListener;

    private ManagerOrdersViewModel mViewModel;
    private ManagerOrdersNewAdapter mOrdersNewAdapter;
    private ManagerOrdersAcceptedAdapter mOrdersAcceptedAdapter;


    public static ManagerOrdersFragment newInstance(ManagerOrdersInteraction listener) {
        ManagerOrdersFragment fragment = new ManagerOrdersFragment();
        fragment.mListener = listener;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_mangers_orders, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        setupUi();
        getData();
        view.setOnTouchListener((v, event) -> {
            onBackPressed();
            return true;
        });
        view.startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.slide_up));
    }

    private void setupUi() {
        rvOrdersAccepted.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        mOrdersAcceptedAdapter = new ManagerOrdersAcceptedAdapter(null);
        rvOrdersAccepted.setAdapter(mOrdersAcceptedAdapter);

        rvOrdersNew.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        mOrdersNewAdapter = new ManagerOrdersNewAdapter(null, this);
        rvOrdersNew.setAdapter(mOrdersNewAdapter);

        rvOrdersAccepted.setNestedScrollingEnabled(false);
        rvOrdersNew.setNestedScrollingEnabled(false);

        refreshLayout.setOnRefreshListener(() -> mViewModel.updateResults());
    }

    private void getData() {
        mViewModel = ViewModelProviders.of(requireActivity()).get(ManagerOrdersViewModel.class);
        mViewModel.fetchManagerOrdersDetails(1);

        mViewModel.getNewData().observe(this, listResource -> {
            if (listResource == null)
                return;
            switch (listResource.status) {
                case SUCCESS:
                    mOrdersNewAdapter.setData(listResource.data);
                    refreshLayout.setRefreshing(false);
                    break;
                case LOADING:
                    refreshLayout.setRefreshing(false);
                    break;
                default: {
                    Utils.toast(getContext(), listResource.message);
                }
            }
        });

        mViewModel.getAcceptedData().observe(this, listResource -> {
            if (listResource == null)
                return;
            switch (listResource.status) {
                case SUCCESS:
                    mOrdersAcceptedAdapter.setData(listResource.data);
                    refreshLayout.setRefreshing(false);
                    break;
                case LOADING:
                    refreshLayout.setRefreshing(false);
                    break;
                default: {
                    Utils.toast(getContext(), listResource.message);
                }
            }
        });

        mViewModel.getObservableData().observe(this, resource -> {
            if (resource == null)
                return;
            switch (resource.status) {
                case SUCCESS: {
                    Toast.makeText(getContext(), "Done!", Toast.LENGTH_SHORT).show();
                    mViewModel.updateResults();
                    break;
                }
                case LOADING:
                    break;
                default: {
                    Utils.toast(getContext(), resource.message);
                }
            }
        });
    }

    public void onBackPressed() {
        if (getFragmentManager() != null) {
            getFragmentManager().beginTransaction()
                    .remove(this)
                    .commit();
        }
    }

    @Override
    public void onOrderStatusChange(SessionOrderedItemModel orderedItem, SessionChatModel.CHAT_STATUS_TYPE statusType) {
        mViewModel.sendOrderStatus(orderedItem.getPk(), statusType.tag);
    }

    public interface ManagerOrdersInteraction {
    }
}

