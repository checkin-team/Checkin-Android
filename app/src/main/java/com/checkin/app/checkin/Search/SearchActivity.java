package com.checkin.app.checkin.Search;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.checkin.app.checkin.R;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {


    private SectionPageAdapter mSectionPageAdapter;
    private SearchViewModel searchViewModel;
    private ViewPager viewPager;
    MaterialSearchView searchView;
    TextView tvSearch;
    Fragment searchFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);


        searchFragment = SearchFragmentNoClick.newInstance();

        mSectionPageAdapter=new SectionPageAdapter(getSupportFragmentManager());
        viewPager=findViewById(R.id.viewpager_on_click);
        setupViewPager(viewPager);

        TabLayout tabLayout =findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        searchViewModel=ViewModelProviders.of(this).get(SearchViewModel.class);


        setupUi();
        setupSearch();
    }





    private void setupUi() {
        getSupportFragmentManager().beginTransaction()
        .add(R.id.search_fragment_placeholder,searchFragment )
        .commit();
    }

    private void setupSearch() {
        searchView =  findViewById(R.id.search_view);
        tvSearch=findViewById(R.id.tv_search);

        tvSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchView.showSearch();
                setupFragments();
            }
        });



        searchView.setStartFromRight(false);
        searchView.setCursorDrawable(R.drawable.color_cursor_white);
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query)
            {
                searchViewModel.getSearchResults(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchViewModel.getSearchResults(newText);
                return true;
            }

            @Override
            public boolean onQueryClear() {
                return false;
            }
        });

        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {
            }

            @Override
            public void onSearchViewClosed() {
                setupUi();
            }
        });
    }

    private void setupFragments() {
        getSupportFragmentManager().beginTransaction()
                .remove(searchFragment)
                .commit();
    }

    private void setupViewPager(ViewPager viewpager) {
        SectionPageAdapter adapter =new SectionPageAdapter(getSupportFragmentManager());
        adapter.addFragment(new SearchFragmentAll(),"ALL");
        adapter.addFragment(new SearchFragmentPeople(),"PEOPLE");
        adapter.addFragment(new SearchFragmentRestaurant(),"RESTAURANT");
        viewpager.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search_activity, menu);

        MenuItem item = menu.findItem(R.id.action_search);
        searchView.setMenuItem(item);

        return true;
    }

    @Override
    public void onBackPressed() {
        if (searchView.isSearchOpen()) {
            searchView.closeSearch();
        } else {
            super.onBackPressed();
        }
    }

    public static class SectionPageAdapter extends FragmentPagerAdapter {

        private final List<Fragment> mFragmentList=new ArrayList<>();
        private final List<String> mFragmentTitleList=new ArrayList<>();

        public void addFragment(Fragment fragment,String title)
        {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        public SectionPageAdapter(FragmentManager fm) {
            super(fm);
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }
    }

}
