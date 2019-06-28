package com.checkin.app.checkin.Menu.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.checkin.app.checkin.R;
import com.checkin.app.checkin.Utility.Utils;
import com.checkin.app.checkin.session.model.TrendingDishModel;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MenuTreatYourselfAdapter extends RecyclerView.Adapter<MenuTreatYourselfAdapter.ViewHolder> {
    private List<TrendingDishModel> mEvent;
    private TreatYourselfInteraction mListener;

    public MenuTreatYourselfAdapter(TreatYourselfInteraction ordersInterface) {
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
        return R.layout.item_menu_treat_yourself;
    }

    public interface TreatYourselfInteraction {
        void onTreatYourselfItemClick(TrendingDishModel itemModel);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.im_menu_cart_treat_item_image)
        ImageView imDish;
        @BindView(R.id.im_menu_cart_treat_item_type)
        ImageView imDishType;
        @BindView(R.id.im_menu_cart_treat_item_name)
        TextView tvName;
//        @BindView(R.id.tv_as_trending_dish_price)
//        TextView tvPrice;

        private TrendingDishModel mItemModel;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(v -> {
                mListener.onTreatYourselfItemClick(mItemModel);});
        }

        void bindData(TrendingDishModel itemModel) {
            this.mItemModel = itemModel;
            Utils.loadImageOrDefault(imDish, itemModel.getImage(),0);
            tvName.setText(itemModel.getName());
            if(itemModel.isVegetarian())
                imDishType.setImageDrawable(imDishType.getContext().getResources().getDrawable(R.drawable.ic_veg));
            else
                imDishType.setImageDrawable(imDishType.getContext().getResources().getDrawable(R.drawable.ic_non_veg));
        }
    }
}
