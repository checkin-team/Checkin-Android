package com.checkin.app.checkin.Inventory;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.checkin.app.checkin.Data.ApiClient;
import com.checkin.app.checkin.Data.ApiResponse;
import com.checkin.app.checkin.Data.NetworkBoundResource;
import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.Data.RetrofitLiveData;
import com.checkin.app.checkin.Data.WebApiService;
import com.checkin.app.checkin.Inventory.Model.InventoryAvailabilityModel;
import com.checkin.app.checkin.Inventory.Model.InventoryModel;
import com.checkin.app.checkin.Menu.MenuRepository;

import java.util.List;

import javax.inject.Singleton;

@Singleton
public class InventoryRepository {
    private static InventoryRepository INSTANCE;
    private WebApiService mWebService;

    private InventoryRepository(Context context) {
        mWebService = ApiClient.Companion.getApiService(context);
    }

    public static InventoryRepository getInstance(Application application) {
        if (INSTANCE == null) {
            synchronized (MenuRepository.class) {
                if (INSTANCE == null) {
                    Context context = application.getApplicationContext();
                    INSTANCE = new InventoryRepository(context);
                }
            }
        }
        return INSTANCE;
    }

    public LiveData<Resource<InventoryModel>> getAvailableRestaurantMenu(final long restaurantId) {
        return new NetworkBoundResource<InventoryModel, InventoryModel>() {
            @Override
            protected boolean shouldUseLocalDb() {
                return false;
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<InventoryModel>> createCall() {
                return new RetrofitLiveData<>(mWebService.getAvailableRestaurantMenu(restaurantId));
            }

            @Override
            protected void saveCallResult(InventoryModel data) {

            }
        }.getAsLiveData();
    }

    public LiveData<Resource<List<InventoryAvailabilityModel>>> postMenuItemAvailability(final long restaurantId, final List<InventoryAvailabilityModel> orders) {
        return new NetworkBoundResource<List<InventoryAvailabilityModel>, List<InventoryAvailabilityModel>>() {
            @Override
            protected boolean shouldUseLocalDb() {
                return false;
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<List<InventoryAvailabilityModel>>> createCall() {
                return new RetrofitLiveData<>(mWebService.postChangeMenuAvailability(restaurantId, orders));
            }

            @Override
            protected void saveCallResult(List<InventoryAvailabilityModel> data) {

            }
        }.getAsLiveData();
    }
}
