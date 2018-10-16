package com.checkin.app.checkin.User.PrivateProfile;

import android.arch.lifecycle.ViewModelProvider;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.R;

public class FollowersActivity extends AppCompatActivity implements CustomFollowersAdapter.FriendsInteraction{
    private RecyclerView mRecyclerView;
    private CustomFollowersAdapter mAdapter;
    private FriendsViewModel mViewModel;
    public final static String KEY_USER_PK = "userPk";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_followers);

        Long userPk = getIntent().getLongExtra(KEY_USER_PK,0);

        mRecyclerView = findViewById(R.id.recycler_view);


        mAdapter = new CustomFollowersAdapter(null, FollowersActivity.this );

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setAdapter(mAdapter);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_appbar_back);
        actionBar.setElevation(10);
        actionBar.setTitle("Connections");

        mViewModel = new ViewModelProvider(this, new FriendsViewModel.Factory(getApplication())).get(FriendsViewModel.class);
        mViewModel.setUserPk(userPk);
        mViewModel.getUserFriends().observe(this, listResource -> {
           if (listResource != null && listResource.status == Resource.Status.SUCCESS)
               mAdapter.setData(listResource.data);
        });

        mViewModel.getObservableData().observe(this, resource -> {
            if (resource != null && resource.status == Resource.Status.SUCCESS) {
                mViewModel.updateResults();
            }
        });

    }

    @Override
    public void onUnfollowConnection(FriendshipModel friend) {
        new AlertDialog.Builder(this)
                .setCancelable(false)
                .setTitle("")
                .setMessage("Do you want to remove connection ")
                .setPositiveButton("Remove", (dialog, which) -> {
                    dialog.dismiss();
                    mViewModel.removeFriend(friend.getUserPk());
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .show();
    }

    @Override
    public void onRequestAccepted(FriendshipModel friend) {
        mViewModel.postNewFriends(friend.getUserPk());
    }
}