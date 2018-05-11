package com.alcatraz.admin.project_alcatraz.Session;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;


import com.alcatraz.admin.project_alcatraz.R;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.ArrayList;
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

        final View rootView = inflater.inflate(R.layout.fragment_menu_user, container, false);

        RecyclerView recList = rootView.findViewById(R.id.groups_list);
        recList.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recList.setLayoutManager(llm);

        GroupAdapter groupAdapter = new GroupAdapter(createDummyData(30));
        recList.setAdapter(groupAdapter);

        mToolbar = getActivity().findViewById(R.id.session_toolbar);

        return rootView;
    }

    private ArrayList<MenuGroup> createDummyData(int count) {
        ArrayList<MenuGroup> menuGroupList = new ArrayList<>();
        Drawable imDrawable = getResources().getDrawable(R.drawable.ic_beer);
        Bitmap imBitmap = ((BitmapDrawable) imDrawable).getBitmap();
        for (int i = 0; i < count; i++) {
            menuGroupList.add(new MenuGroup(String.format(Locale.US, "Beer #%d", i), null, imBitmap));
        }
        return menuGroupList;
    }

}


