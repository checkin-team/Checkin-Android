package com.checkin.app.checkin.Misc;

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

import java.util.List;

/**
 * Created by Jogi Miglani on 26-10-2018.
 */

public class SearchFragmentAll extends Fragment {

    SearchViewModel mViewModel;
    SearchTabsAdapter searchTabsAdapter;
    RecyclerView searchRVAll;
    private ProgressBar spinner;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.search_fragment_all,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        searchRVAll=view.findViewById(R.id.rv_search_all);
        searchRVAll.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
        searchTabsAdapter=new SearchTabsAdapter();
        searchRVAll.setAdapter(searchTabsAdapter);
        spinner = (ProgressBar)view.findViewById(R.id.progress_load);

        mViewModel= ViewModelProviders.of(getActivity()).get(SearchViewModel.class);
        mViewModel.getAll().observe(this, (Resource<List<SearchRVPojo>> resource) -> {
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

    private void doneLoading() {
        spinner.setVisibility(View.GONE);
    }

    private void showLoading() {
        spinner.setVisibility(View.VISIBLE);
    }



}
