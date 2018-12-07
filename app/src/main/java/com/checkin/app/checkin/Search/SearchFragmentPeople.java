package com.checkin.app.checkin.Search;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.R;

/**
 * Created by Jogi Miglani on 26-10-2018.
 */

public class SearchFragmentPeople extends Fragment implements SearchTabsAdapter.OnSearchResultInteractionListener {
    SearchTabsAdapter searchTabsAdapter;
    RecyclerView searchRVPeople;
    SearchViewModel mViewModel;
    private ProgressBar spinner;
    private onResultInteraction mResultInteraction;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.search_fragment_people,container,false);
    }

    public void setResultInteraction(onResultInteraction mPeopleInteraction) {
        this.mResultInteraction = mPeopleInteraction;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        searchRVPeople=view.findViewById(R.id.rv_search_people);
        searchRVPeople.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
        searchTabsAdapter=new SearchTabsAdapter();
        searchTabsAdapter.setSearchItemInteractionListener(this);
        searchRVPeople.setAdapter(searchTabsAdapter);
        spinner = (ProgressBar)view.findViewById(R.id.progress_load);

        mViewModel= ViewModelProviders.of(getActivity()).get(SearchViewModel.class);
        mViewModel.getPeople().observe(this,resource -> {
            if(resource.status.equals(Resource.Status.LOADING) )
            {
                showLoading();
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


    private void doneLoading() {
        spinner.setVisibility(View.GONE);
    }

    private void showLoading() {
        spinner.setVisibility(View.VISIBLE);
    }


}
