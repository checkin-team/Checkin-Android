package com.checkin.app.checkin.Misc;

import android.app.Fragment;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.LongSparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.checkin.app.checkin.Menu.MenuItemAdapter;
import com.checkin.app.checkin.Menu.MenuSearchFragment;
import com.checkin.app.checkin.Menu.MenuViewModel;
import com.checkin.app.checkin.Menu.OrderedItemModel;
import com.checkin.app.checkin.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class ActiveTableFragment extends Fragment implements WaiterItemAdapter.OnItemInteractionListener {
    private final String TAG = MenuSearchFragment.class.getSimpleName();

    @BindView(R.id.active_table_list)
    RecyclerView rvTablesList;
    @BindView(R.id.ongoing_table_list)
    RecyclerView rvOngoingList;
    private List<TableModel> tables;
    private Unbinder unbinder;
    private WaiterEndNavigationTableAdapter waiterEndNavigationTableAdapter;
    public ActiveTableFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_active_tables, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        rvTablesList.setLayoutManager(llm);
        Log.e("ActiveTable","Maine view toh create kra");
        waiterEndNavigationTableAdapter=new WaiterEndNavigationTableAdapter(tables);

        rvTablesList.setAdapter(waiterEndNavigationTableAdapter);

        rvTablesList.setNestedScrollingEnabled(false);
        return rootView;


    }

    public void setupActiveTables(List<TableModel> tableModels) {
        Log.e("ActiveTable","Maine tables bhi set kri");
        this.tables=tableModels;

    }

    @Override
    public void onClickCompleted(EventModel item, int position) {

    }
}
