package com.checkin.app.checkin.Manager.Fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.checkin.app.checkin.Data.Message.MessageModel;
import com.checkin.app.checkin.Data.Message.MessageObjectModel;
import com.checkin.app.checkin.Data.Message.MessageUtils;
import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.Manager.Adapter.ManagerWorkTableAdapter;
import com.checkin.app.checkin.Manager.ManagerSessionActivity;
import com.checkin.app.checkin.Manager.ManagerWorkViewModel;
import com.checkin.app.checkin.Misc.BriefModel;
import com.checkin.app.checkin.R;
import com.checkin.app.checkin.Session.ActiveSession.Chat.SessionChatModel.CHAT_EVENT_TYPE;
import com.checkin.app.checkin.Session.Model.EventBriefModel;
import com.checkin.app.checkin.Session.Model.RestaurantTableModel;
import com.checkin.app.checkin.Utility.Utils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.checkin.app.checkin.Data.Message.MessageModel.MESSAGE_TYPE.MANAGER_SESSION_CHECKOUT_REQUEST;
import static com.checkin.app.checkin.Data.Message.MessageModel.MESSAGE_TYPE.MANAGER_SESSION_END;
import static com.checkin.app.checkin.Data.Message.MessageModel.MESSAGE_TYPE.MANAGER_SESSION_EVENT_CONCERN;
import static com.checkin.app.checkin.Data.Message.MessageModel.MESSAGE_TYPE.MANAGER_SESSION_EVENT_SERVICE;
import static com.checkin.app.checkin.Data.Message.MessageModel.MESSAGE_TYPE.MANAGER_SESSION_HOST_ASSIGNED;
import static com.checkin.app.checkin.Data.Message.MessageModel.MESSAGE_TYPE.MANAGER_SESSION_NEW;
import static com.checkin.app.checkin.Data.Message.MessageModel.MESSAGE_TYPE.MANAGER_SESSION_NEW_ORDER;

public class ManagerTablesFragment extends Fragment implements ManagerWorkTableAdapter.ManagerTableInteraction {
    private static final String TAG = ManagerTablesFragment.class.getSimpleName();

    private Unbinder unbinder;

    @BindView(R.id.rv_shop_manager_table)
    RecyclerView rvShopManagerTable;

    private ManagerWorkTableAdapter mAdapter;
    private ManagerWorkViewModel mViewModel;

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            MessageModel message = MessageUtils.parseMessage(intent);
            if (message == null) return;

            EventBriefModel eventModel;
            BriefModel user;
            switch (message.getType()) {
                case MANAGER_SESSION_NEW:
                    String tableName = message.getRawData().getSessionTableName();
                    eventModel = EventBriefModel.getFromManagerEventModel(message.getRawData().getSessionEventBrief());
                    eventModel.setType(CHAT_EVENT_TYPE.EVENT_SESSION_CHECKIN);
                    RestaurantTableModel tableModel = new RestaurantTableModel(message.getObject().getPk(), tableName, null, eventModel);
                    if (message.getActor().getType() == MessageObjectModel.MESSAGE_OBJECT_TYPE.RESTAURANT_MEMBER) {
                        user = message.getActor().getBriefModel();
                        tableModel.setHost(user);
                    }
                    ManagerTablesFragment.this.addTable(tableModel);
                    break;
                case MANAGER_SESSION_NEW_ORDER:
                case MANAGER_SESSION_EVENT_SERVICE:
                case MANAGER_SESSION_EVENT_CONCERN:
                case MANAGER_SESSION_CHECKOUT_REQUEST:
                    eventModel = EventBriefModel.getFromManagerEventModel(message.getRawData().getSessionEventBrief());
                    ManagerTablesFragment.this.updateSessionEventCount(message.getTarget().getPk(), eventModel);
                    break;
                case MANAGER_SESSION_HOST_ASSIGNED:
                    user = message.getObject().getBriefModel();
                    ManagerTablesFragment.this.updateSessionHost(message.getTarget().getPk(), user);
                    break;
                case MANAGER_SESSION_END:
                    ManagerTablesFragment.this.removeTable(message.getSessionDetail().getPk());
                    break;
            }
        }
    };

    public ManagerTablesFragment() {
    }

    public static ManagerTablesFragment newInstance() {
        return new ManagerTablesFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shop_manager_table, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAdapter = new ManagerWorkTableAdapter(this);
        rvShopManagerTable.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        rvShopManagerTable.setAdapter(mAdapter);

        mViewModel = ViewModelProviders.of(requireActivity()).get(ManagerWorkViewModel.class);
        mViewModel.getActiveTables().observe(this, input -> {
            if (input == null) return;
            if (input.status == Resource.Status.SUCCESS && input.data != null) {
                mAdapter.setRestaurantTableList(input.data);
            }
        });
        mViewModel.getCheckoutData().observe(this, resource -> {
            if (resource == null)
                return;
            if (resource.status == Resource.Status.SUCCESS && resource.data != null) {
                Utils.toast(requireContext(), resource.data.getMessage());
                if (resource.data.isCheckout())
                    mViewModel.updateRemoveTable(resource.data.getSessionPk());
                else
                    mViewModel.updateResults();
            } else if (resource.status != Resource.Status.LOADING) {
                Utils.toast(requireContext(), "Error: " + resource.message);
            }
        });
    }

    // region UI-Update
    private void addTable(RestaurantTableModel tableModel) {
        tableModel.setEventCount(1);
        mViewModel.addRestaurantTable(tableModel);
    }

    private void updateSessionEventCount(long sessionPk, EventBriefModel event) {
        int pos = mViewModel.getTablePositionWithPk(sessionPk);
        RestaurantTableModel table = mViewModel.getTableWithPosition(pos);
        if (table != null) {
            table.setEvent(event);
            if (event.getType() == CHAT_EVENT_TYPE.EVENT_REQUEST_CHECKOUT)
                table.setRequestedCheckout(true);
            table.addEventCount();
            mAdapter.updateSession(pos);
        }
    }

    private void updateSessionHost(long sessionPk, BriefModel user) {
        int pos = mViewModel.getTablePositionWithPk(sessionPk);
        RestaurantTableModel table = mViewModel.getTableWithPosition(pos);
        if (table != null) {
            table.setHost(user);
            mAdapter.updateSession(pos);
        }
    }

    private void removeTable(long sessionPk){
        mViewModel.updateRemoveTable(sessionPk);
    }
    // endregion

    @Override
    public void onResume() {
        super.onResume();
        MessageModel.MESSAGE_TYPE[] types = new MessageModel.MESSAGE_TYPE[] {
                MANAGER_SESSION_NEW, MANAGER_SESSION_NEW_ORDER, MANAGER_SESSION_EVENT_SERVICE, MANAGER_SESSION_CHECKOUT_REQUEST,
                MANAGER_SESSION_EVENT_CONCERN, MANAGER_SESSION_HOST_ASSIGNED, MANAGER_SESSION_END
        };
        MessageUtils.registerLocalReceiver(requireContext(), mReceiver, types);
    }

    @Override
    public void onPause() {
        super.onPause();
        MessageUtils.unregisterLocalReceiver(requireContext(), mReceiver);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onClickTable(RestaurantTableModel tableModel) {
        Intent intent = new Intent(getContext(), ManagerSessionActivity.class);
        intent.putExtra(ManagerSessionActivity.KEY_SESSION_PK, tableModel.getPk())
                .putExtra(ManagerSessionActivity.KEY_SHOP_PK, mViewModel.getShopPk());
        startActivity(intent);

        int pos = mViewModel.getTablePositionWithPk(tableModel.getPk());
        tableModel.setEventCount(0);
        mAdapter.updateSession(pos);
    }

    @Override
    public void onMarkSessionDone(RestaurantTableModel tableModel) {
        mViewModel.markSessionDone(tableModel.getPk());
        mViewModel.updateResults();
    }
}
