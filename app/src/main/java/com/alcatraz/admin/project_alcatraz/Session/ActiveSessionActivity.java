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

public class ActiveSessionActivity extends AppCompatActivity{

    private static final String TAG = ActiveSessionActivity.class.getSimpleName();
    private ActiveSessionViewModel mActiveSessionViewModel;
    ActiveSessionMemberAdapter activeSessionMemberAdapter;
    @BindView(R.id.food_with) RecyclerView foodWith;
    @BindView(R.id.button3) Button bill;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_active_session);
        ButterKnife.bind(this);

        foodWith.setLayoutManager(new GridLayoutManager(ActiveSessionActivity.this,2));
        activeSessionMemberAdapter = new ActiveSessionMemberAdapter(this,null);
        foodWith.setAdapter(activeSessionMemberAdapter);

        mActiveSessionViewModel = ViewModelProviders.of(this, new ActiveSessionViewModel.Factory(getApplication())).get(ActiveSessionViewModel.class);
        mActiveSessionViewModel.getActiveSessionDetail().observe(this, activeSessionResource -> {
            if (activeSessionResource == null) return;
            if (activeSessionResource.status == Resource.Status.SUCCESS) {
                activeSessionMemberAdapter.setUsers(activeSessionResource.data.getUsers());
                bill.setText(String.valueOf(activeSessionResource.data.getBill()));
                //TODO complete ordered items
            } else if (activeSessionResource.status == Resource.Status.LOADING) {
                // LOADING
            } else{
                Toast.makeText(ActiveSessionActivity.this, "Error fetching active session! Status: " +
                        activeSessionResource.status.toString() + "\nDetails: " + activeSessionResource.message, Toast.LENGTH_LONG).show();
            }
        });
    }
}
