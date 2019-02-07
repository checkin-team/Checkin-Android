package com.checkin.app.checkin.Manager.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.checkin.app.checkin.Menu.Model.ItemCustomizationGroupModel;
import com.checkin.app.checkin.R;
import com.checkin.app.checkin.Session.ActiveSession.Chat.SessionChatModel;
import com.checkin.app.checkin.Session.Model.SessionOrderedItemModel;
import com.checkin.app.checkin.Utility.Utils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.checkin.app.checkin.Session.ActiveSession.Chat.SessionChatModel.CHAT_STATUS_TYPE.CANCELLED;
import static com.checkin.app.checkin.Session.ActiveSession.Chat.SessionChatModel.CHAT_STATUS_TYPE.IN_PROGRESS;
import static com.checkin.app.checkin.Session.ActiveSession.Chat.SessionChatModel.CHAT_STATUS_TYPE.OPEN;

public class ManagerSessionOrderAdapter extends RecyclerView.Adapter<ManagerSessionOrderAdapter.ViewHolder> {
    private List<SessionOrderedItemModel> mOrders;
    private SessionOrdersInteraction mListener;

    public ManagerSessionOrderAdapter(SessionOrdersInteraction ordersInterface) {
        mListener = ordersInterface;
    }

    public void setData(List<SessionOrderedItemModel> data) {
        this.mOrders = data;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bindData(mOrders.get(position));
    }

    @Override
    public int getItemCount() {
        return mOrders != null ? mOrders.size() : 0;
    }

    @Override
    public int getItemViewType(final int position) {
        return R.layout.item_manager_session_order;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_ms_order_item_name)
        TextView tvItemName;
        @BindView(R.id.tv_ms_order_item_quantity)
        TextView tvQuantity;
        @BindView(R.id.btn_ms_order_accept)
        Button tvOrderConfirm;
        @BindView(R.id.btn_ms_order_cancel)
        Button tvOrderReject;
        @BindView(R.id.tv_ms_order_remarks)
        TextView tvRemarks;
        @BindView(R.id.container_ms_order_customizations)
        ViewGroup containerCustomizations;
        @BindView(R.id.container_ms_order_customizations_left)
        LinearLayout containerCustomizationsLeft;
        @BindView(R.id.container_ms_order_customizations_right)
        LinearLayout containerCustomizationsRight;
        @BindView(R.id.container_ms_order_remarks)
        LinearLayout containerRemarks;
        @BindView(R.id.tv_ms_order_status)
        TextView tvOrderStatus;
        @BindView(R.id.container_ms_order_status_open)
        ViewGroup containerStatusOpen;

        private SessionOrderedItemModel mOrderModel;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            tvOrderConfirm.setOnClickListener(v -> mListener.onOrderStatusChange(mOrderModel, IN_PROGRESS));
            tvOrderReject.setOnClickListener(v -> mListener.onOrderStatusChange(mOrderModel, CANCELLED));
        }

        void bindData(SessionOrderedItemModel order) {
            this.mOrderModel = order;

            tvItemName.setText(order.getItem().getName());
            tvQuantity.setText(order.formatQuantityItemType());

            if (!order.getItem().isVegetarian())
                tvItemName.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_non_veg, 0, 0, 0);
            else
                tvItemName.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_veg, 0, 0, 0);

            if (order.getRemarks() == null) containerRemarks.setVisibility(View.GONE);
            else {
                tvRemarks.setText(order.getRemarks());
                containerRemarks.setVisibility(View.VISIBLE);
            }

            if (order.getCustomizations().size() > 0) {
                containerCustomizations.setVisibility(View.VISIBLE);
                containerCustomizationsRight.removeAllViews();
                containerCustomizationsLeft.removeAllViews();
                for (int i = 0; i < order.getCustomizations().size(); i++) {
                    addCustomizations(
                            itemView.getContext(), order.getCustomizations().get(i), i % 2 == 0 ? containerCustomizationsLeft : containerCustomizationsRight);
                }
            } else {
                containerCustomizations.setVisibility(View.GONE);
            }

            if (order.getStatus() == OPEN) {
                containerStatusOpen.setVisibility(View.VISIBLE);
                tvOrderStatus.setVisibility(View.GONE);
            } else {
                containerStatusOpen.setVisibility(View.GONE);
                tvOrderStatus.setVisibility(View.VISIBLE);
                switch (order.getStatus()) {
                    case IN_PROGRESS:
                        tvOrderStatus.setText(R.string.status_order_in_progress);
                        tvOrderStatus.setBackgroundColor(tvOrderStatus.getContext().getResources().getColor(R.color.apple_green));
                        break;
                    case DONE:
                        tvOrderStatus.setText(R.string.status_order_delivered);
                        tvOrderStatus.setBackgroundColor(tvOrderStatus.getContext().getResources().getColor(R.color.primary_red));
                        break;
                    case CANCELLED:
                        tvOrderStatus.setText(R.string.status_cancelled);
                        tvOrderStatus.setBackgroundColor(tvOrderStatus.getContext().getResources().getColor(R.color.primary_red));
                        break;
                }
            }
        }

        void addCustomizations(Context context, ItemCustomizationGroupModel group, ViewGroup container) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_active_session_order_customization, container, false);
            TextView tvGroupName = view.findViewById(R.id.tv_order_customization_group);
            TextView tvFieldNames = view.findViewById(R.id.tv_order_customization_names);
            tvGroupName.setText(group.getName());
            tvFieldNames.setText(Utils.joinCollection(group.getCustomizationFields(), "\n"));
            container.addView(view);

        }
    }

    public interface SessionOrdersInteraction {
        void onOrderStatusChange(SessionOrderedItemModel orderedItem, SessionChatModel.CHAT_STATUS_TYPE statusType);
    }
}
