package com.checkin.app.checkin.Shop.ShopInvoice;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.checkin.app.checkin.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ShopInvoiceDetailFeedbackActivity extends AppCompatActivity {

    @BindView(R.id.iv_shop_invoice_detail_feedback_back)
    ImageView ivShopInvoiceDetailFeedbackBack;
    @BindView(R.id.tv_order_title)
    TextView tvOrderTitle;
    @BindView(R.id.tv_s_id)
    TextView tvSId;
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
    @BindView(R.id.tv_shop_invoice_detail_feedback_bill_title)
    TextView tvShopInvoiceDetailFeedbackBillTitle;
    @BindView(R.id.tv_shop_invoice_detail_feedback_rupee_title)
    TextView tvShopInvoiceDetailFeedbackRupeeTitle;
    @BindView(R.id.tv_shop_invoice_detail_feedback_bill)
    TextView tvShopInvoiceDetailFeedbackBill;
    @BindView(R.id.rl_shop_invoice_detail_feedback)
    RelativeLayout rlShopInvoiceDetailFeedback;

    private static final int TAB_COUNT = 2;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_invoice_detail_feedback);
        ButterKnife.bind(this);

        mSetMyViewPager();
    }

    private void mSetMyViewPager() {
        ShopInvoiceDetailFeedbackPagerAdapter shopInvoiceDetailFeedbackPagerAdapter = new ShopInvoiceDetailFeedbackPagerAdapter(TAB_COUNT,getSupportFragmentManager());
        vpShopInvoiceDetailFeedback.setAdapter(shopInvoiceDetailFeedbackPagerAdapter);
        tlShopInvoiceDetailFeedback.setupWithViewPager(vpShopInvoiceDetailFeedback,true);
    }

    @OnClick(R.id.iv_shop_invoice_detail_feedback_back)
    public void onViewClicked() {
        onBackPressed();
    }
}
