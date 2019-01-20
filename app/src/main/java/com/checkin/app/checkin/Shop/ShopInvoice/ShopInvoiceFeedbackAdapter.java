package com.checkin.app.checkin.Shop.ShopInvoice;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.borjabravo.readmoretextview.ReadMoreTextView;
import com.checkin.app.checkin.Misc.BriefModel;
import com.checkin.app.checkin.Misc.ImageThumbnailHolder;
import com.checkin.app.checkin.R;
import com.checkin.app.checkin.Review.ShopReview.ShopReviewAdapter;
import com.checkin.app.checkin.Review.ShopReview.ShopReviewModel;
import com.xw.repo.BubbleSeekBar;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ShopInvoiceFeedbackAdapter extends RecyclerView.Adapter<ShopInvoiceFeedbackAdapter.ShopInvoiceFeedbackHolder> {

    private List<ShopSessionFeedbackModel> mList;
    public static ShopInvoiceFeedbackHolder.ReviewInteraction mListener;

    ShopInvoiceFeedbackAdapter(ShopInvoiceFeedbackHolder.ReviewInteraction listener){
        mListener = listener;
    }

    @NonNull
    @Override
    public ShopInvoiceFeedbackHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.child_shop_invoice_feedback, parent, false);
        return new ShopInvoiceFeedbackHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ShopInvoiceFeedbackHolder holder, int position) {
        ShopSessionFeedbackModel data = mList.get(position);
        holder.bindData(data);
    }

    @Override
    public int getItemCount() {
        return mList != null ? mList.size():0;
    }

    void addSessionFeedbackData(List<ShopSessionFeedbackModel> mList) {
        this.mList = mList;
        notifyDataSetChanged();
    }

    static class ShopInvoiceFeedbackHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_shop_invoice_feedback_customer_name)
        TextView tvShopInvoiceFeedbackCustomerName;
        @BindView(R.id.tv_shop_invoice_feedback_experience_title)
        TextView tvShopInvoiceFeedbackExperienceTitle;
        @BindView(R.id.tv_shop_invoice_feedback_experience)
        ReadMoreTextView tvShopInvoiceFeedbackExperience;
        @BindView(R.id.sb_shop_invoice_feedback_food_quality_rating)
        BubbleSeekBar sbShopInvoiceFeedbackFoodQualityRating;
        @BindView(R.id.tv_shop_invoice_feedback_food_quality_rating)
        TextView tvShopInvoiceFeedbackFoodQualityRating;
        @BindView(R.id.sb_shop_invoice_feedback_ambience_rating)
        BubbleSeekBar sbShopInvoiceFeedbackAmbienceRating;
        @BindView(R.id.tv_shop_invoice_feedback_ambience_rating)
        TextView tvShopInvoiceFeedbackAmbienceRating;
        @BindView(R.id.sb_shop_invoice_feedback_service_hospitality_rating)
        BubbleSeekBar sbShopInvoiceFeedbackServiceHospitalityRating;
        @BindView(R.id.tv_shop_invoice_feedback_service_hospitality_rating)
        TextView tvShopInvoiceFeedbackServiceHospitalityRating;
        @BindView(R.id.rl_shop_invoice_feedback_rating)
        RelativeLayout rlShopInvoiceFeedbackRating;
        @BindView(R.id.ll_shop_invoice_detail_feedback_overall_rating)
        LinearLayout llShopInvoiceDetailFeedbackOverallRating;
        @BindView(R.id.v_shop_invoice_detail_feedback_image)
        View vShopInvoiceDetailFeedbackImage;
        @BindView(R.id.im_thumbnail_1)
        ImageView imThumbnail1;
        @BindView(R.id.im_thumbnail_2)
        ImageView imThumbnail2;
        @BindView(R.id.im_thumbnail_3)
        ImageView imThumbnail3;
        @BindView(R.id.tv_thumbnail_count)
        TextView tvThumbnailCount;
        @BindView(R.id.container_thumbnail_3)
        FrameLayout containerThumbnail3;
        @BindView(R.id.v_shop_invoice_detail_feedback_overall_rating)
        View vShopInvoiceDetailFeedbackOverallRating;
        @BindView(R.id.tv_shop_invoice_detail_feedback_overall_rating)
        TextView tvShopInvoiceDetailFeedbackOverallRating;

        ImageThumbnailHolder imageThumbnailHolder;

        ShopInvoiceFeedbackHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);

            imageThumbnailHolder = new ImageThumbnailHolder(itemView, new ImageThumbnailHolder.ImageThumbnailInteraction() {
                @Override
                public void onThumbnailClick(int index) {
                    mListener.onUserClick(null);
                }
            });
        }

        public void bindData(ShopSessionFeedbackModel data) {
            Integer ambienceRating = data.getAmbienceRating();
            String body = data.getBody();
            String created = data.getCreated();
            Integer foodRating = data.getFoodRating();
            Integer hospitalityRating = data.getHospitalityRating();
            Integer overAllRating = data.getOverallRating();
            List<String> thumbanials = data.getThumbnails();
            String displayName = data.getUser().getDisplayName();

            tvShopInvoiceFeedbackCustomerName.setText(displayName);
            tvShopInvoiceFeedbackExperience.setText(body);

            sbShopInvoiceFeedbackFoodQualityRating.setProgress((float)foodRating);
            sbShopInvoiceFeedbackAmbienceRating.setProgress((float)ambienceRating);
            sbShopInvoiceFeedbackServiceHospitalityRating.setProgress((float)hospitalityRating);

            tvShopInvoiceFeedbackFoodQualityRating.setText(String.valueOf(foodRating));
            tvShopInvoiceFeedbackAmbienceRating.setText(String.valueOf(ambienceRating));
            tvShopInvoiceFeedbackServiceHospitalityRating.setText(String.valueOf(hospitalityRating));

            tvShopInvoiceDetailFeedbackOverallRating.setText(String.valueOf(overAllRating));

            String[] stringThumbs = new String[thumbanials.size()];
            stringThumbs = thumbanials.toArray(stringThumbs);

            imageThumbnailHolder.bindThumbnails(stringThumbs);
        }

        public interface ReviewInteraction {
            void onToggleLike(ShopReviewModel review);
            void onThumbnailClick(ShopReviewModel review, int index);
            void onRatingClick(ShopReviewModel review);
            void onFollowClick(BriefModel user);
            void onUserClick(BriefModel user);
        }
    }
}
