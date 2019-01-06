package com.checkin.app.checkin.Search;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

public class SearchResultAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<SearchResultModel> mData;
    private final static int TYPE_PEOPLE=1,TYPE_RESTAURANT=2;

    public void setData(List<SearchResultModel> results) {
        mData = results;
        notifyDataSetChanged();
    }



    public SearchResultModel getAt(int position) {
        return mData.get(position);
    }

    @Override
    public int getItemViewType(int position) {
        SearchResultModel type = mData.get(position);
        if(type.isTypePeople()){
            return TYPE_PEOPLE;
        }else if(type.isTypeRestaurant()){
            return TYPE_RESTAURANT;
        }else
        return R.layout.item_search_result;
       // return  -1;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView;

        RecyclerView.ViewHolder viewHolder = null;
        switch (viewType) {
            case TYPE_PEOPLE:
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_search_people_result, parent, false);
                return new PeopleViewHolder(itemView);

            case TYPE_RESTAURANT:
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_search_restaurant_result, parent, false);
                return new RestaurantViewHolder(itemView);

             default:
                    itemView =  LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.item_search_result, parent, false);
                    return new ViewHolder(itemView);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

     //   holder.bindData(mData.get(position));

        int viewtype = getItemViewType(position);
        switch (viewtype){
            case TYPE_PEOPLE:
                PeopleViewHolder peopleViewHolder = (PeopleViewHolder) holder;
                break;
            case TYPE_RESTAURANT:
                RestaurantViewHolder restaurantViewHolder = (RestaurantViewHolder) holder;
                break;
            default:
                ViewHolder viewHolder = null;
                viewHolder.bindData(mData.get(position));

        }
    }




    @Override
    public int getItemCount() {
        return mData != null ? mData.size() : 0;
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

    class PeopleViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_result_name)
        TextView tvName;
        @BindView(R.id.im_result_pic)
        CircleImageView imPic;
        @BindView(R.id.status)
        Button status;


        public PeopleViewHolder(View itemView) {
            super(itemView);
        }

        public void bindData(final SearchPeopleResultModel searchPeopleResultModel) {
            tvName.setText(searchPeopleResultModel.getName());
            if (searchPeopleResultModel.getImageUrl() != null) {
                GlideApp.with(itemView.getContext())
                        .load(searchPeopleResultModel.getImageUrl())
                        .into(imPic);
            } else {
                imPic.setImageResource(R.drawable.cover_unknown_male);
            }

            SearchPeopleResultModel.FRIEND_STATUS friend_status = searchPeopleResultModel.getmStatus();
            if(friend_status == SearchPeopleResultModel.FRIEND_STATUS.NONE
                    || friend_status == SearchPeopleResultModel.FRIEND_STATUS.PENDING_REQUEST ){
                status.setText("Follow");
            }else if(friend_status == SearchPeopleResultModel.FRIEND_STATUS.FRIENDS){
                status.setText("Following");
            }

        }
    }

    class RestaurantViewHolder extends RecyclerView.ViewHolder{


        @BindView(R.id.tv_rating) TextView tvRating;
        @BindView(R.id.tv_category) TextView tvCategory;
        @BindView(R.id.tv_result_name) TextView tvName;
        @BindView(R.id.im_result_pic) CircleImageView imPic;
        @BindView(R.id.tv_cuisine) TextView tvCuisine;
        @BindView(R.id.btn_status) Button status;


        public RestaurantViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bindData(final SearchRestaurantResultModel restaurantResultModel){

            tvRating.setText(restaurantResultModel.getRating());
            tvCategory.setText(restaurantResultModel.getCategory());
            tvName.setText(restaurantResultModel.getName());
            tvCuisine.setText(restaurantResultModel.getCuisine());
            if (restaurantResultModel.getImageUrl() != null) {
                GlideApp.with(itemView.getContext())
                        .load(restaurantResultModel.getImageUrl())
                        .into(imPic);
            } else {
                    imPic.setImageResource(R.drawable.cover_restaurant_unknown);
            }

            SearchRestaurantResultModel.FRIEND_STATUS friend_status = restaurantResultModel.getmStatus();
            if(friend_status == SearchRestaurantResultModel.FRIEND_STATUS.NONE
                    || friend_status == SearchRestaurantResultModel.FRIEND_STATUS.PENDING_REQUEST ){
                status.setText("Follow");
            }else if(friend_status == SearchRestaurantResultModel.FRIEND_STATUS.FRIENDS){
                status.setText("Following");
            }

        }
    }
}
