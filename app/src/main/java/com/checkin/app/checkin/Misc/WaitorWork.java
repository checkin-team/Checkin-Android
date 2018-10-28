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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.checkin.app.checkin.Home.TrendingShopAdapter;
import com.checkin.app.checkin.Menu.MenuItemModel;
import com.checkin.app.checkin.Menu.OrderedItemModel;
import com.checkin.app.checkin.R;
import com.checkin.app.checkin.Shop.ShopModel;
import com.checkin.app.checkin.Utility.EndDrawerToggle;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WaitorWork extends AppCompatActivity implements WaiterActiveTableAdapter.onTableInterActionListener
    {
    @BindView(R.id.rv_active_tables)
    RecyclerView rvActiveTables;
    @BindView(R.id.drawer_open)
        ImageView drawerToggle;
        @BindView(R.id.action_table)
        ImageView tableToggle;
    @BindView(R.id.item_container)
    View itemContainer;
    @BindView(R.id.root_view)
    View vRoot;

    List<EventModel> items,itemsDone;
        List<TableModel> tables;
    private WaiterActiveTableAdapter mWaiterTableAdapter;
    private WaiterItemAdapter mWaiterItemAdapter;
    private WaiterItemAdapter mWaiterItemAdapter2;
        ActiveTableFragment activeTableFragment;
        WaiterEndNavigationTableAdapter waiterEndNavigationTableAdapter;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiter_work);
        ButterKnife.bind(this);

                setupUI();

        setupUserItem(new ArrayList<>());



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


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_waiter_profiles);
        NavigationHeaderFragment navigationHeaderFragment=new NavigationHeaderFragment();
        navigationHeaderFragment.setDetails("Vivek Sharma","Waiter's account");
        getFragmentManager().beginTransaction().add(navigationView.getId(),navigationHeaderFragment).commit();




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
    }

    private void setupUserItem(List<EventModel> eventModels) {
        TableItemFragment tableItemFragment =new TableItemFragment();

        getFragmentManager().beginTransaction()
                .replace(itemContainer.getId(), tableItemFragment)
                .commit();

        List<String> list=new ArrayList<>();
        items = new ArrayList<>();
        itemsDone = new ArrayList<>();

        for(int i=0;i<eventModels.size();i++)
        {
            if(eventModels.get(i).getStatus()== EventModel.STATUS.INCOMPLETE)
            {
                items.add(eventModels.get(i));
            }
            else
                itemsDone.add(eventModels.get(i));
        }

    }

    public WaiterActiveTableAdapter setupActiveTables() {

        for (int i = 1; i <= 15; i++) {
            if (i % 2 == 0)
                tables.add(new TableModel("Table" + i + "","STANDARD", true, 4, 2));
        }
            rvActiveTables.setLayoutManager(new LinearLayoutManager(
                    this, LinearLayoutManager.HORIZONTAL, false));
        mWaiterTableAdapter = new WaiterActiveTableAdapter(tables);
        mWaiterTableAdapter.setNoItems(items.size());
        mWaiterTableAdapter.setOnTableInterActionListener(this);
        rvActiveTables.setAdapter(mWaiterTableAdapter);
        activeTableFragment.setupActiveTables(tables);
        tables.add(new TableModel("Table" + 15 + "","PREMIUM", true, 4, 2));
        waiterEndNavigationTableAdapter=new WaiterEndNavigationTableAdapter(tables);
        return mWaiterTableAdapter;
    }


        @Override
        public void selectedTableChanged(int position) {
            mWaiterTableAdapter.setItem_position(position);
            mWaiterTableAdapter.notifyDataSetChanged();
        }
    }

