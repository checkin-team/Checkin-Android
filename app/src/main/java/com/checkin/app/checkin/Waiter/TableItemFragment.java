package com.checkin.app.checkin.Waiter;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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

public class TableItemFragment extends Fragment implements WaiterItemAdapter.OnItemInteractionListener {

    private List<EventModel> items,incompleteItems,CompletedItems;
    private WaiterItemAdapter waiterItemAdapter;
    private WaiterItemAdapter waiterItemAdapter2;
    private Unbinder unbinder;
    @BindView(R.id.rv_user_items)
    RecyclerView rvUserItems;
    @BindView(R.id.rv_delivered_items)
    RecyclerView rvDeliveredItems;
    @BindView(R.id.Deliver_text)
    TextView delivered;
    public TableItemFragment() {}

    public void setItems(List<EventModel> items) {
        this.items = items;
    }

    public List<EventModel> getItems() {
        return items;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



    }



    public void ArrangeItems()
    {
        for(int i=0;i<items.size();i++)
        {
            if(incompleteItems==null && CompletedItems==null)
            {
                incompleteItems=new ArrayList<>();
                CompletedItems=new ArrayList<>();
            }
            if(items.get(i).getStatus()== EventModel.STATUS.INCOMPLETE)
            {
                incompleteItems.add(items.get(i));
            }
            else
                CompletedItems.add(items.get(i));

        }
    }



    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_waiter_item, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        setupUI();

        return rootView;


    }
    public void setupUI()
    {
        LinearLayoutManager llm = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        rvUserItems.setLayoutManager(llm);
        rvDeliveredItems.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));
        delivered.setText("DELIVERED");
        waiterItemAdapter=new WaiterItemAdapter(null);
        waiterItemAdapter.setItemInteractionListener(this);
        rvUserItems.setAdapter(waiterItemAdapter);
        waiterItemAdapter2=new WaiterItemAdapter(null);
        rvDeliveredItems.setAdapter(waiterItemAdapter2);
        rvDeliveredItems.setNestedScrollingEnabled(false);
        rvDeliveredItems.setAlpha((float) 0.4);
        rvUserItems.setNestedScrollingEnabled(false);
        Log.e("TableItem","Maine view toh create kra");
    }

    @Override
    public void onClickCompleted(EventModel item, int position) {

    }

    @Override
    public void onClickCancelled(EventModel item, int position) {

    }
}
