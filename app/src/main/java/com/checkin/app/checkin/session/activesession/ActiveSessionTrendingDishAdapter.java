package com.checkin.app.checkin.session.activesession;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.checkin.app.checkin.R;
import com.checkin.app.checkin.Utility.Utils;
import com.checkin.app.checkin.session.models.TrendingDishModel;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ActiveSessionTrendingDishAdapter extends RecyclerView.Adapter<ActiveSessionTrendingDishAdapter.ViewHolder> {
    private List<TrendingDishModel> mEvent;
    private SessionTrendingDishInteraction mListener;

    public ActiveSessionTrendingDishAdapter(SessionTrendingDishInteraction ordersInterface) {
        this.mListener = ordersInterface;
    }

    public void setData(List<TrendingDishModel> data) {
        this.mEvent = data;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bindData(mEvent.get(position));
    }

    @Override
    public int getItemCount() {
        return mEvent != null ? mEvent.size() : 0;
    }

    @Override
    public int getItemViewType(final int position) {
        return R.layout.item_active_session_trending_dish;
    }

    public interface SessionTrendingDishInteraction {
        void onDishClick(TrendingDishModel itemModel);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.im_as_trending_dish_group_icon)
        ImageView imDishGroupIcon;
        @BindView(R.id.im_as_trending_dish_image)
        ImageView imDishImage;
        @BindView(R.id.tv_as_trending_dish_name)
        TextView tvName;
        @BindView(R.id.tv_as_trending_dish_price)
        TextView tvPrice;

        private TrendingDishModel mItemModel;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(v -> {
                mListener.onDishClick(mItemModel);});
        }

        void bindData(TrendingDishModel itemModel) {
            this.mItemModel = itemModel;

            if(itemModel.getImage().contains("static/menu/icons")){
                Utils.loadImageOrDefault(imDishGroupIcon, itemModel.getImage(),0);
                imDishGroupIcon.setVisibility(View.VISIBLE);
                imDishImage.setVisibility(View.GONE);
            }else {
                Utils.loadImageOrDefault(imDishImage, itemModel.getImage(),0);
                imDishGroupIcon.setVisibility(View.GONE);
                imDishImage.setVisibility(View.VISIBLE);
            }

            tvName.setText(itemModel.getName());
            tvPrice.setText(Utils.formatIntegralCurrencyAmount(tvPrice.getContext(), itemModel.getTypeCosts().get(0)));
        }
    }
}
