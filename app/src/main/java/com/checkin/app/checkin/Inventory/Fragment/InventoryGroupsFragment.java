package com.checkin.app.checkin.Inventory.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.Inventory.Adapter.InventoryGroupAdapter;
import com.checkin.app.checkin.Inventory.Adapter.InventoryItemAdapter;
import com.checkin.app.checkin.Inventory.InventoryMenuItemInteraction;
import com.checkin.app.checkin.Inventory.InventoryViewModel;
import com.checkin.app.checkin.Inventory.Model.InventoryItemModel;
import com.checkin.app.checkin.Menu.Adapter.MenuGroupAdapter;
import com.checkin.app.checkin.Menu.Adapter.MenuItemAdapter;
import com.checkin.app.checkin.Menu.MenuItemInteraction;
import com.checkin.app.checkin.Menu.MenuViewModel;
import com.checkin.app.checkin.Misc.BaseFragment;
import com.checkin.app.checkin.R;
import com.checkin.app.checkin.Utility.Utils;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;

public class InventoryGroupsFragment extends BaseFragment implements InventoryGroupAdapter.GroupInteraction {
    private static final String TAG = InventoryGroupsFragment.class.getSimpleName();
    @BindView(R.id.rv_menu_groups)
    RecyclerView rvGroupsList;
    @BindView(R.id.tv_menu_current_category)
    TextView tvCurrentCategory;

    private InventoryViewModel mViewModel;
    private InventoryGroupAdapter mAdapter;

    @Nullable
    private InventoryMenuItemInteraction mListener;
    private boolean mIsSessionActive = true;

    public static final String KEY_SESSION_STATUS = "menu.status";


    public enum SESSION_STATUS {
        ACTIVE, INACTIVE
    }

    @Override
    protected int getRootLayout() {
        return R.layout.fragment_menu_groups;
    }

    public static InventoryGroupsFragment newInstance(SESSION_STATUS sessionStatus, InventoryMenuItemInteraction listener) {
        InventoryGroupsFragment fragment = new InventoryGroupsFragment();
        fragment.mIsSessionActive = (sessionStatus == SESSION_STATUS.ACTIVE);
        fragment.mListener = listener;
        return fragment;
    }

    public InventoryGroupsFragment() {
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        setupGroupRecycler();
        initRefreshScreen(R.id.sr_session_menu);

        mViewModel = ViewModelProviders.of(requireActivity()).get(InventoryViewModel.class);

        mViewModel.getMenuGroups().observe(this, menuGroupResource -> {
            if (menuGroupResource == null)
                return;
            if (menuGroupResource.status == Resource.Status.SUCCESS) {
                mAdapter.setGroupList(menuGroupResource.data);
                stopRefreshing();
            } else if (menuGroupResource.status == Resource.Status.LOADING) {
                startRefreshing();
                if (menuGroupResource.data != null)
                    mAdapter.setGroupList(menuGroupResource.data);
            } else {
                stopRefreshing();
                Utils.toast(requireContext(), menuGroupResource.message);
            }
        });

        mViewModel.getMenuItemAvailabilityData().observe(this,listResource -> {
            if (listResource == null)
                return;
            if (listResource.status == Resource.Status.SUCCESS) {

            } else if (listResource.status == Resource.Status.LOADING) {
            }
        });
    }

    private void setupGroupRecycler() {
        rvGroupsList.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        mAdapter = new InventoryGroupAdapter(null, requireFragmentManager(), mListener, this);
        mAdapter.setSessionActive(mIsSessionActive);
        rvGroupsList.setAdapter(mAdapter);

        rvGroupsList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                tvCurrentCategory.setText(mAdapter.getCurrentCategory());
            }
        });
    }

    public boolean onBackPressed() {
        if (isGroupExpanded()) {
            mAdapter.contractView();
            return true;
        }
        return false;
    }

    private boolean isGroupExpanded() {
        return mAdapter.isGroupExpanded();
    }

    public void scrollToCategory(String title) {
        rvGroupsList.smoothScrollToPosition(mAdapter.getCategoryPosition(title));
    }

    @Override
    protected void updateScreen() {
        mViewModel.updateResults();
    }

    @Override
    public void onGroupAvailability(List<InventoryItemModel> items, boolean isChecked) {
        mViewModel.confirmMenuItemAvailability(items, isChecked);
    }

}
