package com.alcatraz.admin.project_alcatraz.Session;

import android.arch.core.util.Function;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Transformations;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.SparseArray;

import com.alcatraz.admin.project_alcatraz.Data.ApiClient;
import com.alcatraz.admin.project_alcatraz.Data.ApiResponse;
import com.alcatraz.admin.project_alcatraz.Data.AppRoomDatabase;
import com.alcatraz.admin.project_alcatraz.Data.NetworkBoundResource;
import com.alcatraz.admin.project_alcatraz.Data.Resource;
import com.alcatraz.admin.project_alcatraz.Data.WebApiService;

import java.util.HashMap;
import java.util.List;

import javax.inject.Singleton;

@Singleton
public class MenuRepository {
    private static MenuRepository INSTANCE;

    private WebApiService mWebService;
    private MenuDao mMenuModel;

    private MenuRepository(MenuDao menuDao) {
        mWebService = ApiClient.getApiService();
        mMenuModel = menuDao;
    }
    public LiveData<Resource<List<MenuItem>>> getMenuItems(final int menuId) {
        return new NetworkBoundResource<List<MenuItem>, List<MenuItem>>() {

            @Override
            protected void saveCallResult(@NonNull List<MenuItem> item) {
                mMenuModel.insertItems(item.toArray(new MenuItem[0]));
            }

            @Override
            protected boolean shouldFetch(@Nullable List<MenuItem> data) {
                return false;
            }

            @NonNull
            @Override
            protected LiveData<List<MenuItem>> loadFromDb() {
                return mMenuModel.getMenuItems(menuId);
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<List<MenuItem>>> createCall() {
                // TODO: Network Fetching!!!!
                return null;
            }
        }.getAsLiveData();
    }

    public LiveData<Resource<SparseArray<MenuGroup>>> getMenuGroups(final int menuId) {
        return new NetworkBoundResource<SparseArray<MenuGroup>, List<MenuGroup>>() {

            @Override
            protected void saveCallResult(@NonNull List<MenuGroup> item) {
                mMenuModel.insertGroups(item.toArray(new MenuGroup[0]));
            }

            @Override
            protected boolean shouldFetch(@Nullable SparseArray<MenuGroup> data) {
                return false;
            }

            @NonNull
            @Override
            protected LiveData<SparseArray<MenuGroup>> loadFromDb() {
                return Transformations.map(mMenuModel.getMenuGroups(menuId), new Function<List<MenuGroup>, SparseArray<MenuGroup>>() {
                    @Override
                    public SparseArray<MenuGroup> apply(List<MenuGroup> input) {
                        SparseArray<MenuGroup> result = new SparseArray<>(input.size());
                        for (MenuGroup item : input) {
                            result.put(item.getId(), item);
                        }
                        return result;
                    }
                });
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<List<MenuGroup>>> createCall() {
                return null;
            }
        }.getAsLiveData();
    }

    public static MenuRepository getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (MenuRepository.class) {
                if (INSTANCE == null) {
                    AppRoomDatabase db = AppRoomDatabase.getDatabase(context);
                    INSTANCE = new MenuRepository(db.menuModel());
                }
            }
        }
        return INSTANCE;
    }
}
