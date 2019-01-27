package com.checkin.app.checkin.ManagerProfile;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.checkin.app.checkin.Misc.BriefModel;
import com.checkin.app.checkin.R;
import com.checkin.app.checkin.Session.Model.RestaurantTableModel;
import com.checkin.app.checkin.Utility.Utils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class ShopManagerTableAdapter extends RecyclerView.Adapter<ShopManagerTableAdapter.ShopManagerTableHolder> {
    private List<RestaurantTableModel> mList;

    @NonNull
    @Override
    public ShopManagerTableHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_shop_manager_table, parent, false);
        return new ShopManagerTableHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ShopManagerTableHolder holder, int position) {
        RestaurantTableModel data = mList.get(position);
        holder.bindData(data);
    }

    @Override
    public int getItemCount() {
        return mList !=null ? mList.size() : 0;
    }

    void setRestaurantTableList(List<RestaurantTableModel> mList) {
        this.mList = mList;
        notifyDataSetChanged();
    }

    class ShopManagerTableHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_shop_manager_table_time)
        TextView tvShopManagerTableTime;
        @BindView(R.id.iv_shop_manager_table_image)
        CircleImageView ivShopManagerTableImage;
        @BindView(R.id.tv_shop_manager_table_name)
        TextView tvShopManagerTableName;
        @BindView(R.id.tv_shop_manager_table_number)
        TextView tvShopManagerTableNumber;
        @BindView(R.id.iv_shop_manager_table_icon)
        ImageView ivShopManagerTableIcon;
        @BindView(R.id.tv_shop_manager_table_detail)
        TextView tvShopManagerTableDetail;
        @BindView(R.id.tv_shop_manager_table_person_number)
        TextView tvShopManagerTablePersonNumber;

        ShopManagerTableHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }

        public void bindData(RestaurantTableModel data) {
            BriefModel host = data.getHost();

            if (host != null){
                tvShopManagerTableName.setText(host.getDisplayName());
                Utils.loadImageOrDefault(ivShopManagerTableImage, host.getDisplayPic(), R.drawable.ic_waiter);
            }else {
                tvShopManagerTableName.setText(R.string.waiter_unassigned);
            }

            tvShopManagerTableTime.setText(data.getEvent().formatTimestamp());
            tvShopManagerTableNumber.setText(data.getTable());
            tvShopManagerTableDetail.setText(data.getEvent().getMessage());
        }
    }
}
