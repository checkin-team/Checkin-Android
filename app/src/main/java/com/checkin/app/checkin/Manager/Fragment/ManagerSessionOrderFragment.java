package com.checkin.app.checkin.Manager.Fragment;

import android.annotation.SuppressLint;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

import butterknife.BindView;

public class ManagerSessionOrderFragment extends BaseFragment implements ManagerSessionOrderAdapter.SessionOrdersInteraction {
    @BindView(R.id.rv_ms_orders_accepted)
    RecyclerView rvOrdersAccepted;
    @BindView(R.id.rv_ms_orders_new)
    RecyclerView rvOrdersNew;
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
        rvOrdersNew.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        mAdapterNew = new ManagerSessionOrderAdapter(this);
        rvOrdersNew.setAdapter(mAdapterNew);

        rvOrdersAccepted.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        mAdapterAccepted = new ManagerSessionOrderAdapter(this);
        rvOrdersAccepted.setAdapter(mAdapterAccepted);

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
                    } else {
                        titleNewHeader.setVisibility(View.GONE);
                        rvOrdersNew.setVisibility(View.GONE);
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
                    mAdapterAccepted.setData(listResource.data);
                    stopRefreshing();
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

    public interface ManagerOrdersInteraction {}
}

