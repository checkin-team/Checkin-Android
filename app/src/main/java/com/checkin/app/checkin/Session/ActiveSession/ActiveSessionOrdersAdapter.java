package com.checkin.app.checkin.Session.ActiveSession;

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
import com.checkin.app.checkin.Session.Model.SessionOrderedItemModel;
import com.checkin.app.checkin.Utility.Utils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ActiveSessionOrdersAdapter extends RecyclerView.Adapter<ActiveSessionOrdersAdapter.ViewHolder> {
    private List<SessionOrderedItemModel> mOrders;
    private SessionOrdersInteraction mListener;

    ActiveSessionOrdersAdapter(List<SessionOrderedItemModel> orders, SessionOrdersInteraction ordersInterface) {
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
        return R.layout.item_active_session_order;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_order_item_name)
        TextView tvItemName;
        @BindView(R.id.tv_order_quantity)
        TextView tvQuantity;
        @BindView(R.id.tv_order_cost)
        TextView tvPrice;
        @BindView(R.id.tv_order_time)
        TextView tvElapsedTime;
        @BindView(R.id.tv_order_remarks)
        TextView tvRemarks;
        @BindView(R.id.im_order_cancel)
        ImageView imCancelOrder;
        @BindView(R.id.tv_order_status)
        TextView tvOrderStatus;
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

            imCancelOrder.setOnClickListener(v -> mListener.onCancelOrder(mOrderModel));
        }

        void bindData(SessionOrderedItemModel order) {
            this.mOrderModel = order;

            tvItemName.setText(order.getItem().getName());
            tvQuantity.setText(order.formatQuantityType());
            tvElapsedTime.setText(order.formatElapsedTime());
            tvPrice.setText(Utils.formatCurrencyAmount(itemView.getContext(), order.getCost()));

            if (!order.canCancel()) imCancelOrder.setVisibility(View.GONE);
            else imCancelOrder.setVisibility(View.VISIBLE);

            if (order.getRemarks() == null) containerRemarks.setVisibility(View.GONE);
            else{
                containerRemarks.setVisibility(View.VISIBLE);
                tvRemarks.setText(order.getRemarks());
            }

            switch (order.getStatus()) {
                case OPEN:
                    tvOrderStatus.setText(R.string.status_order_open);
                    tvOrderStatus.setTextColor(itemView.getResources().getColor(R.color.order_status_bad));
                    tvOrderStatus.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_order_new_grey, 0, 0);
                    break;
                case IN_PROGRESS:
                    tvOrderStatus.setText(R.string.status_order_in_progress);
                    tvOrderStatus.setTextColor(itemView.getResources().getColor(R.color.order_status_good));
                    tvOrderStatus.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_order_cooking_grey, 0, 0);
                    break;
                case DONE:
                    tvOrderStatus.setText(R.string.status_order_delivered);
                    tvOrderStatus.setTextColor(itemView.getResources().getColor(R.color.order_status_good));
                    tvOrderStatus.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_order_delivered_grey, 0, 0);
                    break;
                case CANCELLED:
                    tvOrderStatus.setText(R.string.status_order_cancelled);
                    tvOrderStatus.setTextColor(itemView.getResources().getColor(R.color.order_status_bad));
                    tvOrderStatus.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                    break;
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
        void onCancelOrder(SessionOrderedItemModel orderedItem);
    }
}
