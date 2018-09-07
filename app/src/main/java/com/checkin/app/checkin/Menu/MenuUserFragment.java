package com.checkin.app.checkin.Menu;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.LongSparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.R;
import com.checkin.app.checkin.Utility.Constants;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class MenuUserFragment extends Fragment implements MenuItemAdapter.OnItemInteractionListener {
    private final String TAG = MenuUserFragment.class.getSimpleName();

    @BindView(R.id.menu_groups_list) RecyclerView rvGroupsList;
    private MenuInteractionListener mMenuInteractionListener;
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

        if (getActivity() == null)
            return;
        mViewModel = ViewModelProviders.of(getActivity(), new MenuViewModel.Factory(getActivity().getApplication())).get(MenuViewModel.class);
        Bundle args = getArguments();
        if (args == null) {
            Log.e(TAG, "No Serializable arguments sent to " + MenuUserFragment.class.getSimpleName());
            return;
        }
        STATUS_VALUES sessionStatus = (STATUS_VALUES) args.getSerializable(SESSION_STATUS);

        if (sessionStatus == STATUS_VALUES.INACTIVE)
            mSessionActive = false;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_menu_user, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        if (getActivity() == null || getArguments() == null)
            return null;

        LinearLayoutManager llm = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        rvGroupsList.setLayoutManager(llm);

        rvGroupsList.setAdapter(new MenuGroupAdapter(null, getContext(), this));

        long shopId = getArguments().getLong(Constants.SHOP_ID);
        mViewModel.getMenuGroups(shopId).observe(getActivity(), menuGroupResource -> {
            if (menuGroupResource == null)
                return;
            if (menuGroupResource.status == Resource.Status.SUCCESS)
                ((MenuGroupAdapter) rvGroupsList.getAdapter()).setGroupList(menuGroupResource.data);
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
        mViewModel.getCurrentItem().observe(getActivity(), orderedItem -> {
            if (orderedItem == null)    return;
            MenuItemAdapter.ItemViewHolder holder = orderedItem.getItem().getItemHolder();
            if (holder != null && holder.getMenuItem() == orderedItem.getItem()) {
                Log.e(TAG, "holder: " + holder.vQuantityPicker.getCurrentItem() + ", item: " + orderedItem.getQuantity());
                holder.changeQuantity(mViewModel.getOrderedCount(orderedItem.getItem()) + orderedItem.getChangeCount());
            }
        });
        return rootView;
    }

    public void onBackPressed() {
        ((MenuGroupAdapter) rvGroupsList.getAdapter()).contractView();
    }

    public boolean isGroupExpanded() {
        return ((MenuGroupAdapter) rvGroupsList.getAdapter()).isGroupExpanded();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mMenuInteractionListener = (MenuInteractionListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement MenuInteractionListener");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    @Override
    public boolean onItemAdded(MenuItemAdapter.ItemViewHolder holder) {
        if (!mSessionActive)
            return false;
        MenuItemModel item = holder.getMenuItem();
        mViewModel.newOrderedItem(item);
        mMenuInteractionListener.onItemInteraction(item, 1);
        return true;
    }

    @Override
    public boolean onItemLongPress(MenuItemModel item) {
        mMenuInteractionListener.onItemShowInfo(item);
        return true;
    }

    @Override
    public boolean onItemChanged(MenuItemAdapter.ItemViewHolder holder, int count) {
        if (!mSessionActive)
            return false;
        MenuItemModel item = holder.getMenuItem();
        if (mViewModel.updateOrderedItem(item, count)) {
            mMenuInteractionListener.onItemInteraction(item, count);
        }
        return true;
    }

    @Override
    public int orderedItemCount(MenuItemModel item) {
        return mViewModel.getOrderedCount(item);
    }

    public interface MenuInteractionListener {
        void onItemInteraction(MenuItemModel item, int count);
        void onItemShowInfo(MenuItemModel item);
    }
}
