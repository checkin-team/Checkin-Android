package com.checkin.app.checkin.Session;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.LongSparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.R;
import com.checkin.app.checkin.Utility.Constants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class MenuUserFragment extends Fragment implements MenuItemAdapter.OnItemInteractionListener {
    private final String TAG = MenuUserFragment.class.getSimpleName();

    public static final String MENU_KEY = "menu_key";
    public static final String CATEGORY_KEY = "category_key";

    //@BindView(R.id.menu_groups_list)
    RecyclerView rvGroupsList;
    @BindView(R.id.currentCategory) TextView currentCategory;
    //private MenuGroupAdapter groupAdapter;
    private MenuGroupFilterAdapter groupAdapter;
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
        rvGroupsList = rootView.findViewById(R.id.menu_groups_list);
        int menuId = 1;
        if (getArguments() != null)
            menuId = getArguments().getInt(MENU_KEY);

        LinearLayoutManager llm = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        rvGroupsList.setLayoutManager(llm);

        //groupAdapter = new MenuGroupAdapter(null, getContext(), this);
        groupAdapter = new MenuGroupFilterAdapter(null,getContext(),this);
        rvGroupsList.setAdapter(groupAdapter);
//        groupAdapter.setGroupList(mViewModel.getGroups());

        rvGroupsList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager = ((LinearLayoutManager)recyclerView.getLayoutManager());
                int firstVisiblePosition = layoutManager.findFirstVisibleItemPosition();
                if(groupAdapter.getItemViewType(firstVisiblePosition) == MenuGroupFilterAdapter.VIEW_TYPE_CATEGORY){
                    currentCategory.setText((String) groupAdapter.getItem(firstVisiblePosition));
                }else if(groupAdapter.getItemViewType(firstVisiblePosition) == MenuGroupFilterAdapter.VIEW_TYPE_GROUP){
                    currentCategory.setText(((MenuGroupModel) groupAdapter.getItem(firstVisiblePosition)).getCategory());
                }
            }
        });

//        final StartSnapHelper snapHelper = new StartSnapHelper();
//        snapHelper.attachToRecyclerView(rvGroupsList);


        mViewModel.getMenuGroups(menuId).observe(getActivity(), menuGroupResource -> {
            if (menuGroupResource == null)
                return;
            Log.i("bhavik","menu group status = " + menuGroupResource.status);
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

    public void scrollToCategory(String title){
        RecyclerView.SmoothScroller smoothScroller = new
                LinearSmoothScroller(getContext()) {
                    @Override protected int getVerticalSnapPreference() {
                        return LinearSmoothScroller.SNAP_TO_START;
                    }
                };
        int catePosi = getCategoryPosition(title);
        smoothScroller.setTargetPosition(catePosi == 0 ? 0 : catePosi + 1);
        rvGroupsList.getLayoutManager().startSmoothScroll(smoothScroller);
    }

    public int getCategoryPosition(String title){
        return groupAdapter.getCategoryPosition(title);
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
    public boolean onItemAdded(View view, MenuItemModel item) {
        if (!mSessionActive)
            return false;
        mMenuInteractionListener.onItemOrderInteraction(item, 1);
        return true;
    }

    @Override
    public boolean onItemLongPress(View view, MenuItemModel item) {
        mMenuInteractionListener.onItemShowInfo(item);
        return true;
    }

    @Override
    public boolean onItemChanged(MenuItemModel item, int count) {
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
        void onItemOrderInteraction(MenuItemModel item, int count);
        void onItemShowInfo(MenuItemModel item);
    }


}
