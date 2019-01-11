package com.checkin.app.checkin.Waiter;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.checkin.app.checkin.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class ActiveTableFragment extends Fragment {

    @BindView(R.id.active_table_list)
    RecyclerView activeTableList;
    @BindView(R.id.ongoing_table_list)
    RecyclerView ongoingTableList;
    Unbinder unbinder;
    @BindView(R.id.tv_waiter_table_recent_occupied)
    TextView tvWaiterTableRecentOccupied;
    @BindView(R.id.tv_waiter_table_ongoing_table)
    TextView tvWaiterTableOngoingTable;

    private WaiterEndNavigationTableAdapter ongoingTableAdapter;
    private WaiterEndNavigationTableAdapter recentOccupiedAadpter;

    public ActiveTableFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.incl_waiter_menu_table, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        List<NavTableModel> navTableModels = new ArrayList<>();

        LinearLayoutManager occupiedLayout = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        LinearLayoutManager ongoingLayout = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);

        activeTableList.setLayoutManager(occupiedLayout);
        ongoingTableList.setLayoutManager(ongoingLayout);

        activeTableList.setNestedScrollingEnabled(false);
        ongoingTableList.setNestedScrollingEnabled(false);

        recentOccupiedAadpter = new WaiterEndNavigationTableAdapter(navTableModels, ActiveTableFragment.this);
        ongoingTableAdapter = new WaiterEndNavigationTableAdapter(navTableModels, ActiveTableFragment.this);

        activeTableList.setAdapter(recentOccupiedAadpter);
        ongoingTableList.setAdapter(ongoingTableAdapter);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        WaiterWorkViewModel waiterWorkViewModel = ViewModelProviders.of(this).get(WaiterWorkViewModel.class);
        waiterWorkViewModel.init();

        waiterWorkViewModel.findAssignedWaiterTable().observe(this, navTableModels -> {
            if (navTableModels != null && navTableModels.size() > 0) {
                recentOccupiedAadpter.setWaiterTableData(navTableModels);
                recentOccupiedAadpter.notifyDataSetChanged();
            } else {
                tvWaiterTableRecentOccupied.setVisibility(View.GONE);
                activeTableList.setVisibility(View.GONE);
            }
        });

        waiterWorkViewModel.findUnassignedWaiterTable().observe(this, navTableModels -> {
            if (navTableModels != null && navTableModels.size() > 0) {
                ongoingTableAdapter.setWaiterTableData(navTableModels);
                ongoingTableAdapter.notifyDataSetChanged();
            } else {
                tvWaiterTableOngoingTable.setVisibility(View.GONE);
                activeTableList.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
