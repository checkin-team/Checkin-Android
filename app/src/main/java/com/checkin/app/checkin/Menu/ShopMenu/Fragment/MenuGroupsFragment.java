package com.checkin.app.checkin.Menu.ShopMenu.Fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.Menu.ShopMenu.Adapter.MenuGroupAdapter;
import com.checkin.app.checkin.Menu.ShopMenu.Adapter.MenuItemAdapter;
import com.checkin.app.checkin.Menu.MenuItemInteraction;
import com.checkin.app.checkin.Menu.ShopMenu.MenuViewModel;
import com.checkin.app.checkin.Menu.Model.MenuGroupModel;
import com.checkin.app.checkin.misc.fragments.BaseFragment;
import com.checkin.app.checkin.R;
import com.checkin.app.checkin.Utility.Utils;
import com.facebook.shimmer.ShimmerFrameLayout;

import java.util.List;

import butterknife.BindView;

public class MenuGroupsFragment extends BaseFragment {
    public static final String KEY_SESSION_STATUS = "menu.status";
    @BindView(R.id.shimmer_menu_group)
    ShimmerFrameLayout shimmerMenu;
    @BindView(R.id.rv_menu_groups)
    RecyclerView rvGroupsList;
    @BindView(R.id.tv_menu_current_category)
    TextView tvCurrentCategory;
    private MenuViewModel mViewModel;
    private MenuGroupAdapter mAdapter;
    @Nullable
    private MenuItemInteraction mListener;
    private boolean mIsSessionActive = true;

    public MenuGroupsFragment() {
    }

    public static MenuGroupsFragment newInstance(SESSION_STATUS sessionStatus, MenuItemInteraction listener) {
        MenuGroupsFragment fragment = new MenuGroupsFragment();
        fragment.mIsSessionActive = (sessionStatus == SESSION_STATUS.ACTIVE);
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

        mViewModel = ViewModelProviders.of(requireActivity()).get(MenuViewModel.class);

        mViewModel.getMenuGroups().observe(this, menuGroupResource -> {
            if (menuGroupResource == null)
                return;
            if (menuGroupResource.getStatus() == Resource.Status.SUCCESS && !menuGroupResource.isCached()) {
                mAdapter.setGroupList(menuGroupResource.getData());
                stopRefreshing();
                setupData(menuGroupResource.getData());
            } else if (menuGroupResource.getStatus() == Resource.Status.LOADING) {
                startRefreshing();
                if (!mAdapter.hasData() && menuGroupResource.getData() != null)
                    setupData(menuGroupResource.getData());
            } else {
                stopRefreshing();
                Utils.toast(requireContext(), menuGroupResource.getMessage());
            }
        });

        mViewModel.getCurrentItem().observe(this, orderedItem -> {
            if (orderedItem == null) return;
            MenuItemAdapter.ItemViewHolder holder = orderedItem.getItemModel().getItemHolder();

            if (holder != null && holder.getMenuItem() == orderedItem.getItemModel()) {
                holder.changeQuantity(mViewModel.getOrderedCount(orderedItem.getItemModel()));
            }
        });
    }

    private void setupData(List<MenuGroupModel> data) {
        mAdapter.setGroupList(data);
        if (shimmerMenu.getVisibility() == View.VISIBLE) {
            shimmerMenu.stopShimmer();
            shimmerMenu.setVisibility(View.GONE);
        }
    }

    private void setupGroupRecycler() {
        rvGroupsList.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));

        mAdapter = new MenuGroupAdapter(null, requireFragmentManager(), mListener);
        mAdapter.setSessionActive(mIsSessionActive);
        rvGroupsList.setAdapter(mAdapter);

        rvGroupsList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
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
        super.updateScreen();
        mViewModel.updateResults();
    }

    public enum SESSION_STATUS {
        ACTIVE, INACTIVE
    }
}
