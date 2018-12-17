package com.checkin.app.checkin.Shop.ShopCover;

import android.media.Image;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;

import com.checkin.app.checkin.R;

import java.util.ArrayList;

public class ShopCoverActivity extends AppCompatActivity {

    ArrayList<ShopModel> model;

    //the recyclerview
    RecyclerView recyclerView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_logo_cover_image);

        initializeData();

    }

    private void initializeData(){
        int i=1;
      model.add(new ShopModel(i++, R.drawable.dummy_shop));
        model.add(new ShopModel(i++, R.drawable.dummy_shop));
        model.add(new ShopModel(i++, R.drawable.dummy_shop));
        model.add(new ShopModel(i++, R.drawable.dummy_shop));
        model.add(new ShopModel(i++, R.drawable.dummy_shop));
        model.add(new ShopModel(i++, R.drawable.dummy_shop));

        initRecyclerView();
    }

    private void initRecyclerView(){
        RecyclerView recyclerView = findViewById(R.id.rv_shop_logo);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        ShopCoverAdapter adapter = new ShopCoverAdapter(model);

        //setting adapter to recyclerview
        recyclerView.setAdapter(adapter);



    }
}
