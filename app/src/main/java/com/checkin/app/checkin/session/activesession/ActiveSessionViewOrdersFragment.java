package com.checkin.app.checkin.session.activesession;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.R;
import com.checkin.app.checkin.Utility.Utils;
import com.checkin.app.checkin.misc.fragments.BaseFragment;
import com.checkin.app.checkin.session.models.SessionOrderedItemModel;

import butterknife.BindView;
import butterknife.OnClick;

public class ActiveSessionViewOrdersFragment extends BaseFragment implements ActiveSessionOrdersAdapter.SessionOrdersInteraction {
    @BindView(R.id.rv_active_session_orders)
    RecyclerView rvOrders;

    private ActiveSessionOrdersAdapter mOrdersAdapter;
    private ActiveSessionViewModel mViewModel;

    public ActiveSessionViewOrdersFragment() {
    }

    public static ActiveSessionViewOrdersFragment newInstance() {
        return new ActiveSessionViewOrdersFragment();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setUpUi();
        setData();
    }

    private void setUpUi() {
        rvOrders.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));
        mOrdersAdapter = new ActiveSessionOrdersAdapter(null, this);
        rvOrders.setAdapter(mOrdersAdapter);
        initRefreshScreen(R.id.sr_active_session_orders);
    }

    private void setData() {
        mViewModel = ViewModelProviders.of(requireActivity()).get(ActiveSessionViewModel.class);

        mViewModel.getSessionOrdersData().observe(this, listResource -> {
            if (listResource == null)
                return;
            if (listResource.getStatus() == Resource.Status.SUCCESS && listResource.getData() != null) {
                mOrdersAdapter.setData(listResource.getData());
                stopRefreshing();
            } else if (listResource.getStatus() == Resource.Status.LOADING) {
                startRefreshing();
            }
        });

        mViewModel.getObservableData().observe(this, resource -> {
            if (resource == null)
                return;
            switch (resource.getStatus()) {
                case SUCCESS: {
                    mViewModel.fetchSessionOrders();
                    break;
                }
                case LOADING:
                    break;
                default: {
                    Utils.toast(requireContext(), resource.getMessage());
                }
            }
        });
    }

    @Override
    protected int getRootLayout() {
        return R.layout.fragment_active_session_view_orders;
    }

    @Override
    public void onCancelOrder(SessionOrderedItemModel orderedItem) {
        mViewModel.deleteSessionOrder(orderedItem.getPk());
    }

    @Override
    protected void updateScreen() {
        mViewModel.fetchSessionOrders();

    }

    @OnClick(R.id.im_session_view_orders_back)
    public void onBack() {
        onBackPressed();
    }

    public boolean onBackPressed() {
        if (getFragmentManager() != null) {
            getFragmentManager().popBackStack();
            return true;
        }
        return false;
    }
}
