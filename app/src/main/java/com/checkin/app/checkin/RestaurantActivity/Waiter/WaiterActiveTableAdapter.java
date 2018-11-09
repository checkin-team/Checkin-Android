package com.checkin.app.checkin.RestaurantActivity.Waiter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.checkin.app.checkin.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WaiterActiveTableAdapter extends RecyclerView.Adapter<WaiterActiveTableAdapter.ViewHolder> {
    List<TableModel> mTables;
    private Context context;
    int noItemsInteger=12;
    private onTableInterActionListener OnTableInterActionListener;
    int item_position=0;





    WaiterActiveTableAdapter(List<TableModel> tables) {
        mTables = tables;

    }

    @NonNull
    @Override
    public WaiterActiveTableAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);

        return new WaiterActiveTableAdapter.ViewHolder(view);

    }

    public void setNoItems(int number){
        this.noItemsInteger=number;

    }

    public void setItem_position(int item_position) {
        this.item_position = item_position;
    }

    public void setOnTableInterActionListener(onTableInterActionListener onTableInterActionListener) {
        OnTableInterActionListener = onTableInterActionListener;
    }

    @Override
    public void onBindViewHolder(@NonNull WaiterActiveTableAdapter.ViewHolder holder, int position) {
            holder.bindData(mTables.get(position));

            holder.itemView.findViewById(R.id.table_oval).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    OnTableInterActionListener.selectedTableChanged(holder.getAdapterPosition());
                }
            });
            if(item_position==holder.getAdapterPosition())
            {
                holder.itemView.findViewById(R.id.table_oval).setBackgroundResource(R.drawable.table_shape);
            }
            else
            {
                holder.itemView.findViewById(R.id.table_oval).setBackgroundResource(R.drawable.combined_shape1);
            }

    }

    @Override
    public int getItemCount() {
        return mTables.size();
    }
    @Override
    public int getItemViewType(final int position) {
        return R.layout.waiter_active_tables_recy;
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.table_label)
        TextView tableLabel;
        @BindView(R.id.text_no_Items)
                TextView noItems;


        ViewHolder(View v) {
            super(v);

            ButterKnife.bind(this, v);
        }

        void bindData(TableModel table) {
            tableLabel.setText(table.getName());
            noItems.setText(String.valueOf(noItemsInteger));
            Log.e("WaiterTableAdapter", String.valueOf(noItemsInteger));
            Glide.with(tableLabel.getContext());
        }

    }
    public interface onTableInterActionListener
    {
        public void selectedTableChanged(int position);
    }

}
