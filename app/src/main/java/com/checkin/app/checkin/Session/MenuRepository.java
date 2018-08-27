package com.checkin.app.checkin.Session;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.checkin.app.checkin.Data.ApiClient;
import com.checkin.app.checkin.Data.ApiResponse;
import com.checkin.app.checkin.Data.AppDatabase;
import com.checkin.app.checkin.Data.NetworkBoundResource;
import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.Data.WebApiService;

import java.util.List;

import javax.inject.Singleton;

import io.objectbox.Box;
import io.objectbox.android.ObjectBoxLiveData;

@Singleton
public class MenuRepository {
    private static MenuRepository INSTANCE;
    private Box<MenuItemModel> mMenuItemModel;
    private Box<MenuGroupModel> mMenuGroupModel;
    private WebApiService mWebService;

    private MenuRepository(Context context) {
        mWebService = ApiClient.getApiService(context);
        mMenuItemModel = AppDatabase.getMenuItemModel(context);
        mMenuGroupModel = AppDatabase.getMenuGroupModel(context);
    }
    public LiveData<List<MenuItemModel>> getMenuItems(final int menuId) {
        /*return new NetworkBoundResource<List<MenuItemModel>, List<MenuItemModel>>() {

            @Override
            protected void saveCallResult(@NonNull List<MenuItemModel> item) {
                mMenuItemModel.put(item);
            }

            @Override
            protected boolean shouldFetch(@Nullable List<MenuItemModel> data) {
                return false;
            }

            @Override
            protected boolean shouldUseLocalDb() {
                return true;
            }

            @NonNull
            @Override
            protected LiveData<List<MenuItemModel>> loadFromDb() {
                return new ObjectBoxLiveData<>(mMenuItemModel.query().equal(MenuItem_.menuId, menuId).build());
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<List<MenuItemModel>>> createCall() {
                // TODO: Network Fetching!!!!
                return null;
            }
        }.getAsLiveData();*/
        return new ObjectBoxLiveData<>(mMenuItemModel.query().equal(MenuItemModel_.menuId, menuId).build());
    }

    public LiveData<Resource<List<MenuGroupModel>>> getMenuGroups(final int menuId) {
        return new NetworkBoundResource<List<MenuGroupModel>, List<MenuGroupModel>>() {

            @Override
            protected void saveCallResult(@NonNull List<MenuGroupModel> item) {
            }

            @Override
            protected boolean shouldFetch(@Nullable List<MenuGroupModel> data) {
                return false;
            }

            @Override
            protected boolean shouldUseLocalDb() {
                return true;
            }

            @NonNull
            @Override
            protected LiveData<List<MenuGroupModel>> loadFromDb() {
                return new ObjectBoxLiveData<>(mMenuGroupModel.query().equal(MenuGroupModel_.menuId, menuId).build());
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<List<MenuGroupModel>>> createCall() {
                return null;
            }
        }.getAsLiveData();
    }

    public static MenuRepository getInstance(Application application) {
        if (INSTANCE == null) {
            synchronized (MenuRepository.class) {
                if (INSTANCE == null) {
                    Context context = application.getApplicationContext();
                    INSTANCE = new MenuRepository(context);
                }
            }
        }
        return INSTANCE;
    }
}
