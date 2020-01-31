package com.checkin.app.checkin.Cook.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.checkin.app.checkin.R;
import com.checkin.app.checkin.menu.models.ItemCustomizationGroupModel;
import com.checkin.app.checkin.session.activesession.chat.SessionChatModel;
import com.checkin.app.checkin.session.models.SessionOrderedItemModel;
import com.checkin.app.checkin.utility.HeaderFooterRecyclerViewAdapter;
import com.checkin.app.checkin.utility.Utils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.checkin.app.checkin.session.activesession.chat.SessionChatModel.CHAT_STATUS_TYPE.CANCELLED;
import static com.checkin.app.checkin.session.activesession.chat.SessionChatModel.CHAT_STATUS_TYPE.COOKED;
import static com.checkin.app.checkin.session.activesession.chat.SessionChatModel.CHAT_STATUS_TYPE.IN_PROGRESS;

public class CookSessionOrderAdapter extends HeaderFooterRecyclerViewAdapter {
    private List<SessionOrderedItemModel> mOrders;
    private SessionOrdersInteraction mListener;
    private boolean mShowFooter;

    public CookSessionOrderAdapter(SessionOrdersInteraction ordersInterface, boolean showFooter) {
        mListener = ordersInterface;
        mShowFooter = showFooter;
    }

    public void setData(List<SessionOrderedItemModel> data) {
        this.mOrders = data;
        notifyDataSetChanged();
    }

    @Override
    public boolean useHeader() {
        return false;
    }

    @Override
    public boolean useFooter() {
        return mShowFooter;
    }

    @Override
    public RecyclerView.ViewHolder onCreateFooterViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_manager_session_confirm_order, parent, false);
        return new FooterViewHolder(view);
    }

    @Override
    public RecyclerView.ViewHolder onCreateBasicItemViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindBasicItemView(RecyclerView.ViewHolder holder, int position) {
        ((ViewHolder) holder).bindData(mOrders.get(position));
    }

    @Override
    public int getBasicItemCount() {
        return mOrders != null ? mOrders.size() : 0;
    }

    @Override
    public int getBasicItemType(int position) {
        return R.layout.item_cook_session_order;
    }

    public interface SessionOrdersInteraction {
        void confirmNewOrders();

        void onSelectDeselect(SessionOrderedItemModel orderedItem, SessionChatModel.CHAT_STATUS_TYPE statusType);

        void onOrderStatusChange(SessionOrderedItemModel orderedItem, SessionChatModel.CHAT_STATUS_TYPE statusType);
    }

    class FooterViewHolder extends RecyclerView.ViewHolder {
        FooterViewHolder(View view) {
            super(view);
            view.findViewById(R.id.btn_ms_order_confirm).setOnClickListener((v) -> mListener.confirmNewOrders());
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_ms_order_item_name)
        TextView tvItemName;
        @BindView(R.id.tv_ms_order_item_quantity)
        TextView tvQuantity;
        @BindView(R.id.cb_ms_order_accept)
        CheckBox cbOrderAccept;
        @BindView(R.id.im_cs_order_done)
        ImageView btnOrderDone;
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

            btnOrderDone.setOnClickListener(v -> mListener.onOrderStatusChange(mOrderModel, COOKED));
            cbOrderAccept.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) mListener.onSelectDeselect(mOrderModel, IN_PROGRESS);
                else mListener.onSelectDeselect(mOrderModel, CANCELLED);
            });
        }

        private void resetLayout() {
            containerRemarks.setVisibility(View.GONE);
            containerCustomizations.setVisibility(View.GONE);
            containerStatusOpen.setVisibility(View.GONE);
            tvOrderStatus.setVisibility(View.GONE);
            btnOrderDone.setVisibility(View.GONE);
        }

        void bindData(SessionOrderedItemModel order) {
            resetLayout();
            this.mOrderModel = order;

            tvItemName.setText(order.getItem().getName());
            tvQuantity.setText(order.formatQuantityItemType());

            if (!order.getItem().isVegetarian())
                tvItemName.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_non_veg, 0, 0, 0);
            else
                tvItemName.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_veg, 0, 0, 0);

            if (order.getRemarks() != null) {
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
            }

            switch (order.getStatus()) {
                case OPEN:
                    containerStatusOpen.setVisibility(View.VISIBLE);
                    break;
                case IN_PROGRESS:
                    btnOrderDone.setVisibility(View.VISIBLE);
                    break;
                case COOKED:
                    tvOrderStatus.setText(R.string.status_order_cooked);
                    tvOrderStatus.setVisibility(View.VISIBLE);
                    tvOrderStatus.setBackgroundColor(tvOrderStatus.getContext().getResources().getColor(R.color.apple_green));
                    break;
                case DONE:
                    tvOrderStatus.setText(R.string.status_order_delivered);
                    tvOrderStatus.setVisibility(View.VISIBLE);
                    tvOrderStatus.setBackgroundColor(tvOrderStatus.getContext().getResources().getColor(R.color.apple_green));
                    break;
                case CANCELLED:
                    tvOrderStatus.setText(R.string.status_cancelled);
                    tvOrderStatus.setVisibility(View.VISIBLE);
                    tvOrderStatus.setBackgroundColor(tvOrderStatus.getContext().getResources().getColor(R.color.primary_red));
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
}
