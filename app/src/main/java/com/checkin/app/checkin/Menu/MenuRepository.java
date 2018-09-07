package com.checkin.app.checkin.Menu;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.support.annotation.NonNull;

import com.checkin.app.checkin.Data.ApiClient;
import com.checkin.app.checkin.Data.ApiResponse;
import com.checkin.app.checkin.Data.AppDatabase;
import com.checkin.app.checkin.Data.NetworkBoundResource;
import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.Data.RetrofitLiveData;
import com.checkin.app.checkin.Data.WebApiService;

import java.util.List;

import javax.inject.Singleton;

import io.objectbox.Box;
import io.objectbox.android.ObjectBoxLiveData;

@Singleton
public class MenuRepository {
    private static MenuRepository INSTANCE;
    private Box<MenuModel> mMenuModel;
    private Box<MenuGroupModel> mMenuGroupModel;
    private WebApiService mWebService;

    private MenuRepository(Context context) {
        mWebService = ApiClient.getApiService(context);
        mMenuGroupModel = AppDatabase.getMenuGroupModel(context);
        mMenuModel = AppDatabase.getMenuModel(context);
    }

    public LiveData<Resource<List<MenuGroupModel>>> getMenuGroups(final String shopId) {
        return new NetworkBoundResource<List<MenuGroupModel>, MenuModel>() {
            @Override
            protected boolean shouldUseLocalDb() {
                return true;
            }

            @Override
            protected boolean shouldFetch(List<MenuGroupModel> data) {
                // TODO: DEBUG mode for Menu
                return false;
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<MenuModel>> createCall() {
                return new RetrofitLiveData<>(mWebService.getAvailableMenu(shopId));
            }

            @Override
            protected LiveData<List<MenuGroupModel>> loadFromDb() {
                long id = ((long) (getVal() != null ? getVal() : 1L));
                return new ObjectBoxLiveData<>(mMenuGroupModel.query().equal(MenuGroupModel_.menuId, id).order(MenuGroupModel_.category).build());
            }

            @Override
            protected void saveCallResult(MenuModel data) {
                setVal(data.getId());
                mMenuModel.put(data);
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
