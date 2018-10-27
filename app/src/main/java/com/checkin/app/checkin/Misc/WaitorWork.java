package com.checkin.app.checkin.Misc;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.checkin.app.checkin.Menu.MenuItemModel;
import com.checkin.app.checkin.Menu.OrderedItemModel;
import com.checkin.app.checkin.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WaitorWork extends AppCompatActivity implements WaiterItemAdapter.OnItemInteractionListener
    {
    @BindView(R.id.rv_active_tables)
    RecyclerView rvActiveTables;
    @BindView(R.id.drawer_open)
        ImageView drawerToggle;
        @BindView(R.id.action_table)
        ImageView tableToggle;
    @BindView(R.id.rv_user_items)
    RecyclerView rvUserItems;
    @BindView(R.id.root_view)
    View vRoot;
    @BindView(R.id.Deliver_text)
    TextView delivered;
    @BindView(R.id.rv_delivered_items)
            RecyclerView rvDelivered;
    List<OrderedItemModel> items,itemsDone;
        List<TableModel> tables;
    private WaiterActiveTableAdapter mWaiterTableAdapter;
    private WaiterItemAdapter mWaiterItemAdapter;
    private WaiterItemAdapter mWaiterItemAdapter2;
        ActiveTableFragment activeTableFragment;
        ActiveTableFragment activeTableFragment2;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiter_work);
        ButterKnife.bind(this);
        delivered.setText("DELIVERED");
                setupUI();
        setupUserItem();
        rvDelivered.setAlpha((float) 0.4);


        tables = new ArrayList<>();
        setupActiveTables();
    }
    private void setupUI()
    {
        Toolbar toolbar = findViewById(R.id.toolbar_home);
        setSupportActionBar(toolbar);
        //(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        DrawerLayout drawerLayout = findViewById(R.id.drawer_waiter);
        ActionBarDrawerToggle startToggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(startToggle);
        startToggle.syncState();

        ActionBarDrawerToggle endToggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.messages_drawer_open, R.string.messages_drawer_close);
        drawerLayout.addDrawerListener(endToggle);
        endToggle.syncState();

        activeTableFragment=new ActiveTableFragment();
        activeTableFragment2=new ActiveTableFragment();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_waiter_profiles);
        NavigationHeaderFragment navigationHeaderFragment=new NavigationHeaderFragment();
        navigationHeaderFragment.setDetails("Vivek Sharma","Waiter's account");
        getSupportFragmentManager().beginTransaction().add(navigationView.getId(),navigationHeaderFragment).commit();




        drawerToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(drawerLayout.isDrawerOpen(GravityCompat.START))
                {
                    drawerLayout.closeDrawer(GravityCompat.START);
                }
                else
                {
                    drawerLayout.openDrawer(GravityCompat.START);
                }
            }
        });

       tableToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(drawerLayout.isDrawerOpen(GravityCompat.END))
                {
                    drawerLayout.closeDrawer(GravityCompat.END);
                }
                else
                {
                    drawerLayout.openDrawer(GravityCompat.END);
                }
            }
        });


        NavigationView endNavigationView=(NavigationView) findViewById(R.id.nav_table_view);

        getFragmentManager().beginTransaction().add(
                endNavigationView.getId(),activeTableFragment).commit();

        getFragmentManager().beginTransaction().add(endNavigationView.getId(),activeTableFragment2).commit();
    }

    private void setupUserItem() {


        List<String> list=new ArrayList<>();
        items = new ArrayList<>();
        itemsDone = new ArrayList<>();

        List<Double> list1=new ArrayList<>();
        list1.add(250.00);
        for (int i = 1; i <= 3; i++) {
            if (i % 2 == 1)
            {
                list.add("PIZZA");
                list1.add(450.00);
                items.add(new OrderedItemModel(new MenuItemModel("FarmHouse Pizza",list,list1,(long) 1,1,true,true,true),2));
            }
            else
        {   list.add("Beverage");
            list1.add(300.00);
            items.add(new OrderedItemModel(new MenuItemModel("Carlsburg",list,list1,(long) 1,1,true,true,true),2));
        }

        }

        rvUserItems.setLayoutManager(new LinearLayoutManager(
                this, LinearLayoutManager.VERTICAL, false));

        mWaiterItemAdapter = new WaiterItemAdapter(items);
        rvUserItems.setAdapter(mWaiterItemAdapter);
        rvDelivered.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        if(itemsDone.size()!=0)
        {mWaiterItemAdapter2=new WaiterItemAdapter(itemsDone);
        rvDelivered.setAdapter(mWaiterItemAdapter2);
        mWaiterItemAdapter2.setStatusSymbol(false);
        rvDelivered.setNestedScrollingEnabled(false);}
        else
            mWaiterItemAdapter2=new WaiterItemAdapter(null);
        rvUserItems.setNestedScrollingEnabled(false);
        mWaiterItemAdapter.setItemInteractionListener(this);


    }

    public WaiterActiveTableAdapter setupActiveTables() {

        for (int i = 1; i <= 15; i++) {
            if (i % 2 == 0)
                tables.add(new TableModel("Table" + i + "", true, 4, 2));
        }
            rvActiveTables.setLayoutManager(new LinearLayoutManager(
                    this, LinearLayoutManager.HORIZONTAL, false));
        mWaiterTableAdapter = new WaiterActiveTableAdapter(tables,this);
        mWaiterTableAdapter.setNoItems(items.size());
        rvActiveTables.setAdapter(mWaiterTableAdapter);
        activeTableFragment.setupActiveTables(tables);
        tables.add(new TableModel("Table" + 15 + "", true, 4, 2));
        activeTableFragment2.setupActiveTables(tables);
        return mWaiterTableAdapter;
    }

    @Override
    public void onClickCompleted(OrderedItemModel item,int position) {

                mWaiterItemAdapter.mItems.remove(position);
                //items.remove(i);


        rvUserItems.setAdapter(mWaiterItemAdapter);
        mWaiterItemAdapter2.mItems.add(item);
        rvDelivered.setAdapter(mWaiterItemAdapter2);
        rvDelivered.setNestedScrollingEnabled(false);
        rvUserItems.setNestedScrollingEnabled(false);
        mWaiterTableAdapter.setNoItems(mWaiterItemAdapter.mItems.size());
        rvActiveTables.setAdapter(mWaiterTableAdapter);
        mWaiterItemAdapter.setItemInteractionListener(this);
    }


}

