package com.checkin.app.checkin.Waiter;

import android.arch.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;

class TabTableRepository {
    private MutableLiveData<List<TabTableModel>> mTabLiveDataList = new MutableLiveData<>();

    TabTableRepository(){
    }

    MutableLiveData<List<TabTableModel>> getTabModelList(){
        List<TabTableModel> mTabList = new ArrayList<>();
        TabTableModel tabTableModel = new TabTableModel();

        for (int i = 0; i < 8; i++){
            tabTableModel.setTableNumber(i+1);
            if (i == 0)
                tabTableModel.setActive(i+1);
            mTabList.add(tabTableModel);
        }

        mTabLiveDataList.setValue(mTabList);

        return mTabLiveDataList;
    }
}
