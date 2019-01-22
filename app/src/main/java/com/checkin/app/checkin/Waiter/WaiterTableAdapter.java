package com.checkin.app.checkin.Waiter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public class WaiterTableAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<WaiterTableModel> mList;

    WaiterTableAdapter(List<WaiterTableModel> mList) {
        this.mList = mList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        switch (viewType){
//            case 1:
//                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.child_waiter_table_normal,parent,false);
//                return new WaiterTableNormalHolder(view);
//            case 2:
//                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.child_waiter_table_accepted,parent,false);
//                return new WaiterTableAcceptHolder(view);
//            case 3:
//                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.child_waiter_table_delayed,parent,false);
//                return new WaiterTableDelayHolder(view);
//            case 4:
//                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.child_waiter_table_delivered,parent,false);
//                return new WaiterTableDeliverHolder(view);
//            case 5:
//                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.child_waiter_table_service,parent,false);
//                return new WaiterTableServicelHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

    }

    class WaiterTableDeliverHolder extends RecyclerView.ViewHolder{
        public WaiterTableDeliverHolder(View itemView) {
            super(itemView);
        }
    }

    class WaiterTableDelayHolder extends RecyclerView.ViewHolder{
        public WaiterTableDelayHolder(View itemView) {
            super(itemView);
        }
    }

    class WaiterTableAcceptHolder extends RecyclerView.ViewHolder{
        public WaiterTableAcceptHolder(View itemView) {
            super(itemView);
        }
    }

    class WaiterTableNormalHolder extends RecyclerView.ViewHolder{
        public WaiterTableNormalHolder(View itemView) {
            super(itemView);
        }
    }

    class WaiterTableServicelHolder extends RecyclerView.ViewHolder{
        public WaiterTableServicelHolder(View itemView) {
            super(itemView);
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    @Override
    public int getItemViewType(int position) {
        int type = mList.get(position).getType();
        switch (type) {
            case 1:
                return WaiterTableModel.NORMAL;
            case 2:
                return WaiterTableModel.ACCEPT;
            case 3:
                return WaiterTableModel.DELAY;
            case 4:
                return WaiterTableModel.DELIVER;
            case 5:
                return WaiterTableModel.SERVICE;
            default:
                return -1;
        }
    }
}
