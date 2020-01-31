package com.checkin.app.checkin.Inventory;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import com.checkin.app.checkin.Inventory.Model.InventoryAvailabilityModel;
import com.checkin.app.checkin.Inventory.Model.InventoryGroupModel;
import com.checkin.app.checkin.Inventory.Model.InventoryItemModel;
import com.checkin.app.checkin.Inventory.Model.InventoryModel;
import com.checkin.app.checkin.data.BaseViewModel;
import com.checkin.app.checkin.data.resource.Resource;
import com.checkin.app.checkin.utility.SourceMappedLiveData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class InventoryViewModel extends BaseViewModel {
    private InventoryRepository mRepository;

    private SourceMappedLiveData<Resource<InventoryModel>> mMenuData = createNetworkLiveData();
    private SourceMappedLiveData<Resource<List<InventoryGroupModel>>> mOriginalMenuGroups = createNetworkLiveData();
    private SourceMappedLiveData<Resource<List<InventoryAvailabilityModel>>> mResultMenuAvailability = createNetworkLiveData();

    private long mRestaurantId;

    public InventoryViewModel(@NonNull Application application) {
        super(application);
        mRepository = InventoryRepository.getInstance(application);
    }

    @Override
    public void updateResults() {
        fetchAvailableMenuDetail(mRestaurantId);
    }

    public void fetchAvailableMenuDetail(long restaurantId) {
        mRestaurantId = restaurantId;
        mMenuData.addSource(mRepository.getAvailableRestaurantMenu(restaurantId), mMenuData::setValue);

        LiveData<Resource<List<InventoryGroupModel>>> resourceLiveData = Transformations.map(mMenuData, menuModelResource -> {
            mOriginalMenuGroups.setValue(Resource.Companion.loading(null));
            if (menuModelResource == null || menuModelResource.getData() == null)
                return Resource.Companion.loading(null);
            List<InventoryGroupModel> groups = menuModelResource.getData().getGroups();
            Collections.sort(groups, (o1, o2) -> o1.getCategory().compareTo(o2.getCategory()));
            return Resource.Companion.cloneResource(menuModelResource, groups);
        });

        mOriginalMenuGroups.addSource(resourceLiveData, mOriginalMenuGroups::setValue);
    }

    public LiveData<Resource<List<InventoryGroupModel>>> getMenuGroups() {
        return Transformations.map(mOriginalMenuGroups, input -> {
            if (input == null || input.getData() == null)
                return null;
            List<InventoryItemModel> unavailableItems = new ArrayList<>();
            for (InventoryGroupModel groupModel : input.getData()) {
                for (InventoryItemModel itemModel : groupModel.getItems()) {
                    if (!itemModel.isAvailable())
                        unavailableItems.add(itemModel);
                }
            }
            List<InventoryGroupModel> groupModels = new ArrayList<>(input.getData());
            if (unavailableItems.size() > 0) {
                groupModels.add(0, new InventoryGroupModel("Unavailable Items", "Default", unavailableItems));
            }
            return Resource.Companion.cloneResource(input, groupModels);
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
        if (resource == null || resource.getData() == null)
            return;
        for (InventoryAvailabilityModel availabilityModel : data) {
            for (int i = 0, groupLength = resource.getData().size(); i < groupLength; i++) {
                List<InventoryItemModel> items = resource.getData().get(i).getItems();
                for (int j = 0, itemsLength = items.size(); j < itemsLength; j++) {
                    if (items.get(j).getPk() == availabilityModel.getPk()) {
                        items.get(j).setAvailable(availabilityModel.isAvailable());
                        i = groupLength;
                        break;
                    }
                }
            }
        }
        mOriginalMenuGroups.setValue(Resource.Companion.cloneResource(resource, resource.getData()));
    }
}
