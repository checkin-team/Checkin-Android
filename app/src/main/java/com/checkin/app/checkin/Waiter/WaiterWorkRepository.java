package com.checkin.app.checkin.Waiter;

import android.arch.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;

class WaiterWorkRepository {

    WaiterWorkRepository(){
    }

    MutableLiveData<List<NavTableModel>> getAssignedTable(){
        final MutableLiveData<List<NavTableModel>> getAssignedTableLiveData = new MutableLiveData<>();
        List<NavTableModel> demoNavList = new ArrayList<>();

        for (int i = 0; i < 5; i++){
            NavTableModel navTableModel = new NavTableModel();
            navTableModel.setTable("Recent");
            navTableModel.setHost(null);

            demoNavList.add(navTableModel);
        }

        getAssignedTableLiveData.setValue(demoNavList);

        return getAssignedTableLiveData;
    }

    MutableLiveData<List<NavTableModel>> getUnassignedTable(){
        final MutableLiveData<List<NavTableModel>> getUnassignedTableLiveData = new MutableLiveData<>();
        List<NavTableModel> demoNavList = new ArrayList<>();

        for (int i = 0; i < 5; i++){

            NavTableModel.Host host = new NavTableModel.Host();
            host.setCustomerName("Ashish");
            host.setTableNumber("Table 4");

            NavTableModel navTableModel = new NavTableModel();
            navTableModel.setTable("Ongoing");
            navTableModel.setHost(host);

            demoNavList.add(navTableModel);
        }

        getUnassignedTableLiveData.setValue(demoNavList);

        return getUnassignedTableLiveData;
    }
}
