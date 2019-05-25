package com.checkin.app.checkin.session.successfultransaction;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.Misc.BaseActivity;
import com.checkin.app.checkin.R;
import com.checkin.app.checkin.Utility.Utils;
import com.checkin.app.checkin.session.model.SessionSuccessfulTransactionModel;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SuccessfulTransactionActivity extends BaseActivity {
    @BindView(R.id.tv_successful_transaction_amount)
    TextView tvAmount;
    @BindView(R.id.tv_successful_transaction_restaurant_name)
    TextView tvRestaurantName;
    @BindView(R.id.im_successful_transaction_payment_mode)
    ImageView imPaymentMode;
    @BindView(R.id.tv_successful_transaction_restaurant_saved_amount)
    TextView tvSavings;
    @BindView(R.id.tv_successful_transaction_restaurant_transaction_id)
    TextView tvTransactionId;
    @BindView(R.id.tv_successful_transaction_restaurant_transaction_date)
    TextView tvTransactionDate;

    private SuccessfulTransactionViewModel mViewModel;
    private long sessionId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_transaction_successful);
        ButterKnife.bind(this);

        mViewModel = ViewModelProviders.of(this).get(SuccessfulTransactionViewModel.class);
        sessionId = getIntent().getLongExtra("sessionId", 0);
        mViewModel.fetchSessionSuccessfulTransaction(sessionId);

        mViewModel.getUserSessionBriefData().observe(this, sessionBriefModelResource -> {
            if (sessionBriefModelResource == null)
                return;
            if (sessionBriefModelResource.status == Resource.Status.SUCCESS && sessionBriefModelResource.data != null) {
                setupData(sessionBriefModelResource.data);
            }
        });


    }

    private void setupData(SessionSuccessfulTransactionModel data) {
        tvAmount.setText(Utils.formatCurrencyAmount(this, data.getTotal()));
        tvRestaurantName.setText(Utils.formatCurrencyAmount(this, data.getRestaurant().getDisplayName()));
        imPaymentMode.setImageDrawable(getResources().getDrawable(SessionSuccessfulTransactionModel.getPaymentModeIcon(data.getPaymentMode())));
        tvSavings.setText("You've saved"+ Utils.formatCurrencyAmount(this, data.getSavings()));
        tvTransactionId.setText(data.getTransactionId());
        tvTransactionDate.setText(data.getFormattedDate());

    }

    @OnClick(R.id.im_payment_successful_finish)
    public void onClickDismiss(){
        Utils.navigateBackToHome(this);
    }

    @Override
    public void onBackPressed() {
        onClickDismiss();
    }

    @OnClick(R.id.ll_successful_transaction_view_transactions)
    public void onViewDetails(){
        startActivity(new Intent(this, SuccessfulTransactionDetailActivity.class).putExtra("sessionId",sessionId));
    }
}
