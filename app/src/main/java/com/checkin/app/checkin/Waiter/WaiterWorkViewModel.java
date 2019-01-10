package com.checkin.app.checkin.Waiter;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

public class WaiterWorkViewModel extends ViewModel {

    private MutableLiveData<List<NavTableModel>> getWaiterTableList;
    private WaiterWorkRepository waiterWorkRepository;

    public WaiterWorkViewModel() {
        this.waiterWorkRepository = new WaiterWorkRepository();
    }

    public void init() {
        if (this.getWaiterTableList != null) {
            return;
        }

        this.getWaiterTableList = waiterWorkRepository.getWaiterTable();
    }

    LiveData<List<NavTableModel>> findUnassignedWaiterTable() {
        return Transformations.map(getWaiterTableList, input -> {
            List<NavTableModel> mUnassignedTableList = new ArrayList<>();
            if (input != null && input.size() > 0) {
                for (NavTableModel navTableModel : input) {
                    if (navTableModel.getHost() != null) {
                        mUnassignedTableList.add(navTableModel);
                    }
                }
            }
            return mUnassignedTableList;
        });
    }

    LiveData<List<NavTableModel>> findAssignedWaiterTable() {
        return Transformations.map(getWaiterTableList, input -> {
            List<NavTableModel> mAssignedTableList = new ArrayList<>();
            if (input != null && input.size() > 0) {
                for (NavTableModel navTableModel : input) {
                    if (navTableModel.getHost() == null) {
                        mAssignedTableList.add(navTableModel);
                    }
                }
            }
            return mAssignedTableList;
        });
    }
}