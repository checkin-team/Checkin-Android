package com.alcatraz.admin.project_alcatraz.Utility;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.alcatraz.admin.project_alcatraz.R;

/**
 * Created by shivanshs9 on 10/5/18.
 */

public class SearchBar {

    private Activity mActivity;
    private Toolbar mToolbar, searchToolbar;
    private EditText edSearch;
    private View mSearchContainer;
    private float positionFromRight;

    public SearchBar(Activity activity, Toolbar toolbar, View searchContainer, int positionFromRight) {
        mActivity = activity;
        mToolbar = toolbar;
        mSearchContainer = searchContainer;
        this.positionFromRight = positionFromRight;

        initSearchBar();
    }

    private void initSearchBar() {
        searchToolbar = mActivity.findViewById(R.id.search_toolbar);
        if (searchToolbar != null) {
            searchToolbar.setNavigationIcon(R.drawable.ic_arrow_back);
            mSearchContainer.setVisibility(View.GONE);
            searchToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    hideSearchBar(positionFromRight);
                }
            });
        }

        edSearch = mActivity.findViewById(R.id.editText_search);
    }

    /**
     * to show the searchAppBarLayout and hide the mainAppBar with animation.
     *
     * @param positionFromRight
     */
    private void showSearchBar(float positionFromRight) {
        AnimatorSet set = new AnimatorSet();
        set.playTogether(
                ObjectAnimator.ofFloat(appBar, "translationY", -tabLayout.getHeight()),
                ObjectAnimator.ofFloat(viewPager, "translationY", -tabLayout.getHeight()),
                ObjectAnimator.ofFloat(appBar, "alpha", 0)
        );
        set.setDuration(100).addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                appBar.setVisibility(View.GONE);
                edSearch.requestFocus();
                Utils.showKeyBoard(searchEditText);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
        set.start();


        // start x-index for circular animation
        int cx = mToolbar.getWidth() - (int) (mActivity.getResources().getDimension(R.dimen.dp48)* (0.5f + positionFromRight));
        // start y-index for circular animation
        int cy = (mToolbar.getTop() + mToolbar.getBottom()) / 2;

        // calculate max radius
        int dx = Math.max(cx, mToolbar.getWidth() - cx);
        int dy = Math.max(cy, mToolbar.getHeight() - cy);
        float finalRadius = (float) Math.hypot(dx, dy);

        // Circular animation declaration begin
        final Animator animator;
        animator = io.codetail.animation.ViewAnimationUtils
                .createCircularReveal(searchAppBarLayout, cx, cy, 0, finalRadius);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.setDuration(200);
        searchAppBarLayout.setVisibility(View.VISIBLE);
        animator.start();
        // Circular animation declaration end
    }


    /**
     * to hide the searchAppBarLayout and show the mainAppBar with animation.
     *
     * @param positionFromRight
     */
    private void hideSearchBar(float positionFromRight) {

        // start x-index for circular animation
        int cx = mToolbar.getWidth() - (int) (mActivity.getResources().getDimension(R.dimen.dp48) * (0.5f + positionFromRight));
        // start  y-index for circular animation
        int cy = (mToolbar.getTop() + mToolbar.getBottom()) / 2;

        // calculate max radius
        int dx = Math.max(cx, mToolbar.getWidth() - cx);
        int dy = Math.max(cy, mToolbar.getHeight() - cy);
        float finalRadius = (float) Math.hypot(dx, dy);

        // Circular animation declaration begin
        Animator animator;
        animator = io.codetail.animation.ViewAnimationUtils
                .createCircularReveal(searchAppBarLayout, cx, cy, finalRadius, 0);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.setDuration(200);
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                searchAppBarLayout.setVisibility(View.GONE);
                Utils.hideKeyBoard(edSearch);
            }

            @Override
            public void onAnimationCancel(Animator animator) {
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
            }
        });

        animator.start();
        // Circular animation declaration end

        appBar.setVisibility(View.VISIBLE);
        AnimatorSet set = new AnimatorSet();
        set.playTogether(
                ObjectAnimator.ofFloat(appBar, "translationY", 0),
                ObjectAnimator.ofFloat(appBar, "alpha", 1),
                ObjectAnimator.ofFloat(viewPager, "translationY", 0)
        );
        set.setDuration(100).start();

    }
    
    /*
    private Toolbar mSearchToolbar, mToolbar;
    private Menu menu, searchMenu;
    private SearchView mSearchBar;
    private Activity mActivity;

    public void setSearchToolbar(Activity activity, Toolbar toolbar)
    {
        mActivity = activity;
        mToolbar = toolbar;
        mSearchToolbar = activity.findViewById(R.id.search_toolbar);
        if (mSearchToolbar != null) {
            mSearchToolbar.inflateMenu(R.menu.menu_search);
            searchMenu = mSearchToolbar.getMenu();
            SearchView searchView = mToolbar.findViewById(R.id.action_search);
            MenuItem searchMenuItem = searchMenu.findItem(R.id.action_searchbar);
            mSearchBar = (SearchView) searchMenuItem.getActionView();

            searchView.setOnSearchClickListener(new SearchView.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mSearchBar.setIconifiedByDefault(true);
                    mSearchBar.setFocusable(true);
                    mSearchBar.setIconified(false);
                    mSearchBar.requestFocusFromTouch();
                    //mSearchToolbar.setVisibility(View.VISIBLE);
                    mToolbar.setVisibility(View.GONE);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        circleReveal(mSearchToolbar, 1, true, true);
                    }
                    else {
                        mSearchToolbar.setVisibility(View.VISIBLE);
                    }
                }
            });
            searchMenuItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
                @Override
                public boolean onMenuItemActionCollapse(MenuItem item) {
                    // Do something when collapsed
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        circleReveal(mSearchToolbar,1,true,false);
                        mToolbar.setVisibility(View.VISIBLE);
                    }
                    else {
                        mSearchToolbar.setVisibility(View.GONE);
                        mToolbar.setVisibility(View.VISIBLE);
                    }
                    return true;
                }

                @Override
                public boolean onMenuItemActionExpand(MenuItem item) {
                    // Do something when expanded
                    return true;
                }
            });

            initSearchView();


        } else
            Log.e("toolbar", "setSearchToolbar: NULL");
    }

    public void initSearchView()
    {
        // Enable/Disable Submit button in the keyboard
        mSearchBar.setSubmitButtonEnabled(false);

        // Change search close button image
        ImageView closeButton = mSearchBar.findViewById(R.id.search_close_btn);
        closeButton.setImageResource(R.drawable.close);


        // set hint and the text colors
        EditText txtSearch = mSearchBar.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        txtSearch.setHint("Search...");
        txtSearch.setHintTextColor(Color.DKGRAY);
        txtSearch.setTextColor(mActivity.getResources().getColor(R.color.colorPrimary));


        // set the cursor
        try {
            Field mCursorDrawableRes = TextView.class.getDeclaredField("mCursorDrawableRes");
            mCursorDrawableRes.setAccessible(true);
            mCursorDrawableRes.set(txtSearch, R.drawable.search_cursor); //This sets the cursor resource ID to 0 or @null which will make it visible on white background
        } catch (Exception e) {
            e.printStackTrace();
        }

        mSearchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                callSearch(query);
                mSearchBar.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                callSearch(newText);
                return true;
            }

            public void callSearch(String query) {
                //Do searching
                Log.e("query", "" + query);
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void circleReveal(final View myView, int posFromRight, boolean containsOverflow, final boolean isShow)
    {
        if (myView == null) {
            return;
        }

        int width = myView.getWidth();

        if(posFromRight>0)
            width -= mActivity.getResources().getDimensionPixelSize(R.dimen.abc_action_button_min_width_material) * (posFromRight - 1/2);

        if(containsOverflow)
            width -= mActivity.getResources().getDimensionPixelSize(R.dimen.abc_action_button_min_width_overflow_material);

        int cx = width;
        int cy = myView.getHeight()/2;

        Animator anim;
        if(isShow)
            anim = ViewAnimationUtils.createCircularReveal(myView, cx, cy, 0,(float)width);
        else
            anim = ViewAnimationUtils.createCircularReveal(myView, cx, cy, (float)width, 0);

        anim.setDuration((long)220);

        // make the view invisible when the animation is done
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if(!isShow)
                {
                    super.onAnimationEnd(animation);
                    myView.setVisibility(View.GONE);
                }
            }
        });

        // make the view visible and start the animation
        if(isShow)
            myView.setVisibility(View.VISIBLE);

        // start the animation
        anim.start();
    }
    */
}
