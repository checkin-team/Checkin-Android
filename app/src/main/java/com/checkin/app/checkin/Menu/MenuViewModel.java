package com.checkin.app.checkin.Menu;

import android.app.Application;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.checkin.app.checkin.Data.BaseViewModel;
import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.Menu.Model.ItemCustomizationFieldModel;
import com.checkin.app.checkin.Menu.Model.MenuGroupModel;
import com.checkin.app.checkin.Menu.Model.MenuItemModel;
import com.checkin.app.checkin.Menu.Model.MenuModel;
import com.checkin.app.checkin.Menu.Model.OrderedItemModel;
import com.checkin.app.checkin.Utility.SourceMappedLiveData;
import com.checkin.app.checkin.session.activesession.ActiveSessionRepository;
import com.checkin.app.checkin.session.model.TrendingDishModel;
import com.fasterxml.jackson.databind.node.ArrayNode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MenuViewModel extends BaseViewModel {
    private final Handler mHandler = new Handler();
    private MenuRepository mRepository;
    private ActiveSessionRepository mActiveSessionRepository;

    private SourceMappedLiveData<Resource<MenuModel>> mMenuData = createNetworkLiveData();
    public SourceMappedLiveData<Resource<List<MenuGroupModel>>> mOriginalMenuGroups = createNetworkLiveData();
    private SourceMappedLiveData<Resource<List<MenuGroupModel>>> mMenuGroups = createNetworkLiveData();
    private SourceMappedLiveData<Resource<List<MenuItemModel>>> mMenuItems = createNetworkLiveData();
    private SourceMappedLiveData<Resource<ArrayNode>> mResultOrder = createNetworkLiveData();
    private SourceMappedLiveData<Resource<List<TrendingDishModel>>> mTrendingData = createNetworkLiveData();
    private SourceMappedLiveData<Resource<List<OrderedItemModel>>> mTreatYourselfData = createNetworkLiveData();

    private MutableLiveData<List<OrderedItemModel>> mOrderedItems = new MutableLiveData<>();
    private MutableLiveData<OrderedItemModel> mCurrentItem = new MutableLiveData<>();
    private MutableLiveData<String> mFilteredString = new MutableLiveData<>();
    public MutableLiveData<String> mSelectedCategory = new MutableLiveData<>();
    private Runnable mRunnable;
    private Long mSessionPk;
    private long mShopPk;
    private Long mTrendingItemPk;

    public MenuViewModel(@NonNull Application application) {
        super(application);
        mRepository = MenuRepository.getInstance(application);
        mActiveSessionRepository = ActiveSessionRepository.getInstance(application);
    }

    @Override
    public void updateResults() {
        mMenuData.addSource(mRepository.getAvailableMenu(mShopPk), mMenuData::setValue);
    }

    public void fetchAvailableMenu(long shopId) {
        mShopPk = shopId;
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
        mSelectedCategory.setValue("default");
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
        mFilteredString.setValue(availableMeal.name());
        LiveData<Resource<List<MenuItemModel>>> resourceLiveData = Transformations.map(mMenuData, input -> {
            if (input == null || input.data == null)
                return Resource.loading(null);
            List<MenuItemModel> items = new ArrayList<>();
            for (MenuGroupModel groupModel : input.data.getGroups()) {
                for (MenuItemModel menuItemModel : groupModel.getItems()) {
                    if(menuItemModel.getAvailableMeals().contains(availableMeal.name())){
                        items.add(menuItemModel);
                    }
                }
            }
            if (items.size() == 0)
                return Resource.errorNotFound(null);
            return Resource.cloneResource(input, items);
        });
        mMenuItems.addSource(resourceLiveData, mMenuItems::setValue);
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
                    }
                    break;
                }
            }
        }
        if (cartCount == 0) {
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

    /*public LiveData<Double> getASOrderedSubTotal() {
        if(mOrderedItems == null)
            return null;

            return Transformations.map(mOrderedItems, input -> {
                double res = 0.0;
                for (OrderedItemModel item : input)
                    res += item.getCost();
                return res;
            });

    }*/

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

    public LiveData<List<String>> getGroupName() {
        return Transformations.map(mMenuGroups, input -> {
            if (input == null || input.data == null)
                return null;
            List<String> categories = new ArrayList<>();
            String category = "";
            for (MenuGroupModel menuGroupModel : input.data) {
                categories.add(menuGroupModel.getName());
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
                return low2high ? diff : -diff;
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

    public void searchMenuItemById(long itemPk) {
        mTrendingItemPk = itemPk;
    }

    public LiveData<MenuItemModel> getMenuItem() {
        if (mTrendingItemPk == null)
            return null;
        mMenuData.setValue(Resource.loading(null));
        return Transformations.map(mMenuData, input -> {
            if (input == null || input.data == null)
                return null;
            for (MenuGroupModel groupModel : input.data.getGroups()) {
                for (MenuItemModel itemModel : groupModel.getItems()) {
                    if (itemModel.getPk() == mTrendingItemPk) {
                        mTrendingItemPk = 0L;
                        return itemModel;
                    }
                }
            }
            return null;
        });
    }

    public LiveData<MenuItemModel> getTreatMenuItem() {
        if (mTrendingItemPk == null)
            return null;
        return Transformations.map(mMenuData, input -> {
            if (input == null || input.data == null)
                return null;
            for (MenuGroupModel groupModel : input.data.getGroups()) {
                for (MenuItemModel itemModel : groupModel.getItems()) {
                    if (itemModel.getPk() == mTrendingItemPk) {
                        mTrendingItemPk = 0L;
                        return itemModel;
                    }
                }
            }
            return null;
        });
    }


    public void fetchTrendingItem() {
        mTrendingData.addSource(mActiveSessionRepository.getTrendingDishes(mShopPk), mTrendingData::setValue);
    }

    public LiveData<Resource<List<TrendingDishModel>>> getMenuTrendingItems() {
        return mTrendingData;
    }

    public void filterMenuCategories(String category) {
        if(mSelectedCategory.getValue() != null && mSelectedCategory.getValue().equalsIgnoreCase(category)){
            mSelectedCategory.setValue("default");
           resetMenuGroups();
           return;
        }
        if (mSelectedCategory != null)
            mSelectedCategory.setValue(category);

        LiveData<Resource<List<MenuGroupModel>>> resourceLiveData = Transformations.map(mOriginalMenuGroups, listResource -> {
            if (listResource == null || listResource.data == null)
                return null;
            List<MenuGroupModel> result = new ArrayList<>();
            for (MenuGroupModel menuGroupModel : listResource.data) {
                List<MenuItemModel> items = new ArrayList<>();
                if (menuGroupModel.getCategory().equalsIgnoreCase(category)){
                    items.addAll(menuGroupModel.getItems());
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
}
