package com.checkin.app.checkin.Search;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.checkin.app.checkin.R;
import com.checkin.app.checkin.Utility.Utils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.checkin.app.checkin.Search.SearchResultModel.TYPE_PEOPLE;
import static com.checkin.app.checkin.Search.SearchResultModel.TYPE_RESTAURANT;

/**
 * Created by Jogi Miglani on 28-10-2018.
 */

public class SearchResultAdapter <S extends SearchResultModel>  extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private SearchResultInteraction mListener;
    private List<S> mData;

    public SearchResultAdapter(SearchResultInteraction listener) {
        mListener = listener;
    }

    public void setData(List<S> results) {
        mData = results;
        notifyDataSetChanged();
    }

    public SearchResultModel getAt(int position) {
        return mData.get(position);
    }

    @Override
    public int getItemViewType(int position) {
        SearchResultModel result = mData.get(position);
        if (result instanceof SearchResultPeopleModel)
            return TYPE_PEOPLE;
        else if (result instanceof SearchResultShopModel)
            return TYPE_RESTAURANT;
        else
            return R.layout.item_search_result;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        switch (viewType) {
            case TYPE_PEOPLE:
                viewHolder = new PeopleViewHolder(LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.item_search_result_people, parent, false));
                break;
            case TYPE_RESTAURANT:
                viewHolder = new RestaurantViewHolder(LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.item_search_result_restaurant, parent, false));
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case TYPE_PEOPLE:
                ((PeopleViewHolder) holder).bindData(((SearchResultPeopleModel) mData.get(position)));
                break;
            case TYPE_RESTAURANT:
                ((RestaurantViewHolder) holder).bindData(((SearchResultShopModel) mData.get(position)));
                break;
        }
    }

    @Override
    public int getItemCount() {
        return mData != null ? mData.size() : 0;
    }

    class PeopleViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_result_name)
        TextView tvName;
        @BindView(R.id.im_result_pic)
        ImageView imPic;
        @BindView(R.id.tv_result_people_extra)
        TextView tvResultExtra;
        @BindView(R.id.container_status_none)
        ViewGroup containerStatusNone;
        @BindView(R.id.container_status_request)
        ViewGroup containerStatusRequest;

        private SearchResultPeopleModel mResult;

        public PeopleViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            containerStatusNone.setVisibility(View.GONE);
            containerStatusRequest.setVisibility(View.GONE);
        }

        @OnClick(R.id.btn_result_people_follow)
        public void onFollowClick() {
            mListener.onClickFollowResult(mResult);
            containerStatusNone.setVisibility(View.GONE);
            containerStatusRequest.setVisibility(View.VISIBLE);
        }

        public void bindData(SearchResultPeopleModel result) {
            mResult = result;
            tvName.setText(result.getName());
            Utils.loadImageOrDefault(imPic, result.getImageUrl(), R.drawable.cover_unknown_male);
            tvResultExtra.setText(result.formatExtra());
        }
    }

    class RestaurantViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_result_name)
        TextView tvName;
        @BindView(R.id.im_result_pic)
        ImageView imPic;
        @BindView(R.id.tv_result_restaurant_location)
        TextView tvShopLocality;
        @BindView(R.id.tv_result_restaurant_extra)
        TextView tvShopExtra;
        @BindView(R.id.container_status_none)
        ViewGroup containerStatusNone;

        private SearchResultShopModel mResult;

        public RestaurantViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @OnClick(R.id.btn_result_shop_follow)
        public void onFollowClick() {
            mListener.onClickFollowResult(mResult);
        }

        public void bindData(final SearchResultShopModel result) {
            mResult = result;
            tvName.setText(result.getName());
            Utils.loadImageOrDefault(imPic, result.getImageUrl(), R.drawable.cover_restaurant_unknown);
            tvShopExtra.setText(result.formatExtra());
            tvShopLocality.setText(result.getLocality());
            if (!result.isFollowing())
                containerStatusNone.setVisibility(View.VISIBLE);
        }
    }
}
