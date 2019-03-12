package com.checkin.app.checkin.Inventory;

import android.app.Application;
import android.util.Log;

import com.checkin.app.checkin.Data.BaseViewModel;
import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.Inventory.Model.InventoryAvailabilityModel;
import com.checkin.app.checkin.Inventory.Model.InventoryGroupModel;
import com.checkin.app.checkin.Inventory.Model.InventoryItemModel;
import com.checkin.app.checkin.Inventory.Model.InventoryModel;
import com.checkin.app.checkin.Menu.Model.ItemCustomizationFieldModel;
import com.checkin.app.checkin.Menu.Model.MenuGroupModel;
import com.checkin.app.checkin.Menu.Model.MenuItemModel;
import com.checkin.app.checkin.Menu.Model.MenuModel;
import com.checkin.app.checkin.Menu.Model.OrderedItemModel;
import com.checkin.app.checkin.Waiter.Model.OrderStatusModel;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

public class InventoryViewModel extends BaseViewModel  {
    private static final String TAG = InventoryViewModel.class.getSimpleName();

    private InventoryRepository mRepository;
    private MediatorLiveData<Resource<InventoryModel>> mMenuData = new MediatorLiveData<>();
    private MediatorLiveData<Resource<List<InventoryGroupModel>>> mOriginalMenuGroups = new MediatorLiveData<>();
    private MediatorLiveData<Resource<List<InventoryItemModel>>> mMenuItems = new MediatorLiveData<>();
    private MutableLiveData<OrderedItemModel> mCurrentItem = new MutableLiveData<>();

    private MutableLiveData<List<InventoryAvailabilityModel>> mMenuItemAvailability = new MutableLiveData<>();
    private MediatorLiveData<Resource<List<InventoryAvailabilityModel>>> mResultMenuAvailability = new MediatorLiveData<>();
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
            mOriginalMenuGroups.setValue(Resource.loading(null));
            if (menuModelResource == null || menuModelResource.data == null)
                return Resource.loading(null);
            List<InventoryGroupModel> groups = menuModelResource.data.getGroups();
            Collections.sort(groups, (o1, o2) -> o1.getCategory().compareTo(o2.getCategory()));
            return Resource.cloneResource(menuModelResource, groups);
        });

        mOriginalMenuGroups.addSource(resourceLiveData, mOriginalMenuGroups::setValue);
    }


    public LiveData<Resource<List<InventoryGroupModel>>> getUnavailableItems() {
        return Transformations.map(mOriginalMenuGroups, input -> {

            List<InventoryGroupModel> groups = new ArrayList<>();
            List<InventoryItemModel> itemModels = new ArrayList<>();

            InventoryGroupModel unavailableGroup;
            for(int i =0 ; i<input.data.size() ; i++){
                for(InventoryItemModel itemModel : input.data.get(i).getItems()){
                    if(itemModel.isAvailable()){
                        unavailableGroup = new InventoryGroupModel();
                        unavailableGroup.setName("Unavailable Items");
                        groups.add(0,unavailableGroup);
                        itemModels.add(itemModel);
                    }
                    groups.get(0).setItems(itemModels);

                }
            }
            return Resource.cloneResource(input, groups);
        });
    }

    public void addItemCustomization(ItemCustomizationFieldModel customizationField) {
        OrderedItemModel item = mCurrentItem.getValue();
        if (item == null) return;
        item.addCustomizationField(customizationField);
        mCurrentItem.setValue(item);
    }

    public LiveData<OrderedItemModel> getCurrentItem() {
        return mCurrentItem;
    }

    public LiveData<Resource<List<InventoryGroupModel>>> getMenuGroups() {
        mOriginalMenuGroups.addSource(getUnavailableItems(), mOriginalMenuGroups::setValue);
        return mOriginalMenuGroups;
    }

    public void confirmMenuItemAvailability(List<InventoryItemModel> items, boolean isChecked) {
        List<InventoryAvailabilityModel> list = new ArrayList<>();
        InventoryAvailabilityModel statusModel;
        for(int i = 0; i<items.size(); i++){
            statusModel = new InventoryAvailabilityModel();
            statusModel.setPk(items.get(i).getPk());
            statusModel.setAvailable(isChecked);
            list.add(statusModel);
        }

        mMenuItemAvailability.setValue(list);
        mResultMenuAvailability.addSource(mRepository.postMenuItemAvailability(mRestaurantId,mMenuItemAvailability.getValue()), mResultMenuAvailability::setValue);
    }

    public LiveData<Resource<List<InventoryAvailabilityModel>>> getMenuItemAvailabilityData() {
        return mResultMenuAvailability;
    }
}
