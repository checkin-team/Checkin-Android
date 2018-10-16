package com.checkin.app.checkin.User.NonPersonalProfile;
// Created by viBHU on 17-10-2018

import android.nfc.Tag;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.checkin.app.checkin.R;
import com.checkin.app.checkin.User.PrivateProfile.CustomFollowersAdapter;

import java.util.ArrayList;

public class UserCheckinsActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private CustomCheckinsAdapter mAdapter;
    private ArrayList<CheckinsPOJO> mCheckins = new ArrayList<>();
    private static final String TAG = UserCheckinsActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_checkins);

        mRecyclerView = findViewById(R.id.recycler_view);

        mAdapter = new CustomCheckinsAdapter(mCheckins);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setAdapter(mAdapter);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_appbar_back);
        actionBar.setElevation(10);
        actionBar.setTitle("Checkins");

        prepareData();

    }
    public void prepareData(){
        CheckinsPOJO checkins = new CheckinsPOJO("Romas Cafe", "Checkins");
        mCheckins.add(checkins);
        CheckinsPOJO checkins1 = new CheckinsPOJO("Romas Cafe", "Checkins");
        mCheckins.add(checkins1);
        CheckinsPOJO checkins2 = new CheckinsPOJO("Romas Cafe", "Checkins");
        mCheckins.add(checkins2);
        CheckinsPOJO checkins3 = new CheckinsPOJO("Romas Cafe", "Checkins");
        mCheckins.add(checkins3);
        CheckinsPOJO checkins4 = new CheckinsPOJO("Romas Cafe", "Checkins");
        mCheckins.add(checkins4);
        CheckinsPOJO checkins5 = new CheckinsPOJO("Romas Cafe", "Checkins");
        mCheckins.add(checkins5);
        CheckinsPOJO checkins6 = new CheckinsPOJO("Romas Cafe", "Checkins");
        mCheckins.add(checkins6);
        CheckinsPOJO checkins7 = new CheckinsPOJO("Romas Cafe", "Checkins");
        mCheckins.add(checkins7);
        CheckinsPOJO checkins8 = new CheckinsPOJO("Romas Cafe", "Checkins");
        mCheckins.add(checkins8);
        CheckinsPOJO checkins9 = new CheckinsPOJO("Romas Cafe", "Checkins");
        mCheckins.add(checkins9);
        mAdapter.notifyDataSetChanged();
        Log.e(TAG,checkins.toString());
    }
}
