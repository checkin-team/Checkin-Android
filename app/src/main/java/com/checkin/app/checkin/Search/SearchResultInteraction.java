package com.checkin.app.checkin.Search;

public interface SearchResultInteraction {
    void onClickResult(SearchResultModel searchItem);
    boolean onLongClickResult(SearchResultModel searchItem);
    void onClickFollowResult(SearchResultModel searchItem);
}
