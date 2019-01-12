package com.checkin.app.checkin.Session.ActiveSession.ActiveSessionChat;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.checkin.app.checkin.R;

import java.util.ArrayList;
import java.util.List;


public class FragementActivitySession2 extends Fragment {

    RecyclerView recyclerView;
    ActiveSessionChatTwoAdapter adapter;
    List<ActiveSessionChatTwoModel> ChatList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_activity_session_2,container,false);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.rv_as2_chat);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext(), LinearLayoutManager.VERTICAL, true));
        ChatList = new ArrayList<>();

        ChatList.add(
                new ActiveSessionChatTwoModel(1,"been the industry's standard dummy text ever since the 1500s, when an unknown printer took a" ,"10:30")
        );

        ChatList.add(
                new ActiveSessionChatTwoModel(1,"been the industry's standard dummy text ever since the 1500s, when an unknown printer took a" ,"10:30")
        );
        ChatList.add(
                new ActiveSessionChatTwoModel(2,"been the industry's standard dummy text ever since the 1500s, when an unknown printer took a" ,"10:30")
        );


        adapter = new ActiveSessionChatTwoAdapter(getActivity(),ChatList);
        recyclerView.setAdapter(adapter);

        return view;
    }


}
