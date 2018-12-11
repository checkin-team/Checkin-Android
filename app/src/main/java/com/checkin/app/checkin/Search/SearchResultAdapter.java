package com.checkin.app.checkin.Search;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.checkin.app.checkin.R;
import com.checkin.app.checkin.Utility.GlideApp;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Jogi Miglani on 28-10-2018.
 */

public class SearchResultAdapter extends RecyclerView.Adapter<SearchResultAdapter.ViewHolder> {
    private List<SearchResultModel> mData;

    public void setData(List<SearchResultModel> results) {
        mData = results;
        notifyDataSetChanged();
    }

    public SearchResultModel getAt(int position) {
        return mData.get(position);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bindData(mData.get(position));
    }

    @Override
    public int getItemCount() {
        return mData != null ? mData.size() : 0;
    }

    @Override
    public int getItemViewType(int position) {
        return R.layout.item_search_result;
    }

    class ViewHolder extends RecyclerView.ViewHolder  {
        @BindView(R.id.tv_result_name) TextView tvName;
        @BindView(R.id.im_result_pic) CircleImageView imPic;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bindData(final SearchResultModel searchResult) {
            tvName.setText(searchResult.getName());
            if (searchResult.getImageUrl() != null) {
                GlideApp.with(itemView.getContext())
                        .load(searchResult.getImageUrl())
                        .into(imPic);
            } else {
                if (searchResult.isTypePeople())
                    imPic.setImageResource(R.drawable.cover_unknown_male);
                else if (searchResult.isTypeRestaurant())
                    imPic.setImageResource(R.drawable.cover_restaurant_unknown);
            }
        }
    }
}
