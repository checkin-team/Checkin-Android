package com.checkin.app.checkin.Search;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.R;

/**
 * Created by Jogi Miglani on 26-10-2018.
 */

public class SearchFragmentRestaurant extends Fragment implements SearchTabsAdapter.OnSearchResultInteractionListener {
    SearchTabsAdapter searchTabsAdapter;
    RecyclerView searchRVRestaurants;
    SearchViewModel mViewModel;
    private onResultInteraction mResultInteraction;
    private ProgressBar spinner;
    View mView;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.search_fragment_restaurant,container,false);
    }

    public void setmResultInteraction(onResultInteraction mResultInteraction) {
        this.mResultInteraction = mResultInteraction;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        searchRVRestaurants=view.findViewById(R.id.rv_search_restaurants);
        searchRVRestaurants.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
        searchTabsAdapter=new SearchTabsAdapter();
        searchTabsAdapter.setSearchItemInteractionListener(this);
        searchRVRestaurants.setAdapter(searchTabsAdapter);
        spinner = (ProgressBar)view.findViewById(R.id.progress_load);

        mView=view.findViewById(R.id.tv_status);

        mViewModel= ViewModelProviders.of(getActivity()).get(SearchViewModel.class);
        mViewModel.getRestaurants().observe(this,resource -> {
            if(resource.status.equals(Resource.Status.LOADING) )
            {
                showLoading();
            }
            else if(resource.status == Resource.Status.ERROR_NOT_FOUND)
            {
                showNotFoundStatus();
            }
            else if (resource.status.equals(Resource.Status.SUCCESS)&&resource.data!=null)
            {
                searchTabsAdapter.setListContent(resource.data);
                doneLoading();
            }

        });

    }
    @Override
    public void onSelectResult(SearchModel selectedResult) {
        mResultInteraction.onResultPressed(selectedResult);
    }

    public interface onResultInteraction{
        public void onResultPressed(SearchModel result);
    }


    private void showNotFoundStatus() {
        mView.setVisibility(View.VISIBLE);

    }

    private void doneLoading() {
        spinner.setVisibility(View.GONE);
    }

    private void showLoading() {
        spinner.setVisibility(View.VISIBLE);
    }


}
