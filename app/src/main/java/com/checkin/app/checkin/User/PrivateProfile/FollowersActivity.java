package com.checkin.app.checkin.User.PrivateProfile;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.R;
import com.checkin.app.checkin.User.UserModel;
import com.checkin.app.checkin.User.UserRepository;
import com.checkin.app.checkin.User.UserViewModel;

import java.util.ArrayList;
import java.util.List;

public class FollowersActivity extends AppCompatActivity implements CustomFollowersAdapter.ConnectionsUnfollow{
    private RecyclerView mRecyclerView;
    private CustomFollowersAdapter mAdapter;
    private UserViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_followers);

        mRecyclerView = findViewById(R.id.recycler_view);


        mAdapter = new CustomFollowersAdapter(null, FollowersActivity.this );

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setAdapter(mAdapter);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_appbar_back);
        actionBar.setElevation(10);
        actionBar.setTitle("Connections");

        mViewModel = new ViewModelProvider(this, new UserViewModel.Factory(getApplication())).get(UserViewModel.class);
        mViewModel.getAllUsers().observe(this, listResource -> {
           if (listResource != null && listResource.status == Resource.Status.SUCCESS)
               mAdapter.setData(listResource.data);
        });
    }

    @Override
    public void onUnfollowConnection(UserModel userModel) {
        new AlertDialog.Builder(this)
                .setCancelable(false)
                .setTitle("")
                .setMessage("Do you want to remove connection ")
                .setPositiveButton("Remove", (dialog, which) -> {
                    dialog.dismiss();
                    mViewModel.unfollowUser(userModel.getId());
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .show();
    }
}