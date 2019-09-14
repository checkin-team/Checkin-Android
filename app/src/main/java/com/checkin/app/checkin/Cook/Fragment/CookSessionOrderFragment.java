package com.checkin.app.checkin.Cook.Fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.checkin.app.checkin.Cook.Adapter.CookSessionOrderAdapter;
import com.checkin.app.checkin.Cook.CookSessionViewModel;
import com.checkin.app.checkin.Misc.BaseFragment;
import com.checkin.app.checkin.R;
import com.checkin.app.checkin.Utility.Utils;
import com.checkin.app.checkin.session.activesession.chat.SessionChatModel;
import com.checkin.app.checkin.session.model.SessionOrderedItemModel;

import butterknife.BindView;

public class CookSessionOrderFragment extends BaseFragment implements CookSessionOrderAdapter.SessionOrdersInteraction {
    @BindView(R.id.rv_cs_orders_accepted)
    RecyclerView rvOrdersAccepted;
    @BindView(R.id.rv_cs_orders_new)
    RecyclerView rvOrdersNew;
    @BindView(R.id.rv_cs_orders_delivered)
    RecyclerView rvOrdersDelivered;
    @BindView(R.id.title_cs_new)
    TextView titleNewHeader;
    @BindView(R.id.title_cs_in_progress)
    TextView titleInProgressHeader;
    @BindView(R.id.title_cs_delivered)
    TextView titleDeliveredHeader;
    @BindView(R.id.nested_sv_cs_order)
    NestedScrollView nestedSVOrder;

    private CookSessionViewModel mViewModel;
    private CookSessionOrderAdapter mAdapterNew;
    private CookSessionOrderAdapter mAdapterAccepted;
    private CookSessionOrderAdapter mAdapterDeliveredRejected;

    public static CookSessionOrderFragment newInstance() {
        return new CookSessionOrderFragment();
    }

    @Override
    protected int getRootLayout() {
        return R.layout.fragment_cook_session_order;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        setupUi();
        getData();
//        view.startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.slide_up));
    }

    private void setupUi() {
        rvOrdersNew.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        mAdapterNew = new CookSessionOrderAdapter(this, true);
        rvOrdersNew.setAdapter(mAdapterNew);

        rvOrdersAccepted.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        mAdapterAccepted = new CookSessionOrderAdapter(this, false);
        rvOrdersAccepted.setAdapter(mAdapterAccepted);

        rvOrdersDelivered.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        mAdapterDeliveredRejected = new CookSessionOrderAdapter(this, false);
        rvOrdersDelivered.setAdapter(mAdapterDeliveredRejected);

        initRefreshScreen(R.id.sr_cook_session_orders);
    }

    private void getData() {
        mViewModel = ViewModelProviders.of(requireActivity()).get(CookSessionViewModel.class);

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

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.clear();
    }
}

