package com.checkin.app.checkin.Cook.Fragment;

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

import com.checkin.app.checkin.Cook.Adapter.CookWorkTableAdapter;
import com.checkin.app.checkin.Cook.CookSessionActivity;
import com.checkin.app.checkin.Cook.CookWorkViewModel;
import com.checkin.app.checkin.Cook.Model.CookTableModel;
import com.checkin.app.checkin.Data.Message.MessageModel;
import com.checkin.app.checkin.Data.Message.MessageObjectModel;
import com.checkin.app.checkin.Data.Message.MessageUtils;
import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.misc.fragments.BaseFragment;
import com.checkin.app.checkin.misc.models.BriefModel;
import com.checkin.app.checkin.R;
import com.checkin.app.checkin.Utility.Utils;

import java.util.Calendar;
import java.util.List;

import butterknife.BindView;

import static com.checkin.app.checkin.Data.Message.MessageModel.MESSAGE_TYPE.COOK_SESSION_END;
import static com.checkin.app.checkin.Data.Message.MessageModel.MESSAGE_TYPE.COOK_SESSION_HOST_ASSIGNED;
import static com.checkin.app.checkin.Data.Message.MessageModel.MESSAGE_TYPE.COOK_SESSION_NEW;
import static com.checkin.app.checkin.Data.Message.MessageModel.MESSAGE_TYPE.COOK_SESSION_NEW_ORDER;
import static com.checkin.app.checkin.Data.Message.MessageModel.MESSAGE_TYPE.COOK_SESSION_SWITCH_TABLE;
import static com.checkin.app.checkin.Data.Message.MessageModel.MESSAGE_TYPE.COOK_SESSION_UPDATE_ORDER;

public class CookTablesFragment extends BaseFragment implements CookWorkTableAdapter.CookTableInteraction {

    @BindView(R.id.rv_shop_cook_tables)
    RecyclerView rvShopManagerTable;
    @BindView(R.id.ll_no_orders)
    LinearLayout llNoLiveOrders;

    private CookWorkTableAdapter mAdapter;
    private CookWorkViewModel mViewModel;
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            MessageModel message = MessageUtils.parseMessage(intent);
            if (message == null) return;

            MessageObjectModel shop = message.getShopDetail();
            if (shop != null && shop.getPk() != mViewModel.getShopPk())
                return;

            BriefModel user;
            switch (message.getType()) {
                case COOK_SESSION_NEW:
                    MessageObjectModel sessionData = message.getSessionDetail();
                    assert sessionData != null;

                    CookTableModel tableModel = new CookTableModel(sessionData.getPk(), null);
                    tableModel.setCreated(Calendar.getInstance().getTime());
                    tableModel.setTable(message.getRawData().getSessionTableName());
                    if (message.getActor().getType() == MessageObjectModel.MESSAGE_OBJECT_TYPE.RESTAURANT_MEMBER) {
                        user = message.getActor().getBriefModel();
                        tableModel.setHost(user);
                    }
                    CookTablesFragment.this.addTable(tableModel);
                    break;
                case COOK_SESSION_NEW_ORDER:
                    CookTablesFragment.this.updateSessionNewOrder(message.getTarget().getPk());
                    break;
                case COOK_SESSION_HOST_ASSIGNED:
                    user = message.getObject().getBriefModel();
                    CookTablesFragment.this.updateSessionHost(message.getTarget().getPk(), user);
                    break;
                case COOK_SESSION_SWITCH_TABLE:
                    mViewModel.fetchActiveTables(mViewModel.getShopPk());
                    break;
                case COOK_SESSION_END:
                    assert message.getSessionDetail() != null;
                    CookTablesFragment.this.removeTable(message.getSessionDetail().getPk());
                    break;
            }
        }
    };

    public CookTablesFragment() {
    }

    public static CookTablesFragment newInstance() {
        return new CookTablesFragment();
    }

    @Override
    protected int getRootLayout() {
        return R.layout.fragment_shop_cook_tables;
    }

    private void updateUi(List<CookTableModel> data) {
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
        mAdapter = new CookWorkTableAdapter(this);
        rvShopManagerTable.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        rvShopManagerTable.setAdapter(mAdapter);

        mViewModel = ViewModelProviders.of(requireActivity()).get(CookWorkViewModel.class);
        mViewModel.getActiveTables().observe(this, input -> {
            if (input == null) return;
            if (input.getStatus() == Resource.Status.SUCCESS && input.getData() != null) {
                updateUi(input.getData());
            } else if (input.getStatus() != Resource.Status.LOADING) {
                Utils.toast(requireContext(), input.getMessage());

            }
        });
    }

    // region UI-Update
    private void addTable(CookTableModel tableModel) {
        tableModel.increaseEventCount();
        mViewModel.addRestaurantTable(tableModel);
    }

    private void updateSessionNewOrder(long sessionPk) {
        mViewModel.updateTable(sessionPk, true);
    }

    private void updateSessionHost(long sessionPk, BriefModel user) {
        int pos = mViewModel.getTablePositionWithPk(sessionPk);
        CookTableModel table = mViewModel.getTableWithPosition(pos);
        if (table != null) {
            table.setHost(user);
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
                COOK_SESSION_NEW, COOK_SESSION_NEW_ORDER, COOK_SESSION_UPDATE_ORDER, COOK_SESSION_HOST_ASSIGNED,
                COOK_SESSION_SWITCH_TABLE, COOK_SESSION_END
        };
        MessageUtils.registerLocalReceiver(requireContext(), mReceiver, types);
        mViewModel.updateResults();

    }

    @Override
    public void onPause() {
        super.onPause();
        MessageUtils.unregisterLocalReceiver(requireContext(), mReceiver);
    }

    @Override
    public void onClickTable(CookTableModel tableModel) {
        Intent intent = new Intent(getContext(), CookSessionActivity.class);
        intent.putExtra(CookSessionActivity.KEY_SESSION_PK, tableModel.getPk())
                .putExtra(CookSessionActivity.KEY_SHOP_PK, mViewModel.getShopPk());
        startActivity(intent);

        int pos = mViewModel.getTablePositionWithPk(tableModel.getPk());
        tableModel.resetEventCount();
        mAdapter.updateSession(pos);
    }
}
