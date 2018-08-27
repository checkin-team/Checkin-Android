package com.checkin.app.checkin.Home;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;

import com.checkin.app.checkin.R;

import java.util.ArrayList;
import java.util.Scanner;

public class RecentCheckInFragment extends Fragment {

    private static final String TAG = "RecentCheckInFragment";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.e(TAG, "onCreateView: FRAGMENT" );
        View view = inflater.inflate(R.layout.layout_recent_checkin,
                container, false);
        view.bringToFront();
        RecyclerView recent=view.findViewById(R.id.recentrv);
        RecyclerView.LayoutManager layoutManager=new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false);
        recent.setLayoutManager(layoutManager);
        ArrayList<Integer> arrayList=new ArrayList<>();
        String arr="0 2 2 2 2 2 1 1 1 1 1 1 1 1 1";
        Scanner sc=new Scanner(arr);
        while(sc.hasNext())
            arrayList.add(Integer.valueOf(sc.next()));
        recent.setAdapter(new RecentsAdapter(arrayList));
        return view;
    }
}
