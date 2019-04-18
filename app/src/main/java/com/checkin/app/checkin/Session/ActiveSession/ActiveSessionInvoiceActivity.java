package com.checkin.app.checkin.Session.ActiveSession;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.Misc.BillHolder;
import com.checkin.app.checkin.R;
import com.checkin.app.checkin.Session.Model.SessionBillModel;
import com.checkin.app.checkin.Session.Model.SessionInvoiceModel;
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

import static com.checkin.app.checkin.Session.ActiveSession.ActiveSessionPaymentOptions.KEY_PAYMENT_MODE_RESULT;

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
    @BindView(R.id.ed_invoice_tip)
    EditText edInvoiceTip;
    @BindView(R.id.btn_invoice_request_checkout)
    Button btnRequestCheckout;

    private ActiveSessionViewModel mViewModel;
    private InvoiceOrdersAdapter mAdapter;
    private SessionBillModel mBillModel;
    private BillHolder mBillHolder;

    private static final int REQUEST_PAYMENT_MODE = 141;
    SessionBillModel.PAYMENT_MODES selectedMode = SessionBillModel.PAYMENT_MODES.CASH;


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
                        if (statusModelResource.data.isCheckout()) {
                            Utils.navigateBackToHome(getApplicationContext());
                        } else {
                            finish();
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
        mViewModel.requestCheckout(mBillModel.getTip(), selectedMode);
    }

    @OnClick(R.id.payment_mode_change_container)
    public void onPaymentModeClick(){
        Intent intent = new Intent(this, ActiveSessionPaymentOptions.class);
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

        if (requestCode == REQUEST_PAYMENT_MODE && resultCode == RESULT_OK) {
            if (data != null) {
                selectedMode = (SessionBillModel.PAYMENT_MODES) data.getSerializableExtra(KEY_PAYMENT_MODE_RESULT);
            }
        }
    }
}
