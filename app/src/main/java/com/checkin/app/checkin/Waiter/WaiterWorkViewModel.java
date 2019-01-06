package com.checkin.app.checkin.Waiter;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.support.annotation.NonNull;

import com.checkin.app.checkin.Data.BaseViewModel;
import com.checkin.app.checkin.Data.Resource;

import java.util.ArrayList;
import java.util.List;

public class WaiterWorkViewModel extends BaseViewModel {
    private MutableLiveData<Resource<List<NavTableModel>>> mTables = new MutableLiveData<>();

    public WaiterWorkViewModel(@NonNull Application application) {
        super(application);
    }

    LiveData<Resource<List<NavTableModel>>> getUnassignedTables() {
        return Transformations.map(mTables, input -> {
            if (input == null)
                return null;
            if (input.status == Resource.Status.SUCCESS && input.data != null) {
                List<NavTableModel> allTables = input.data;
                List<NavTableModel> unassignedTables = new ArrayList<>();
                for (NavTableModel table: allTables) {
                    if (table.getHost() == null)
                        unassignedTables.add(table);
                }
                return Resource.cloneResource(input, unassignedTables);
            }
            return null;
        });
    }

    LiveData<Resource<List<NavTableModel>>> getAssignedTables() {
        return Transformations.map(mTables, input -> {
            if (input == null)
                return null;
            if (input.status == Resource.Status.SUCCESS && input.data != null) {
                List<NavTableModel> allTables = input.data;
                List<NavTableModel> assignedTables = new ArrayList<>();
                for (NavTableModel table: allTables) {
                    if (table.getHost() != null)
                        assignedTables.add(table);
                }
                return Resource.cloneResource(input, assignedTables);
            }
            return null;
        });
    }

    @Override
    public void updateResults() {

    }
}
