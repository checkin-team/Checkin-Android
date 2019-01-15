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

import com.checkin.app.checkin.R;
import com.checkin.app.checkin.Session.SessionViewOrdersModel;
import com.checkin.app.checkin.Utility.Utils;

import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ActiveSessionViewOrdersAdapters extends RecyclerView.Adapter<ActiveSessionViewOrdersAdapters.ViewHolder> {

    List<SessionViewOrdersModel> mOrders;
    Context mContext;
    private SessionOrdersInteraction mOrdersInterface;

    ActiveSessionViewOrdersAdapters(List<SessionViewOrdersModel> orders, Context context, SessionOrdersInteraction ordersInterface) {
        mOrders = orders;
        mContext = context;
        mOrdersInterface = ordersInterface;
    }

    public void setData(List<SessionViewOrdersModel> data) {
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
        return R.layout.item_active_session_view_orders;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_food_name)
        TextView tv_food_name;
        @BindView(R.id.tv_quantity)
        TextView tv_quantity;
        @BindView(R.id.tv_price)
        TextView tv_price;
        @BindView(R.id.tv_orders_time)
        TextView tv_orders_time;
        @BindView(R.id.tv_remarks)
        TextView tv_remarks;
        @BindView(R.id.im_cancel_order)
        ImageView im_cancel_order;
        @BindView(R.id.tv_order_status)
        TextView tv_order_status;
        @BindView(R.id.add_on_container)
        LinearLayout add_on_container;
        @BindView(R.id.ll_toppings)
        LinearLayout ll_toppings;
        @BindView(R.id.ll_crust)
        LinearLayout ll_crust;
        @BindView(R.id.ll_customization_title)
        LinearLayout ll_customization_title;
        @BindView(R.id.request_container)
        LinearLayout request_container;
        @BindView(R.id.line_after_add_on)
        View line_after_add_on;
        SessionViewOrdersModel order;


        ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }

        void bindData(SessionViewOrdersModel order) {
            this.order = order;
            tv_food_name.setText(order.getItem().getName());
            tv_quantity.setText("QTY: " + order.getQuantity() + " " +order.getItem_type());
            tv_price.setText(mContext.getResources().getString(R.string.rs) + " " + order.formatCost());
            tv_orders_time.setText(Utils.formatElapsedTime(order.getOrdered(), Calendar.getInstance().getTime()));

            if (order.canCancel()) im_cancel_order.setVisibility(View.VISIBLE);
            else im_cancel_order.setVisibility(View.GONE);

            if (order.getRemarks() == null) {
                request_container.setVisibility(View.GONE);
                line_after_add_on.setVisibility(View.GONE);
            } else {
                request_container.setVisibility(View.VISIBLE);
                line_after_add_on.setVisibility(View.VISIBLE);
                tv_remarks.setText(order.getRemarks());
            }

            if(order.getStatus() == SessionViewOrdersModel.SESSIONEVENT.OPEN){
                tv_order_status.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0);
                tv_order_status.setText(mContext.getResources().getString(R.string.status_order_accepted));
                tv_order_status.setTextColor(mContext.getResources().getColor(R.color.apple_green));
            }
            else if(order.getStatus() == SessionViewOrdersModel.SESSIONEVENT.CANCELLED){
                tv_order_status.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0);
                tv_order_status.setText(mContext.getResources().getString(R.string.status_order_cancelled));
                tv_order_status.setTextColor(mContext.getResources().getColor(R.color.primary_red));
                if(im_cancel_order.getVisibility() == View.VISIBLE) im_cancel_order.setVisibility(View.GONE);
            }
            else if(order.getStatus() == SessionViewOrdersModel.SESSIONEVENT.INPROGRESS){
                tv_order_status.setCompoundDrawablesWithIntrinsicBounds(0,R.drawable.noun_cooking_76171,0,0);
                tv_order_status.setText(mContext.getResources().getString(R.string.status_order_in_progress));
                tv_order_status.setTextColor(mContext.getResources().getColor(R.color.blue));
            }else if(order.getStatus() == SessionViewOrdersModel.SESSIONEVENT.DONE){
                tv_order_status.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0);
                tv_order_status.setText(mContext.getResources().getString(R.string.status_order_delivered));
                tv_order_status.setTextColor(mContext.getResources().getColor(R.color.apple_green));
            }

            ll_crust.removeAllViews();
            ll_toppings.removeAllViews();

            if (order.getCustomizations().size() > 0) {
                ll_customization_title.setVisibility(View.VISIBLE);
                add_on_container.setVisibility(View.VISIBLE);
                for (int i = 0; i < order.getCustomizations().size(); i++) {
                    View viewCustomizationLeft = View.inflate(mContext, R.layout.item_active_session_view_orders_customization_left, null);
                    View viewCustomizationRight = View.inflate(mContext, R.layout.item_active_session_view_orders_customization_right, null);

                    if (order.getCustomizations().get(i).getGroup().equalsIgnoreCase("Crust"))
                        new addCustomizationItems(viewCustomizationRight, order.getCustomizations().get(i).getName(), ll_crust);
                    else
                        new addCustomizationItems(viewCustomizationLeft, order.getCustomizations().get(i).getName(), ll_toppings);
                }
            } else {
                ll_customization_title.setVisibility(View.GONE);
                add_on_container.setVisibility(View.GONE);
            }
        }

        @OnClick(R.id.im_cancel_order)
        void onCancelOrder() {
            mOrdersInterface.onCancelOrder(order.getPk());
        }
    }

    //add view
    public class addCustomizationItems {
        @BindView(R.id.tv_customization_item_name)
        TextView tv_customization_item_name;
        View viewCustomization;

        public addCustomizationItems(View viewCustomization, String name, LinearLayout ll_toppings) {
            this.viewCustomization = viewCustomization;
            ButterKnife.bind(this, viewCustomization);
            tv_customization_item_name.setText(name);
            ll_toppings.addView(viewCustomization);
        }

    }

    public interface SessionOrdersInteraction{
        void onCancelOrder(int pk);
    }
}
