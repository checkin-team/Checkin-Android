package com.checkin.app.checkin.Waiter;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import java.util.List;

public class WaiterWorkViewModel extends ViewModel {

    private MutableLiveData<List<NavTableModel>> assignedTableList;
    private MutableLiveData<List<NavTableModel>> unassignedTableList;
    private WaiterWorkRepository waiterWorkRepository;

    public WaiterWorkViewModel (){
        this.waiterWorkRepository = new WaiterWorkRepository();
    }

    public void init(){
        if (this.assignedTableList != null && this.unassignedTableList != null){
            return;
        }

        assignedTableList = waiterWorkRepository.getAssignedTable();
        unassignedTableList = waiterWorkRepository.getUnassignedTable();
    }

    MutableLiveData<List<NavTableModel>> findAssignedTableList(){
        return this.assignedTableList;
    }

    MutableLiveData<List<NavTableModel>> findUnassignedTableList(){
        return this.unassignedTableList;
    }
}