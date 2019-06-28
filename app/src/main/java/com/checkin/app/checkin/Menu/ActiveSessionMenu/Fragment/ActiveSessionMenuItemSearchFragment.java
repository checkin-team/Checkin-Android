package com.checkin.app.checkin.Menu.ActiveSessionMenu.Fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.checkin.app.checkin.Data.Resource.Status;
import com.checkin.app.checkin.Menu.MenuItemInteraction;
import com.checkin.app.checkin.Menu.MenuViewModel;
import com.checkin.app.checkin.Menu.Model.MenuItemModel;
import com.checkin.app.checkin.Menu.ActiveSessionMenu.Adapter.ActiveSessionMenuItemAdapter;
import com.checkin.app.checkin.Menu.SessionMenuActivity;
import com.checkin.app.checkin.Misc.BaseSearchFragment;
import com.checkin.app.checkin.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class ActiveSessionMenuItemSearchFragment extends BaseSearchFragment implements ActiveSessionMenuItemAdapter.OnItemInteractionListener {
    private static final String TAG = ActiveSessionMenuItemSearchFragment.class.getSimpleName();
    @BindView(R.id.rv_menu_items)
    RecyclerView rvMenuItems;
    private Unbinder unbinder;
    private MenuViewModel mViewModel;
    private ActiveSessionMenuItemAdapter mAdapter;
    private MenuItemInteraction mListener;

    public ActiveSessionMenuItemSearchFragment() {
    }

    public static ActiveSessionMenuItemSearchFragment newInstance(MenuItemInteraction listener, boolean isSessionActive) {
        ActiveSessionMenuItemSearchFragment fragment = new ActiveSessionMenuItemSearchFragment();
        fragment.mListener = listener;
        fragment.mAdapter = new ActiveSessionMenuItemAdapter(null, fragment, isSessionActive);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_as_menu_items_search, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    public void resetResults() {
        mAdapter.setMenuItems(null);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        view.setOnTouchListener((v, event) -> {
            ((SessionMenuActivity) requireActivity()).closeSearch();
            return true;
        });

        rvMenuItems.setAdapter(mAdapter);
        rvMenuItems.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));

        mViewModel = ViewModelProviders.of(requireActivity()).get(MenuViewModel.class);
        mViewModel.getFilteredMenuItems().observe(this, listResource -> {
            if (listResource == null)
                return;
            if (listResource.status == Status.SUCCESS && listResource.data != null) {
                mAdapter.setMenuItems(listResource.data);
                this.hideLoadProgress();
            } else if (listResource.status == Status.LOADING) {
                this.showLoadProgress();
                this.resetResults();
            } else if (listResource.status == Status.ERROR_NOT_FOUND) {
                this.noResultFound();
            } else if (listResource.status == Status.NO_REQUEST) {
                this.resetResults();
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    @Override
    public boolean onItemAdded(MenuItemModel item) {
        return mListener.onMenuItemAdded(item);
    }

    @Override
    public boolean onItemLongPress(MenuItemModel item) {
        mListener.onMenuItemShowInfo(item);
        return true;
    }

    @Override
    public boolean onItemChanged(MenuItemModel item, int count) {
        return mListener.onMenuItemChanged(item, count);
    }

    @Override
    public int orderedItemCount(MenuItemModel item) {
        return mListener.getItemOrderedCount(item);
    }
}
