package com.alcatraz.admin.project_alcatraz.Home;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.alcatraz.admin.project_alcatraz.R;

import org.jetbrains.annotations.NotNull;

import java.util.List;


/**
 * Created by TAIYAB on 05-02-2018.
 */

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.MyView> {

    private List<Transaction> list;

    public static class MyView extends RecyclerView.ViewHolder {

        public TextView date,debit,mymoney;
        public ImageView rupsmall;

        public MyView(View view) {
            super(view);

            date = (TextView) view.findViewById(R.id.somedate);
            debit=view.findViewById(R.id.debit);
            mymoney=view.findViewById(R.id.mymoney);
            rupsmall=view.findViewById(R.id.rupsmall);

        }
    }


    public TransactionAdapter(List<Transaction> list) {
        this.list = list;
    }

    @Override
    public MyView onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_transaction, parent, false);
        return new MyView(itemView);
    }

    @Override
    public void onBindViewHolder(@NotNull final MyView holder, final int position) {
        Transaction transaction=list.get(position);
        if(!transaction.showdate)
        {
            holder.date.setVisibility(View.GONE);
        }
        else {

            holder.date.setVisibility(View.VISIBLE);
            holder.date.setText(transaction.date);
        }
        if(transaction.cashback)
        {
            holder.debit.setText("Cashback from XYZ");

            holder.mymoney.setTextColor(Color.GREEN);
            holder.rupsmall.setImageResource(R.drawable.rupsmall);
            holder.mymoney.setText(transaction.amount);
        }
        else {
            holder.debit.setText("Paid To XYZ Restaurant");

            holder.mymoney.setTextColor(Color.BLACK);
            holder.rupsmall.setImageResource(R.drawable.rupee);

            holder.mymoney.setText(transaction.amount);
        }


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

}