package com.checkin.app.checkin.Manager.Adapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.checkin.app.checkin.Misc.BriefModel;
import com.checkin.app.checkin.R;
import com.checkin.app.checkin.Session.ActiveSession.Chat.SessionEventBasicModel;
import com.checkin.app.checkin.Session.Model.RestaurantTableModel;
import com.checkin.app.checkin.Utility.Utils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

public class ManagerWorkTableAdapter extends RecyclerView.Adapter<ManagerWorkTableAdapter.ShopManagerTableHolder> {
    private List<RestaurantTableModel> mList;
    private ManagerTableInteraction mListener;

    public ManagerWorkTableAdapter(ManagerTableInteraction listener) {
        mListener = listener;
    }

    @NonNull
    @Override
    public ShopManagerTableHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_shop_manager_table, parent, false);
        return new ShopManagerTableHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ShopManagerTableHolder holder, int position) {
        holder.bindData(mList.get(position));
    }

    public void updateSession(int position) {
        notifyItemChanged(position);
    }

    public void removeSession(int position) {
        notifyItemRemoved(position);
    }

    @Override
    public int getItemCount() {
        return mList !=null ? mList.size() : 0;
    }

    public void setRestaurantTableList(List<RestaurantTableModel> mList) {
        this.mList = mList;
        notifyDataSetChanged();
    }

    class ShopManagerTableHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_shop_manager_table_time)
        TextView tvShopManagerTableTime;
        @BindView(R.id.iv_shop_manager_table_waiter)
        CircleImageView ivShopManagerTableImage;
        @BindView(R.id.tv_shop_manager_table_name)
        TextView tvShopManagerTableName;
        @BindView(R.id.tv_shop_manager_table_number)
        TextView tvShopManagerTableNumber;
        @BindView(R.id.tv_shop_manager_table_detail)
        TextView tvShopManagerTableDetail;
        @BindView(R.id.tv_shop_manager_table_event_badge)
        TextView tvEventBadge;
        @BindView(R.id.iv_shop_manager_table_icon)
        ImageView ivShopManagerTableIcon;
        @BindView(R.id.container_manager_table_active)
        ViewGroup containerSessionActive;
        @BindView(R.id.container_manager_table_end)
        ViewGroup containerSessionEnd;

        private RestaurantTableModel mTableModel;

        ShopManagerTableHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);

            itemView.setOnClickListener(v -> mListener.onClickTable(mTableModel));
        }

        public void bindData(RestaurantTableModel data) {
            mTableModel = data;

            BriefModel host = data.getHost();

            if (host != null){
                tvShopManagerTableName.setText(host.getDisplayName());
                Utils.loadImageOrDefault(ivShopManagerTableImage, host.getDisplayPic(), R.drawable.ic_waiter);
            } else {
                tvShopManagerTableName.setText(R.string.waiter_unassigned);
            }

            if (data.getEventCount() > 0) {
                tvEventBadge.setText(data.formatEventCount());
                tvEventBadge.setVisibility(View.VISIBLE);
            } else tvEventBadge.setVisibility(View.GONE);
            ivShopManagerTableIcon.setImageResource(SessionEventBasicModel.getEventIcon(
                    data.getEvent().getType(), data.getEvent().getService(), data.getEvent().getConcern()));
            tvShopManagerTableTime.setText(data.getEvent().formatTimestamp());
            tvShopManagerTableNumber.setText(data.getTable());
            tvShopManagerTableDetail.setText(data.getEvent().getMessage());

            if (data.isRequestedCheckout())
                containerSessionEnd.setVisibility(View.VISIBLE);
            else
                containerSessionActive.setVisibility(View.VISIBLE);
        }

        @OnClick(R.id.btn_manager_table_session_done)
        public void onClickSessionEnd(View v) {
            mListener.onMarkSessionDone(mTableModel);
        }
    }

    public interface ManagerTableInteraction {
        void onClickTable(RestaurantTableModel tableModel);
        void onMarkSessionDone(RestaurantTableModel tableModel);
    }
}
