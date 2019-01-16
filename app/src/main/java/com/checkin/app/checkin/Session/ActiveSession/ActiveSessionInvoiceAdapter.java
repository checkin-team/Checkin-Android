package com.checkin.app.checkin.Session.ActiveSession;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.checkin.app.checkin.Menu.Model.OrderedItemModel;
import com.checkin.app.checkin.R;
import com.checkin.app.checkin.Session.SessionCustomerModel;
import com.checkin.app.checkin.Utility.HeaderFooterRecyclerViewAdapter;
import com.checkin.app.checkin.Utility.Utils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class ActiveSessionInvoiceAdapter extends RecyclerView.Adapter<ActiveSessionInvoiceAdapter.ViewHolder> {

    private List<OrderedItemModel> mOrderedItems;

    ActiveSessionInvoiceAdapter(List<OrderedItemModel> orderedItems/*, SessionMemberInteraction memberInterface*/){
        this.mOrderedItems = orderedItems;
//        this.memberInterface = memberInterface;
    }

    public void setData(List<OrderedItemModel> data) {
        this.mOrderedItems = data;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bindData(mOrderedItems.get(position));
    }

    @Override
    public int getItemCount() {
        return mOrderedItems != null ? mOrderedItems.size() : 0;
    }


    public void setUsers(List<OrderedItemModel> users) {
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

        void bindData(OrderedItemModel orderedItem) {
            tv_menu_item_name.setText(orderedItem.getItemModel().getName() + " X " + orderedItem.getQuantity());
            tv_item_price.setText(String.valueOf(orderedItem.getCost()));
        }
    }

}
