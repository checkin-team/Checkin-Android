package com.checkin.app.checkin.Inventory.Fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.Inventory.Adapter.InventoryGroupAdapter;
import com.checkin.app.checkin.Inventory.Adapter.InventoryItemAdapter;
import com.checkin.app.checkin.Inventory.InventoryViewModel;
import com.checkin.app.checkin.Inventory.Model.InventoryItemModel;
import com.checkin.app.checkin.misc.fragments.BaseFragment;
import com.checkin.app.checkin.R;
import com.checkin.app.checkin.Utility.Utils;
import com.facebook.shimmer.ShimmerFrameLayout;

import java.util.List;

import butterknife.BindView;

public class InventoryGroupsFragment extends BaseFragment implements InventoryGroupAdapter.GroupInteraction {
    @BindView(R.id.rv_menu_groups)
    RecyclerView rvGroupsList;
    @BindView(R.id.tv_menu_current_category)
    TextView tvCurrentCategory;
    @BindView(R.id.shimmer_menu_group)
    ShimmerFrameLayout shimmerFrameLayout;

    private InventoryViewModel mViewModel;
    private InventoryGroupAdapter mAdapter;

    @Nullable
    private InventoryItemAdapter.OnItemInteractionListener mListener;

    public InventoryGroupsFragment() {
    }

    public static InventoryGroupsFragment newInstance(InventoryItemAdapter.OnItemInteractionListener listener) {
        InventoryGroupsFragment fragment = new InventoryGroupsFragment();
        fragment.mListener = listener;
        return fragment;
    }

    @Override
    protected int getRootLayout() {
        return R.layout.fragment_menu_groups;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        setupGroupRecycler();
        initRefreshScreen(R.id.sr_session_menu);

        mViewModel = ViewModelProviders.of(requireActivity()).get(InventoryViewModel.class);

        mViewModel.getMenuGroups().observe(this, menuGroupResource -> {
            if (menuGroupResource == null)
                return;
            if (menuGroupResource.getStatus() == Resource.Status.SUCCESS) {
                mAdapter.setGroupList(menuGroupResource.getData());
                stopRefreshing();
                if (shimmerFrameLayout.getVisibility() == View.VISIBLE) {
                    shimmerFrameLayout.stopShimmer();
                    shimmerFrameLayout.setVisibility(View.GONE);
                }
            } else if (menuGroupResource.getStatus() == Resource.Status.LOADING) {
                startRefreshing();
                if (menuGroupResource.getData() != null)
                    mAdapter.setGroupList(menuGroupResource.getData());
            } else {
                stopRefreshing();
                Utils.toast(requireContext(), menuGroupResource.getMessage());
            }
        });

        mViewModel.getMenuItemAvailabilityData().observe(this, listResource -> {
            if (listResource == null)
                return;
            if (listResource.getStatus() == Resource.Status.SUCCESS) {
                mViewModel.updateUiItemListAvailability(listResource.getData());
            } else if (listResource.getStatus() != Resource.Status.LOADING) {
                Utils.toast(requireContext(), listResource.getMessage());
            }
        });
    }

    private void setupGroupRecycler() {
        rvGroupsList.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        mAdapter = new InventoryGroupAdapter(null, requireFragmentManager(), mListener, this);
        mAdapter.setSessionActive(false);
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

    @Override
    public void updateScreen() {
        mViewModel.updateResults();
    }

    @Override
    public void onGroupAvailability(List<InventoryItemModel> items, boolean isChecked) {
        mViewModel.confirmMenuItemAvailability(items, isChecked);
    }
}
