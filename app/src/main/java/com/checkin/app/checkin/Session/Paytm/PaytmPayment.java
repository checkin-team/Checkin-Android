package com.checkin.app.checkin.Session.Paytm;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.checkin.app.checkin.R;
import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPGService;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;

import java.util.HashMap;

public abstract class PaytmPayment {


    public void initializePayment(PaytmModel paytmModel, Activity activity){
        PaytmPGService Service = PaytmPGService.getStagingService();

        HashMap<String, String> paramMap = new HashMap<>();
        paramMap.put("MID", paytmModel.getMerchantId());
        paramMap.put("ORDER_ID", paytmModel.getOrderId());
        paramMap.put("CUST_ID", paytmModel.getCustId());
        paramMap.put("MOBILE_NO" , paytmModel.getPhone());
        paramMap.put("EMAIL" , paytmModel.getEmail());
        paramMap.put("CHANNEL_ID", "WAP");
        paramMap.put("TXN_AMOUNT", paytmModel.getAmount());
        paramMap.put("WEBSITE", paytmModel.getWebsite());
        paramMap.put("INDUSTRY_TYPE_ID", paytmModel.getIndustryTypeId());
        paramMap.put("CALLBACK_URL", paytmModel.getCallbackURL());
        paramMap.put("CHECKSUMHASH", paytmModel.getChecksumHash());

        Log.e("checksumhash", paytmModel.getChecksumHash() + "");
        PaytmOrder order = new PaytmOrder(paramMap);
        Service.initialize(order, null);

        Service.startPaymentTransaction(activity, true, true, new PaytmPaymentTransactionCallback() {
            @Override
            public void onTransactionResponse(Bundle inResponse) {
                onPaytmTransactionResponse(inResponse);
            }

            @Override
            public void networkNotAvailable() {
                onPaytmError(activity.getResources().getString(R.string.error_unavailable_network));
            }

            @Override
            public void clientAuthenticationFailed(String inErrorMessage) {
                onPaytmError("Authentication failed: Server error " + inErrorMessage);
            }

            @Override
            public void someUIErrorOccurred(String inErrorMessage) {
                onPaytmError("UI Error " + inErrorMessage);
            }

            @Override
            public void onErrorLoadingWebPage(int iniErrorCode, String inErrorMessage, String inFailingUrl) {
                onPaytmError("Unable to load webpage " + inErrorMessage);
            }

            @Override
            public void onBackPressedCancelTransaction() {
                onPaytmError("Transaction cancelled" );
            }

            @Override
            public void onTransactionCancel(String inErrorMessage, Bundle inResponse) {
                onPaytmTransactionCancel(inResponse, "Transaction cancelled" + inErrorMessage );
            }
        });

    }

    protected abstract void onPaytmTransactionResponse(Bundle inResponse);
    protected abstract void onPaytmTransactionCancel(Bundle inResponse, String msg);
    protected abstract void onPaytmError(String inErrorMessage);

}
