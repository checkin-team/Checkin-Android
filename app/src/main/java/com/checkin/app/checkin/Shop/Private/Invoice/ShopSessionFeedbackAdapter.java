package com.checkin.app.checkin.Shop.Private.Invoice;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.appyvet.materialrangebar.RangeBar;
import com.checkin.app.checkin.Misc.ImageThumbnailHolder;
import com.checkin.app.checkin.Misc.ImageThumbnailHolder.ImageThumbnailInteraction;
import com.checkin.app.checkin.R;
import com.checkin.app.checkin.User.bills.UserTransactionBriefModel;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ShopSessionFeedbackAdapter extends RecyclerView.Adapter<ShopSessionFeedbackAdapter.ShopInvoiceFeedbackHolder> {
    private List<ShopSessionFeedbackModel> mList;

    ShopSessionFeedbackAdapter(List<ShopSessionFeedbackModel> data) {
        this.mList = data;
    }

    @NonNull
    @Override
    public ShopInvoiceFeedbackHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_feedback_review, parent, false);
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
        @BindView(R.id.tv_sf_user_name)
        TextView tvCustomerName;
        @BindView(R.id.tv_sf_rating_number)
        TextView tvRatingNumber;
        @BindView(R.id.tv_sf_rating_text)
        TextView tvRatingText;
        @BindView(R.id.im_sf_emoji_angry_1)
        ImageView imAngry;
        @BindView(R.id.im_sf_emoji_sad_2)
        ImageView imSad;
        @BindView(R.id.im_sf_emoji_confused_3)
        ImageView imConfused;
        @BindView(R.id.im_sf_emoji_smile_4)
        ImageView imSmile;
        @BindView(R.id.im_sf_emoji_love_5)
        ImageView imLove;

        ShopInvoiceFeedbackHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bindData(ShopSessionFeedbackModel data) {
            tvCustomerName.setText(data.getUser().getDisplayName());

            tvRatingNumber.setText(String.valueOf(data.getFoodRating()));
            tvRatingText.setText(UserTransactionBriefModel.getFeedbackText(data.getFoodRating()));
            defaultRatingEmoji();
            switch (data.getFoodRating()) {
                case 1:
                    imAngry.setImageDrawable(imAngry.getContext().getResources().getDrawable(R.drawable.ic_emoji_angry_yellow));
                    break;
                case 2:
                    imSad.setImageDrawable(imAngry.getContext().getResources().getDrawable(R.drawable.ic_emoji_sad_yellow));
                    break;
                case 3:
                    imConfused.setImageDrawable(imAngry.getContext().getResources().getDrawable(R.drawable.ic_emoji_confused_yellow));
                    break;
                case 4:
                    imSmile.setImageDrawable(imAngry.getContext().getResources().getDrawable(R.drawable.ic_emoji_smiling_yellow));
                    break;
                case 5:
                    imLove.setImageDrawable(imAngry.getContext().getResources().getDrawable(R.drawable.ic_emoji_in_love_yellow));
                    break;
                default:
                    imLove.setImageDrawable(imAngry.getContext().getResources().getDrawable(R.drawable.ic_emoji_in_love_yellow));
                    break;

            }
        }

        public void defaultRatingEmoji() {
            imAngry.setImageDrawable(imAngry.getContext().getResources().getDrawable(R.drawable.ic_emoji_angry));
            imSad.setImageDrawable(imAngry.getContext().getResources().getDrawable(R.drawable.ic_emoji_sad));
            imConfused.setImageDrawable(imAngry.getContext().getResources().getDrawable(R.drawable.ic_emoji_confused));
            imSmile.setImageDrawable(imAngry.getContext().getResources().getDrawable(R.drawable.ic_emoji_smiling));
            imLove.setImageDrawable(imAngry.getContext().getResources().getDrawable(R.drawable.ic_emoji_in_love));
        }
    }
}
