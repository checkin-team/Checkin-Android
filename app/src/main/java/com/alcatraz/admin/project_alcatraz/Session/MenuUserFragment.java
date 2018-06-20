package com.alcatraz.admin.project_alcatraz.Session;

import android.content.Context;
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
import com.alcatraz.admin.project_alcatraz.Utility.StartSnapHelper;

import java.util.ArrayList;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class MenuUserFragment extends Fragment implements MenuItemAdapter.OnItemClickListener {
    private final String TAG = MenuUserFragment.class.getSimpleName();

    @BindView(R.id.chips_item_list) RecyclerView chipsList;
    @BindView(R.id.groups_list) RecyclerView groupsList;
    private MenuChipAdapter mChipAdapter;
    private MenuGroupAdapter groupAdapter;
    private OnMenuFragmentInteractionListener mMenuInteractionListener;
    private Unbinder unbinder;

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
        Log.e(TAG, "onCreate");
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_menu_user, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        chipsList.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false));
        mChipAdapter = new MenuChipAdapter(getContext(), true, mMenuInteractionListener);
        chipsList.setAdapter(mChipAdapter);

        groupsList.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        groupsList.setLayoutManager(llm);

        groupAdapter = new MenuGroupAdapter(createDummyData(30), getContext());
        groupsList.setAdapter(groupAdapter);

        groupAdapter.setPriceClickListener(this);

        final StartSnapHelper snapHelper = new StartSnapHelper();
        snapHelper.attachToRecyclerView(groupsList);

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
        MenuChip menuChip = new MenuChip(item);
        Log.e("ItemOrdered", menuChip.getLabel() + " -> " + menuChip.getInfo());
        mChipAdapter.addChip(menuChip);
        Log.e(TAG, "ItemOrdered: " + areItemsPending());
    }

    public void onBackPressed() {
        groupAdapter.contractView(groupAdapter.mPrevExpandedViewHolder);
    }

    public boolean isGroupExpanded() {
        return groupAdapter.mPrevExpandedViewHolder != null;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mMenuInteractionListener = (OnMenuFragmentInteractionListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnMenuFragmentInteractionListener");
        }
    }

    public OrderedItem[] getOrderedItems() {
        if (areItemsPending())
            return mChipAdapter.getItems();
        return null;
    }

    public void clearPendingItems() {
        if (areItemsPending())
            mChipAdapter.clearItems();
    }

    public boolean areItemsPending() {
        if (mChipAdapter == null) {
            Log.e(TAG, "Chip Adapter is null?!");
            return false;//mChipAdapter = (MenuChipAdapter) chipsList.getAdapter();
        }
        Log.e(TAG, "Pending items: " + mChipAdapter.getItemCount());
        return mChipAdapter.getItemCount() > 0;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "onDestroy");
        unbinder.unbind();
    }

    public interface OnMenuFragmentInteractionListener {
        void onItemOrderInteraction(int count);
    }
}
