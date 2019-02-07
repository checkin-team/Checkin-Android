package com.checkin.app.checkin.Menu.Fragment;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.Menu.Adapter.MenuGroupAdapter;
import com.checkin.app.checkin.Menu.Adapter.MenuItemAdapter;
import com.checkin.app.checkin.Menu.MenuItemInteraction;
import com.checkin.app.checkin.Menu.MenuViewModel;
import com.checkin.app.checkin.Misc.BaseFragment;
import com.checkin.app.checkin.R;
import com.checkin.app.checkin.Utility.Utils;

import butterknife.BindView;

public class MenuGroupsFragment extends BaseFragment {
    @BindView(R.id.rv_menu_groups)
    RecyclerView rvGroupsList;
    @BindView(R.id.tv_menu_current_category)
    TextView tvCurrentCategory;

    private MenuViewModel mViewModel;
    private MenuGroupAdapter mAdapter;

    @Nullable
    private MenuItemInteraction mListener;
    private boolean mIsSessionActive = true;

    public static final String KEY_SESSION_STATUS = "menu.status";

    public enum SESSION_STATUS {
        ACTIVE, INACTIVE
    }

    @Override
    protected int getRootLayout() {
        return R.layout.fragment_menu_groups;
    }

    public static MenuGroupsFragment newInstance(SESSION_STATUS sessionStatus, MenuItemInteraction listener) {
        MenuGroupsFragment fragment = new MenuGroupsFragment();
        fragment.mIsSessionActive = (sessionStatus == SESSION_STATUS.ACTIVE);
        fragment.mListener = listener;
        return fragment;
    }

    public MenuGroupsFragment() {
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        setupGroupRecycler();
        initRefreshScreen(R.id.sr_session_menu);

        mViewModel = ViewModelProviders.of(requireActivity()).get(MenuViewModel.class);

        mViewModel.getMenuGroups().observe(this, menuGroupResource -> {
            if (menuGroupResource == null)
                return;
            if (menuGroupResource.status == Resource.Status.SUCCESS) {
                mAdapter.setGroupList(menuGroupResource.data);
                stopRefreshing();
            } else if (menuGroupResource.status == Resource.Status.LOADING) {
                startRefreshing();
            } else {
                stopRefreshing();
                Utils.toast(requireContext(), menuGroupResource.message);
            }
        });

        mViewModel.getCurrentItem().observe(this, orderedItem -> {
            if (orderedItem == null) return;
            MenuItemAdapter.ItemViewHolder holder = orderedItem.getItemModel().getItemHolder();

            if (holder != null && holder.getMenuItem() == orderedItem.getItemModel()) {
                holder.changeQuantity(mViewModel.getOrderedCount(orderedItem.getItemModel()) + orderedItem.getChangeCount());
            }
        });
    }

    private void setupGroupRecycler() {
        rvGroupsList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        mAdapter = new MenuGroupAdapter(null, requireFragmentManager(), mListener);
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
}
