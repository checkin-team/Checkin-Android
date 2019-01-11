package com.checkin.app.checkin.Waiter;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class TabTableViewModel extends ViewModel {
    private MutableLiveData<List<TabTableModel>> mTabLiveDataList = new MutableLiveData<>();
    private TabTableRepository mTabTableRepository;

    TabTableViewModel(){
        mTabTableRepository = new TabTableRepository();
    }

    public void init(){
        if (this.mTabLiveDataList != null){
            return;
        }

        this.mTabLiveDataList = mTabTableRepository.getTabModelList();
    }

    LiveData<List<TabTableModel>> findTabModelList(){
        return Transformations.map(mTabLiveDataList, input -> {

            List<TabTableModel> mList = new ArrayList<>();

            if (input != null && input.size() > 0) {
               for (int i = 0; i < input.size(); i++){
                   int mActive = input.get(i).getActive();
                   if (mActive != 0){
                       TabTableModel mData = input.get(i);
                       mList.add(mData);
                   }else {
                       TabTableModel mData = input.get(i);
                       mList.add(mData);
                   }
               }
            }

            Log.d("Viewmodel List Size",mList.size()+"");

            return mList;
        });
    }
}
