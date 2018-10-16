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

import com.checkin.app.checkin.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class MenuSearchFragment extends Fragment implements MenuItemAdapter.OnItemInteractionListener  {
    private final String TAG = MenuSearchFragment.class.getSimpleName();

    @BindView(R.id.menu_groups_list)
    RecyclerView rvGroupsList;
    private MenuInteractionListener mMenuInteractionListener;
    private Unbinder unbinder;
    private MenuViewModel mViewModel;
    private MenuItemAdapter.OnItemInteractionListener itemInteractionListener;
    String queryG;
    private MenuItemAdapter mItemAdapter;
    private boolean mSessionActive = true;
    private LongSparseArray<Integer> orderedItemsCount;

    public MenuSearchFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getActivity() == null)
            return;
        Bundle args = getArguments();
        if (args == null) {
            Log.e(TAG, "No Serializable arguments sent to " + MenuSearchFragment.class.getSimpleName());
            return;
        }


    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_search_item, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        if (getActivity() == null||getArguments() == null)
            return null;

        LinearLayoutManager llm = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        rvGroupsList.setLayoutManager(llm);
        mViewModel = ViewModelProviders.of(getActivity(), new MenuViewModel.Factory(getActivity().getApplication())).get(MenuViewModel.class);


        rvGroupsList.setAdapter(mItemAdapter);

        rvGroupsList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

            }
        });
        queryG=getArguments().getString("query");
        Log.e(TAG,queryG);
        mViewModel.search(queryG);
        boolean mSessionStatus=getArguments().getBoolean("SessionStatus");

        mViewModel.getMenuItems().observe(getActivity(),menuItemModels -> {
            if(menuItemModels==null)
            {
                Log.e(TAG,"Kuch nhi mila");
                return;
            }
            else
            {   Log.e(TAG,"Itna mila"+ menuItemModels.size());
                mItemAdapter = new MenuItemAdapter(menuItemModels);
            }
        });

        rvGroupsList.setAdapter(mItemAdapter);
        mItemAdapter.setActivate(mSessionStatus);
        mItemAdapter.setItemInteractionListener(this);

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
        mMenuInteractionListener.onItemInteraction1(item, 1);
        return true;
    }

    @Override
    public boolean onItemLongPress(MenuItemModel item) {
        mMenuInteractionListener.onItemShowInfo1(item);
        return true;
    }

    @Override
    public boolean onItemChanged(MenuItemAdapter.ItemViewHolder holder, int count) {
        if (!mSessionActive)
            return false;
        MenuItemModel item = holder.getMenuItem();
        if (mViewModel.updateOrderedItem(item, count)) {
            mMenuInteractionListener.onItemInteraction1(item, count);
        }
        return true;
    }

    @Override
    public int orderedItemCount(MenuItemModel item) {
        return mViewModel.getOrderedCount(item);
    }


    public interface MenuInteractionListener

    {


        void onItemInteraction1 (MenuItemModel item,int count);

        void onItemShowInfo1 (MenuItemModel item);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {


    }

}
