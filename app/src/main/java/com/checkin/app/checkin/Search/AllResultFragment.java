package com.checkin.app.checkin.Search;

import com.checkin.app.checkin.R;

/**
 * Created by Jogi Miglani on 26-10-2018.
 */

public class AllResultFragment extends BaseResultFragment {
    public static AllResultFragment newInstance(SearchResultInteraction listener) {
        AllResultFragment fragment = new AllResultFragment();
        fragment.mListener = listener;
        return fragment;
    }

    public AllResultFragment() {}

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_search_all_result;
    }

    @Override
    protected void setupObservers() {
        mViewModel.getAllResults().observe(this, this::onResultChanged);
    }
}
