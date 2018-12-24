package com.checkin.app.checkin.Search;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.R;
import com.checkin.app.checkin.Utility.ItemClickSupport;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public abstract class BaseResultFragment extends Fragment implements ItemClickSupport.OnItemClickListener, ItemClickSupport.OnItemLongClickListener {
    private Unbinder unbinder;

    @BindView(R.id.container_progress) ViewGroup vContainerProgress;
    @BindView(R.id.rv_results) RecyclerView rvResults;
    @BindView(R.id.tv_status) TextView tvResultStatus;
    @BindView(R.id.load_progress) ProgressBar vLoadProgress;

    protected SearchResultAdapter mAdapter;
    protected SearchViewModel mViewModel;
    protected SearchResultInteraction mListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(getLayoutRes(), container, false);
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mAdapter = new SearchResultAdapter();
        rvResults.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));
        rvResults.setAdapter(mAdapter);

        ItemClickSupport.addTo(rvResults)
                .setOnItemClickListener(this)
                .setOnItemLongClickListener(this);

        mViewModel = ViewModelProviders.of(requireActivity()).get(SearchViewModel.class);

        setupObservers();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    protected void showLoadProgress() {
        tvResultStatus.setText(R.string.status_searching);
        vLoadProgress.setVisibility(View.VISIBLE);
        vContainerProgress.setVisibility(View.VISIBLE);
    }

    protected void resetResults() {
        mAdapter.setData(null);
    }

    protected void hideLoadProgress() {
        vContainerProgress.setVisibility(View.GONE);
    }

    protected void noResultFound() {
        tvResultStatus.setText(R.string.status_search_not_found);
        vLoadProgress.setVisibility(View.GONE);
        vContainerProgress.setVisibility(View.VISIBLE);
    }

    protected void updateResults(List<SearchResultModel> results) {
        mAdapter.setData(results);
    }

    protected void onResultChanged(Resource<List<SearchResultModel>> listResource) {
        if (listResource == null)
            return;
        if (listResource.status == Resource.Status.SUCCESS && listResource.data != null) {
            this.updateResults(listResource.data);
            this.hideLoadProgress();
        } else if (listResource.status == Resource.Status.LOADING) {
            this.showLoadProgress();
            this.resetResults();
        } else if (listResource.status == Resource.Status.ERROR_NOT_FOUND) {
            this.noResultFound();
        }
    }

    @Override
    public void onItemClicked(RecyclerView recyclerView, int position, View v) {
        if (mListener == null)
            return;
        SearchResultModel resultModel = ((SearchResultAdapter) recyclerView.getAdapter()).getAt(position);
        mListener.onClickResult(resultModel);
    }

    @Override
    public boolean onItemLongClicked(RecyclerView recyclerView, int position, View v) {
        if (mListener == null)
            return false;
        SearchResultModel resultModel = ((SearchResultAdapter) recyclerView.getAdapter()).getAt(position);
        return mListener.onLongClickResult(resultModel);
    }

    @LayoutRes
    protected abstract int getLayoutRes();

    protected abstract void setupObservers();
}
