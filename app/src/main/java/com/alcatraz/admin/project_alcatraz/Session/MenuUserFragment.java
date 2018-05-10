package com.alcatraz.admin.project_alcatraz.Session;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;


import com.alcatraz.admin.project_alcatraz.R;
import com.alcatraz.admin.project_alcatraz.Utility.SearchBar;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MenuUserFragment extends Fragment {

    private Toolbar mToolbar;

    public MenuUserFragment() {
        setHasOptionsMenu(true);
    }

    public static MenuUserFragment newInstance() {
        MenuUserFragment fragment = new MenuUserFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_menu_user, container, false);

        RecyclerView recList = rootView.findViewById(R.id.groups_list);
        recList.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recList.setLayoutManager(llm);

        GroupAdapter groupAdapter = new GroupAdapter(createDummyData(30));
        recList.setAdapter(groupAdapter);

        mToolbar = getActivity().findViewById(R.id.session_toolbar);
        initSearchBar();

        /*searchView.setOnSearchClickListener(new SearchView.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().findViewById(R.id.action_finish).setVisibility(View.GONE);
                getActivity().findViewById(R.id.session_appbar_title).setVisibility(View.GONE);
                circleReveal(R.id.session_toolbar, 1, true, false);
            }
        });
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                circleReveal(R.id.session_toolbar, 1, false, true);
                *//*getActivity().findViewById(R.id.action_finish).setVisibility(View.VISIBLE);
                getActivity().findViewById(R.id.session_appbar_title).setVisibility(View.VISIBLE);*//*
                return false;
            }
        });*/

        return rootView;
    }

    private List<MenuGroup> createDummyData(int count) {
        List<MenuGroup> menuGroupList = new ArrayList<>();
        Drawable imDrawable = getResources().getDrawable(R.drawable.coffee);
        Bitmap imBitmap = ((BitmapDrawable) imDrawable).getBitmap();
        for (int i = 0; i < count; i++) {
            menuGroupList.add(new MenuGroup(String.format(Locale.US, "Coffee #%d", i), null, imBitmap));
        }
        return menuGroupList;
    }

}


