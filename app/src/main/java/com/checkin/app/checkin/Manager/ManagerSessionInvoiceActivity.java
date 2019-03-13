package com.checkin.app.checkin.Manager;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.Manager.Model.ManagerSessionInvoiceModel;
import com.checkin.app.checkin.Misc.BillHolder;
import com.checkin.app.checkin.R;
import com.checkin.app.checkin.Session.ActiveSession.InvoiceOrdersAdapter;
import com.checkin.app.checkin.Session.Model.SessionBillModel;
import com.checkin.app.checkin.Utility.Utils;
import com.checkin.app.checkin.Waiter.Model.SessionContactModel;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ManagerSessionInvoiceActivity extends AppCompatActivity {
    public static final String KEY_SESSION = "com.checkin.app.checkin.Manager.key.session";
    public static final String TABLE_NAME = "com.checkin.app.checkin.Manager.table.name";
    public static final String IS_REQUESTED_CHECKOUT = "com.checkin.app.checkin.Manager.session.is.requested.checkout";

    @BindView(R.id.rv_ms_invoice_ordered_items)
    RecyclerView rvOrderedItems;
    @BindView(R.id.tv_invoice_discount_title)
    TextView tvInvoiceDiscountTitle;
    @BindView(R.id.ed_ms_invoice_discount)
    EditText edInvoiceDiscount;
    @BindView(R.id.tv_ms_invoice_change)
    TextView tvInvoiceChange;
    @BindView(R.id.btn_ms_invoice_save_change)
    Button btnSaveChange;
    @BindView(R.id.tv_ms_invoice_total)
    TextView tvInvoiceTotal;
    @BindView(R.id.tv_invoice_discount)
    TextView tvInvoiceDiscount;
    @BindView(R.id.ll_request_checkout_session_invoice)
    LinearLayout llRequestedCheckoutView;
    @BindView(R.id.ed_ms_invoice_contact)
    EditText edMsInvoiceContact;
    @BindView(R.id.tv_ms_invoice_contact_change)
    TextView tvMsInvoiceContactChange;
    @BindView(R.id.btn_ms_invoice_contact_save_change)
    Button btnMsInvoiceContactSaveChange;

    private ManagerSessionViewModel mViewModel;
    private InvoiceOrdersAdapter mAdapter;
    private SessionBillModel mBillModel;
    private BillHolder mBillHolder;
    private boolean isRequestedCheckout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_session_invoice);
        ButterKnife.bind(this);

        Intent intent = getIntent();

        long keySession = intent.getLongExtra(KEY_SESSION, 0L);
        String tableName = intent.getStringExtra(TABLE_NAME);

        updateRequestCheckoutStatus(intent.getBooleanExtra(IS_REQUESTED_CHECKOUT, false));

        mViewModel = ViewModelProviders.of(this).get(ManagerSessionViewModel.class);

        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setTitle(tableName);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_back_grey);
        }

        rvOrderedItems.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        mAdapter = new InvoiceOrdersAdapter(null);
        rvOrderedItems.setAdapter(mAdapter);

        mViewModel.getSessionInvoice(keySession).observe(this, resource -> {
            if (resource == null)
                return;
            if (resource.status == Resource.Status.SUCCESS && resource.data != null) {
                setupData(resource.data);
            }
        });
        mViewModel.getDetailData().observe(this, resource -> {
            if (resource == null)
                return;
            if (resource.status == Resource.Status.SUCCESS && resource.data != null) {
                Utils.toast(this, resource.data.getDetail());
            } else if (resource.status != Resource.Status.LOADING) {
                Utils.toast(this, resource.message);
            }
        });
        mViewModel.getCheckoutData().observe(ManagerSessionInvoiceActivity.this, input -> {
            if (input == null)
                return;
            if (input.status == Resource.Status.SUCCESS && input.data != null) {
                Utils.toast(ManagerSessionInvoiceActivity.this, input.data.getMessage());
                if (input.data.isCheckout()) finish();
                else updateRequestCheckoutStatus(true);
            } else if (input.status != Resource.Status.LOADING) {
                Utils.toast(this, input.message);
            }
        });

        mViewModel.fetchSessionContacts();

        mViewModel.getSessionContactListData().observe(this, input -> {
            if (input == null)
                return;
            if (input.status == Resource.Status.SUCCESS && input.data != null) {
                if (input.data.size() > 0) {
                    setupContactData(input.data.get(input.data.size() - 1));
                }
            } else if (input.status != Resource.Status.LOADING && input.message != null)
                Utils.toast(this, input.message);
        });

        mViewModel.getObservableData().observe(this, input -> {
            if (input == null)
                return;
            if (input.status == Resource.Status.SUCCESS && input.data != null) {
                Utils.toast(this, "Contact updated successfully.");
            } else if (input.status != Resource.Status.LOADING && input.message != null)
                Utils.toast(this, input.message);
        });

        mBillHolder = new BillHolder(findViewById(android.R.id.content));
    }

    private void setupContactData(SessionContactModel sessionContactModel) {
        String email = sessionContactModel.getEmail();
        String phone = sessionContactModel.getPhone();

        if (phone != null)
            edMsInvoiceContact.setText(phone);
        else if (email != null)
            edMsInvoiceContact.setText(email);

    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @OnClick({R.id.tv_ms_invoice_change, R.id.btn_ms_invoice_save_change, R.id.btn_ms_invoice_collect_cash, R.id.tv_ms_invoice_contact_change, R.id.btn_ms_invoice_contact_save_change})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_ms_invoice_change:
                setUpUi("Update Discount", true, R.drawable.bordered_card_white, View.GONE, View.VISIBLE);
                break;
            case R.id.btn_ms_invoice_save_change:
                setUpUi("Discount", false, R.drawable.bordered_text_light_grey, View.VISIBLE, View.GONE);
                updateDiscount();
                break;
            case R.id.btn_ms_invoice_collect_cash:
                alertDialogForCloseSession();
                break;
            case R.id.tv_ms_invoice_contact_change:
                setUpContactUi(true,  R.drawable.bordered_card_white, View.GONE, View.VISIBLE);
                break;
            case R.id.btn_ms_invoice_contact_save_change:
                setUpContactUi(false,  R.drawable.bordered_text_light_grey, View.VISIBLE, View.GONE);
                saveContact();
                break;
        }
    }

    private void saveContact() {
        String contact = edMsInvoiceContact.getText().toString();

        if (!TextUtils.isEmpty(contact) && Patterns.PHONE.matcher(contact).matches())
            mViewModel.postSessionContact(null, contact);
        else if (!TextUtils.isEmpty(contact) && Patterns.EMAIL_ADDRESS.matcher(contact).matches()) {
            mViewModel.postSessionContact(contact, null);
        } else {
            Utils.toast(this, "Please enter at least phone number or email.");
        }
    }

    private void setUpContactUi(boolean enableOrDisabled, int drawable, int visibilityChange, int visibilitySave) {
        edMsInvoiceContact.setEnabled(enableOrDisabled);
        edMsInvoiceContact.setBackground(ContextCompat.getDrawable(this, drawable));
        tvMsInvoiceContactChange.setVisibility(visibilityChange);
        btnMsInvoiceContactSaveChange.setVisibility(visibilitySave);
    }

    private void updateRequestCheckoutStatus(boolean isRequestedCheckout) {
        this.isRequestedCheckout = isRequestedCheckout;
        if (isRequestedCheckout) {
            llRequestedCheckoutView.setVisibility(View.VISIBLE);
            tvInvoiceChange.setVisibility(View.GONE);
        } else {
            llRequestedCheckoutView.setVisibility(View.GONE);
            tvInvoiceChange.setVisibility(View.VISIBLE);
        }
    }

    private void alertDialogForCloseSession() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this).setTitle("Are you sure you want to close session?")
                .setPositiveButton("Close session", (dialog, which) -> mViewModel.putSessionCheckout())
                .setNegativeButton("No", (dialog, which) -> dialog.cancel());
        if (!isRequestedCheckout) {
            builder.setNeutralButton("Notify waiter", ((dialogInterface, i) -> mViewModel.requestSessionCheckout()));
        }
        builder.show();
    }

    private void updateDiscount() {
        double percent = 0d;
        try {
            percent = Double.parseDouble(edInvoiceDiscount.getText().toString());
        } catch (NumberFormatException ignored) {
        }
        mBillModel.calculateDiscount(percent);
        mViewModel.updateDiscount(percent);
        tvInvoiceDiscount.setText(Utils.formatCurrencyAmount(this, mBillModel.getDiscount()));
        tvInvoiceTotal.setText(Utils.formatCurrencyAmount(this, mBillModel.getTotal()));
    }

    private void setupData(ManagerSessionInvoiceModel data) {
        mBillModel = data.getBill();

        mBillModel.setDiscountPercentage(data.getDiscountPercent());
        mAdapter.setData(data.getOrderedItems());
        edInvoiceDiscount.setText(data.formatDiscountPercent());
        mBillHolder.bind(data.getBill());
        tvInvoiceTotal.setText(Utils.formatCurrencyAmount(this, data.getBill().getTotal()));

        setUpUi("Discount", false, R.drawable.bordered_text_light_grey, View.VISIBLE, View.GONE);
    }

    private void setUpUi(String title, boolean enableOrDisabled, int drawable, int visibilityChange, int visibilitySave) {
        tvInvoiceDiscountTitle.setText(title);
        edInvoiceDiscount.setEnabled(enableOrDisabled);
        edInvoiceDiscount.setBackground(ContextCompat.getDrawable(this, drawable));
        tvInvoiceChange.setVisibility(visibilityChange);
        btnSaveChange.setVisibility(visibilitySave);
    }
}
