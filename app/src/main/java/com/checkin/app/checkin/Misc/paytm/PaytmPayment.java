package com.checkin.app.checkin.Misc.paytm;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.checkin.app.checkin.R;
import com.checkin.app.checkin.Utility.Constants;
import com.paytm.pgsdk.PaytmPGService;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;

import static com.checkin.app.checkin.Utility.Constants.getPaytmService;

public abstract class PaytmPayment {
    private static final String TAG = PaytmPayment.class.getSimpleName();

    public void initializePayment(PaytmModel paytmModel, Context context) {
        PaytmPGService paytmPGService = getPaytmService();
        paytmPGService.initialize(paytmModel.getPaytmOrder(), null);

        paytmPGService.startPaymentTransaction(context, true, true, new PaytmPaymentTransactionCallback() {
            @Override
            public void onTransactionResponse(Bundle inResponse) {
                Log.e(TAG, "Response: " + inResponse.toString());
                onPaytmTransactionResponse(inResponse);
            }

            @Override
            public void networkNotAvailable() {
                onPaytmError(context.getResources().getString(R.string.error_unavailable_network));
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
                onPaytmError("Transaction cancelled");
            }

            @Override
            public void onTransactionCancel(String inErrorMessage, Bundle inResponse) {
                Log.e(TAG, "Cancel: " + inResponse.toString());
                onPaytmTransactionCancel(inResponse, "Transaction cancelled" + inErrorMessage);
            }
        });
    }

    protected abstract void onPaytmTransactionResponse(Bundle inResponse);

    protected abstract void onPaytmTransactionCancel(Bundle inResponse, String msg);

    protected abstract void onPaytmError(String inErrorMessage);
}
