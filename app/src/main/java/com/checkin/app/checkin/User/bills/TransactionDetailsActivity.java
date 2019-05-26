package com.checkin.app.checkin.User.bills;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.Misc.BillHolder;
import com.checkin.app.checkin.Misc.BriefModel;
import com.checkin.app.checkin.R;
import com.checkin.app.checkin.Shop.Private.Invoice.RestaurantSessionModel;
import com.checkin.app.checkin.Shop.Private.Invoice.ShopSessionViewModel;
import com.checkin.app.checkin.Shop.ShopModel;
import com.checkin.app.checkin.Utility.Utils;
import com.checkin.app.checkin.session.activesession.InvoiceOrdersAdapter;
import com.google.android.material.appbar.AppBarLayout;

import java.util.Locale;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class TransactionDetailsActivity extends AppCompatActivity {
    public static final String KEY_SESSION_DATA = "shop.invoice_session";

    @BindView(R.id.tv_user_transaction_session_id)
    TextView tvSessionId;
    @BindView(R.id.tv_user_transaction_session_date)
    TextView tvDate;
    @BindView(R.id.tv_user_transaction_session_item_count)
    TextView tvItemCount;
    @BindView(R.id.tv_user_transaction_session_total_time)
    TextView tvTotalTime;
    @BindView(R.id.tv_user_transaction_session_waiter)
    TextView tvWaiter;
    @BindView(R.id.appbar_user_transaction_details)
    AppBarLayout appBarLayout;
    @BindView(R.id.rv_user_transaction_session_orders)
    RecyclerView rvSessionOrders;
    @BindView(R.id.tv_user_transaction_session_bill_total)
    TextView tvBillTotal;
    @BindView(R.id.tv_user_transaction_session_paid_via)
    TextView tvPaidVia;

    private UserTransactionsViewModel mViewModel;
    private Unbinder unbinder;
    private BillHolder mBillHolder;
    private InvoiceOrdersAdapter mOrdersAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_transaction_details);
        ButterKnife.bind(this);

        setupUI();
        getData();
    }

    private void setupUI(){
        mOrdersAdapter = new InvoiceOrdersAdapter(null);
        rvSessionOrders.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        rvSessionOrders.setAdapter(mOrdersAdapter);
        mBillHolder = new BillHolder(findViewById(android.R.id.content));
    }

    private void getData() {
        mViewModel = ViewModelProviders.of(this).get(UserTransactionsViewModel.class);
        mViewModel.fetchUserSessionDetail(getIntent().getLongExtra("sessionId", 0));
        mViewModel.getUserSessionDetail().observe(this, shopSessionDetailModelResource -> {
            if (shopSessionDetailModelResource == null)
                return;
            if (shopSessionDetailModelResource.status == Resource.Status.SUCCESS && shopSessionDetailModelResource.data != null) {
                setupDetails(shopSessionDetailModelResource.data);
            }
        });
    }

    private void setupDetails(UserTransactionDetailsModel data) {
        BriefModel host = data.getHost();
        tvWaiter.setText(String.format(Locale.ENGLISH, "Waiter : %s", host != null ? host.getDisplayName() : getResources().getString(R.string.waiter_unassigned)));

        tvSessionId.setText(data.getHashId());
        tvDate.setText(data.getFormattedDate());
        tvItemCount.setText(String.format(Locale.ENGLISH, " | %d item(s)", data.getCountOrders()));
        tvTotalTime.setText(data.formatTotalTime());
        mOrdersAdapter.setData(data.getOrderedItems());
        mBillHolder.bind(data.getBill());
        tvBillTotal.setText(String.format(Locale.ENGLISH, Utils.getCurrencyFormat(this), data.getBill().getTotal()));
        if (data.getPaymentMode() != null)
            tvPaidVia.setCompoundDrawablesWithIntrinsicBounds(0, 0, ShopModel.getPaymentModeIcon(data.getPaymentMode()), 0);

    }
}
