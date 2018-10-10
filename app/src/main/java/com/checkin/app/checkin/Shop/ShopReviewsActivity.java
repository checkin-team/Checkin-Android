package com.checkin.app.checkin.Shop;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import com.checkin.app.checkin.R;
import com.checkin.app.checkin.Shop.ShopPublicProfile.ShopReviewsAdapter;

import java.util.ArrayList;

public class ShopReviewsActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private ShopReviewsAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<ShopReviewPOJO> listContentArr= new ArrayList<>();
    private Button mBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_reviews);
        mRecyclerView = (RecyclerView) findViewById(R.id.reviews_recycler_view);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new ShopReviewsAdapter(this);

        populateRecyclerViewValues();

        mBack=findViewById(R.id.btn_back);
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }

    private void populateRecyclerViewValues() {

        for(int iter=1;iter<=50;iter++) {
            //Creating POJO class object
            ShopReviewPOJO pojoObject = new ShopReviewPOJO();
            //Values are binded using set method of the POJO class
            pojoObject.setName("Ishu Darshan");
            pojoObject.setReviewsAndFollowers("86 reviews,50 followers");
            pojoObject.setTime("14 hours ago");
            pojoObject.setFullReview("A paragraph (from the Ancient Greek παράγραφος paragraphos, \"to write beside\" or \"written beside\") is a self-contained unit of a discourse in writing dealing with a particular point or idea. A paragraph consists of one or more sentences.[1] Though not required by the syntax of any language, paragraphs are usually an expected part of formal writing, used to organize longer prose");
            pojoObject.setRating(5);
            pojoObject.setTotalVisits("54 total visits");
            //After setting the values, we add all the Objects to the array
            //Hence, listConentArr is a collection of Array of POJO objects
            listContentArr.add(pojoObject);
        }
        //We set the array to the adapter
        mAdapter.setListContent(listContentArr);
        //We in turn set the adapter to the RecyclerView
        mRecyclerView.setAdapter(mAdapter);
    }
}
