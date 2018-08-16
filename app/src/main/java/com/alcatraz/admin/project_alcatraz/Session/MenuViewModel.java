package com.alcatraz.admin.project_alcatraz.Session;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;
import android.util.Log;
import android.util.LongSparseArray;

import com.alcatraz.admin.project_alcatraz.Data.Resource;

import java.util.ArrayList;
import java.util.List;

public class MenuViewModel extends AndroidViewModel {
    private final String TAG = MenuViewModel.class.getSimpleName();
    private MenuRepository mRepository;
    private int mMenuId;
    private LiveData<List<MenuItemModel>> mMenuItems;
    private LiveData<Resource<List<MenuGroupModel>>> mMenuGroups;
    private MutableLiveData<List<OrderedItemModel>> mOrderedItems = new MutableLiveData<>();

    MenuViewModel(@NonNull Application application) {
        super(application);
        mRepository = MenuRepository.getInstance(application);
    }

    public LiveData<List<MenuItemModel>> getMenuItems() {
        if (mMenuItems == null)
            mMenuItems = mRepository.getMenuItems(mMenuId);
        return mMenuItems;
    }

    public LiveData<List<OrderedItemModel>> getOrderedItems() {
        if (mOrderedItems.getValue() == null)
            mOrderedItems.setValue(new ArrayList<>());
        return mOrderedItems;
    }

    public LiveData<Integer> getTotalOrderedCount() {
        return Transformations.switchMap(mOrderedItems, input -> {
            MutableLiveData<Integer> res = new MutableLiveData<>();
            int count = 0;
            for (OrderedItemModel item: input)
                count += item.getCount();
            res.setValue(count);
            return res;
        });
    }

    public LiveData<LongSparseArray<Integer>> getOrderedItemsCount() {
        return Transformations.switchMap(getOrderedItems(), input -> {
            MutableLiveData<LongSparseArray<Integer>> res = new MutableLiveData<>();
            LongSparseArray<Integer> data = new LongSparseArray<>(input.size());
            for (OrderedItemModel item: input) {
                data.put(item.getItem().getId(), item.getCount());
            }
            res.setValue(data);
            return res;
        });
    }

    public void orderItem(MenuItemModel menuItem, int count, int type) {
        OrderedItemModel item = menuItem.order(count, type);
        Log.e(TAG, "Item (" + menuItem.getId() + ") count: " + count);
        List<OrderedItemModel> orderedItems = mOrderedItems.getValue();
        int index;
        if (orderedItems == null) {
            orderedItems = new ArrayList<>();
            orderedItems.add(item);
        } else if ((index = orderedItems.indexOf(item)) != -1) {
            if (item.getCount() > 0)
                orderedItems.set(index, item);
            else
                orderedItems.remove(index);
        } else {
            orderedItems.add(item);
        }
        mOrderedItems.setValue(orderedItems);
    }

    public void removeItem(OrderedItemModel item) {
        List<OrderedItemModel> orderedItems = mOrderedItems.getValue();
        if (orderedItems != null) {
            orderedItems.remove(item);
            mOrderedItems.setValue(orderedItems);
        }
    }

    private LiveData<List<MenuItemModel>> filterMenuItems() {
        LiveData<List<MenuItemModel>> input = getMenuItems();
        return input;
    }

    public LiveData<Resource<List<MenuGroupModel>>> getMenuGroups(int menuId) {
        mMenuId = menuId;
        if (mMenuGroups == null)
            mMenuGroups = mRepository.getMenuGroups(menuId);
        return mMenuGroups;
    }

    public static class Factory extends ViewModelProvider.NewInstanceFactory {
        @NonNull
        private final Application mApplication;

        public Factory(@NonNull Application application) {
            mApplication = application;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            return (T) new MenuViewModel(mApplication);
        }
    }
}