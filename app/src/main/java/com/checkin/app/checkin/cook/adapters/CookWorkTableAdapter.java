package com.checkin.app.checkin.cook.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.checkin.app.checkin.R;
import com.checkin.app.checkin.misc.models.BriefModel;
import com.checkin.app.checkin.session.models.RestaurantTableModel;
import com.checkin.app.checkin.utility.Utils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class CookWorkTableAdapter extends RecyclerView.Adapter<CookWorkTableAdapter.ShopCookTableHolder> {
    private List<RestaurantTableModel> mList;
    private CookTableInteraction mListener;

    public CookWorkTableAdapter(CookTableInteraction listener) {
        mListener = listener;
    }

    @NonNull
    @Override
    public ShopCookTableHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        return new ShopCookTableHolder(view);
    }

    @Override
    public int getItemViewType(int position) {
        return R.layout.item_shop_manager_table;
    }

    @Override
    public void onBindViewHolder(@NonNull ShopCookTableHolder holder, int position) {
        holder.bindData(mList.get(position));
    }

    public void updateSession(int position) {
        notifyItemChanged(position);
    }

    @Override
    public int getItemCount() {
        return mList != null ? mList.size() : 0;
    }

    public void setRestaurantTableList(List<RestaurantTableModel> mList) {
        this.mList = mList;
        notifyDataSetChanged();
    }

    public interface CookTableInteraction {
        void onClickTable(RestaurantTableModel tableModel);
    }

    class ShopCookTableHolder extends RecyclerView.ViewHolder {

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
        @BindView(R.id.container_manager_table_active)
        ViewGroup containerSessionActive;

        private RestaurantTableModel mTableModel;

        ShopCookTableHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(v -> mListener.onClickTable(mTableModel));
            itemView.findViewById(R.id.container_manager_table_event_icon).setVisibility(View.GONE);
        }

        public void bindData(RestaurantTableModel data) {
            mTableModel = data;

            tvShopManagerTableNumber.setText(data.getTable());
            BriefModel host = data.getTableSession() != null ? data.getTableSession().getHost() : null;

            if (host != null) {
                tvShopManagerTableName.setText(host.getDisplayName());
                Utils.loadImageOrDefault(ivShopManagerTableImage, host.getDisplayPic(), R.drawable.ic_waiter);
            } else {
                ivShopManagerTableImage.setImageDrawable(ivShopManagerTableImage.getContext().getResources().getDrawable(R.drawable.ic_waiter));
                tvShopManagerTableName.setText(R.string.waiter_unassigned);
            }
            tvShopManagerTableDetail.setText(data.getFormatOrderStatus());
            if (data.getUnseenEventCount() > 0) {
                tvEventBadge.setText(data.getFormatEventCount());
                tvEventBadge.setVisibility(View.VISIBLE);
            } else {
                tvEventBadge.setVisibility(View.GONE);
            }
        }
    }
}
