package com.checkin.app.checkin.session.activesession;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import com.checkin.app.checkin.Utility.Constants;
import com.checkin.app.checkin.session.model.SessionPromoModel;
import com.checkin.app.checkin.session.paytm.PaytmModel;
import com.checkin.app.checkin.session.paytm.PaytmPayment;
import com.checkin.app.checkin.R;
import com.checkin.app.checkin.session.model.SessionBillModel;
import com.checkin.app.checkin.session.model.SessionInvoiceModel;
import com.checkin.app.checkin.Shop.ShopModel;
import com.checkin.app.checkin.Utility.Utils;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;

import static com.checkin.app.checkin.session.activesession.ActiveSessionPaymentOptions.KEY_PAYMENT_MODE_RESULT;
import static com.checkin.app.checkin.session.activesession.ActiveSessionPaymentOptions.KEY_SESSION_AMOUNT;
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
    @BindView(R.id.tv_invoice_promo_code)
    TextView tvPromoCode;
    @BindView(R.id.tv_invoice_promo)
    TextView tvPromoCodeAmount;
    @BindView(R.id.ed_invoice_tip)
    EditText edInvoiceTip;
    @BindView(R.id.container_invoice_tip_waiter)
    ViewGroup tipWaiterContainer;
    @BindView(R.id.btn_invoice_request_checkout)
    Button btnRequestCheckout;
    @BindView(R.id.payment_mode_change_container)
    ViewGroup paymentModeChangeContainer;
    @BindView(R.id.saving_info_container)
    ViewGroup savingInfoContainer;
    @BindView(R.id.container_invoice_promo)
    ViewGroup invoicePromoCodeContainer;

    private ActiveSessionViewModel mViewModel;
    private InvoiceOrdersAdapter mAdapter;
    private SessionBillModel mBillModel;
    private BillHolder mBillHolder;
    private PaytmPayment paytmPayment;
    private SharedPreferences prefs;

    private static final int REQUEST_PAYMENT_MODE = 141;
    private static final int REQUEST_PROMO_CODE_APPLY = 142;
    ShopModel.PAYMENT_MODE selectedMode;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_active_session_invoice);
        ButterKnife.bind(this);

        setupUi();
        getData();
        setupObserver();
        paytmObserver();

    }

    private void setupUi() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_grey);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        String paymentTag = prefs.getString(Constants.SP_LAST_USED_PAYMENT_MODE, PAYTM.tag);
        selectedMode = ShopModel.PAYMENT_MODE.getByTag(paymentTag);

        setPaymentModeUpdates();
        rvOrderedItems.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        mAdapter = new InvoiceOrdersAdapter(null);
        rvOrderedItems.setAdapter(mAdapter);
        mBillHolder = new BillHolder(findViewById(android.R.id.content));
    }

    private void setPaymentModeUpdates(){
        tvPaymentMode.setText(ShopModel.getPaymentMode(selectedMode));
        tvPaymentMode.setCompoundDrawablesWithIntrinsicBounds(ShopModel.getPaymentModeIcon(selectedMode), 0, 0, 0);
        if (selectedMode != null && selectedMode.equals(PAYTM))
            btnRequestCheckout.setText("Pay");
        else
            btnRequestCheckout.setText(getResources().getString(R.string.title_request_checkout));
    }

    private void getData() {
        mViewModel = ViewModelProviders.of(this).get(ActiveSessionViewModel.class);
        mViewModel.fetchSessionInvoice();
        mViewModel.getSessionInvoice().observe(this, resource -> {
            if (resource == null)
                return;
            if (resource.status == Resource.Status.SUCCESS && resource.data != null) {
                SessionInvoiceModel data = resource.data;
                mAdapter.setData(data.getOrderedItems());
                mBillModel = data.getBill();

                if (data.getHost() != null)
                    Utils.loadImageOrDefault(imWaiterPic, data.getHost().getDisplayPic(), R.drawable.ic_waiter);
                else
                    tipWaiterContainer.setVisibility(View.GONE);

                mBillHolder.bind(data.getBill());

                edInvoiceTip.setText(data.getBill().formatTip());

                tvInvoiceTotal.setText(Utils.formatCurrencyAmount(this, data.getBill().getTotal()));

                if(data.getBill().getTotalSaving() != null)
                    setDiscountInfo("You're saving", data.getBill().getTotalSaving());

                boolean isRequestedCheckout = getIntent().getBooleanExtra(KEY_SESSION_REQUESTED_CHECKOUT, false);
                edInvoiceTip.setEnabled(!isRequestedCheckout);
                btnRequestCheckout.setEnabled(!isRequestedCheckout);
                if (isRequestedCheckout) {
                    edInvoiceTip.setBackground(getResources().getDrawable(R.drawable.bordered_text_light_grey));
                    edInvoiceTip.setPadding(15, 0, 0, 0);
                    btnRequestCheckout.setText("Requested Checkout");
                }
            }
        });
    }

    private void setupObserver() {
        mViewModel.getCheckoutData().observe(this, statusModelResource -> {
            if (statusModelResource == null)
                return;

            if (statusModelResource.status == Resource.Status.SUCCESS && statusModelResource.data != null) {
                Utils.toast(this, statusModelResource.data.getMessage());
                if (selectedMode == PAYTM)
                    mViewModel.requestPaytmDetails();
                else if (selectedMode == CASH)
                    if (statusModelResource.data.isCheckout())
                        Utils.navigateBackToHome(getApplicationContext());
                    else
                        finish();

            } else {
                Utils.toast(this, statusModelResource.message);
            }
        });

        mViewModel.getPaytmDetails().observe(this, paytmModelResource -> {
            if (paytmModelResource == null)
                return;
            if (paytmModelResource.status == Resource.Status.SUCCESS && paytmModelResource.data != null) {
                PaytmModel paytmModel = paytmModelResource.data;
                mViewModel.setChecksumCustId(paytmModel.getChecksumHash(), paytmModel.getCustId());
                paytmPayment.initializePayment(paytmModel, this);
            } else {
                Utils.toast(this, paytmModelResource.message);
            }
        });
    }

    private void paytmObserver() {
        paytmPayment = new PaytmPayment() {
            @Override
            protected void onPaytmTransactionResponse(Bundle inResponse) {
                mViewModel.postPaytmCallback(inResponse);
            }

            @Override
            protected void onPaytmTransactionCancel(Bundle inResponse, String msg) {
                Utils.toast(ActiveSessionInvoiceActivity.this, msg);
            }

            @Override
            protected void onPaytmError(String inErrorMessage) {
                Utils.toast(ActiveSessionInvoiceActivity.this, inErrorMessage);
            }
        };

        mViewModel.getObservableData().observe(this, objectNodeResource -> {
            if (objectNodeResource == null)
                return;
            if (objectNodeResource.status == Resource.Status.SUCCESS && objectNodeResource.data != null) {
                Utils.toast(this, objectNodeResource.message);
            }
        });
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
        if (selectedMode != null)
            mViewModel.requestCheckout(mBillModel.getTip(), selectedMode, false);
        else
            Utils.toast(this, "Please select the Payment Mode.");
    }

    @OnClick(R.id.payment_mode_change_container)
    public void onPaymentModeClick() {
        Intent intent = new Intent(this, ActiveSessionPaymentOptions.class);
        intent.putExtra(KEY_SESSION_AMOUNT, tvInvoiceTotal.getText().toString());
        startActivityForResult(intent, REQUEST_PAYMENT_MODE);
    }

    @OnClick(R.id.promo_code_apply_container)
    public void onPromoCodeClick(){
        startActivity(new Intent(this, ActiveSessionPromoActivity.class));
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
                if (resultCode == RESULT_OK && data != null) {
                    paymentModeChangeContainer.setVisibility(View.VISIBLE);
                    selectedMode = (ShopModel.PAYMENT_MODE) data.getSerializableExtra(KEY_PAYMENT_MODE_RESULT);
                    prefs.edit()
                            .putString(Constants.SP_LAST_USED_PAYMENT_MODE, selectedMode.tag)
                            .apply();

                    if (selectedMode.equals(CASH)) {
                        setPaymentModeUpdates();
                    } else {
                        setPaymentModeUpdates();
                        setTotalAmount();
                    }
                    tvPaymentMode.setCompoundDrawablesWithIntrinsicBounds(ShopModel.getPaymentModeIcon(selectedMode), 0, 0, 0);
                }
                break;

            /*case REQUEST_PROMO_CODE_APPLY:
                if (resultCode == RESULT_OK && data != null) {
                    invoicePromoCodeContainer.setVisibility(View.VISIBLE);
                    SessionPromoModel promoModel = (SessionPromoModel) data.getSerializableExtra("session.promo_code");
                    mViewModel.availPromoCode(promoModel.getCode());

//                tvPromoCode.setText("Promo-("+promoModel.getCode()+")");
                tvPromoCodeAmount.setText(promoModel);
                }
                break;*/
            default:
                break;
        }
    }

    @OnTextChanged(R.id.tv_invoice_total)
    public void setTotalAmount() {
        if (selectedMode != null && selectedMode.equals(PAYTM))
            tvPaymentMode.setText(tvInvoiceTotal.getText().toString());
    }

    @Override
    protected void onResume() {
        super.onResume();
//        mViewModel.fetchSessionInvoice();
    }

    private void setDiscountInfo(String label, String offPercent) {
        savingInfoContainer.setVisibility(View.VISIBLE);
        tvSavingInfoLabel.setText(label);
        tvSavingPercent.setText(Utils.formatCurrencyAmount(this, offPercent));
    }
}
