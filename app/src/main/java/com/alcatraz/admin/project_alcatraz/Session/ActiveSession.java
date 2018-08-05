package com.alcatraz.admin.project_alcatraz.Session;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;
import android.widget.Toast;

import com.alcatraz.admin.project_alcatraz.Data.Resource;
import com.alcatraz.admin.project_alcatraz.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ActiveSession extends AppCompatActivity{

    private static final String TAG = ActiveSession.class.getSimpleName();
    private ActiveSessionViewModel mActiveSessionViewModel;
    AdapterFoodWith adapterFoodWith;
    @BindView(R.id.food_with) RecyclerView foodWith;
    @BindView(R.id.button3) Button bill;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_active_session);
        ButterKnife.bind(this);

        foodWith.setLayoutManager(new GridLayoutManager(ActiveSession.this,2));
        adapterFoodWith = new AdapterFoodWith(this,null);
        foodWith.setAdapter(adapterFoodWith);

        mActiveSessionViewModel = ViewModelProviders.of(this, new ActiveSessionViewModel.Factory(getApplication())).get(ActiveSessionViewModel.class);
        mActiveSessionViewModel.getActiveSessionModel().observe(this, activeSessionModel -> {
            if(activeSessionModel == null) return;
            if (activeSessionModel.status == Resource.Status.SUCCESS) {
                adapterFoodWith.setUsers(activeSessionModel.data.getListUsers());
                bill.setText(String.valueOf(activeSessionModel.data.getBill()));
                //TODO complete ordered items
            } else if (activeSessionModel.status == Resource.Status.LOADING) {
                // LOADING
            } else{
                Toast.makeText(ActiveSession.this, "Error fetching active session! Status: " +
                        activeSessionModel.status.toString() + "\nDetails: " + activeSessionModel.message, Toast.LENGTH_LONG).show();
            }
        });
    }
}
