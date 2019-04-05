package com.checkin.app.checkin.Search;

import com.checkin.app.checkin.R;

/**
 * Created by Jogi Miglani on 26-10-2018.
 */

public class PeopleResultFragment extends BaseResultFragment<SearchResultPeopleModel> {
    public PeopleResultFragment() {
    }

    public static PeopleResultFragment newInstance(SearchResultInteraction listener) {
        PeopleResultFragment fragment = new PeopleResultFragment();
        fragment.mListener = listener;
        return fragment;
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_search_people_result;
    }

    @Override
    protected void setupObservers() {
        mViewModel.getPeopleResults().observe(this, this::onResultChanged);
    }
}
