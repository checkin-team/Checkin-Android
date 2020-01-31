package com.checkin.app.checkin.user.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.checkin.app.checkin.R;
import com.checkin.app.checkin.user.models.ShopCustomerModel;
import com.checkin.app.checkin.utility.Utils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class UserCheckinAdapter extends RecyclerView.Adapter<UserCheckinAdapter.UserCheckinHolder> {

    private List<ShopCustomerModel> mList;

    public UserCheckinAdapter() {
    }

    @NonNull
    @Override
    public UserCheckinHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        return new UserCheckinHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserCheckinHolder holder, int position) {
        holder.bindData(mList.get(position));
    }

    @Override
    public int getItemViewType(int position) {
        return R.layout.item_user_checkins;
    }

    @Override
    public int getItemCount() {
        return mList != null ? mList.size() : 0;
    }

    public void setUserCheckinsData(List<ShopCustomerModel> data) {
        mList = data;
        notifyDataSetChanged();
    }

    class UserCheckinHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.iv_uc_user_name)
        CircleImageView ivUserName;
        @BindView(R.id.tv_uc_user_name)
        TextView tvUserName;
        private ShopCustomerModel mShopCustomerModel;

        UserCheckinHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bindData(ShopCustomerModel shopCustomerModel) {
            this.mShopCustomerModel = shopCustomerModel;
            Utils.loadImageOrDefault(ivUserName, shopCustomerModel.getShop().getDisplayPic(), R.drawable.cover_restaurant_unknown);
            tvUserName.setText(shopCustomerModel.getShop().getDisplayName());
        }
    }
}
