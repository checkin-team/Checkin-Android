package com.checkin.app.checkin.Shop.ShopInvoice;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.checkin.app.checkin.Misc.BriefModel;
import com.checkin.app.checkin.R;

import java.util.ArrayList;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ShopInvoiceDetailFeedbackActivity extends AppCompatActivity {

    @BindView(R.id.iv_shop_invoice_detail_feedback_back)
    ImageView ivShopInvoiceDetailFeedbackBack;
    @BindView(R.id.tv_order_title)
    TextView tvOrderTitle;
    @BindView(R.id.tv_hash_id)
    TextView tvHashId;
    @BindView(R.id.tv_shop_invoice_date)
    TextView tvShopInvoiceDate;
    @BindView(R.id.tv_shop_invoice_waiter_title)
    TextView tvShopInvoiceWaiterTitle;
    @BindView(R.id.tv_shop_invoice_waiter_name)
    TextView tvShopInvoiceWaiterName;
    @BindView(R.id.tv_shop_invoice_view)
    View tvShopInvoiceView;
    @BindView(R.id.tv_shop_invoice_item)
    TextView tvShopInvoiceItem;
    @BindView(R.id.tv_shop_invoice_user_number)
    TextView tvShopInvoiceUserNumber;
    @BindView(R.id.tv_shop_invoice_table_number)
    TextView tvShopInvoiceTableNumber;
    @BindView(R.id.ll_shop_invoice_detail_feedback)
    LinearLayout llShopInvoiceDetailFeedback;
    @BindView(R.id.tl_shop_invoice_detail_feedback)
    TabLayout tlShopInvoiceDetailFeedback;
    @BindView(R.id.vp_shop_invoice_detail_feedback)
    ViewPager vpShopInvoiceDetailFeedback;

    public static final String ORDER_DETAIL = "ORDER_DETAIL";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_invoice_detail_feedback);
        ButterKnife.bind(this);

        RestaurantSessionModel data = (RestaurantSessionModel) getIntent().getSerializableExtra("ORDER_DETAIL");

        if (data != null){
            mSetMyToolbarData(data);
            mSetMyViewPager(data);
        }
    }

    private void mSetMyToolbarData(RestaurantSessionModel data) {
        Date mDate = data.getCheckedOut();
        Integer mCountCustomer = data.getCountCustomers();
        Integer mCountOrder = data.getCountOrders();
        String mHashId = data.getHashId();
        String mTable = data.getTable();

        BriefModel mHost = data.getHost();

        if (mHost != null){
            tvShopInvoiceWaiterName.setText(mHost.getDisplayName());
        }else {
            tvShopInvoiceWaiterName.setText("Unassigned");
        }

        tvHashId.setText(mHashId);

        try{
            String mFinalDate = data.getFormattedDate(mDate);
            tvShopInvoiceDate.setText(mFinalDate);
        }catch (Exception ex){
            Log.d("Parse Exception",ex.getLocalizedMessage());
        }
        tvShopInvoiceUserNumber.setText(String.valueOf(mCountCustomer));
        tvShopInvoiceItem.setText(String.format("%s %s",mCountOrder,"Item's"));
        tvShopInvoiceTableNumber.setText(mTable);
    }

    private void mSetMyViewPager(RestaurantSessionModel data) {

        ArrayList<Fragment> mFragmentList = new ArrayList<>();
        ArrayList<String> mTitleList =  new ArrayList<>();
         ;
        mFragmentList.add(ShopInvoiceDetailFragment.newInstance(data.getPk()));
        mFragmentList.add(ShopInvoiceFeedbackFragment.newInstance(data.getPk()));

        mTitleList.add("DETAILS");
        mTitleList.add("FEEDBACKS");

        ShopInvoiceDetailFeedbackPagerAdapter shopInvoiceDetailFeedbackPagerAdapter = new ShopInvoiceDetailFeedbackPagerAdapter(mFragmentList,mTitleList,getSupportFragmentManager());
        vpShopInvoiceDetailFeedback.setAdapter(shopInvoiceDetailFeedbackPagerAdapter);
        tlShopInvoiceDetailFeedback.setupWithViewPager(vpShopInvoiceDetailFeedback,true);
    }

    @OnClick(R.id.iv_shop_invoice_detail_feedback_back)
    public void onViewClicked() {
        onBackPressed();
    }
}
