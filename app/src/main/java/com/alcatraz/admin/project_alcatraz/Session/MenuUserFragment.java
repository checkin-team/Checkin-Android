package com.alcatraz.admin.project_alcatraz.Session;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.LongSparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.alcatraz.admin.project_alcatraz.Data.Resource;
import com.alcatraz.admin.project_alcatraz.R;
import com.alcatraz.admin.project_alcatraz.Utility.Constants;
import com.alcatraz.admin.project_alcatraz.Utility.StartSnapHelper;
import com.alcatraz.admin.project_alcatraz.Utility.Util;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class MenuUserFragment extends Fragment implements MenuItemAdapter.OnItemInteractionListener {
    private final String TAG = MenuUserFragment.class.getSimpleName();

    public static final String MENU_KEY = "menu_key";

    @BindView(R.id.menu_groups_list) RecyclerView rvGroupsList;
    private MenuGroupAdapter groupAdapter;
    private OnMenuFragmentInteractionListener mMenuInteractionListener;
    private Unbinder unbinder;
    private MenuViewModel mViewModel;
    private boolean mSessionActive = true;
    private LongSparseArray<Integer> orderedItemsCount;

    public static final String SESSION_STATUS = "status";
    public enum STATUS_VALUES {
        STARTED, RESUMED, INACTIVE
    }

    public MenuUserFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mViewModel = ViewModelProviders.of(getActivity(), new MenuViewModel.Factory(getActivity().getApplication())).get(MenuViewModel.class);
        Bundle args = getArguments();
        if (args == null) {
            Log.e(TAG, "No Serializable arguments sent to " + MenuUserFragment.class.getSimpleName());
            return;
        }
        STATUS_VALUES sessionStatus = (STATUS_VALUES) args.getSerializable(SESSION_STATUS);
        int shopId = args.getInt(Constants.SHOP_ID);
        int menuId = args.getInt(MENU_KEY);

        if (sessionStatus == STATUS_VALUES.INACTIVE)
            mSessionActive = false;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_menu_user, container, false);
        unbinder = ButterKnife.bind(this, rootView);

        int menuId = 1;
        if (getArguments() != null)
            menuId = getArguments().getInt(MENU_KEY);

        LinearLayoutManager llm = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        rvGroupsList.setLayoutManager(llm);

        groupAdapter = new MenuGroupAdapter(null, getContext(), this);
        rvGroupsList.setAdapter(groupAdapter);

        final StartSnapHelper snapHelper = new StartSnapHelper();
        snapHelper.attachToRecyclerView(rvGroupsList);

        mViewModel.getMenuGroups(menuId).observe(getActivity(), menuGroupResource -> {
            if (menuGroupResource == null)
                return;
            if (menuGroupResource.status == Resource.Status.SUCCESS)
                groupAdapter.setGroupList(menuGroupResource.data);
            else if (menuGroupResource.status == Resource.Status.LOADING) {
                // TODO: DO some Loading magic!
            } else {
                Toast.makeText(
                        MenuUserFragment.this.getContext(),
                        "An error occurred: " + menuGroupResource.message,
                        Toast.LENGTH_SHORT
                ).show();
            }
        });
        mViewModel.getOrderedItemsCount().observe(getActivity(), data -> {
            if (data == null)
                return;
            orderedItemsCount = data;
        });

        return rootView;
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    @Override
    public boolean onItemAdded(View view, MenuItem item) {
        if (!mSessionActive)
            return false;
        mMenuInteractionListener.onItemOrderInteraction(item, 1);
        return true;
    }

    @Override
    public boolean onItemLongPress(View view, MenuItem item) {
        mMenuInteractionListener.onItemShowInfo(item);
        return true;
    }

    @Override
    public boolean onItemChanged(MenuItem item, int count) {
        if (!mSessionActive)
            return false;
        mMenuInteractionListener.onItemOrderInteraction(item, count);
        return true;
    }

    @Override
    public LongSparseArray<Integer> orderedItemsCount() {
        return orderedItemsCount;
    }

    public interface OnMenuFragmentInteractionListener {
        void onItemOrderInteraction(MenuItem item, int count);
        void onItemShowInfo(MenuItem item);
    }
}
