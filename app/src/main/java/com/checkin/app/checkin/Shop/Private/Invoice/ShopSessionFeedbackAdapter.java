package com.checkin.app.checkin.Shop.Private.Invoice;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.appyvet.materialrangebar.RangeBar;
import com.checkin.app.checkin.Misc.ImageThumbnailHolder;
import com.checkin.app.checkin.Misc.ImageThumbnailHolder.ImageThumbnailInteraction;
import com.checkin.app.checkin.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ShopSessionFeedbackAdapter extends RecyclerView.Adapter<ShopSessionFeedbackAdapter.ShopInvoiceFeedbackHolder> {
    public ImageThumbnailInteraction mListener;
    private List<ShopSessionFeedbackModel> mList;

    ShopSessionFeedbackAdapter(ImageThumbnailInteraction listener) {
        mListener = listener;
    }

    @NonNull
    @Override
    public ShopInvoiceFeedbackHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_shop_invoice_session_feedback, parent, false);
        return new ShopInvoiceFeedbackHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ShopInvoiceFeedbackHolder holder, int position) {
        ShopSessionFeedbackModel data = mList.get(position);
        holder.bindData(data);
    }

    @Override
    public int getItemCount() {
        return mList != null ? mList.size() : 0;
    }

    public void setData(List<ShopSessionFeedbackModel> data) {
        this.mList = data;
        notifyDataSetChanged();
    }

    class ShopInvoiceFeedbackHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_feedback_overall)
        TextView tvRatingOverall;
        @BindView(R.id.seekbar_feedback_ambiance)
        RangeBar seekbarAmbiance;
        @BindView(R.id.seekbar_feedback_food)
        RangeBar seekbarFood;
        @BindView(R.id.seekbar_feedback_hospitality)
        RangeBar seekbarHospitality;
        @BindView(R.id.tv_shop_session_review_customer_name)
        TextView tvCustomerName;
        @BindView(R.id.tv_shop_session_review_body)
        TextView tvBody;

        private ImageThumbnailHolder mThumbnailHolder;

        ShopInvoiceFeedbackHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            seekbarFood.setEnabled(false);
            seekbarHospitality.setEnabled(false);
            seekbarAmbiance.setEnabled(false);
            mThumbnailHolder = new ImageThumbnailHolder(itemView, mListener);
        }

        public void bindData(ShopSessionFeedbackModel data) {
            tvBody.setText(data.getBody());
            tvCustomerName.setText(data.getUser().getDisplayName());

            tvRatingOverall.setText(String.valueOf(data.getOverallRating()));
            seekbarFood.setSeekPinByIndex(data.getFoodRating());
            seekbarAmbiance.setSeekPinByIndex(data.getAmbienceRating());
            seekbarHospitality.setSeekPinByIndex(data.getHospitalityRating());

            mThumbnailHolder.bindThumbnails(data.getThumbnails());
        }
    }
}
