package com.checkin.app.checkin.Session.ActiveSession;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.Misc.BillHolder;
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
    @BindView(R.id.tv_invoice_tip)
    TextView tvInvoiceTip;
    @BindView(R.id.tv_invoice_total)
    TextView tvInvoiceTotal;
    @BindView(R.id.ed_invoice_tip)
    EditText edInvoiceTip;

    private ActiveSessionViewModel mViewModel;
    private InvoiceOrdersAdapter mAdapter;
    private BillHolder mBillHolder;

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

        mViewModel = ViewModelProviders.of(this).get(ActiveSessionViewModel.class);
        mViewModel.fetchSessionInvoice();
        mViewModel.getSessionInvoice().observe(this, resource -> {
            if (resource == null)
                return;
            if (resource.status == Resource.Status.SUCCESS && resource.data != null) {
                setupUi(resource.data);
            }
        });

        mViewModel.getObservableData().observe(this, objectNodeResource -> {
            if (objectNodeResource == null)
                return;
            switch (objectNodeResource.status) {
                case SUCCESS: {
                    Utils.toast(this, "Done!");
                    break;
                }
                case LOADING:
                    break;
                default: {
                    Utils.toast(this, objectNodeResource.message);
                }
            }
        });
    }

    private void setupUi(SessionInvoiceModel data) {
        mAdapter.setData(data.getOrderedItems());

        if (data.getHost() != null)
            Utils.loadImageOrDefault(imWaiterPic, data.getHost().getDisplayPic(), R.drawable.ic_waiter);

        mBillHolder = new BillHolder(findViewById(android.R.id.content));
        mBillHolder.bind(data.getBill());

        edInvoiceTip.setText(data.getBill().formatTip());
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
