package com.checkin.app.checkin.Session.ActiveSession;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.checkin.app.checkin.R;
import com.checkin.app.checkin.Session.SessionViewOrdersModel;
import com.checkin.app.checkin.Utility.Utils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class ActiveSessionInvoiceAdapter extends RecyclerView.Adapter<ActiveSessionInvoiceAdapter.ViewHolder> {

    private List<SessionViewOrdersModel> mOrderedItems;
    Activity mContext;

    ActiveSessionInvoiceAdapter(List<SessionViewOrdersModel> orderedItems, Activity context){
        this.mOrderedItems = orderedItems;
        this.mContext = context;
    }

    public void setData(List<SessionViewOrdersModel> data) {
        this.mOrderedItems = data;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public int getItemViewType(final int position) {
        return R.layout.item_active_session_checkout_menu;
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bindData(mOrderedItems.get(position));
    }

    @Override
    public int getItemCount() {
        return mOrderedItems != null ? mOrderedItems.size() : 0;
    }


    public void setUsers(List<SessionViewOrdersModel> users) {
        this.mOrderedItems = users;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_menu_item_name) TextView tv_menu_item_name;
        @BindView(R.id.tv_item_price) TextView tv_item_price;
        ViewHolder(View view){
            super(view);
            ButterKnife.bind(this, view);
        }

        void bindData(SessionViewOrdersModel orderedItem) {
            tv_menu_item_name.setText(orderedItem.getItem().getName() + " x " + orderedItem.getQuantity());
            if(orderedItem.getItem().isVegetarian())
            tv_menu_item_name.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_veg,0,0,0);
            else
                tv_menu_item_name.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_non_veg,0,0,0);
            tv_item_price.setText(mContext.getResources().getString(R.string.rs) + " " +String.valueOf(orderedItem.getCost()));

        }
    }

}
