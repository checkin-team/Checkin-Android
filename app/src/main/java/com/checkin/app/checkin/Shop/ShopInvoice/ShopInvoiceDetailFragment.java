package com.checkin.app.checkin.Shop.ShopInvoice;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.R;

import java.util.List;

public class ShopInvoiceDetailFragment extends Fragment {

    public static final String SESSION_ID = "SESSION_ID";
    int sessionId;

    RecyclerView rv_shop_invoice_detail;
    TextView tv_shop_invoice_detail_feedback_session_time,
             tv_shop_invoice_detail_feedback_avg_confirmation_time,
             tv_shop_invoice_detail_subtotal,
             tv_shop_invoice_detail_charges,
             tv_shop_invoice_detail_taxes,
             tv_shop_invoice_detail_promo;
    TextView tv_shop_invoice_detail_discount,
             tv_shop_invoice_detail_feedback_bill,
             tv_shop_invoice_detail_tip;
    RelativeLayout rl_shop_invoice_detail_feedback;
    LinearLayout ll_shop_invoice_detail_subtotal,
                 ll_shop_invoice_detail_charges,
                 ll_shop_invoice_detail_taxes,
                 ll_shop_invoice_detail_promo,
                 ll_shop_invoice_detail_discount,
                 ll_shop_invoice_detail_tip;

    public static ShopInvoiceDetailFragment newInstance(Integer pk) {
        ShopInvoiceDetailFragment shopInvoiceDetailFragment = new ShopInvoiceDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(SESSION_ID,pk);
        shopInvoiceDetailFragment.setArguments(bundle);
        return shopInvoiceDetailFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            sessionId = savedInstanceState.getInt(SESSION_ID,0);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_shop_invoice_detail,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rv_shop_invoice_detail = view.findViewById(R.id.rv_shop_invoice_detail);
        tv_shop_invoice_detail_feedback_session_time = view.findViewById(R.id.tv_shop_invoice_detail_feedback_session_time);
        tv_shop_invoice_detail_feedback_avg_confirmation_time = view.findViewById(R.id.tv_shop_invoice_detail_feedback_avg_confirmation_time);
        tv_shop_invoice_detail_subtotal = view.findViewById(R.id.tv_shop_invoice_detail_subtotal);
        tv_shop_invoice_detail_charges = view.findViewById(R.id.tv_shop_invoice_detail_charges);
        tv_shop_invoice_detail_taxes = view.findViewById(R.id.tv_shop_invoice_detail_taxes);
        tv_shop_invoice_detail_promo = view.findViewById(R.id.tv_shop_invoice_detail_promo);
        tv_shop_invoice_detail_discount = view.findViewById(R.id.tv_shop_invoice_detail_discount);
        tv_shop_invoice_detail_feedback_bill = view.findViewById(R.id.tv_shop_invoice_detail_feedback_bill);
        tv_shop_invoice_detail_tip = view.findViewById(R.id.tv_shop_invoice_detail_tip);
        rl_shop_invoice_detail_feedback = view.findViewById(R.id.rl_shop_invoice_detail_feedback);
        ll_shop_invoice_detail_subtotal = view.findViewById(R.id.ll_shop_invoice_detail_subtotal);
        ll_shop_invoice_detail_charges = view.findViewById(R.id.ll_shop_invoice_detail_charges);
        ll_shop_invoice_detail_taxes = view.findViewById(R.id.ll_shop_invoice_detail_taxes);
        ll_shop_invoice_detail_promo = view.findViewById(R.id.ll_shop_invoice_detail_promo);
        ll_shop_invoice_detail_discount = view.findViewById(R.id.ll_shop_invoice_detail_discount);
        ll_shop_invoice_detail_tip = view.findViewById(R.id.ll_shop_invoice_detail_tip);


        ShopInvoiceDetailAdapter shopInvoiceDetailAdapter = new ShopInvoiceDetailAdapter();
        rv_shop_invoice_detail.setLayoutManager(new LinearLayoutManager(getActivity()));
        rv_shop_invoice_detail.setAdapter(shopInvoiceDetailAdapter);

        String session_Id = String.valueOf(sessionId);

        ShopSessionViewModel mShopSessionSessionViewModel = ViewModelProviders.of(this).get(ShopSessionViewModel.class);
        mShopSessionSessionViewModel.getShopSessionDetailById(session_Id);
        mShopSessionSessionViewModel.getShopSessioDetailModel().observe(this, input ->{
            if (input == null)
                return;
            if (input.status == Resource.Status.SUCCESS && input.data !=  null){

                Integer avgTime = input.data.getAvgPreparationTime();
                Integer mSessionTime = input.data.getSessionTime();

                tv_shop_invoice_detail_feedback_avg_confirmation_time.setText(String.valueOf(avgTime));
                tv_shop_invoice_detail_feedback_session_time.setText(String.valueOf(mSessionTime));

                ShopSessionDetailModel.Bill mBill = input.data.getBill();

                String mDiscount = mBill.getDiscount();
                String mOffer = mBill.getOffers();
                String mTotal = mBill.getTotal();
                String mTax = mBill.getTax();
                String mTip = mBill.getTip();
                String mSubTotal = mBill.getSubtotal();

                if (mDiscount != null){
                    tv_shop_invoice_detail_discount.setText(mDiscount);
                }else {
                    ll_shop_invoice_detail_discount.setVisibility(View.GONE);
                }

                if (mOffer != null){
                    tv_shop_invoice_detail_promo.setText(mOffer);
                }else {
                    ll_shop_invoice_detail_promo.setVisibility(View.GONE);
                }

                if (mTax != null){
                    tv_shop_invoice_detail_taxes.setText(mTax);
                }else {
                    ll_shop_invoice_detail_taxes.setVisibility(View.GONE);
                }

                if (mTip != null){
                    tv_shop_invoice_detail_tip.setText(mTip);
                }else {
                    ll_shop_invoice_detail_tip.setVisibility(View.GONE);
                }

                if (mSubTotal != null){
                    tv_shop_invoice_detail_subtotal.setText(mSubTotal);
                }else {
                    ll_shop_invoice_detail_subtotal.setVisibility(View.GONE);
                }

                if (mTotal != null){
                    tv_shop_invoice_detail_feedback_bill.setText(mTotal);
                }else {
                    rl_shop_invoice_detail_feedback.setVisibility(View.GONE);
                }


                List<ShopSessionDetailModel.OrderedItem> mList = input.data.getOrderedItems();

                if (mList != null && mList.size() > 0){
                    shopInvoiceDetailAdapter.addSessionDetailData(mList);
                }
            }
        });
    }
}
