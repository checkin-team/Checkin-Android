package com.checkin.app.checkin.Review.NewReview;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.checkin.app.checkin.Misc.GenericDetailModel;
import com.checkin.app.checkin.R;
import com.checkin.app.checkin.Utility.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ReviewImageAdapter extends RecyclerView.Adapter<ReviewImageAdapter.ViewHolder> {
    private List<ReviewImageShowModel> mOrders = new ArrayList<>();
    private ImageInteraction mListener;
    private static final String TAG = ReviewImageAdapter.class.getSimpleName();

    ReviewImageAdapter(ImageInteraction ordersInterface) {
        mListener = ordersInterface;
    }

    public void setData(List<ReviewImageShowModel> data) {
        this.mOrders = data;
        notifyDataSetChanged();
    }

    public void addData(ReviewImageShowModel data) {
        this.mOrders.add(data);
        notifyItemInserted(mOrders.size() - 1);
    }

    public void updateData(int pos, long pk){
        mOrders.get(pos).setPk(pk);
        notifyItemChanged(pos);
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
        @BindView(R.id.im_add_review_delete_image)
        ImageView imDeleteImage;
        @BindView(R.id.im_add_review_display_pic)
        ImageView imDisplayPic;
        @BindView(R.id.progress_bar_add_review)
        ProgressBar progressBarAddReview;

        private ReviewImageShowModel mOrderModel;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            imDeleteImage.setOnClickListener(v -> mListener.onDeleteImage(mOrderModel));
        }

        void bindData(ReviewImageShowModel order) {
            this.mOrderModel = order;
            if (order.getPk() == 0) {
                progressBarAddReview.setVisibility(View.VISIBLE);
                imDeleteImage.setVisibility(View.GONE);
                imDisplayPic.setVisibility(View.GONE);
            } else {
                if(order.getImage().exists()) {
                    imDisplayPic.setImageURI(Uri.fromFile(order.getImage()));
                }
            }
        }

    }

    public interface ImageInteraction {
        void onDeleteImage(ReviewImageShowModel orderedItem);
    }
}
