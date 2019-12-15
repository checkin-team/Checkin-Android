package com.checkin.app.checkin.Manager.Fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.checkin.app.checkin.Data.Message.MessageModel;
import com.checkin.app.checkin.Data.Message.MessageObjectModel;
import com.checkin.app.checkin.Data.Message.MessageUtils;
import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.Manager.Adapter.ManagerWorkTableAdapter;
import com.checkin.app.checkin.Manager.ManagerSessionActivity;
import com.checkin.app.checkin.Manager.ManagerSessionInvoiceActivity;
import com.checkin.app.checkin.Manager.ManagerWorkViewModel;
import com.checkin.app.checkin.misc.fragments.BaseFragment;
import com.checkin.app.checkin.misc.models.BriefModel;
import com.checkin.app.checkin.R;
import com.checkin.app.checkin.Utility.Utils;
import com.checkin.app.checkin.session.activesession.chat.SessionChatModel.CHAT_EVENT_TYPE;
import com.checkin.app.checkin.session.model.EventBriefModel;
import com.checkin.app.checkin.session.model.RestaurantTableModel;
import com.checkin.app.checkin.session.model.TableSessionModel;

import java.util.Calendar;
import java.util.List;

import butterknife.BindView;

import static com.checkin.app.checkin.Data.Message.MessageModel.MESSAGE_TYPE.MANAGER_SESSION_CHECKOUT_REQUEST;
import static com.checkin.app.checkin.Data.Message.MessageModel.MESSAGE_TYPE.MANAGER_SESSION_END;
import static com.checkin.app.checkin.Data.Message.MessageModel.MESSAGE_TYPE.MANAGER_SESSION_EVENT_CONCERN;
import static com.checkin.app.checkin.Data.Message.MessageModel.MESSAGE_TYPE.MANAGER_SESSION_EVENT_SERVICE;
import static com.checkin.app.checkin.Data.Message.MessageModel.MESSAGE_TYPE.MANAGER_SESSION_HOST_ASSIGNED;
import static com.checkin.app.checkin.Data.Message.MessageModel.MESSAGE_TYPE.MANAGER_SESSION_NEW;
import static com.checkin.app.checkin.Data.Message.MessageModel.MESSAGE_TYPE.MANAGER_SESSION_NEW_ORDER;

public class ManagerTablesFragment extends BaseFragment implements ManagerWorkTableAdapter.ManagerTableInteraction {

    @BindView(R.id.rv_shop_manager_table)
    RecyclerView rvShopManagerTable;
    @BindView(R.id.ll_no_orders)
    LinearLayout llNoLiveOrders;

    private ManagerWorkTableAdapter mAdapter;
    private ManagerWorkViewModel mViewModel;
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            MessageModel message = MessageUtils.parseMessage(intent);
            if (message == null) return;

            MessageObjectModel shop = message.getShopDetail();
            if (shop != null && shop.getPk() != mViewModel.getShopPk())
                return;

            EventBriefModel eventModel;
            BriefModel user;
            switch (message.getType()) {
                case MANAGER_SESSION_NEW:
                    String tableName = message.getRawData().getSessionTableName();
                    eventModel = EventBriefModel.getFromManagerEventModel(message.getRawData().getSessionEventBrief());
                    eventModel.setType(CHAT_EVENT_TYPE.EVENT_SESSION_CHECKIN);
                    MessageObjectModel sessionData = message.getSessionDetail();
                    assert sessionData != null;
                    TableSessionModel tableSessionModel = new TableSessionModel(sessionData.getPk(), null, eventModel);
                    tableSessionModel.setCreated(Calendar.getInstance().getTime());
                    RestaurantTableModel tableModel = new RestaurantTableModel(message.getObject().getPk(), tableName, tableSessionModel);
                    if (message.getActor().getType() == MessageObjectModel.MESSAGE_OBJECT_TYPE.RESTAURANT_MEMBER) {
                        user = message.getActor().getBriefModel();
                        if (tableModel.getTableSession() != null) {
                            tableModel.getTableSession().setHost(user);
                        }
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
    private List<RestaurantTableModel> mListData;

    public ManagerTablesFragment() {
    }

    public static ManagerTablesFragment newInstance() {
        return new ManagerTablesFragment();
    }

    @Override
    protected int getRootLayout() {
        return R.layout.fragment_shop_manager_table;
    }

    private void updateUi(List<RestaurantTableModel> data) {
        if (data.size() > 0) {
            mAdapter.setRestaurantTableList(data);
            rvShopManagerTable.setVisibility(View.VISIBLE);
            llNoLiveOrders.setVisibility(View.GONE);
        } else {
            rvShopManagerTable.setVisibility(View.GONE);
            llNoLiveOrders.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mAdapter = new ManagerWorkTableAdapter(this);
        rvShopManagerTable.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        rvShopManagerTable.setAdapter(mAdapter);

        mViewModel = ViewModelProviders.of(requireActivity()).get(ManagerWorkViewModel.class);
        mViewModel.getActiveTables().observe(this, input -> {
            if (input == null) return;
            if (input.getStatus() == Resource.Status.SUCCESS && input.getData() != null) {
                mListData = input.getData();
                updateUi(mListData);
            } else if (input.getStatus() != Resource.Status.LOADING) {
                Utils.toast(requireContext(), input.getMessage());

            }
        });
        mViewModel.getCheckoutData().observe(this, resource -> {
            if (resource == null)
                return;
            if (resource.getStatus() == Resource.Status.SUCCESS && resource.getData() != null) {
                Utils.toast(requireContext(), resource.getData().getMessage());
                if (resource.getData().isCheckout())
                    mViewModel.updateRemoveTable(resource.getData().getSessionPk());
                else
                    mViewModel.updateResults();
            } else if (resource.getStatus() != Resource.Status.LOADING) {
                Utils.toast(requireContext(), resource.getMessage());
            }
        });
        mViewModel.getSessionInitiated().observe(this, qrResultModelResource -> {
            if (qrResultModelResource == null) return;
            if (qrResultModelResource.getStatus() == Resource.Status.SUCCESS && qrResultModelResource.getData() != null) {
                mViewModel.fetchActiveTables(mViewModel.getShopPk());
            } else {
                Utils.toast(requireContext(), qrResultModelResource.getMessage());
            }
        });
    }

    // region UI-Update
    private void addTable(RestaurantTableModel tableModel) {
        tableModel.setEventCount(1);
        mViewModel.addRestaurantTable(tableModel);
    }

    private void updateSessionEventCount(long sessionPk, EventBriefModel event) {
        mViewModel.updateTable(sessionPk, event);
    }

    private void updateSessionHost(long sessionPk, BriefModel user) {
        int pos = mViewModel.getTablePositionWithPk(sessionPk);
        RestaurantTableModel table = mViewModel.getTableWithPosition(pos);
        if (table != null) {
            TableSessionModel tableSessionModel = table.getTableSession();
            if (tableSessionModel != null) {
                tableSessionModel.setHost(user);
            }
            mAdapter.updateSession(pos);
        }
    }

    private void removeTable(long sessionPk) {
        mViewModel.updateRemoveTable(sessionPk);
    }
    // endregion

    @Override
    public void onResume() {
        super.onResume();
        MessageModel.MESSAGE_TYPE[] types = new MessageModel.MESSAGE_TYPE[]{
                MANAGER_SESSION_NEW, MANAGER_SESSION_NEW_ORDER, MANAGER_SESSION_EVENT_SERVICE, MANAGER_SESSION_CHECKOUT_REQUEST,
                MANAGER_SESSION_EVENT_CONCERN, MANAGER_SESSION_HOST_ASSIGNED, MANAGER_SESSION_END
        };
        MessageUtils.registerLocalReceiver(requireContext(), mReceiver, types);
        mViewModel.updateResults();
//        MessageUtils.dismissNotification(this, MessageObjectModel.MESSAGE_OBJECT_TYPE.SESSION, mViewModel.getSessionPk());

    }

    @Override
    public void onPause() {
        super.onPause();
        MessageUtils.unregisterLocalReceiver(requireContext(), mReceiver);
    }

    @Override
    public void onClickTable(RestaurantTableModel tableModel) {
        if (tableModel.getTableSession() != null) {
            Intent intent = new Intent(getContext(), ManagerSessionActivity.class);
            intent.putExtra(ManagerSessionActivity.KEY_SESSION_PK, tableModel.getTableSession().getPk())
                    .putExtra(ManagerSessionActivity.KEY_SHOP_PK, mViewModel.getShopPk());
            startActivity(intent);

            int pos = mViewModel.getTablePositionWithPk(tableModel.getTableSession().getPk());
            tableModel.setEventCount(0);
            mAdapter.updateSession(pos);
        }
    }

    @Override
    public void onClickBill(RestaurantTableModel tableModel) {
        Intent intent = new Intent(getContext(), ManagerSessionInvoiceActivity.class);
        intent.putExtra(ManagerSessionInvoiceActivity.TABLE_NAME, tableModel.getTable())
                .putExtra(ManagerSessionInvoiceActivity.KEY_SESSION, tableModel.getTableSession().getPk())
                .putExtra(ManagerSessionInvoiceActivity.IS_REQUESTED_CHECKOUT, tableModel.getTableSession().isRequestedCheckout());
        startActivity(intent);
    }

    @Override
    public void onMarkSessionDone(RestaurantTableModel tableModel) {
        if (tableModel.getTableSession() != null) {
            mViewModel.markSessionDone(tableModel.getTableSession().getPk());
            mViewModel.updateResults();
        }
    }
}
