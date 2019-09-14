package com.checkin.app.checkin.Cook.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.checkin.app.checkin.Cook.Model.CookTableModel;
import com.checkin.app.checkin.Misc.BriefModel;
import com.checkin.app.checkin.R;
import com.checkin.app.checkin.Utility.Utils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class CookWorkTableAdapter extends RecyclerView.Adapter<CookWorkTableAdapter.ShopCookTableHolder> {
    private List<CookTableModel> mList;
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

    public void setRestaurantTableList(List<CookTableModel> mList) {
        this.mList = mList;
        notifyDataSetChanged();
    }

    public interface CookTableInteraction {
        void onClickTable(CookTableModel tableModel);
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

        private CookTableModel mTableModel;

        ShopCookTableHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(v -> mListener.onClickTable(mTableModel));
            itemView.findViewById(R.id.container_manager_table_event_icon).setVisibility(View.GONE);
        }

        public void bindData(CookTableModel data) {
            mTableModel = data;

            tvShopManagerTableNumber.setText(data.getTable());
            BriefModel host = data.getHost();

            if (host != null) {
                tvShopManagerTableName.setText(host.getDisplayName());
                Utils.loadImageOrDefault(ivShopManagerTableImage, host.getDisplayPic(), R.drawable.ic_waiter);
            } else {
                ivShopManagerTableImage.setImageDrawable(ivShopManagerTableImage.getContext().getResources().getDrawable(R.drawable.ic_waiter));
                tvShopManagerTableName.setText(R.string.waiter_unassigned);
            }
            tvShopManagerTableDetail.setText(data.formatOrderStatus());
            if (data.getEventCount() > 0) {
                tvEventBadge.setText(data.formatEventCount());
                tvEventBadge.setVisibility(View.VISIBLE);
            } else {
                tvEventBadge.setVisibility(View.GONE);
            }
        }
    }
}
