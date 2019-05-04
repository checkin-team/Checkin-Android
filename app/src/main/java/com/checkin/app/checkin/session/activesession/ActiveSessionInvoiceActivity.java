package com.checkin.app.checkin.session.activesession;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.Misc.BillHolder;
import com.checkin.app.checkin.Misc.paytm.PaytmModel;
import com.checkin.app.checkin.Misc.paytm.PaytmPayment;
import com.checkin.app.checkin.R;
import com.checkin.app.checkin.Shop.ShopModel;
import com.checkin.app.checkin.Utility.Constants;
import com.checkin.app.checkin.Utility.Utils;
import com.checkin.app.checkin.session.model.SessionBillModel;
import com.checkin.app.checkin.session.model.SessionInvoiceModel;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;

import static com.checkin.app.checkin.Shop.ShopModel.PAYMENT_MODE.CASH;
import static com.checkin.app.checkin.Shop.ShopModel.PAYMENT_MODE.PAYTM;
import static com.checkin.app.checkin.session.activesession.ActiveSessionPaymentOptions.KEY_PAYMENT_MODE_RESULT;
import static com.checkin.app.checkin.session.activesession.ActiveSessionPaymentOptions.KEY_SESSION_AMOUNT;

public class ActiveSessionInvoiceActivity extends AppCompatActivity {
    public static final String KEY_SESSION_REQUESTED_CHECKOUT = "invoice.session.requested_checkout";
    private static final int REQUEST_PAYMENT_MODE = 141;

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
    @BindView(R.id.container_invoice_tip_waiter)
    ViewGroup tipWaiterContainer;
    @BindView(R.id.btn_invoice_request_checkout)
    Button btnRequestCheckout;
    @BindView(R.id.payment_mode_change_container)
    ViewGroup paymentModeChangeContainer;
    @BindView(R.id.saving_info_container)
    ViewGroup savingInfoContainer;

    private ActiveSessionViewModel mViewModel;
    private InvoiceOrdersAdapter mAdapter;
    private SessionBillModel mBillModel;
    private BillHolder mBillHolder;
    private PaytmPayment paytmPayment;
    private SharedPreferences prefs;

    private ShopModel.PAYMENT_MODE selectedMode;

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

    private void setPaymentModeUpdates() {
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
                setupData(resource.data);
            }
        });
    }

    private void setupData(SessionInvoiceModel data) {
        mAdapter.setData(data.getOrderedItems());
        mBillModel = data.getBill();

        if (data.getHost() != null)
            Utils.loadImageOrDefault(imWaiterPic, data.getHost().getDisplayPic(), R.drawable.ic_waiter);
        else
            tipWaiterContainer.setVisibility(View.GONE);

        mBillHolder.bind(data.getBill());

        edInvoiceTip.setText(data.getBill().formatTip());

        tvInvoiceTotal.setText(Utils.formatCurrencyAmount(this, data.getBill().getTotal()));

        boolean isRequestedCheckout = getIntent().getBooleanExtra(KEY_SESSION_REQUESTED_CHECKOUT, false);
        edInvoiceTip.setEnabled(!isRequestedCheckout);

        if (isRequestedCheckout) {
            edInvoiceTip.setBackground(getResources().getDrawable(R.drawable.bordered_text_light_grey));
            edInvoiceTip.setPadding(15, 0, 0, 0);
            btnRequestCheckout.setText("Requested Checkout");
        }
    }

    private void setupObserver() {
        mViewModel.getCheckoutData().observe(this, statusModelResource -> {
            if (statusModelResource == null)
                return;

            if (statusModelResource.status == Resource.Status.SUCCESS && statusModelResource.data != null) {
                Utils.toast(this, statusModelResource.data.getMessage());
                if (statusModelResource.data.isCheckout())
                    Utils.navigateBackToHome(getApplicationContext());
                else {
                    selectedMode = ShopModel.PAYMENT_MODE.getByTag(statusModelResource.data.getPaymentMode());
                    switch (selectedMode) {
                        case PAYTM:
                            mViewModel.requestPaytmDetails();
                            break;
                        case CASH:
                            finish();
                            break;
                        default:
                            break;
                    }
                }
            } else if (statusModelResource.status != Resource.Status.LOADING) {
                if ("Session already requested to checkout.".equals(statusModelResource.message)) {
                    alertDialogForOverridingPaymentRequest();
                } else
                    Utils.toast(this, statusModelResource.message);
            }
        });

        mViewModel.getObservableData().observe(this, resource -> {
            if (resource == null)
                return;
            Utils.toast(this, resource.message);
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

        mViewModel.getPaytmDetails().observe(this, paytmModelResource -> {
            if (paytmModelResource == null)
                return;
            if (paytmModelResource.status == Resource.Status.SUCCESS && paytmModelResource.data != null) {
                PaytmModel paytmModel = paytmModelResource.data;
                paytmPayment.initializePayment(paytmModel, this);
            } else {
                Utils.toast(this, paytmModelResource.message);
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
    public void onClickCheckout() {
        onRequestCheckout(false);
    }

    private void onRequestCheckout(boolean override) {
        if (selectedMode != null)
            mViewModel.requestCheckout(mBillModel.getTip(), selectedMode, override);
        else
            Utils.toast(this, "Please select the Payment Mode.");
    }

    @OnClick(R.id.payment_mode_change_container)
    public void onPaymentModeClick() {
        Intent intent = new Intent(this, ActiveSessionPaymentOptions.class);
        intent.putExtra(KEY_SESSION_AMOUNT, tvInvoiceTotal.getText().toString());
        startActivityForResult(intent, REQUEST_PAYMENT_MODE);
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
            default:
                break;
        }
    }

    @OnTextChanged(R.id.tv_invoice_total)
    public void setTotalAmount() {
        if (selectedMode != null && selectedMode.equals(PAYTM))
            tvPaymentMode.setText(tvInvoiceTotal.getText().toString());
    }

    private void setDiscountInfo(String label, String offPercent) {
        savingInfoContainer.setVisibility(View.VISIBLE);
        tvSavingInfoLabel.setText(label);
        tvSavingPercent.setText(offPercent);
    }

    private void alertDialogForOverridingPaymentRequest() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this).setTitle("Override checkout request?")
                .setMessage("A payment request is in progress. Are you sure you want to cancel that request and initiate a new one?")
                .setPositiveButton("Yes", (dialog, which) -> onRequestCheckout(true))
                .setNegativeButton("No", (dialog, which) -> dialog.cancel());
        builder.show();
    }
}
