package com.checkin.app.checkin.ManagerOrders;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

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

        private SessionOrderedItemModel mOrderModel;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        void bindData(SessionOrderedItemModel orderedItem) {
            if (orderedItem.getStatus() != OPEN) {
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

            }
        }

    }
}

