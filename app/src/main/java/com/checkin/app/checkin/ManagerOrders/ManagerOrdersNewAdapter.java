package com.checkin.app.checkin.ManagerOrders;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
    private List<SessionOrderedItemModel> mOrdersNew;
    private List<SessionOrderedItemModel> mOrdersAccepted;
    private SessionOrdersInteraction mListener;
    private boolean mIsNew;

    ManagerOrdersNewAdapter(List<SessionOrderedItemModel> orders, SessionOrdersInteraction ordersInterface, boolean isNew) {
        mOrdersNew = orders;
//        mOrdersAccepted = orders;
        mIsNew = isNew;
        mListener = ordersInterface;
    }

    public void setDataNew(List<SessionOrderedItemModel> data) {
        this.mOrdersNew = data;
//        mIsNew = isNew;
        notifyDataSetChanged();
    }

    public void setDataAccepted(List<SessionOrderedItemModel> data) {
        this.mOrdersAccepted = data;
//        mIsNew = isNew;
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
        Log.e("is=====bindview=", String.valueOf(mOrdersNew.size()));
        holder.bindData(mOrdersNew.get(position));
//       if(mIsNew) holder.bindData(mOrdersNew.get(position), mIsNew);
//       else holder.bindData(mOrdersAccepted.get(position), mIsNew);
    }

    @Override
    public int getItemCount() {
        return mOrdersNew != null /*&& mOrdersAccepted !=null */? mOrdersNew.size()/* + mOrdersAccepted.size()*/ : 0;
    }

    @Override
    public int getItemViewType(final int position) {
        return R.layout.item_manager_orders_new;
//        if (mOrdersNew.get(position).getStatus() == OPEN)
//            return R.layout.item_manager_orders_new;
//        else return R.layout.item_manager_orders_accepted;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_order_item_name)
        TextView tvItemName;
        @BindView(R.id.tv_order_quantity)
        TextView tvQuantity;
        @BindView(R.id.tv_order_confirm)
        Button tvOrderConfirm;
        @BindView(R.id.tv_order_reject)
        Button tvOrderReject;
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
        @BindView(R.id.tv_order_status)
        TextView tvOrderStatus;

        private SessionOrderedItemModel mOrderModel;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            tvOrderConfirm.setOnClickListener(v -> mListener.onOrderStatusChange(mOrderModel, IN_PROGRESS));
            tvOrderReject.setOnClickListener(v -> mListener.onOrderStatusChange(mOrderModel, CANCELLED));
        }

        void bindData(SessionOrderedItemModel order) {
            this.mOrderModel = order;
            Log.e("is======11==", order.getItem().getName());

//            if (order.getStatus() == OPEN) {
                tvItemName.setText(order.getItem().getName());
                tvQuantity.setText(order.formatQuantityItemType());

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

                /*if(!mIsNew){
                    Log.e("is======", String.valueOf(mIsNew));
                    tvOrderStatus.setVisibility(View.VISIBLE);
                    tvOrderConfirm.setVisibility(View.GONE);
                    tvOrderReject.setVisibility(View.GONE);
                } else{
                    Log.e("is====else==", String.valueOf(mIsNew));
                    tvOrderConfirm.setVisibility(View.VISIBLE);
                    tvOrderReject.setVisibility(View.VISIBLE);
                    tvOrderStatus.setVisibility(View.GONE);
                }

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
                }*/
//            }
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
