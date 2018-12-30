package com.checkin.app.checkin.Menu;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.support.annotation.NonNull;

import com.checkin.app.checkin.Data.ApiClient;
import com.checkin.app.checkin.Data.ApiResponse;
import com.checkin.app.checkin.Data.NetworkBoundResource;
import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.Data.RetrofitLiveData;
import com.checkin.app.checkin.Data.WebApiService;
import com.checkin.app.checkin.Menu.Model.MenuModel;
import com.checkin.app.checkin.Menu.Model.OrderedItemModel;
import com.fasterxml.jackson.databind.node.ArrayNode;

import java.util.List;

import javax.inject.Singleton;

@Singleton
public class MenuRepository {
    private static MenuRepository INSTANCE;
    private WebApiService mWebService;

    private MenuRepository(Context context) {
        mWebService = ApiClient.getApiService(context);
    }

    public LiveData<Resource<MenuModel>> getAvailableMenu(final String shopId) {
        return new NetworkBoundResource<MenuModel, MenuModel>() {
            @Override
            protected boolean shouldUseLocalDb() {
                return false;
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<MenuModel>> createCall() {
                return new RetrofitLiveData<>(mWebService.getAvailableMenu(shopId));
            }

            @Override
            protected void saveCallResult(MenuModel data) {}
        }.getAsLiveData();
    }

    public LiveData<Resource<ArrayNode>> postMenuOrders(final List<OrderedItemModel> orders) {
        return new NetworkBoundResource<ArrayNode, ArrayNode>() {
            @Override
            protected boolean shouldUseLocalDb() {
                return false;
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<ArrayNode>> createCall() {
                return new RetrofitLiveData<>(mWebService.postSessionOrders(orders));
            }

            @Override
            protected void saveCallResult(ArrayNode data) {

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
