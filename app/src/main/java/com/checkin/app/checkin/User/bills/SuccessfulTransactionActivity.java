package com.checkin.app.checkin.User.bills;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;

import com.checkin.app.checkin.data.resource.Resource;
import com.checkin.app.checkin.misc.activities.BaseActivity;
import com.checkin.app.checkin.R;
import com.checkin.app.checkin.Utility.Constants;
import com.checkin.app.checkin.Utility.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SuccessfulTransactionActivity extends BaseActivity {
    public static final String KEY_SESSION_ID = "user.session_id";

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
    @BindView(R.id.container_user_end_feedback_take)
    ViewGroup containerFeedbackTake;
    @BindView(R.id.container_user_end_feedback_show)
    ViewGroup containerFeedbackShow;
    @BindView(R.id.im_emoji_angry_1)
    ImageView imAngry;
    @BindView(R.id.im_emoji_sad_2)
    ImageView imSad;
    @BindView(R.id.im_emoji_confused_3)
    ImageView imConfused;
    @BindView(R.id.im_emoji_smile_4)
    ImageView imSmile;
    @BindView(R.id.im_emoji_love_5)
    ImageView imLove;
    @BindView(R.id.im_feedback_show_emoji)
    ImageView imFeedbackShowEmoji;
    @BindView(R.id.tv_feedback_text)
    TextView tvFeedbacktext;

    private UserTransactionsViewModel mViewModel;
    private long sessionId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_user_session_end);
        ButterKnife.bind(this);
        takeFeedback();

        mViewModel = ViewModelProviders.of(this).get(UserTransactionsViewModel.class);
        sessionId = getIntent().getLongExtra(KEY_SESSION_ID, 0L);
        mViewModel.fetchSessionSuccessfulTransaction(sessionId);

        mViewModel.getUserSessionBriefData().observe(this, sessionBriefModelResource -> {
            if (sessionBriefModelResource == null)
                return;
            if (sessionBriefModelResource.getStatus() == Resource.Status.SUCCESS && sessionBriefModelResource.getData() != null) {
                setupData(sessionBriefModelResource.getData());
            }
        });

        mViewModel.getUserReviewDetail().observe(this, objectNodeResource -> {
            if (objectNodeResource == null)
                return;
            if (objectNodeResource.getStatus() == Resource.Status.SUCCESS && objectNodeResource.getData() != null) {
            }
        });
    }

    private void setupData(UserTransactionBriefModel data) {
        tvAmount.setText(Utils.formatCurrencyAmount(this, data.getTotal()));
        tvRestaurantName.setText(data.getRestaurant().getDisplayName());
        imPaymentMode.setImageDrawable(getResources().getDrawable(UserTransactionBriefModel.getPaymentModeIcon(data.getPaymentMode())));
        if(data.getSavings() > 0)
        tvSavings.setText(String.format("You've saved %s!", Utils.formatCurrencyAmount(this, data.getSavings())));
        tvTransactionId.setText(data.getTransactionId());
        tvTransactionDate.setText(data.getFormattedDate());

    }

    @OnClick(R.id.im_payment_successful_finish)
    public void onClickDismiss() {
        Utils.navigateBackToHome(this);
    }

    @OnClick(R.id.tv_se_rate_checkin)
    public void onClickRateCheckin() {
        startActivity(new Intent(Intent.ACTION_VIEW, Constants.INSTANCE.getPLAY_STORE_URI()));
    }

    @Override
    public void onBackPressed() {
        onClickDismiss();
    }

    @OnClick(R.id.ll_successful_transaction_view_transactions)
    public void onViewDetails() {
        startActivity(new Intent(this, TransactionDetailsActivity.class).putExtra(TransactionDetailsActivity.KEY_SESSION_ID, sessionId));
    }

    @OnClick({R.id.im_emoji_angry_1, R.id.im_emoji_sad_2, R.id.im_emoji_confused_3, R.id.im_emoji_smile_4, R.id.im_emoji_love_5})
    public void onLove(View v) {
        switch (v.getId()) {
            case R.id.im_emoji_angry_1:
                showFeedback(1);
                break;
            case R.id.im_emoji_sad_2:
                showFeedback(2);
                break;
            case R.id.im_emoji_confused_3:
                showFeedback(3);
                break;
            case R.id.im_emoji_smile_4:
                showFeedback(4);
                break;
            case R.id.im_emoji_love_5:
                showFeedback(5);
                break;
        }
    }

    public void showFeedback(int rate) {
        containerFeedbackTake.setVisibility(View.GONE);
        imFeedbackShowEmoji.setImageDrawable(getResources().getDrawable(UserTransactionBriefModel.getFeedbackEmoji(rate)));
        imFeedbackShowEmoji.startAnimation(AnimationUtils.loadAnimation(this, R.anim.expand_in));
        tvFeedbacktext.setText(UserTransactionBriefModel.getFeedbackText(rate));
        tvFeedbacktext.startAnimation(AnimationUtils.loadAnimation(this, R.anim.expand_in));
        containerFeedbackShow.setVisibility(View.VISIBLE);
        mViewModel.submitReview(rate);
    }

    public void takeFeedback() {
        containerFeedbackShow.setVisibility(View.GONE);
        containerFeedbackTake.setVisibility(View.VISIBLE);
    }
}
