package com.checkin.app.checkin.Menu;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.Log;

import com.checkin.app.checkin.Data.BaseViewModel;
import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.Menu.Model.ItemCustomizationFieldModel;
import com.checkin.app.checkin.Menu.Model.MenuGroupModel;
import com.checkin.app.checkin.Menu.Model.MenuItemModel;
import com.checkin.app.checkin.Menu.Model.MenuModel;
import com.checkin.app.checkin.Menu.Model.OrderedItemModel;
import com.fasterxml.jackson.databind.node.ArrayNode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MenuViewModel extends BaseViewModel {
    private final String TAG = MenuViewModel.class.getSimpleName();

    private MenuRepository mRepository;

    private MediatorLiveData<Resource<MenuModel>> mMenuData = new MediatorLiveData<>();
    private MediatorLiveData<Resource<List<MenuGroupModel>>> mOriginalMenuGroups = new MediatorLiveData<>();
    private MediatorLiveData<Resource<List<MenuGroupModel>>> mMenuGroups = new MediatorLiveData<>();
    private MediatorLiveData<Resource<List<MenuItemModel>>> mMenuItems = new MediatorLiveData<>();
    private MediatorLiveData<Resource<ArrayNode>> mResultOrder = new MediatorLiveData<>();

    private MutableLiveData<List<OrderedItemModel>> mOrderedItems = new MutableLiveData<>();
    private MutableLiveData<OrderedItemModel> mCurrentItem = new MutableLiveData<>();
    private MutableLiveData<String> mFilteredString = new MutableLiveData<>();

    private final Handler mHandler = new Handler();
    private Runnable mRunnable;
    private Long mSessionPk;

    MenuViewModel(@NonNull Application application) {
        super(application);
        mRepository = MenuRepository.getInstance(application);
    }

    @Override
    public void updateResults() {
    }

    public void fetchAvailableMenu(long shopId) {
        mMenuData.addSource(mRepository.getAvailableMenu(shopId), mMenuData::setValue);
        resetMenuGroups();

        LiveData<Resource<List<MenuGroupModel>>> resourceLiveData = Transformations.map(mMenuData, menuModelResource -> {
            mOriginalMenuGroups.setValue(Resource.loading(null));
            if (menuModelResource == null || menuModelResource.data == null)
                return Resource.loading(null);
            List<MenuGroupModel> groups = menuModelResource.data.getGroups();
            Collections.sort(groups, (o1, o2) -> o1.getCategory().compareTo(o2.getCategory()));
            return Resource.cloneResource(menuModelResource, groups);
        });
        mOriginalMenuGroups.addSource(resourceLiveData, listResource -> {
            mOriginalMenuGroups.setValue(listResource);
            resetMenuGroups();
        });
    }

    public void resetMenuGroups() {
        mMenuGroups.setValue(mOriginalMenuGroups.getValue());
    }

    public void clearFilters() {
        mFilteredString.setValue(null);
        resetMenuItems();
        resetMenuGroups();
    }

    public LiveData<Resource<List<MenuGroupModel>>> getMenuGroups() {
        return mMenuGroups;
    }

    public void resetMenuItems() {
        mMenuItems.setValue(Resource.noRequest());
    }

    public void searchMenuItems(final String query) {
        if (query == null || query.isEmpty())
            return;
        mMenuItems.setValue(Resource.loading(null));
        if (mRunnable != null)
            mHandler.removeCallbacks(mRunnable);
        mRunnable = () -> {
            LiveData<Resource<List<MenuItemModel>>> resourceLiveData = Transformations.map(mMenuData, input -> {
                if (input == null || input.data == null)
                    return Resource.loading(null);
                List<MenuItemModel> items = new ArrayList<>();
                for (MenuGroupModel groupModel : input.data.getGroups()) {
                    for (MenuItemModel itemModel : groupModel.getItems()) {
                        if (itemModel.getName().toLowerCase().contains(query.toLowerCase()))
                            items.add(itemModel);
                    }
                }
                if (items.size() == 0)
                    return Resource.errorNotFound(null);
                return Resource.cloneResource(input, items);
            });
            mMenuItems.addSource(resourceLiveData, mMenuItems::setValue);
        };
        mHandler.postDelayed(mRunnable, 500);
    }

    public void filterMenuGroups(final MenuItemModel.AVAILABLE_MEAL availableMeal) {
        mFilteredString.setValue("Filtered: " + availableMeal.name());
        LiveData<Resource<List<MenuGroupModel>>> resourceLiveData = Transformations.map(mOriginalMenuGroups, listResource -> {
            if (listResource == null || listResource.data == null)
                return null;
            List<MenuGroupModel> result = new ArrayList<>();
            for (MenuGroupModel menuGroupModel : listResource.data) {
                List<MenuItemModel> items = new ArrayList<>();
                for (MenuItemModel menuItemModel : menuGroupModel.getItems()) {
                    if (menuItemModel.hasAvailableMeal(availableMeal)) {
                        items.add(menuItemModel);
                    }
                }
                if (items.size() > 0) {
                    menuGroupModel.setItems(items);
                    result.add(menuGroupModel);
                }
            }
            return Resource.cloneResource(listResource, result);
        });
        mMenuGroups.addSource(resourceLiveData, mMenuGroups::setValue);
    }

    public LiveData<Resource<List<MenuItemModel>>> getFilteredMenuItems() {
        return mMenuItems;
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

    public void setCurrentItem(OrderedItemModel item) {
        try {
            mCurrentItem.setValue(item.clone());
        } catch (CloneNotSupportedException e) {
            Log.e(TAG, "Couldn't clone OrderedItem!");
        }
    }

    public void removeItemCustomization(ItemCustomizationFieldModel customizationField) {
        OrderedItemModel item = mCurrentItem.getValue();
        if (item == null) return;
        item.removeCustomizationField(customizationField);
        mCurrentItem.setValue(item);
    }

    public void setSelectedType(int selectedType) {
        OrderedItemModel item = mCurrentItem.getValue();
        if (item == null) return;
        item.setTypeIndex(selectedType);
        mCurrentItem.setValue(item);
    }

    public void setQuantity(int quantity) {
        OrderedItemModel item = mCurrentItem.getValue();
        if (item == null) return;
        if (quantity != item.getQuantity()) {
            item.setQuantity(quantity);
            mCurrentItem.setValue(item);
        }
        if (quantity == 0) {
            removeItem(item);
        }
    }

    public void changeQuantity(int diff) {
        OrderedItemModel item = mCurrentItem.getValue();
        if (item == null) return;
        if (diff != 0)
            setQuantity(item.getQuantity() + diff);
    }

    public void cancelItem() {
        OrderedItemModel item = mCurrentItem.getValue();
        if (item == null) return;
        item.setQuantity(0);
        item.setChangeCount(0);
        mCurrentItem.setValue(item);
        resetItem();
    }

    public boolean canOrder() {
        OrderedItemModel item = mCurrentItem.getValue();
        return item != null && item.canOrder();
    }

    public LiveData<Double> getItemCost() {
        return Transformations.map(mCurrentItem, orderedItem -> (orderedItem != null) ? orderedItem.getCost() / orderedItem.getQuantity() : 0);
    }

    public LiveData<Integer> getQuantity() {
        return Transformations.map(mCurrentItem, orderedItem -> (orderedItem != null) ? orderedItem.getQuantity() : 0);
    }

    public void orderItem() {
        updateCart();
        resetItem();
    }

    public void resetItem() {
        mCurrentItem.setValue(null);
    }

    public LiveData<List<OrderedItemModel>> getOrderedItems() {
        if (mOrderedItems.getValue() == null)
            mOrderedItems.setValue(new ArrayList<>());
        return mOrderedItems;
    }

    public void newOrderedItem(MenuItemModel item) {
        OrderedItemModel orderedItem = item.order(1);
        mCurrentItem.setValue(orderedItem);
    }

    public boolean updateOrderedItem(@NonNull MenuItemModel item, int count) {
        List<OrderedItemModel> items = mOrderedItems.getValue();
        boolean result = false;
        if (items == null) {
            Log.e(TAG, "updateOrderedItem - No items in Cart!!!");
            return result;
        }
        OrderedItemModel orderedItem = null;
        int cartCount = 0;
        for (OrderedItemModel listItem : items) {
            if (item.equals(listItem.getItemModel())) {
                cartCount += listItem.getQuantity();
                if (!item.isComplexItem()) {
                    try {
                        orderedItem = listItem.clone();
                    } catch (CloneNotSupportedException e) {
                        Log.e(TAG, "Couldn't clone OrderedItem!");
                    }
                    break;
                }
            }
        }
        if (cartCount == 0) {
            Log.e(TAG, "Didn't find item in cart!!");
            return result;
        }
        if (item.isComplexItem() && cartCount != count) {
            orderedItem = item.order(1);
            setCurrentItem(orderedItem);
            result = true;
        } else if (orderedItem != null && orderedItem.getQuantity() != count) {
            orderedItem.setQuantity(count);
            setCurrentItem(orderedItem);
            result = true;
        }
        return result;
    }

    private void updateCart() {
        OrderedItemModel item = mCurrentItem.getValue();
        if (item == null) {
            Log.e(TAG, "Current Item is NULL!");
            return;
        }
        List<OrderedItemModel> orderedItems = mOrderedItems.getValue();
        int index;
        if (orderedItems == null) {
            orderedItems = new ArrayList<>();
            orderedItems.add(item);
        } else if ((index = orderedItems.indexOf(item)) != -1) {
            if (item.getQuantity() > 0) {
                item.setQuantity(orderedItems.get(index).getQuantity() + item.getChangeCount());
                orderedItems.set(index, item);
            } else
                orderedItems.remove(index);
        } else {
            if (item.getItemModel().isComplexItem())
                item.setQuantity(item.getChangeCount());
            orderedItems.add(item);
        }
        mOrderedItems.setValue(orderedItems);
    }

    public int getOrderedCount(MenuItemModel item) {
        int count = 0;
        List<OrderedItemModel> orderedItems = getOrderedItems().getValue();
        if (orderedItems != null) {
            for (OrderedItemModel orderedItem : orderedItems) {
                if (orderedItem.getItemModel().equals(item)) {
                    count += orderedItem.getQuantity();
                    if (!item.isComplexItem())
                        break;
                }
            }
        }
        return count;
    }

    public void removeItem(OrderedItemModel item) {
        item.setQuantity(0);
        mCurrentItem.setValue(item);
        List<OrderedItemModel> orderedItems = mOrderedItems.getValue();
        if (orderedItems != null) {
            orderedItems.remove(item);
            mOrderedItems.setValue(orderedItems);
        }
        mCurrentItem.setValue(null);
    }

    public LiveData<Integer> getTotalOrderedCount() {
        return Transformations.map(mOrderedItems, input -> {
            int res = 0;
            for (OrderedItemModel item : input)
                res += item.getQuantity();
            return res;
        });
    }

    public LiveData<Double> getOrderedSubTotal() {
        return Transformations.map(mOrderedItems, input -> {
            double res = 0.0;
            for (OrderedItemModel item : input)
                res += item.getCost();
            return res;
        });
    }

    public LiveData<List<String>> getCategories() {
        return Transformations.map(mOriginalMenuGroups, input -> {
            if (input == null || input.data == null)
                return null;
            List<String> categories = new ArrayList<>();
            String category = "";
            for (MenuGroupModel menuGroupModel : input.data) {
                if (!category.contentEquals(menuGroupModel.getCategory())) {
                    category = menuGroupModel.getCategory();
                    categories.add(category);
                }
            }
            return categories;
        });
    }

    public void sortMenuItems(boolean low2high) {
        mFilteredString.setValue("Sorted: " + (low2high ? "Low-High" : "High-Low"));
        LiveData<Resource<List<MenuItemModel>>> resourceLiveData = Transformations.map(mMenuData, input -> {
            if (input == null || input.data == null)
                return Resource.loading(null);
            List<MenuItemModel> items = new ArrayList<>();
            for (MenuGroupModel groupModel : input.data.getGroups()) {
                items.addAll(groupModel.getItems());
            }
            if (items.size() == 0)
                return Resource.errorNotFound(null);
            Collections.sort(items, (o1, o2) -> {
                int diff = (int) (o1.getTypeCosts().get(0) - o2.getTypeCosts().get(0));
                return low2high ?  diff : -diff;
            });
            return Resource.cloneResource(input, items);
        });
        mMenuItems.addSource(resourceLiveData, mMenuItems::setValue);
    }

    public void confirmOrder() {
        if (mSessionPk == null)
            mResultOrder.addSource(mRepository.postMenuOrders(mOrderedItems.getValue()), mResultOrder::setValue);
        else
            mResultOrder.addSource(mRepository.postMenuManageOrders(mSessionPk, mOrderedItems.getValue()), mResultOrder::setValue);
    }

    public void manageSession(long sessionPk) {
        mSessionPk = sessionPk;
    }

    public LiveData<Resource<ArrayNode>> getServerOrderedItems() {
        return mResultOrder;
    }

    public LiveData<String> getFilteredString() {
        return mFilteredString;
    }
}
