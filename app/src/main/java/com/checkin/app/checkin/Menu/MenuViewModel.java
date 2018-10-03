package com.checkin.app.checkin.Menu;

import android.app.Application;
import android.arch.core.util.Function;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.nfc.Tag;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.MenuItem;

import com.checkin.app.checkin.Data.Resource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class MenuViewModel extends AndroidViewModel {
    private final String TAG = MenuViewModel.class.getSimpleName();
    private MenuRepository mRepository;
//    private LiveData<List<MenuItemModel>> mMenuItems;
    private LiveData<List<String>> mCategories;
    private LiveData<Resource<List<MenuGroupModel>>> mMenuGroups;
    private MutableLiveData<List<OrderedItemModel>> mOrderedItems = new MutableLiveData<>();
    private MutableLiveData<OrderedItemModel> mCurrentItem = new MutableLiveData<>();
    private MediatorLiveData<List<MenuItemModel>> var=new MediatorLiveData<>();

    MenuViewModel(@NonNull Application application) {
        super(application);
        mRepository = MenuRepository.getInstance(application);
    }

    public void addItemCustomization(ItemCustomizationFieldModel customizationField) {
        OrderedItemModel item = mCurrentItem.getValue();
        if (item == null)   return;
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

    public void setRemarks(String remarks) {
        OrderedItemModel item = mCurrentItem.getValue();
        if (item == null)   return;
        item.setRemarks(remarks);
        mCurrentItem.setValue(item);
    }

    public void removeItemCustomization(ItemCustomizationFieldModel customizationField) {
        OrderedItemModel item = mCurrentItem.getValue();
        if (item == null)   return;
        item.removeCustomizationField(customizationField);
        mCurrentItem.setValue(item);
    }

    public void setSelectedType(int selectedType) {
        OrderedItemModel item = mCurrentItem.getValue();
        if (item == null)   return;
        item.setTypeIndex(selectedType);
        mCurrentItem.setValue(item);
    }

    public void setQuantity(int quantity) {
        OrderedItemModel item = mCurrentItem.getValue();
        if (item == null)   return;
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
        if (item == null)   return;
        if (diff != 0)
            setQuantity(item.getQuantity() + diff);
    }

    public void cancelItem() {
        OrderedItemModel item = mCurrentItem.getValue();
        if (item == null)   return;
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
            if (item.equals(listItem.getItem())) {
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
        }
        else if (orderedItem != null && orderedItem.getQuantity() != count) {
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
            }
            else
                orderedItems.remove(index);
        } else {
            if (item.getItem().isComplexItem())
                item.setQuantity(item.getChangeCount());
            orderedItems.add(item);
        }
        mOrderedItems.setValue(orderedItems);
    }

    public int getOrderedCount(MenuItemModel item) {
        int count = 0;
        List<OrderedItemModel> orderedItems = getOrderedItems().getValue();
        if (orderedItems != null) {
            for (OrderedItemModel orderedItem: orderedItems) {
                if (orderedItem.getItem().getId() == item.getId()) {
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

    /*private LiveData<List<MenuItemModel>> getMenuItems() {
        if (mMenuItems == null)
            mMenuItems = mRepository.getMenuItems(mMenuId);
        return mMenuItems;
    }

    private LiveData<List<MenuItemModel>> filterMenuItems() {
        LiveData<List<MenuItemModel>> input = getMenuItems();
        return input;
    }*/

    public void search(String s) {



        Log.e(TAG,"AAo function mei");
        var.addSource(mMenuGroups, new Observer<Resource<List<MenuGroupModel>>>() {
            @Override
            public void onChanged(@Nullable Resource<List<MenuGroupModel>> listResource) {

                Log.e(TAG,"Mai yha bhi hun");

                LiveData<List<MenuItemModel>> list= Transformations.map(mMenuGroups, new Function<Resource<List<MenuGroupModel>>, List<MenuItemModel>>() {
                    @Override
                    public List<MenuItemModel> apply(Resource<List<MenuGroupModel>> input) {
                            Log.e(TAG, String.valueOf("Mai Andar aa gya"));
                        List<MenuItemModel> items =new ArrayList<>();

                        if (input != null && input.status == Resource.Status.SUCCESS) {
                            List<MenuGroupModel> groups = input.data;
                            Log.e(TAG,"Input pyaara hai");
                            for(int i=0;i<groups.size();i++)
                            {
                                Log.e(TAG,"Bada Loop");
                                int size =groups.get(i).getItems().size();
                                for(int j=0;j<size;j++)
                                {   Log.e(TAG,"Chota Loop");
                                    if(groups.get(i).getItems().get(j).getName().toLowerCase().contains(s.toLowerCase()))
                                    {
                                        Log.e(TAG,"Kuch Mila");
                                        items.add(groups.get(i).getItems().get(j));
                                        var.setValue(items);
                                        Log.e(TAG,groups.get(i).getItems().get(j).getName());
                                    }
                                }
                            }

                        }
                        Log.e(TAG,"LOOP se Baharr");
                        Log.e(TAG, String.valueOf(items.size()));
                        var.setValue(items);
                        return items;

                    }

//            @Override
//            public List<MenuItemModel> apply(Resource<List<MenuGroupModel>> input) {
//                if (input != null && input.status == Resource.Status.SUCCESS) {
//                    List<MenuGroupModel> groups = input.data;

                });
                list.observeForever(new Observer<List<MenuItemModel>>() {
                    @Override
                    public void onChanged(@Nullable List<MenuItemModel> menuItemModels) {

                    }
                });
                var.removeSource(mMenuGroups);
            }


        });



    }

    public LiveData<List<MenuItemModel>> getMenuItems() {
        Log.e(TAG,"Maine kuch bheja");
        return var;
//
//                }
//            }
//        });
    }

    public LiveData<Resource<List<MenuGroupModel>>> getMenuGroups(long shopId) {
        if (mMenuGroups == null)
            mMenuGroups = mRepository.getMenuGroups(String.valueOf(shopId));
        return mMenuGroups;
    }

    public LiveData<Integer> getTotalOrderedCount() {
        return Transformations.switchMap(mOrderedItems, input -> {
            MutableLiveData<Integer> res = new MutableLiveData<>();
            int count = 0;
            for (OrderedItemModel item: input)
                count += item.getQuantity();
            res.setValue(count);
            return res;
        });
    }

    public LiveData<List<String>> getCategories(int menuId){
        if (mCategories == null) {
            if (mMenuGroups == null) getMenuGroups(menuId);
            mCategories = Transformations.map(mMenuGroups, input -> {
                List<String> categories = new ArrayList<>();
                String category = "";
                if (input.data != null) {
                    for (MenuGroupModel menuGroupModel : input.data) {
                        if (!category.contentEquals(menuGroupModel.getCategory())) {
                            category = menuGroupModel.getCategory();
                            categories.add(category);
                        }
                    }
                }
                return categories;
            });
        }
        return mCategories;
    }

    public static class Factory extends ViewModelProvider.NewInstanceFactory {
        @NonNull
        private final Application mApplication;

        public Factory(@NonNull Application application) {
            mApplication = application;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            return (T) new MenuViewModel(mApplication);
        }
    }
}
