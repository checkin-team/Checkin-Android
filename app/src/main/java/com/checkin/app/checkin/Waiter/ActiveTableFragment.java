package com.checkin.app.checkin.Waiter;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.checkin.app.checkin.Menu.Fragment.MenuItemSearchFragment;
import com.checkin.app.checkin.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class ActiveTableFragment extends android.support.v4.app.Fragment {

    private final String TAG = MenuItemSearchFragment.class.getSimpleName();

    @BindView(R.id.active_table_list)
    RecyclerView activeTableList;
    @BindView(R.id.ongoing_table_list)
    RecyclerView ongoingTableList;
    Unbinder unbinder;

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

        LinearLayoutManager occupiedLayout = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        LinearLayoutManager ongoingLayout = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);

        List<NavTableModel> navRecentList = getNavRecentList();
        List<NavTableModel> navOngoingList = getNavOngoingList();

        activeTableList.setLayoutManager(occupiedLayout);
        ongoingTableList.setLayoutManager(ongoingLayout);

        activeTableList.setNestedScrollingEnabled(false);
        ongoingTableList.setNestedScrollingEnabled(false);

        WaiterEndNavigationTableAdapter onRecentAdapter = new WaiterEndNavigationTableAdapter(navRecentList, ActiveTableFragment.this);
        activeTableList.setAdapter(onRecentAdapter);

        WaiterEndNavigationTableAdapter ongoingAdapter = new WaiterEndNavigationTableAdapter(navOngoingList, ActiveTableFragment.this);
        ongoingTableList.setAdapter(ongoingAdapter);

//        WaiterWorkViewModel viewModel = ViewModelProviders.of(getActivity()).get(WaiterWorkViewModel.class);
//        viewModel.getUnassignedTables().observe(this, new Observer<Resource<List<NavTableModel>>>() {
//            @Override
//            public void onChanged(@Nullable Resource<List<NavTableModel>> listResource) {
//                if (listResource != null && listResource.status == Resource.Status.SUCCESS && listResource.data != null) {
//                    List<NavTableModel> unassignedList = listResource.data;
//                    WaiterEndNavigationTableAdapter onRecentAdapter = new WaiterEndNavigationTableAdapter(unassignedList, ActiveTableFragment.this);
//                    activeTableList.setAdapter(onRecentAdapter);
//                }
//            }
//        });
//
//        viewModel.getAssignedTables().observe(this, new Observer<Resource<List<NavTableModel>>>() {
//            @Override
//            public void onChanged(@Nullable Resource<List<NavTableModel>> listResource) {
//                if (listResource != null && listResource.status == Resource.Status.SUCCESS && listResource.data != null) {
//                    List<NavTableModel> assignedList = listResource.data;
//
//                    WaiterEndNavigationTableAdapter ongoingAdapter = new WaiterEndNavigationTableAdapter(assignedList, ActiveTableFragment.this);
//                    ongoingTableList.setAdapter(ongoingAdapter);
//                }
//            }
//        });
    }

    private List<NavTableModel> getNavRecentList() {
        List<NavTableModel> demoNavList = new ArrayList<>();

        for (int i = 0; i < 5; i++){
            NavTableModel navTableModel = new NavTableModel();
            navTableModel.setTable("Recent");
            navTableModel.setHost(null);

            demoNavList.add(navTableModel);
        }

        return demoNavList;
    }

    private List<NavTableModel> getNavOngoingList() {
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

        return demoNavList;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
