package com.checkin.app.checkin.Search;

import com.checkin.app.checkin.R;

/**
 * Created by Jogi Miglani on 26-10-2018.
 */

public class RestaurantResultFragment extends BaseResultFragment<SearchResultShopModel> {
    public static RestaurantResultFragment newInstance(SearchResultInteraction listener) {
        RestaurantResultFragment fragment = new RestaurantResultFragment();
        fragment.mListener = listener;
        return fragment;
    }

    public RestaurantResultFragment() {}

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_search_restaurant_result;
    }

    @Override
    protected void setupObservers() {
        mViewModel.getRestaurantResults().observe(this, this::onResultChanged);
    }
}
