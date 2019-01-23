package com.checkin.app.checkin.Review.NewReview;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.checkin.app.checkin.Misc.GenericDetailModel;
import com.checkin.app.checkin.R;
import com.checkin.app.checkin.Utility.Utils;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ReviewImageAdapter extends RecyclerView.Adapter<ReviewImageAdapter.ViewHolder> {
    private List<ReviewImageShowModel> mOrders;
    private ImageInteraction mListener;
    private static final String TAG = ReviewImageAdapter.class.getSimpleName();

    ReviewImageAdapter(List<ReviewImageShowModel> orders, ImageInteraction ordersInterface) {
        mOrders = orders;
        mListener = ordersInterface;
    }

    public void setData(List<ReviewImageShowModel> data) {
        this.mOrders = data;
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
        holder.bindData(mOrders.get(position));
    }

    @Override
    public int getItemCount() {
        return mOrders != null ? mOrders.size() : 0;
    }

    @Override
    public int getItemViewType(final int position) {
        return R.layout.item_review_add_image;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.im_delete_image)
        ImageView imDeleteImage;
        @BindView(R.id.im_display_pic)
        ImageView imDisplayPic;

        private ReviewImageShowModel mOrderModel;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            imDeleteImage.setOnClickListener(v -> mListener.onDeleteImage(mOrderModel));
        }

        void bindData(ReviewImageShowModel order) {
            this.mOrderModel = order;

            if(order !=null){
                Utils.loadImageOrDefault(imDisplayPic, order.getImage().getPath(), R.drawable.card_image_add);
            }

        }

    }

    public interface ImageInteraction {
        void onDeleteImage(ReviewImageShowModel orderedItem);
    }
}
