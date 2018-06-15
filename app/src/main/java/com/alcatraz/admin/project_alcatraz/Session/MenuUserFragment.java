package com.alcatraz.admin.project_alcatraz.Session;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.alcatraz.admin.project_alcatraz.R;
import com.alcatraz.admin.project_alcatraz.Utility.ItemClickSupport;
import com.alcatraz.admin.project_alcatraz.Utility.StartSnapHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

public class MenuUserFragment extends Fragment implements MenuItemAdapter.OnItemClickListener {

    private MenuChipAdapter mChipAdapter;
    private MenuGroupAdapter groupAdapter;

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
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_menu_user, container, false);

        final RecyclerView chipsList = rootView.findViewById(R.id.chips_item_list);
        chipsList.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false));
        mChipAdapter = new MenuChipAdapter();
        chipsList.setAdapter(mChipAdapter);

        final RecyclerView recList = rootView.findViewById(R.id.groups_list);
        recList.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        recList.setLayoutManager(llm);

        groupAdapter = new MenuGroupAdapter(createDummyData(30));
        recList.setAdapter(groupAdapter);

        groupAdapter.setPriceClickListener(this);

        final StartSnapHelper snapHelper = new StartSnapHelper();
        snapHelper.attachToRecyclerView(recList);

        return rootView;
    }

    private ArrayList<MenuGroup> createDummyData(int count) {
        ArrayList<MenuGroup> menuGroupList = new ArrayList<>();
        Drawable imDrawable = getResources().getDrawable(R.drawable.ic_beer);
        Bitmap imBitmap = ((BitmapDrawable) imDrawable).getBitmap();
        for (int i = 1; i <= count; i++) {
            ArrayList<MenuItem> menuItems = new ArrayList<>();
            for (int j = 0; j <= i/2; j++) {
                float[] costs = {300, 150, 100};
                String[] types = {"Pint", "Bottle", "Can"};
                menuItems.add(new MenuItem("Carlsburg #" + j, types, costs));
            }
            menuGroupList.add(new MenuGroup(String.format(Locale.US, "Beer #%d", i), menuItems, imBitmap));
        }
        return menuGroupList;
    }

    @Override
    public void onItemOrdered(View view, OrderedItem item) {
        MenuChip menuChip = new MenuChip(item.title, item.quantity);
        Log.e("ItemOrdered", menuChip.title + " X " + menuChip.count);
        mChipAdapter.addChip(menuChip);
    }

    public void onBackPressed() {
        groupAdapter.contractView(groupAdapter.mPrevExpandedViewHolder);
    }

    public boolean isGroupExpanded() {
        return groupAdapter.mPrevExpandedViewHolder != null;
    }
}
