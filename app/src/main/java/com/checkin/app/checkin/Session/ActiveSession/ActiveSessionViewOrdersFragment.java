package com.checkin.app.checkin.Session.ActiveSession;

import android.os.Bundle;
import android.view.View;

import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.Misc.BaseFragment;
import com.checkin.app.checkin.R;
import com.checkin.app.checkin.Session.Model.SessionOrderedItemModel;
import com.checkin.app.checkin.Utility.Utils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.OnClick;

public class ActiveSessionViewOrdersFragment extends BaseFragment implements ActiveSessionOrdersAdapter.SessionOrdersInteraction {
    @BindView(R.id.rv_active_session_orders)
    RecyclerView rvOrders;

    private ActiveSessionOrdersAdapter mOrdersAdapter;
    private ActiveSessionViewModel mViewModel;

    public static ActiveSessionViewOrdersFragment newInstance() {
        return new ActiveSessionViewOrdersFragment();
    }

    public ActiveSessionViewOrdersFragment() {
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
            if (listResource.status == Resource.Status.SUCCESS && listResource.data != null) {
                mOrdersAdapter.setData(listResource.data);
                stopRefreshing();
            } else if (listResource.status == Resource.Status.LOADING) {
                startRefreshing();
            }
        });

        mViewModel.getObservableData().observe(this, resource -> {
            if (resource == null)
                return;
            switch (resource.status) {
                case SUCCESS: {
                    mViewModel.fetchSessionOrders();
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
    public boolean onBackPressed() {
        if (getFragmentManager() != null) {
            getFragmentManager().popBackStack();
            return true;
        }
        return false;
    }
}
