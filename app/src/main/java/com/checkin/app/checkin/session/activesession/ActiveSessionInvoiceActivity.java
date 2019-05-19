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
import com.checkin.app.checkin.Misc.paytm.PaytmPayment;
import com.checkin.app.checkin.R;
import com.checkin.app.checkin.Shop.ShopModel;
import com.checkin.app.checkin.Utility.Constants;
import com.checkin.app.checkin.Utility.Utils;
import com.checkin.app.checkin.session.model.SessionBillModel;
import com.checkin.app.checkin.session.model.SessionInvoiceModel;
import com.checkin.app.checkin.session.model.SessionPromoModel;

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
    @BindView(R.id.ed_invoice_tip)
    EditText edInvoiceTip;
    @BindView(R.id.im_invoice_remove_promo_code)
    ImageView removePromoCode;
    @BindView(R.id.tv_as_promo_applied_details)
    TextView tvAppliedPromoDetails;
    @BindView(R.id.tv_as_promo_invalid_status)
    TextView tvPromoInvalidStatus;
    @BindView(R.id.container_remove_promo_code)
    ViewGroup containerRemovePromo;
    @BindView(R.id.container_promo_code_apply)
    ViewGroup containerApplyPromo;
    @BindView(R.id.container_invoice_tip_waiter)
    ViewGroup containerTipWaiter;
    @BindView(R.id.btn_invoice_request_checkout)
    Button btnRequestCheckout;
    @BindView(R.id.container_as_session_benefits)
    ViewGroup containerSessionBenefits;
    @BindView(R.id.tv_as_session_benefits)
    TextView tvSessionBenefits;

    private ActiveSessionInvoiceViewModel mViewModel;
    private InvoiceOrdersAdapter mAdapter;
    private SessionBillModel mBillModel;
    private BillHolder mBillHolder;
    private SharedPreferences mPrefs;
    private PaytmPayment mPaymentPayTm;
    private ActiveSessionPromoFragment mPromoFragment;

    private ShopModel.PAYMENT_MODE selectedMode;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_active_session_invoice);
        ButterKnife.bind(this);

        mViewModel = ViewModelProviders.of(this).get(ActiveSessionInvoiceViewModel.class);

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
        mPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        String paymentTag = mPrefs.getString(Constants.SP_LAST_USED_PAYMENT_MODE, PAYTM.tag);
        selectedMode = ShopModel.PAYMENT_MODE.getByTag(paymentTag);

        setPaymentModeUpdates();
        rvOrderedItems.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        mAdapter = new InvoiceOrdersAdapter(null);
        rvOrderedItems.setAdapter(mAdapter);
        mBillHolder = new BillHolder(findViewById(android.R.id.content));

        mPromoFragment = ActiveSessionPromoFragment.newInstance();
    }

    private void setPaymentModeUpdates() {
        tvPaymentMode.setText(ShopModel.getPaymentMode(selectedMode));
        tvPaymentMode.setCompoundDrawablesWithIntrinsicBounds(ShopModel.getPaymentModeIcon(selectedMode), 0, 0, 0);
        if (selectedMode != null && selectedMode.equals(PAYTM))
            btnRequestCheckout.setText(R.string.title_pay_checkout);
        else
            btnRequestCheckout.setText(R.string.title_request_checkout);
    }

    private void getData() {
        mViewModel.fetchSessionInvoice();
        mViewModel.fetchAvailablePromoCodes();
        mViewModel.fetchSessionAppliedPromo();

        boolean isRequestedCheckout = getIntent().getBooleanExtra(KEY_SESSION_REQUESTED_CHECKOUT, false);
        if (isRequestedCheckout && !mViewModel.isRequestedCheckout())
            mViewModel.updateRequestCheckout(true);
    }

    private void setupData(SessionInvoiceModel data) {
        mAdapter.setData(data.getOrderedItems());
        mBillModel = data.getBill();

        if (data.getHost() != null)
            Utils.loadImageOrDefault(imWaiterPic, data.getHost().getDisplayPic(), R.drawable.ic_waiter);
        else
            containerTipWaiter.setVisibility(View.GONE);

        mBillHolder.bind(data.getBill());
        edInvoiceTip.setText(data.getBill().formatTip());
        tvInvoiceTotal.setText(Utils.formatCurrencyAmount(this, data.getBill().getTotal()));
//
//        if (data.getBill().getTotalSaving() > 0) {
//            setDiscountInfo("You're saving", data.getBill().getTotalSaving(), getResources().getColor(R.color.apple_green));
//        }
    }

    private void setupObserver() {
        mViewModel.getSessionInvoice().observe(this, resource -> {
            if (resource == null)
                return;
            if (resource.status == Resource.Status.SUCCESS && resource.data != null) {
                setupData(resource.data);
            }
        });

        mViewModel.getCheckoutData().observe(this, statusModelResource -> {
            if (statusModelResource == null)
                return;

            if (statusModelResource.status == Resource.Status.SUCCESS && statusModelResource.data != null) {
                Utils.toast(this, statusModelResource.data.getMessage());
                if (statusModelResource.data.isCheckout())
                    Utils.navigateBackToHome(getApplicationContext());
                else {
                    mViewModel.updateRequestCheckout(true);
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
                Utils.toast(this, statusModelResource.message);
            }
        });

        mViewModel.getRequestedCheckout().observe(this, isRequestedCheckout -> {
            edInvoiceTip.setEnabled(!isRequestedCheckout);

            if (isRequestedCheckout) {
                edInvoiceTip.setBackground(getResources().getDrawable(R.drawable.bordered_text_light_grey));
                edInvoiceTip.setPadding(15, 0, 0, 0);
                btnRequestCheckout.setText("Requested Checkout");
            } else {
                setPaymentModeUpdates();
            }
        });

        mViewModel.getObservableData().observe(this, resource -> {
            if (resource == null)
                return;
            Utils.toast(this, resource.message);
        });

        mViewModel.getSessionAppliedPromo().observe(this, sessionPromoModelResource -> {
            if (sessionPromoModelResource == null)
                return;
            if (sessionPromoModelResource.status == Resource.Status.SUCCESS && sessionPromoModelResource.data != null) {
                showPromoDetails(sessionPromoModelResource.data);
            } else if (sessionPromoModelResource.status == Resource.Status.ERROR_NOT_FOUND) {
                showPromoApply();
            }
        });

        mViewModel.getPromoCodes().observe(this, listResource -> {
            if (listResource == null)
                return;
            if (listResource.status == Resource.Status.SUCCESS && listResource.data != null) {
//                setDiscountInfo("Pay online to avail ", listResource.data.get(0).getName(), getResources().getColor(R.color.primary_red));
            } else if (listResource.status == Resource.Status.ERROR_INVALID_REQUEST) {
                showPromoInvalid();
            } else if (listResource.status != Resource.Status.LOADING) {
                Utils.toast(this, listResource.message);
            }
        });
    }

    private void showPromoInvalid() {
        resetPromoCards();
        tvAppliedPromoDetails.setVisibility(View.VISIBLE);
        tvAppliedPromoDetails.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_error_exclamation, 0, 0, 0);
    }

    private void showPromoApply() {
        resetPromoCards();
        containerApplyPromo.setVisibility(View.VISIBLE);
    }

    private void showPromoDetails(SessionPromoModel data) {
        resetPromoCards();
        containerRemovePromo.setVisibility(View.VISIBLE);
        tvAppliedPromoDetails.setText(data.getDetails());
    }

    private void resetPromoCards() {
        containerRemovePromo.setVisibility(View.GONE);
        containerApplyPromo.setVisibility(View.GONE);
        tvPromoInvalidStatus.setVisibility(View.GONE);
    }

    private void paytmObserver() {
        mPaymentPayTm = new PaytmPayment() {
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
                mPaymentPayTm.initializePayment(paytmModelResource.data, this);
            } else if (paytmModelResource.status != Resource.Status.LOADING) {
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
        if (mViewModel.isRequestedCheckout())
            alertDialogForOverridingPaymentRequest();
        else
            onRequestCheckout(false);
    }

    @OnClick(R.id.container_promo_code_apply)
    public void onPromoCodeClick() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container_promo, mPromoFragment)
                .addToBackStack(null)
                .commit();
    }

    @OnClick(R.id.im_invoice_remove_promo_code)
    public void onRemovePromoCode() {
        mViewModel.removePromoCode();
    }

    private void onRequestCheckout(boolean override) {
        if (selectedMode != null)
            mViewModel.requestCheckout(mBillModel.getTip(), selectedMode, override);
        else
            Utils.toast(this, "Please select the Payment Mode.");
    }

    @OnClick(R.id.container_as_payment_mode_change)
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
                    selectedMode = (ShopModel.PAYMENT_MODE) data.getSerializableExtra(KEY_PAYMENT_MODE_RESULT);
                    mPrefs.edit()
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

    private void alertDialogForOverridingPaymentRequest() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this).setTitle("Override checkout request?")
                .setMessage("A payment request is in progress. Are you sure you want to cancel that request and initiate a new one?")
                .setPositiveButton("Yes", (dialog, which) -> onRequestCheckout(true))
                .setNegativeButton("No", (dialog, which) -> dialog.cancel());
        builder.show();
    }
}
