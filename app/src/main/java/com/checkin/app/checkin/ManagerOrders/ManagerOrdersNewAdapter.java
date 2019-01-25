package com.checkin.app.checkin.ManagerOrders;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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

public class ManagerOrdersNewAdapter extends RecyclerView.Adapter<ManagerOrdersNewAdapter.ViewHolder> {
    private List<SessionOrderedItemModel> mOrders;
    private SessionOrdersInteraction mListener;

    ManagerOrdersNewAdapter(List<SessionOrderedItemModel> orders, SessionOrdersInteraction ordersInterface) {
        mOrders = orders;
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
        return R.layout.item_manager_orders_new;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_order_item_name)
        TextView tvItemName;
        @BindView(R.id.tv_order_quantity)
        TextView tvQuantity;
        @BindView(R.id.tv_order_confirm)
        TextView tvOrderConfirm;
        @BindView(R.id.tv_order_reject)
        TextView tvOrderReject;
        @BindView(R.id.tv_order_remarks)
        TextView tvRemarks;
        @BindView(R.id.im_order_type)
        ImageView imOrderType;
        @BindView(R.id.container_order_customizations)
        ViewGroup containerCustomizations;
        @BindView(R.id.container_order_customizations_left)
        LinearLayout containerCustomizationsLeft;
        @BindView(R.id.container_order_customizations_right)
        LinearLayout containerCustomizationsRight;
        @BindView(R.id.container_order_remarks)
        LinearLayout containerRemarks;

        private SessionOrderedItemModel mOrderModel;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            tvOrderConfirm.setOnClickListener(v -> mListener.onOrderStatusChange(mOrderModel, IN_PROGRESS));
            tvOrderReject.setOnClickListener(v -> mListener.onOrderStatusChange(mOrderModel, CANCELLED));
        }

        void bindData(SessionOrderedItemModel order) {
            this.mOrderModel = order;

            if (order.getStatus() == OPEN) {
                tvItemName.setText(order.getItem().getName());
                tvQuantity.setText(order.formatQuantityType());

                if (!order.getItem().isVegetarian())
                    imOrderType.setImageDrawable(imOrderType.getContext().getResources().getDrawable(R.drawable.ic_non_veg_square));

                if (order.getRemarks() == null) containerRemarks.setVisibility(View.GONE);
                else tvRemarks.setText(order.getRemarks());

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
            }
        }

        void addCustomizations(Context context, ItemCustomizationGroupModel group, ViewGroup
                container) {
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
