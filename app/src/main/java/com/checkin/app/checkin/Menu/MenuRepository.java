package com.checkin.app.checkin.Menu;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.checkin.app.checkin.Data.ApiClient;
import com.checkin.app.checkin.Data.ApiResponse;
import com.checkin.app.checkin.Data.AppDatabase;
import com.checkin.app.checkin.Data.NetworkBoundResource;
import com.checkin.app.checkin.Data.ObjectBoxInstanceLiveData;
import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.Data.RetrofitLiveData;
import com.checkin.app.checkin.Data.WebApiService;
import com.checkin.app.checkin.Menu.Model.MenuModel;
import com.checkin.app.checkin.Menu.Model.MenuModel_;
import com.checkin.app.checkin.Menu.Model.OrderedItemModel;
import com.fasterxml.jackson.databind.node.ArrayNode;

import java.util.List;

import javax.inject.Singleton;

import io.objectbox.Box;

@Singleton
public class MenuRepository {
    private static MenuRepository INSTANCE;
    private Box<MenuModel> mMenuBox;
    private WebApiService mWebService;

    private MenuRepository(Context context) {
        mWebService = ApiClient.getApiService(context);
        mMenuBox = AppDatabase.getMenuModel(context);
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

    public LiveData<Resource<MenuModel>> getAvailableMenu(final long shopId) {
        return new NetworkBoundResource<MenuModel, MenuModel>() {
            @Override
            protected boolean shouldUseLocalDb() {
                return false;
            }

            @Override
            protected boolean shouldFetch(MenuModel data) {
                return true;
            }

            @Override
            protected LiveData<MenuModel> loadFromDb() {
                return new ObjectBoxInstanceLiveData<>(mMenuBox
                        .query().equal(MenuModel_.restaurantPk, shopId)
                        .build());
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<MenuModel>> createCall() {
                return new RetrofitLiveData<>(mWebService.getAvailableMenu(shopId));
            }

            @Override
            protected void saveCallResult(MenuModel data) {
                if (data != null) {
                    data.setRestaurantPk(shopId);
                    mMenuBox.put(data);
                }
            }
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
                return new RetrofitLiveData<>(mWebService.postActiveSessionOrders(orders));
            }

            @Override
            protected void saveCallResult(ArrayNode data) {

            }
        }.getAsLiveData();
    }

    public LiveData<Resource<ArrayNode>> postMenuManageOrders(final long sessionPk, final List<OrderedItemModel> orders) {
        return new NetworkBoundResource<ArrayNode, ArrayNode>() {
            @Override
            protected boolean shouldUseLocalDb() {
                return false;
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<ArrayNode>> createCall() {
                return new RetrofitLiveData<>(mWebService.postSessionManagerOrders(sessionPk, orders));
            }

            @Override
            protected void saveCallResult(ArrayNode data) {

            }
        }.getAsLiveData();
    }
}
