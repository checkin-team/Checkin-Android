package com.checkin.app.checkin.Inventory;

import android.app.Application;

import com.checkin.app.checkin.Data.BaseViewModel;
import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.Inventory.Model.InventoryAvailabilityModel;
import com.checkin.app.checkin.Inventory.Model.InventoryGroupModel;
import com.checkin.app.checkin.Inventory.Model.InventoryItemModel;
import com.checkin.app.checkin.Inventory.Model.InventoryModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Transformations;

public class InventoryViewModel extends BaseViewModel {
    private InventoryRepository mRepository;
    private MediatorLiveData<Resource<InventoryModel>> mMenuData = new MediatorLiveData<>();
    private MediatorLiveData<Resource<List<InventoryGroupModel>>> mOriginalMenuGroups = new MediatorLiveData<>();

    private MediatorLiveData<Resource<List<InventoryAvailabilityModel>>> mResultMenuAvailability = new MediatorLiveData<>();

    private long mRestaurantId;

    public InventoryViewModel(@NonNull Application application) {
        super(application);
        mRepository = InventoryRepository.getInstance(application);
    }

    @Override
    protected void registerProblemHandlers() {
        mMenuData = registerProblemHandler(mMenuData);
        mOriginalMenuGroups = registerProblemHandler(mOriginalMenuGroups);
        mResultMenuAvailability = registerProblemHandler(mResultMenuAvailability);
    }

    @Override
    public void updateResults() {
        fetchAvailableMenuDetail(mRestaurantId);
    }

    public void fetchAvailableMenuDetail(long restaurantId) {
        mRestaurantId = restaurantId;
        mMenuData.addSource(mRepository.getAvailableRestaurantMenu(restaurantId), mMenuData::setValue);

        LiveData<Resource<List<InventoryGroupModel>>> resourceLiveData = Transformations.map(mMenuData, menuModelResource -> {
            mOriginalMenuGroups.setValue(Resource.loading(null));
            if (menuModelResource == null || menuModelResource.data == null)
                return Resource.loading(null);
            List<InventoryGroupModel> groups = menuModelResource.data.getGroups();
            Collections.sort(groups, (o1, o2) -> o1.getCategory().compareTo(o2.getCategory()));
            return Resource.cloneResource(menuModelResource, groups);
        });

        mOriginalMenuGroups.addSource(resourceLiveData, mOriginalMenuGroups::setValue);
    }

    public LiveData<Resource<List<InventoryGroupModel>>> getMenuGroups() {
        return Transformations.map(mOriginalMenuGroups, input -> {
            if (input == null || input.data == null)
                return null;
            List<InventoryItemModel> unavailableItems = new ArrayList<>();
            for (InventoryGroupModel groupModel : input.data) {
                for (InventoryItemModel itemModel : groupModel.getItems()) {
                    if (!itemModel.isAvailable())
                        unavailableItems.add(itemModel);
                }
            }
            List<InventoryGroupModel> groupModels = new ArrayList<>(input.data);
            if (unavailableItems.size() > 0) {
                groupModels.add(0, new InventoryGroupModel("Unavailable Items", "Default", unavailableItems));
            }
            return Resource.cloneResource(input, groupModels);
        });
    }

    public void confirmMenuItemAvailability(List<InventoryItemModel> items, boolean isChecked) {
        List<InventoryAvailabilityModel> data = new ArrayList<>();
        for (int i = 0; i < items.size(); i++) {
            data.add(new InventoryAvailabilityModel(items.get(i).getPk(), isChecked));
        }
        mResultMenuAvailability.addSource(mRepository.postMenuItemAvailability(mRestaurantId, data), mResultMenuAvailability::setValue);
    }

    public LiveData<Resource<List<InventoryAvailabilityModel>>> getMenuItemAvailabilityData() {
        return mResultMenuAvailability;
    }

    public void updateUiItemListAvailability(List<InventoryAvailabilityModel> data) {
        Resource<List<InventoryGroupModel>> resource = mOriginalMenuGroups.getValue();
        if (resource == null || resource.data == null)
            return;
        for (InventoryAvailabilityModel availabilityModel : data) {
            for (int i = 0, groupLength = resource.data.size(); i < groupLength; i++) {
                List<InventoryItemModel> items = resource.data.get(i).getItems();
                for (int j = 0, itemsLength = items.size(); j < itemsLength; j++) {
                    if (items.get(j).getPk() == availabilityModel.getPk()) {
                        items.get(j).setAvailable(availabilityModel.isAvailable());
                        i = groupLength;
                        break;
                    }
                }
            }
        }
        mOriginalMenuGroups.setValue(Resource.cloneResource(resource, resource.data));
    }
}
