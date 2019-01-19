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
import com.checkin.app.checkin.R;
import com.xw.repo.BubbleSeekBar;

import butterknife.BindView;

public class ShopInvoiceFeedbackAdapter extends RecyclerView.Adapter<ShopInvoiceFeedbackAdapter.ShopInvoiceFeedbackHolder> {

    @NonNull
    @Override
    public ShopInvoiceFeedbackHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.child_shop_invoice_feedback, parent, false);
        return new ShopInvoiceFeedbackHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ShopInvoiceFeedbackHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 3;
    }

    class ShopInvoiceFeedbackHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_shop_invoice_feedback_customer_name)
        TextView tvShopInvoiceFeedbackCustomerName;
        @BindView(R.id.tv_shop_invoice_feedback_experience_title)
        TextView tvShopInvoiceFeedbackExperienceTitle;
        @BindView(R.id.tv_shop_invoice_feedback_experience)
        ReadMoreTextView tvShopInvoiceFeedbackExperience;
        @BindView(R.id.sb_shop_invoice_feedback_food_quality_rating)
        BubbleSeekBar sbShopInvoiceFeedbackFoodQualityRating;
        @BindView(R.id.sb_shop_invoice_feedback_ambience_rating)
        BubbleSeekBar sbShopInvoiceFeedbackAmbienceRating;
        @BindView(R.id.sb_shop_invoice_feedback_service_hospitality)
        BubbleSeekBar sbShopInvoiceFeedbackServiceHospitality;
        @BindView(R.id.rl_shop_invoice_feedback_rating)
        RelativeLayout rlShopInvoiceFeedbackRating;
        @BindView(R.id.incl_shop_invoice_detail_feedback_overall_rating)
        RelativeLayout incl_shop_invoice_detail_feedback_overall_rating;
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

        ShopInvoiceFeedbackHolder(View itemView) {
            super(itemView);
        }
    }
}
