package com.checkin.app.checkin.Search;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.checkin.app.checkin.R;
import com.checkin.app.checkin.Utility.GlideApp;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by Jogi Miglani on 25-10-2018.
 */

public class SuggestionsFragment extends Fragment {
    SearchRVAdapter searchRVAdapterPopular;
    SearchRVAdapter searchRVAdapterRestaurants;
    SearchRVAdapter searchRVAdapterRecent;
    RecyclerView searchRVPopular;
    RecyclerView searchRVRestaurant;
    private OnSearchResultInteractionListener mSearchItemInteractionListener;
    RecyclerView searchRVRecent;

    public static SuggestionsFragment newInstance() {

        Bundle args = new Bundle();

        SuggestionsFragment fragment = new SuggestionsFragment();
        fragment.setArguments(args);
        return fragment;
    }
    public interface OnSearchResultInteractionListener{
        public void onResultPressed(SearchResultModel selectedResult);
    }

    public void setSearchItemInteractionListener(OnSearchResultInteractionListener mSearchItemInteractionListener) {
        this.mSearchItemInteractionListener = mSearchItemInteractionListener;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search_suggestions,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        searchRVPopular=view.findViewById(R.id.search_rv_user);
        searchRVPopular.setLayoutManager(new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.HORIZONTAL,false));
        searchRVAdapterPopular =new SearchRVAdapter(getContext());
        searchRVPopular.setAdapter(searchRVAdapterPopular);
        populateRVpopular();

        searchRVRestaurant=view.findViewById(R.id.search_rv_restaurants);
        searchRVRestaurant.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false));
        searchRVAdapterRestaurants=new SearchRVAdapter(getContext());
        searchRVRestaurant.setAdapter(searchRVAdapterRestaurants);
        populateRVRestaurant();

        searchRVRecent=view.findViewById(R.id.search_rv_recent_search);
        searchRVRecent.setLayoutManager(new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.HORIZONTAL,false));
        searchRVAdapterRecent=new SearchRVAdapter(getContext());
        searchRVRecent.setAdapter(searchRVAdapterRecent);
        populateRVRecent();
    }

    private void populateRVRecent() {
        List<SearchResultModel> searchRVRecentList = new ArrayList<>();

        for (int i=0;i<50;i++)
        {
            SearchResultModel searchResultModel =new SearchResultModel();
            searchResultModel.setName("Marie");
            searchResultModel.setImageUrl("https://storage.googleapis.com/checkin-app-18.appspot.com/images/users/ishudarshan/profile.jpg");

            searchRVRecentList.add(searchResultModel);
        }

        searchRVAdapterRecent.setListContent(searchRVRecentList);
    }

    private void populateRVRestaurant() {
        List<SearchResultModel> searchRVRestaurantList = new ArrayList<>();

        for (int i=0;i<50;i++)
        {
            SearchResultModel searchResultModel =new SearchResultModel();
            searchResultModel.setName("Romas Cafe");
            searchResultModel.setImageUrl("https://storage.googleapis.com/checkin-app-18.appspot.com/images/users/ishudarshan/profile.jpg");

            searchRVRestaurantList.add(searchResultModel);
        }

        searchRVAdapterRestaurants.setListContent(searchRVRestaurantList);
    }

    private void populateRVpopular() {
        List<SearchResultModel> searchRVPopularList = new ArrayList<>();

        for (int i=0;i<50;i++)
        {
            SearchResultModel searchResultModel =new SearchResultModel();
            searchResultModel.setName("Ishu Darshan");
            searchResultModel.setImageUrl("https://storage.googleapis.com/checkin-app-18.appspot.com/images/users/ishudarshan/profile.jpg");

            searchRVPopularList.add(searchResultModel);
        }

        searchRVAdapterPopular.setListContent(searchRVPopularList);
    }

      class SearchRVAdapter extends RecyclerView.Adapter<SearchRVAdapter.SearchViewHolder>
    {
        SearchViewHolder searchViewHolder;
        private List<SearchResultModel> searchItems=new ArrayList<>();
        private Context context;
        LayoutInflater inflater;
        View view;
        private OnSearchResultInteractionListener mSearchItemInteractionListener;

        public SearchRVAdapter(Context context)
        {
            this.context=context;
            inflater= LayoutInflater.from(context);
        }



        @NonNull
        @Override
        public SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            view=inflater.inflate(R.layout.item_search_suggestions,parent,false);
            searchViewHolder=new SearchViewHolder(view);
            return searchViewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull SearchViewHolder holder, int position) {
            SearchResultModel searchResultModel =searchItems.get(position);
            holder.userName.setText(searchResultModel.getName());
            GlideApp.with(getApplicationContext()).load(searchResultModel.getImageUrl()).into(holder.imageView);
        }

        @Override
        public int getItemCount() {
            return searchItems.size();
        }

        class SearchViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
        {
            TextView userName;
            CircleImageView imageView;
            public SearchViewHolder(View itemView) {
                super(itemView);
                userName=itemView.findViewById(R.id.search_item_username);
                imageView=itemView.findViewById(R.id.im_search_item);

            }

            @Override
            public void onClick(View v) {
                mSearchItemInteractionListener.onResultPressed(searchItems.get(getAdapterPosition()));
            }


        }

        public void setListContent(List<SearchResultModel> list_members){
            this.searchItems=list_members;
            notifyDataSetChanged();
        }

        public void removeAt(int position) {
            searchItems.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(0, searchItems.size());
        }


    }
}

