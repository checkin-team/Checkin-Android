package com.checkin.app.checkin.Waiter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.checkin.app.checkin.Menu.Model.ItemCustomizationGroupModel;
import com.checkin.app.checkin.R;
import com.checkin.app.checkin.session.activesession.chat.SessionChatModel;
import com.checkin.app.checkin.session.activesession.chat.SessionChatModel.CHAT_EVENT_TYPE;
import com.checkin.app.checkin.session.activesession.chat.SessionEventBasicModel;
import com.checkin.app.checkin.session.model.SessionOrderedItemModel;
import com.checkin.app.checkin.Utility.Utils;
import com.checkin.app.checkin.Waiter.Model.WaiterEventModel;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class WaiterEventAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<WaiterEventModel> mData;
    private WaiterEventInteraction mListener;

    public WaiterEventAdapter(WaiterEventInteraction listener) {
        mListener = listener;
    }

    public void setData(List<WaiterEventModel> data) {
        mData = data;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);

        if (viewType == R.layout.item_waiter_table_order)
            return new OrderedItemViewHolder(view);
        else
            return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        WaiterEventModel event = mData.get(position);
        if (event.getType() == CHAT_EVENT_TYPE.EVENT_MENU_ORDER_ITEM)
            ((OrderedItemViewHolder) holder).bindData(event.getOrderedItem());
        else
            ((EventViewHolder) holder).bindData(event);
    }

    @Override
    public int getItemViewType(int position) {
        WaiterEventModel event = mData.get(position);
        if (event.getType() == CHAT_EVENT_TYPE.EVENT_MENU_ORDER_ITEM)
            return R.layout.item_waiter_table_order;
        else
            return R.layout.item_waiter_table_event;
    }

    @Override
    public int getItemCount() {
        return mData != null ? mData.size() : 0;
    }

    public interface WaiterEventInteraction {
        void onEventMarkDone(WaiterEventModel eventModel);

        void onOrderMarkDone(SessionOrderedItemModel orderedItemModel);

        void onOrderAccept(SessionOrderedItemModel orderedItemModel);

        void onOrderCancel(SessionOrderedItemModel orderedItemModel);
    }

    class OrderedItemViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_waiter_order_item_name)
        TextView tvItemName;
        @BindView(R.id.tv_waiter_order_time)
        TextView tvOrderTime;
        @BindView(R.id.tv_waiter_order_cost)
        TextView tvOrderCost;
        @BindView(R.id.tv_waiter_order_quantity)
        TextView tvQuantity;
        @BindView(R.id.tv_waiter_order_remarks)
        TextView tvRemarks;
        @BindView(R.id.container_waiter_order_customizations)
        ViewGroup containerCustomizations;
        @BindView(R.id.container_waiter_order_customizations_left)
        LinearLayout containerCustomizationsLeft;
        @BindView(R.id.container_waiter_order_customizations_right)
        LinearLayout containerCustomizationsRight;
        @BindView(R.id.container_waiter_order_remarks)
        ViewGroup containerRemarks;
        @BindView(R.id.container_waiter_order_status_open)
        ViewGroup containerStatusOpen;
        @BindView(R.id.container_waiter_order_status_progress)
        ViewGroup containerStatusProgress;
        @BindView(R.id.im_waiter_order_mark_done)
        ImageView imOrderMarkDone;
        @BindView(R.id.im_waiter_order_mark_cancel)
        ImageView imOrderMarkCancel;
        @BindView(R.id.tv_waiter_order_cancel)
        TextView tvOrderCancel;
        @BindView(R.id.tv_waiter_order_accept)
        TextView tvOrderAccept;

        private SessionOrderedItemModel mOrderedItem;

        public OrderedItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            imOrderMarkDone.setOnClickListener(v -> mListener.onOrderMarkDone(mOrderedItem));
            tvOrderAccept.setOnClickListener(v -> mListener.onOrderAccept(mOrderedItem));
            tvOrderCancel.setOnClickListener(v -> mListener.onOrderCancel(mOrderedItem));
            imOrderMarkCancel.setOnClickListener(view -> {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(view.getContext());
                alertDialogBuilder.setMessage("Are you sure want to cancel?").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        mListener.onOrderCancel(mOrderedItem);
                    }
                }).setNegativeButton("No", (dialog, which) -> dialog.dismiss()).show();
            });
        }

        public void bindData(SessionOrderedItemModel order) {
            this.mOrderedItem = order;

            tvItemName.setText(order.getItem().getName());
            tvQuantity.setText(order.formatQuantityItemType());
            tvOrderTime.setText(order.formatElapsedTime());
            tvOrderCost.setText(Utils.formatCurrencyAmount(itemView.getContext(), order.getCost()));

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

            switch (order.getStatus()) {
                case OPEN:
                    containerStatusOpen.setVisibility(View.VISIBLE);
                    containerStatusProgress.setVisibility(View.GONE);
                    break;
                case IN_PROGRESS:
                    containerStatusProgress.setVisibility(View.VISIBLE);
                    containerStatusOpen.setVisibility(View.GONE);
                    break;
                case DONE:
                    containerStatusProgress.setVisibility(View.GONE);
                    containerStatusOpen.setVisibility(View.GONE);
                    ((FrameLayout) itemView).setForeground(itemView.getResources().getDrawable(R.color.translucent_white));
                    break;
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

    class EventViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.im_waiter_event_type)
        ImageView imEventType;
        @BindView(R.id.tv_waiter_event_message)
        TextView tvEventMessage;
        @BindView(R.id.im_waiter_event_mark_done)
        ImageView imEventMarkDone;

        private WaiterEventModel mEventModel;

        public EventViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            imEventMarkDone.setOnClickListener(v -> mListener.onEventMarkDone(mEventModel));
        }

        public void bindData(WaiterEventModel eventModel) {
            this.mEventModel = eventModel;

            tvEventMessage.setText(eventModel.getMessage());
            imEventType.setImageResource(SessionEventBasicModel.getEventIcon(eventModel.getType(), eventModel.getService(), null));
            if (eventModel.getStatus() == SessionChatModel.CHAT_STATUS_TYPE.DONE || eventModel.getStatus() == SessionChatModel.CHAT_STATUS_TYPE.CANCELLED) {
                imEventMarkDone.setVisibility(View.GONE);
                ((FrameLayout) itemView).setForeground(itemView.getResources().getDrawable(R.color.translucent_white));
            }
        }
    }
}
