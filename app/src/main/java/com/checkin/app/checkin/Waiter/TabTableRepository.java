package com.checkin.app.checkin.Waiter;

import android.arch.lifecycle.MutableLiveData;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

class TabTableRepository {
    private MutableLiveData<List<TabTableModel>> mTabLiveDataList = new MutableLiveData<>();
    private List<TabTableModel> mTabList = new ArrayList<>();

    TabTableRepository(){
    }

    MutableLiveData<List<TabTableModel>> getTabModelList(){

        for (int i = 0; i < 8; i++){

            TabTableModel tabTableModel = new TabTableModel();

            tabTableModel.setTableNumber(i+1);
            tabTableModel.setTabTitle("TABLE");
            if (i == 0)
                tabTableModel.setActive(i+1);
            mTabList.add(tabTableModel);
        }

        mTabLiveDataList.setValue(mTabList);

        Log.d("Repo List Size",mTabList.size()+"");

        return mTabLiveDataList;
    }
}
