package com.checkin.app.checkin.Menu.ActiveSessionMenu;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.checkin.app.checkin.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MenuBestSellerAdapter extends RecyclerView.Adapter<MenuBestSellerAdapter.ViewHolder> {
    private List<MenuBestSellerModel> mEvent;
    private SessionTrendingDishInteraction mListener;

    public MenuBestSellerAdapter() {
    }

    public void setData(List<MenuBestSellerModel> data) {
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
        return R.layout.item_menu_bestseller_dish;
    }

    public interface SessionTrendingDishInteraction {
        void onDishClick(MenuBestSellerModel itemModel);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.im_menu_dish)
        ImageView imDish;
        @BindView(R.id.im_menu_dish_name)
        TextView tvName;
        @BindView(R.id.tv_menu_dish_price)
        TextView tvPrice;

        private MenuBestSellerModel mItemModel;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(v -> {
                mListener.onDishClick(mItemModel);
            });
        }

        void bindData(MenuBestSellerModel itemModel) {
            this.mItemModel = itemModel;
//            Utils.loadImageOrDefault(imDish, itemModel.getImage(),0);
            tvName.setText(itemModel.getName());
//            tvPrice.setText(Utils.formatCurrencyAmount(tvPrice.getContext(), itemModel.getTypeCosts().get(0)));
        }
    }
}
