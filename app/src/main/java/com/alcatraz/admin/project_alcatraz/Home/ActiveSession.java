package com.alcatraz.admin.project_alcatraz.Home;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.alcatraz.admin.project_alcatraz.R;

import java.util.ArrayList;

public class ActiveSession extends AppCompatActivity{
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_active_session);
        RecyclerView recyclerView=findViewById(R.id.food_with);
        recyclerView.setLayoutManager(new GridLayoutManager(ActiveSession.this,2));
        ArrayList<Integer> intlist=new ArrayList<>();
        ArrayList<String> names=new ArrayList<>();
        for(int i=1;i<20;i++){
            intlist.add(R.drawable.flier);
            names.add("Laura");
        }
        recyclerView.setAdapter(new AdapterFoodWith(intlist,names));
    }
}
