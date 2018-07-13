package com.alcatraz.admin.project_alcatraz.Session;

import android.app.Application;
import android.arch.core.util.Function;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;
import android.util.Log;
import android.util.SparseArray;

import com.alcatraz.admin.project_alcatraz.Data.Resource;

import java.util.List;

public class MenuViewModel extends AndroidViewModel {
    private final String TAG = MenuViewModel.class.getSimpleName();
    private MenuRepository mRepository;
    private int mMenuId;
    private LiveData<Resource<List<MenuItem>>> mMenuItems;
    private LiveData<Resource<SparseArray<MenuGroup>>> mMenuGroups;

    MenuViewModel(@NonNull Application application, MenuRepository menuRepository) {
        super(application);
        mRepository = menuRepository;
    }

    public LiveData<Resource<List<MenuItem>>> getMenuItems() {
        if (mMenuItems == null)
            mMenuItems = mRepository.getMenuItems(mMenuId);
        return mMenuItems;
    }

    private LiveData<Resource<List<MenuItem>>> filterMenuItems() {
        LiveData<Resource<List<MenuItem>>> input = getMenuItems();
        return input;
    }

    public LiveData<Resource<SparseArray<MenuGroup>>> getMenuGroups(int menuId) {
        mMenuId = menuId;
        if (mMenuGroups == null)
            mMenuGroups = mRepository.getMenuGroups(menuId);
        return Transformations.switchMap(mMenuGroups, new Function<Resource<SparseArray<MenuGroup>>, LiveData<Resource<SparseArray<MenuGroup>>>>() {
            @Override
            public LiveData<Resource<SparseArray<MenuGroup>>> apply(Resource<SparseArray<MenuGroup>> input) {
                return loadMenuGroups(input);
            }
        });
    }

    private LiveData<Resource<SparseArray<MenuGroup>>> loadMenuGroups(Resource<SparseArray<MenuGroup>> menuGroupResource) {
        LiveData<Resource<List<MenuItem>>> menuItems = filterMenuItems();
        final SparseArray<MenuGroup> menuGroups = menuGroupResource.data;
        if (menuItems == null) {
            Log.e(TAG, "MenuItems are NULL!");
            return null;
        }
        return Transformations.map(menuItems, input -> {
            List<MenuItem> items = input.data;
            if (items == null) {
                Log.e(TAG, "Items are NULL.");
                return null;
            }
            for (MenuItem item: items) {
                menuGroups.get(item.getGroupId()).addItem(item);
            }
            return Resource.cloneResource(input, menuGroups);
        });
    }

    public static class Factory extends ViewModelProvider.NewInstanceFactory {
        @NonNull
        private final Application mApplication;
        private final MenuRepository mRepository;

        public Factory(@NonNull Application application) {
            mApplication = application;
            mRepository = MenuRepository.getInstance(application.getApplicationContext());
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            return (T) new MenuViewModel(mApplication, mRepository);
        }
    }
}
