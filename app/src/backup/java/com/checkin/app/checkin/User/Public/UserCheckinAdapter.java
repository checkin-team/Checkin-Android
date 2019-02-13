package com.checkin.app.checkin.User.Public;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.checkin.app.checkin.Misc.BriefModel;
import com.checkin.app.checkin.R;
import com.checkin.app.checkin.Utility.Utils;

import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class UserCheckinAdapter extends RecyclerView.Adapter<UserCheckinAdapter.UserCheckinHolder> {
    private List<ShopCustomerModel> mList;
    private UserCheckinShopInteraction mListener;

    public UserCheckinAdapter(UserCheckinShopInteraction listener) {
        mListener = listener;
    }

    @NonNull
    @Override
    public UserCheckinHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_checkin_shop, parent, false);
        return new UserCheckinHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserCheckinHolder holder, int position) {
        ShopCustomerModel data = mList.get(position);
        holder.bindData(data);
    }

    @Override
    public int getItemCount() {
        return mList != null ? mList.size() : 0;
    }

    void setData(List<ShopCustomerModel> mList) {
        this.mList = mList;
        notifyDataSetChanged();
    }

    class UserCheckinHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.iv_user_checkin_image)
        CircleImageView ivUserCheckinImage;
        @BindView(R.id.tv_user_checkin_name)
        TextView tvUserCheckinName;
        @BindView(R.id.tv_user_checkin_address)
        TextView tvUserCheckinAddress;
        @BindView(R.id.tv_user_checkin_total_visits)
        TextView tvUserCheckinTotalVisits;

        private ShopCustomerModel mCheckinData;

        UserCheckinHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(v -> mListener.onClickShop(mCheckinData.getShop()));
        }

        public void bindData(ShopCustomerModel data) {
            mCheckinData = data;

            Utils.loadImageOrDefault(ivUserCheckinImage, data.getShop().getDisplayPic(), R.drawable.cover_restaurant_unknown);
            tvUserCheckinName.setText(data.getShop().getDisplayName());
            tvUserCheckinAddress.setText(data.getLocation());
            tvUserCheckinTotalVisits.setText(String.format(Locale.getDefault(), "Total Visits : %d", data.getCountVisits()));
        }
    }

    public interface UserCheckinShopInteraction {
        void onClickShop(BriefModel shop);
    }
}
