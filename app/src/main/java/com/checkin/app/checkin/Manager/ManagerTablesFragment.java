package com.checkin.app.checkin.Manager;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.R;
import com.checkin.app.checkin.Session.Model.RestaurantTableModel;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class ManagerTablesFragment extends Fragment implements ManagerWorkTableAdapter.ManagerTableInteraction {

    @BindView(R.id.rv_shop_manager_table)
    RecyclerView rvShopManagerTable;
    Unbinder unbinder;

    public ManagerTablesFragment() {
    }

    public static ManagerTablesFragment newInstance() {
        return new ManagerTablesFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shop_manager_table, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ManagerWorkTableAdapter managerWorkTableAdapter = new ManagerWorkTableAdapter(this);
        rvShopManagerTable.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        rvShopManagerTable.setAdapter(managerWorkTableAdapter);

        ManagerWorkViewModel managerWorkViewModel = ViewModelProviders.of(requireActivity()).get(ManagerWorkViewModel.class);
        managerWorkViewModel.getActiveTables().observe(this, input->{
            if (input != null && input.data == null)
                return;
            if (input != null && input.data.size() > 0 && input.status == Resource.Status.SUCCESS) {
                managerWorkTableAdapter.setRestaurantTableList(input.data);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onClickTable(RestaurantTableModel tableModel) {
        Intent intent = new Intent(getContext(), ManagerSessionActivity.class);
        intent.putExtra(ManagerSessionActivity.KEY_SESSION_PK, tableModel.getPk());
        startActivity(intent);
    }
}
