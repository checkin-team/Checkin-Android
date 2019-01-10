package com.checkin.app.checkin.Waiter;

import android.arch.lifecycle.MutableLiveData;

import com.checkin.app.checkin.Misc.BriefModel;

import java.util.ArrayList;
import java.util.List;

class WaiterWorkRepository {

    private MutableLiveData<List<NavTableModel>> getTableLiveData = new MutableLiveData<>();
    private List<NavTableModel> demoNavList = new ArrayList<>();

    WaiterWorkRepository(){
    }

    MutableLiveData<List<NavTableModel>> getWaiterTable(){

        for (int i = 0; i < 5; i++){
            NavTableModel navTableModel = new NavTableModel();
            navTableModel.setTableNumber("Table " + (i+1));
            navTableModel.setHost(null);

            demoNavList.add(navTableModel);
        }

        for (int i = 0; i < 5; i++){
            NavTableModel navTableModel = new NavTableModel();
            navTableModel.setTableNumber("Table " + (i+1));

            BriefModel host = new BriefModel("","Ashish Gupta","");

            navTableModel.setHost(host);

            demoNavList.add(navTableModel);
        }

        getTableLiveData.setValue(demoNavList);

        return getTableLiveData;
    }
}