package com.checkin.app.checkin.Manager.Fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.checkin.app.checkin.Manager.Adapter.ManagerSessionEventAdapter;
import com.checkin.app.checkin.Manager.ManagerSessionViewModel;
import com.checkin.app.checkin.Manager.Model.ManagerSessionEventModel;
import com.checkin.app.checkin.Menu.ShopMenu.SessionMenuActivity;
import com.checkin.app.checkin.R;
import com.checkin.app.checkin.Utility.Utils;
import com.checkin.app.checkin.misc.fragments.BaseFragment;
import com.checkin.app.checkin.session.activesession.chat.SessionChatModel;
import com.checkin.app.checkin.session.models.SessionBriefModel;

import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;

public class ManagerSessionEventFragment extends BaseFragment implements ManagerSessionEventAdapter.SessionEventInteraction {
    @BindView(R.id.rv_ms_events)
    RecyclerView rvMSEvent;
    @BindView(R.id.tv_ms_customer_count)
    TextView tvCustomerCount;
    @BindView(R.id.tv_ms_event_session_time)
    TextView tvSessionTime;
    @BindView(R.id.nested_sv_ms_event)
    NestedScrollView nestedSVEvent;

    private ManagerSessionViewModel mViewModel;
    private ManagerSessionEventAdapter mAdapter;

    public ManagerSessionEventFragment() {
    }

    public static ManagerSessionEventFragment newInstance() {
        return new ManagerSessionEventFragment();
    }

    @Override
    protected int getRootLayout() {
        return R.layout.fragment_manager_session_event;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        setupUi();
        getData();
    }

    private void setupUi() {
        rvMSEvent.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        mAdapter = new ManagerSessionEventAdapter(this);
        rvMSEvent.setAdapter(mAdapter);
        rvMSEvent.setNestedScrollingEnabled(false);
        initRefreshScreen(R.id.sr_manager_session_event);

    }

    private void getData() {
        mViewModel = ViewModelProviders.of(requireActivity()).get(ManagerSessionViewModel.class);
        mViewModel.fetchSessionEvents();
        mViewModel.getSessionBriefData().observe(this, resource -> {
            if (resource == null) return;
            SessionBriefModel data = resource.getData();
            switch (resource.getStatus()) {
                case SUCCESS: {
                    if (data == null)
                        return;
                    setupSessionData(data);
                }
            }
        });

        mViewModel.getSessionEventData().observe(this, listResource -> {
            if (listResource == null || listResource.getData() == null)
                return;
            switch (listResource.getStatus()) {
                case SUCCESS:
                    mAdapter.setData(listResource.getData());
                    stopRefreshing();
                    nestedSVEvent.scrollTo(0, 0);
                    break;
                case LOADING:
                    startRefreshing();
                    break;
                default: {
                    Utils.toast(requireContext(), listResource.getMessage());
                    stopRefreshing();
                }
            }
        });

        mViewModel.getDetailData().observe(this, resource -> {
            if (resource == null || resource.getData() == null)
                return;
            switch (resource.getStatus()) {
                case SUCCESS:
                    mViewModel.updateUiEventStatus(Long.valueOf(resource.getData().getPk()), SessionChatModel.CHAT_STATUS_TYPE.DONE);
                    break;
                default: {
                    Utils.toast(requireContext(), resource.getMessage());
                }
            }
        });


    }

    private void setupSessionData(SessionBriefModel data) {
        tvCustomerCount.setText(data.formatCustomerCount());
        tvSessionTime.setText(String.format(Locale.ENGLISH, "Session Time: %s", data.formatTimeDuration()));
    }

    @OnClick(R.id.btn_ms_event_menu)
    public void onListMenu() {
        SessionBriefModel sessionBriefModel = mViewModel.getSessionData();
        if (sessionBriefModel != null && sessionBriefModel.isRequestedCheckout()) {
            Utils.toast(requireContext(), "Bill already approved for session.");
            return;
        }
        SessionMenuActivity.startWithSession(getContext(), mViewModel.getShopPk(), mViewModel.getSessionPk(), null);
        mViewModel.updateResults();
    }

    @Override
    public void onEventMarkDone(ManagerSessionEventModel eventModel) {
        mViewModel.markEventDone(eventModel.getPk());
    }

    @Override
    protected void updateScreen() {
        mViewModel.updateResults();
    }
}
