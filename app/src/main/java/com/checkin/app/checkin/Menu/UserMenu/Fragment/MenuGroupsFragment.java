package com.checkin.app.checkin.Menu.UserMenu.Fragment;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.Menu.UserMenu.Adapter.MenuGroupAdapter;
import com.checkin.app.checkin.Menu.UserMenu.Adapter.MenuItemAdapter;
import com.checkin.app.checkin.Menu.MenuItemInteraction;
import com.checkin.app.checkin.Menu.UserMenu.MenuViewModel;
import com.checkin.app.checkin.Menu.Model.MenuGroupModel;
import com.checkin.app.checkin.Misc.BaseFragment;
import com.checkin.app.checkin.R;
import com.checkin.app.checkin.Utility.Utils;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.miguelcatalan.materialsearchview.utils.AnimationUtil;

import java.util.List;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import butterknife.BindView;
import butterknife.OnClick;

public class MenuGroupsFragment extends BaseFragment implements MenuGroupAdapter.OnGroupInteractionInterface {
    public static final String KEY_SESSION_STATUS = "menu.status";

    @BindView(R.id.rv_menu_groups)
    RecyclerView rvGroupsList;
    @BindView(R.id.container_as_menu_current_category)
    ViewGroup containerCurrentCategory;
    @BindView(R.id.tv_as_menu_current_category)
    TextView tvCurrentCategory;
    @BindView(R.id.shimmer_as_menu_group)
    ShimmerFrameLayout shimmerMenu;
    @BindView(R.id.sr_session_menu)
    SwipeRefreshLayout swipeRefreshLayout;

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
        return R.layout.fragment_as_menu_group;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        setupGroupRecycler();
        initRefreshScreen(R.id.sr_session_menu);

        mViewModel = ViewModelProviders.of(requireActivity()).get(MenuViewModel.class);

        mViewModel.getMenuGroups().observe(this, menuGroupResource -> {
            if (menuGroupResource == null)
                return;
            if (menuGroupResource.status == Resource.Status.SUCCESS && !menuGroupResource.isCached()) {
                stopRefreshing();
                setupData(menuGroupResource.data);
            } else if (menuGroupResource.status == Resource.Status.LOADING) {
                startRefreshing();
                if (!mAdapter.hasData() && menuGroupResource.data != null)
                    setupData(menuGroupResource.data);
            } else {
                stopRefreshing();
                Utils.toast(requireContext(), menuGroupResource.message);
            }
        });

        mViewModel.getCurrentItem().observe(this, orderedItem -> {
            if (orderedItem == null) return;
            MenuItemAdapter.ItemViewHolder holder = orderedItem.getItemModel().getASItemHolder();

            if (holder != null && holder.getMenuItem() == orderedItem.getItemModel()) {
                holder.changeQuantity(mViewModel.getOrderedCount(orderedItem.getItemModel()) + orderedItem.getChangeCount());
            }
        });
    }

    private void setupData(List<MenuGroupModel> data) {
        mAdapter.setGroupList(data);
        if (shimmerMenu.getVisibility() == View.VISIBLE) {
            shimmerMenu.stopShimmer();
            shimmerMenu.setVisibility(View.GONE);
        }
        containerCurrentCategory.setVisibility(View.GONE);
    }

    private void setupGroupRecycler() {
        rvGroupsList.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));

        mAdapter = new MenuGroupAdapter(null, requireFragmentManager(), mListener, this);
        mAdapter.setSessionActive(mIsSessionActive);
        rvGroupsList.setAdapter(mAdapter);
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
        rvGroupsList.smoothScrollToPosition(mAdapter.getGroupPosition(title));
//        mAdapter.expandView();
    }

    @Override
    protected void updateScreen() {
        mViewModel.updateResults();
    }

    @Override
    public void onGroupExpandCollapse(boolean isExpanded, MenuGroupModel groupModel) {
        if (isExpanded) {
            AnimationUtil.fadeInView(containerCurrentCategory);
            tvCurrentCategory.setText(groupModel.getName());
        } else {
            AnimationUtil.fadeOutView(containerCurrentCategory);
        }
    }

    @OnClick(R.id.tv_as_menu_current_category)
    public void onStickyGroup() {
        mAdapter.contractView();
    }

    public enum SESSION_STATUS {
        ACTIVE, INACTIVE
    }
}
