package com.checkin.app.checkin.Shop;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import com.checkin.app.checkin.R;
import com.checkin.app.checkin.Shop.ShopPublicProfile.ShopReviewsAdapter;

import java.util.ArrayList;

public class ShopReviewsActivity extends AppCompatActivity {

    private ReviewsViewModel reviewsViewModel;
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

        mAdapter = new ShopReviewsAdapter();

        reviewsViewModel= ViewModelProviders.of(this,new ReviewsViewModel.Factory(getApplication())).get(ReviewsViewModel.class);

        mBack=findViewById(R.id.btn_back);
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
}
