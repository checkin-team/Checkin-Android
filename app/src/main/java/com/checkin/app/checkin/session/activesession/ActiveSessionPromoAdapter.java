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
import com.checkin.app.checkin.session.models.PromoDetailModel;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ActiveSessionPromoAdapter extends RecyclerView.Adapter<ActiveSessionPromoAdapter.ItemViewHolder> {
    private List<PromoDetailModel> mItemsList;

    private onPromoCodeItemListener mListener;

    public ActiveSessionPromoAdapter(List<PromoDetailModel> itemsList, onPromoCodeItemListener listener) {
        mItemsList = itemsList;
        mListener = listener;
    }

    public void setData(List<PromoDetailModel> menuItems) {
        this.mItemsList = menuItems;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(final int position) {
        return R.layout.item_active_session_promo;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        holder.bindData(mItemsList.get(position));
    }

    @Override
    public int getItemCount() {
        return mItemsList != null ? mItemsList.size() : 0;
    }

    public interface onPromoCodeItemListener {
        void onPromoApply(PromoDetailModel item);
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_as_promo_code)
        TextView tvPromoCode;
        @BindView(R.id.tv_as_promo_name)
        TextView tvPromoName;
        @BindView(R.id.tv_as_promo_apply)
        TextView tvPromoApply;
        @BindView(R.id.tv_as_promo_summary)
        TextView tvPromoSummary;
        @BindView(R.id.im_as_promo_icon)
        ImageView imPromoIcon;

        private PromoDetailModel mItem;

        ItemViewHolder(final View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            tvPromoApply.setOnClickListener(v -> mListener.onPromoApply(mItem));
        }

        void bindData(PromoDetailModel promoModel) {
            this.mItem = promoModel;

            if(mListener == null)
                tvPromoApply.setVisibility(View.GONE);

            tvPromoCode.setText(promoModel.getCode());
            tvPromoName.setText(promoModel.getFormatName());
            tvPromoSummary.setText(promoModel.getSummary());
            Utils.loadImageOrDefault(imPromoIcon, promoModel.getIcon(), 0);
        }
    }
}
