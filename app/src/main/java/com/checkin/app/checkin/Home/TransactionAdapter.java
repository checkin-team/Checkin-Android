package com.checkin.app.checkin.Home;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.checkin.app.checkin.R;

import java.util.List;


/**
 * Created by TAIYAB on 05-02-2018.
 */

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.MyView> {

    private List<Transaction> list;

    public static class MyView extends RecyclerView.ViewHolder {

        public TextView date, debit, mymoney;
        public ImageView rupsmall;

        public MyView(View view,int x) {
            super(view);
            if(x==0)
                return;
            date = (TextView) view.findViewById(R.id.somedate);
            debit = view.findViewById(R.id.debit);
            mymoney = view.findViewById(R.id.mymoney);

        }
    }


    public TransactionAdapter(List<Transaction> list) {
        this.list = list;
    }

    @Override
    public MyView onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType!=0) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_transaction, parent, false);
            return new MyView(itemView,1);
        }
        return new MyView(LayoutInflater.from(parent.getContext()).inflate(R.layout.transaction_header, parent, false),0);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyView holder, final int position) {
        if(position==0)
            return;
        Transaction transaction = list.get(position);
        if (!transaction.showdate) {
            holder.date.setVisibility(View.GONE);
        } else {

            holder.date.setVisibility(View.VISIBLE);
            holder.date.setText(transaction.date);
        }
        if (transaction.cashback) {
            SpannableString ss = new SpannableString("CashBack received\nRestaurant "+transaction.hotel+" \nOrder no. "+transaction.Order);
            ss.setSpan(TransactionActivity.clickableSpan, 29, 29+transaction.hotel.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            holder.debit.setText(ss);
            holder.debit.setMovementMethod(LinkMovementMethod.getInstance());
            holder.debit.setHighlightColor(Color.TRANSPARENT);
            holder.debit.setText(ss);
            holder.mymoney.setTextColor(Color.GREEN);
            holder.mymoney.setText("₹  +"+transaction.amount);
        } else {
            SpannableString ss = new SpannableString("Paid for Order\nRestaurant "+transaction.hotel+" \nOrder no. "+transaction.Order);
            ss.setSpan(TransactionActivity.clickableSpan, 26, 26+transaction.hotel.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            holder.debit.setText(ss);
            holder.debit.setMovementMethod(LinkMovementMethod.getInstance());
            holder.debit.setHighlightColor(Color.TRANSPARENT);
            holder.debit.setText(ss);
            holder.mymoney.setTextColor(Color.rgb(129,129,129));

            holder.mymoney.setText("₹  -"+transaction.amount);
        }


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    /**
     * Return the view type of the item at <code>position</code> for the purposes
     * of view recycling.
     * <p>
     * <p>The default implementation of this method returns 0, making the assumption of
     * a single view type for the adapter. Unlike ListView adapters, types need not
     * be contiguous. Consider using id resources to uniquely identify item view types.
     *
     * @param position position to query
     * @return integer value identifying the type of the view needed to represent the item at
     * <code>position</code>. Type codes need not be contiguous.
     */
    @Override
    public int getItemViewType(int position) {
        return position==0?0:1;
    }

    public List<Transaction> getList(){
        return list;
    }
}