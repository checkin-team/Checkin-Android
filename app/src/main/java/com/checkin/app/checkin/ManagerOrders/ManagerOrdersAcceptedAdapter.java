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
import com.checkin.app.checkin.Session.Model.SessionOrderedItemModel;
import com.checkin.app.checkin.Utility.Utils;

import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.checkin.app.checkin.Session.ActiveSession.Chat.SessionChatModel.CHAT_STATUS_TYPE.OPEN;

public class ManagerOrdersAcceptedAdapter extends RecyclerView.Adapter<ManagerOrdersAcceptedAdapter.ViewHolder> {
    private List<SessionOrderedItemModel> mOrders;

    ManagerOrdersAcceptedAdapter(List<SessionOrderedItemModel> orders) {
        mOrders = orders;
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
        return R.layout.item_manager_orders_accepted;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_order_item_name)
        TextView tvName;
        @BindView(R.id.tv_order_status)
        TextView tvOrderStatus;
        @BindView(R.id.tv_order_quantity)
        TextView tvQuality;
        @BindView(R.id.im_order_type)
        ImageView imOrderType;
        @BindView(R.id.tv_order_remarks)
        TextView tvRemarks;
        @BindView(R.id.container_order_customizations)
        ViewGroup containerCustomizations;
        @BindView(R.id.container_order_customizations_left)
        LinearLayout containerCustomizationsLeft;
        @BindView(R.id.container_order_customizations_right)
        LinearLayout containerCustomizationsRight;
        @BindView(R.id.container_order_remarks)
        LinearLayout containerRemarks;

        private SessionOrderedItemModel mOrderModel;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        void bindData(SessionOrderedItemModel orderedItem) {

                tvName.setText(orderedItem.getItem().getName());
                tvQuality.setText(orderedItem.formatQuantityItemType());
                if (!orderedItem.getItem().isVegetarian())
                    imOrderType.setImageDrawable(imOrderType.getContext().getResources().getDrawable(R.drawable.ic_non_veg_square));

                switch (orderedItem.getStatus()) {
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

            if (orderedItem.getRemarks() == null) containerRemarks.setVisibility(View.GONE);
            else tvRemarks.setText(orderedItem.getRemarks());

            if (orderedItem.getCustomizations().size() > 0) {
                containerCustomizations.setVisibility(View.VISIBLE);
                containerCustomizationsRight.removeAllViews();
                containerCustomizationsLeft.removeAllViews();
                for (int i = 0; i < orderedItem.getCustomizations().size(); i++) {
                    addCustomizations(
                            itemView.getContext(), orderedItem.getCustomizations().get(i), i % 2 == 0 ? containerCustomizationsLeft : containerCustomizationsRight);
                }
            } else {
                containerCustomizations.setVisibility(View.GONE);
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
}

