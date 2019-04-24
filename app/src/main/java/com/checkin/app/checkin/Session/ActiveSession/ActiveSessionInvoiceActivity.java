package com.checkin.app.checkin.Session.ActiveSession;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.Misc.BillHolder;
import com.checkin.app.checkin.Session.Paytm.PaytmModel;
import com.checkin.app.checkin.Session.Paytm.PaytmPayment;
import com.checkin.app.checkin.R;
import com.checkin.app.checkin.Session.Model.SessionBillModel;
import com.checkin.app.checkin.Session.Model.SessionInvoiceModel;
import com.checkin.app.checkin.Shop.ShopModel;
import com.checkin.app.checkin.Utility.Utils;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;

import static com.checkin.app.checkin.Session.ActiveSession.ActiveSessionPaymentOptions.KEY_PAYMENT_MODE_RESULT;
import static com.checkin.app.checkin.Session.ActiveSession.ActiveSessionPaymentOptions.KEY_SESSION_AMOUNT;
import static com.checkin.app.checkin.Shop.ShopModel.PAYMENT_MODE.CASH;
import static com.checkin.app.checkin.Shop.ShopModel.PAYMENT_MODE.PAYTM;

public class ActiveSessionInvoiceActivity extends AppCompatActivity {
    public static final String KEY_SESSION_REQUESTED_CHECKOUT = "invoice.session..requested_checkout";

    @BindView(R.id.rv_invoice_ordered_items)
    RecyclerView rvOrderedItems;
    @BindView(R.id.im_invoice_waiter)
    ImageView imWaiterPic;
    @BindView(R.id.tv_invoice_tip)
    TextView tvInvoiceTip;
    @BindView(R.id.tv_invoice_total)
    TextView tvInvoiceTotal;
    @BindView(R.id.tv_as_payment_mode)
    TextView tvPaymentMode;
    @BindView(R.id.tv_saving_info_label)
    TextView tvSavingInfoLabel;
    @BindView(R.id.tv_saving_percent)
    TextView tvSavingPercent;
    @BindView(R.id.ed_invoice_tip)
    EditText edInvoiceTip;
    @BindView(R.id.btn_invoice_request_checkout)
    Button btnRequestCheckout;
    @BindView(R.id.btn_select_payment_mode)
    Button btnSelectPaymentMode;
    @BindView(R.id.payment_mode_change_container)
    ViewGroup paymentModeChangeContainer;
    @BindView(R.id.saving_info_container)
    ViewGroup savingInfoContainer;

    private ActiveSessionViewModel mViewModel;
    private InvoiceOrdersAdapter mAdapter;
    private SessionBillModel mBillModel;
    private BillHolder mBillHolder;
    private PaytmPayment paytmPayment;

    private static final int REQUEST_PAYMENT_MODE = 141;
    ShopModel.PAYMENT_MODE selectedMode;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_active_session_invoice);
        ButterKnife.bind(this);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_grey);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        rvOrderedItems.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        mAdapter = new InvoiceOrdersAdapter(null);
        rvOrderedItems.setAdapter(mAdapter);
        mBillHolder = new BillHolder(findViewById(android.R.id.content));

        mViewModel = ViewModelProviders.of(this).get(ActiveSessionViewModel.class);
        mViewModel.fetchSessionInvoice();
        mViewModel.getSessionInvoice().observe(this, resource -> {
            if (resource == null)
                return;
            if (resource.status == Resource.Status.SUCCESS && resource.data != null) {
                setupUi(resource.data);
            }
        });

        mViewModel.getCheckoutData().observe(this, statusModelResource -> {
            if (statusModelResource == null)
                return;
            switch (statusModelResource.status) {
                case SUCCESS: {
                    if (statusModelResource.data != null) {
                        Utils.toast(this, statusModelResource.data.getMessage());
                        if (selectedMode == PAYTM) {
                            mViewModel.requestPaytmDetails();
                        }

                        if (statusModelResource.data.isCheckout()) {
//                            Utils.navigateBackToHome(getApplicationContext());
                        } else {
//                            finish();
                        }
                    }
                    break;
                }
                case LOADING:
                    break;
                default: {
                    Utils.toast(this, statusModelResource.message);
                }
            }
        });

        mViewModel.getPaytmDetails().observe(this, paytmModelResource -> {
            if (paytmModelResource == null)
                return;
            switch (paytmModelResource.status) {
                case SUCCESS: {
                    PaytmModel paytmModel = paytmModelResource.data;
                    if (paytmModel!= null){
                        mViewModel.setChecksumCustId(paytmModel.getChecksumHash(),paytmModel.getCustId());
                        paytmPayment.initializePayment(paytmModel, this);
                    }

                    break;
                }
                case LOADING:
                    break;
                default: {
                    Utils.toast(this, paytmModelResource.message);
                }
            }
        });


        paytmPayment = new PaytmPayment() {
            @Override
            protected void onPaytmTransactionResponse(Bundle inResponse) {
                Log.e("OnSuccess===", inResponse.toString() + "=====");
                mViewModel.postPaytmCallback(inResponse);
            }

            @Override
            protected void onPaytmTransactionCancel(Bundle inResponse, String msg) {
                Utils.toast(ActiveSessionInvoiceActivity.this,msg);
            }

            @Override
            protected void onPaytmError(String inErrorMessage) {
                Utils.toast(ActiveSessionInvoiceActivity.this,inErrorMessage);
            }
        };

        mViewModel.getObservableData().observe(this, objectNodeResource -> {
            if (objectNodeResource == null)
                return;
            if (objectNodeResource.status == Resource.Status.SUCCESS && objectNodeResource.data != null) {
                Utils.toast(this,objectNodeResource.message);
            }
        });
    }

    private void setupUi(SessionInvoiceModel data) {
        mAdapter.setData(data.getOrderedItems());
        mBillModel = data.getBill();

        if (data.getHost() != null)
            Utils.loadImageOrDefault(imWaiterPic, data.getHost().getDisplayPic(), R.drawable.ic_waiter);

        mBillHolder.bind(data.getBill());

        edInvoiceTip.setText(data.getBill().formatTip());
        // Total
        tvInvoiceTotal.setText(Utils.formatCurrencyAmount(this, data.getBill().getTotal()));

        boolean isRequestedCheckout = getIntent().getBooleanExtra(KEY_SESSION_REQUESTED_CHECKOUT, false);
        edInvoiceTip.setEnabled(!isRequestedCheckout);
        btnRequestCheckout.setEnabled(!isRequestedCheckout);
        if (isRequestedCheckout) {
            edInvoiceTip.setBackground(getResources().getDrawable(R.drawable.bordered_text_light_grey));
            edInvoiceTip.setPadding(15, 0, 0, 0);
            btnRequestCheckout.setText("Requested Checkout");
        }
    }

    @OnTextChanged(value = R.id.ed_invoice_tip, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void onTipChange(Editable editable) {
        double amount = 0d;
        try {
            amount = Double.parseDouble(editable.toString());
        } catch (NumberFormatException ignored) {
        }
        if (mBillModel != null) {
            mBillModel.giveTip(amount);
            tvInvoiceTip.setText(Utils.formatCurrencyAmount(this, mBillModel.getTip()));
            tvInvoiceTotal.setText(Utils.formatCurrencyAmount(this, mBillModel.getTotal()));
        }
    }

    @OnClick(R.id.btn_invoice_request_checkout)
    public void onRequestCheckout() {
        mViewModel.requestCheckout(mBillModel.getTip(), selectedMode, false);


    }

    @OnClick(R.id.payment_mode_change_container)
    public void onPaymentModeClick() {
        Intent intent = new Intent(this, ActiveSessionPaymentOptions.class);
        intent.putExtra(KEY_SESSION_AMOUNT, tvInvoiceTotal.getText().toString());
        startActivityForResult(intent, REQUEST_PAYMENT_MODE);
    }

    @OnClick(R.id.btn_select_payment_mode)
    public void onSelectPaymentMode() {
        onPaymentModeClick();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQUEST_PAYMENT_MODE:
                if (resultCode == RESULT_OK) {
                    if (data != null) {
                        btnSelectPaymentMode.setVisibility(View.GONE);
                        paymentModeChangeContainer.setVisibility(View.VISIBLE);
                        selectedMode = (ShopModel.PAYMENT_MODE) data.getSerializableExtra(KEY_PAYMENT_MODE_RESULT);
//                        tvPaymentMode.setText(ShopModel.getPaymentMode(selectedMode));
                        if(selectedMode.equals(CASH)){
                            tvPaymentMode.setText("via Cash");
                        }else {
                            setTotalAmount();
                        }
                        tvPaymentMode.setCompoundDrawablesWithIntrinsicBounds(ShopModel.getPaymentModeIcon(selectedMode),0,0,0);
                    }
                }
                break;
        }
    }

    @OnTextChanged(R.id.tv_invoice_total)
    public void setTotalAmount(){
        if(selectedMode!= null && selectedMode.equals(PAYTM))
        tvPaymentMode.setText(tvInvoiceTotal.getText().toString());
    }

    private void setDiscountInfo(String label, String offPercent){
        savingInfoContainer.setVisibility(View.VISIBLE);
        tvSavingInfoLabel.setText(label);
        tvSavingPercent.setText(offPercent);
    }

    private void alertDialogForOverridingPaymentRequest() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this).setTitle("Already a payment request is processing! Are you sure you want to initiate a new request?")
                .setPositiveButton("Yes", (dialog, which) -> onRequestCheckout())
                .setNegativeButton("No", (dialog, which) -> dialog.cancel());
        builder.show();
    }
}
