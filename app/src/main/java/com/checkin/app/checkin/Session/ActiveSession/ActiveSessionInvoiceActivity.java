package com.checkin.app.checkin.Session.ActiveSession;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.R;
import com.checkin.app.checkin.Session.Model.SessionInvoiceModel;
import com.checkin.app.checkin.Shop.ShopModel;
import com.checkin.app.checkin.Utility.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;

public class ActiveSessionInvoiceActivity extends AppCompatActivity {
    public static final String KEY_SESSION_PK = "invoice.session_pk";

    @BindView(R.id.rv_invoice_ordered_items)
    RecyclerView rvOrderedItems;
    @BindView(R.id.im_invoice_waiter)
    ImageView imWaiterPic;
    @BindView(R.id.tv_invoice_subtotal)
    TextView tvInvoiceSubtotal;
    @BindView(R.id.tv_invoice_tax)
    TextView tvInvoiceTax;
    @BindView(R.id.tv_invoice_discount)
    TextView tvInvoiceDiscount;
    @BindView(R.id.tv_invoice_tip)
    TextView tvInvoiceTip;
    @BindView(R.id.tv_invoice_promo)
    TextView tvInvoicePromo;
    @BindView(R.id.tv_invoice_total)
    TextView tvInvoiceTotal;
    @BindView(R.id.ed_invoice_tip)
    EditText edInvoiceTip;
    @BindView(R.id.container_invoice_tax)
    ViewGroup containerInvoiceTax;
    @BindView(R.id.container_invoice_promo)
    ViewGroup containerInvoicePromo;
    @BindView(R.id.container_invoice_discount)
    ViewGroup containerInvoiceDiscount;

    private ActiveSessionViewModel mViewModel;
    private InvoiceOrdersAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_active_session_invoice);
        ButterKnife.bind(this);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        rvOrderedItems.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mAdapter = new InvoiceOrdersAdapter(null);
        rvOrderedItems.setAdapter(mAdapter);

        int sessionPk = getIntent().getIntExtra(KEY_SESSION_PK, -1);
        if (sessionPk == -1)
            throw new IllegalArgumentException("No session PK passed.");

        mViewModel = ViewModelProviders.of(this).get(ActiveSessionViewModel.class);
        mViewModel.setSessionPk(sessionPk);
        mViewModel.fetchSessionInvoice();
        mViewModel.getSessionInvoice().observe(this, resource -> {
            if (resource == null)
                return;
            if (resource.status == Resource.Status.SUCCESS && resource.data != null) {
                setupUi(resource.data);
            }
        });
    }

    private void setupUi(SessionInvoiceModel data) {
        mAdapter.setData(data.getOrderedItems());

        if (data.getHost() != null)
            Utils.loadImageOrDefault(imWaiterPic, data.getHost().getDisplayPic(), R.drawable.ic_waiter);

        // Subtotal
        if (data.getBill().getSubtotal() != null)
            tvInvoiceSubtotal.setText(Utils.formatCurrencyAmount(this, data.getBill().getSubtotal()));
        // Tax
        if (data.getBill().getTax() != null)
            tvInvoiceTax.setText(Utils.formatCurrencyAmount(this, data.getBill().getTax()));
        else
            containerInvoiceTax.setVisibility(View.GONE);
        // Promo
        if (data.getBill().getOffers() != null)
            tvInvoicePromo.setText(Utils.formatCurrencyAmount(this, data.getBill().getOffers()));
        else
            containerInvoicePromo.setVisibility(View.GONE);
        // Discount
        if (data.getBill().getDiscount() != null)
            tvInvoiceDiscount.setText(Utils.formatCurrencyAmount(this, data.getBill().getDiscount()));
        else
            containerInvoiceDiscount.setVisibility(View.GONE);
        // Tip
        tvInvoiceTip.setText(Utils.formatCurrencyAmount(this, data.getBill().getTip()));
        edInvoiceTip.setText(data.getBill().getTip());
        // Total
        tvInvoiceTotal.setText(Utils.formatCurrencyAmount(this, data.getBill().getTotal()));
    }

    @OnTextChanged(value = R.id.ed_invoice_tip, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void onTipChange(Editable editable) {
        Double amount = 0d;
        try {
            amount = Double.valueOf(editable.toString());
        } catch (NumberFormatException ignored) {
        }
        tvInvoiceTip.setText(Utils.formatCurrencyAmount(this, amount));
    }

    @OnClick(R.id.btn_invoice_request_checkout)
    public void onRequestCheckout() {
        double tip;
        try {
            tip = Double.valueOf(edInvoiceTip.getText().toString());
        } catch (NumberFormatException ignored) {
            Utils.toast(this, "Provide valid tip amount!");
            return;
        }
        mViewModel.requestCheckout(tip, ShopModel.PAYMENT_MODE.CASH);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
