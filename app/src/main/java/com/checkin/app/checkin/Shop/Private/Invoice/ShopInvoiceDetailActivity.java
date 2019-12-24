package com.checkin.app.checkin.Shop.Private.Invoice;

import android.os.Bundle;
import android.widget.TextView;

import com.checkin.app.checkin.misc.models.BriefModel;
import com.checkin.app.checkin.R;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.tabs.TabLayout;

import java.util.Locale;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ShopInvoiceDetailActivity extends AppCompatActivity {
    public static final String KEY_SESSION_DATA = "shop.invoice_session";

    @BindView(R.id.tv_invoice_session_id)
    TextView tvSessionId;
    @BindView(R.id.tv_invoice_session_date)
    TextView tvDate;
    @BindView(R.id.tv_invoice_session_item_count)
    TextView tvItemCount;
    @BindView(R.id.tv_invoice_session_member_count)
    TextView tvMemberCount;
    @BindView(R.id.tv_invoice_session_waiter)
    TextView tvWaiter;
    @BindView(R.id.tv_invoice_session_table)
    TextView tvTable;
    @BindView(R.id.appbar_shop_invoice)
    AppBarLayout appBarLayout;
    @BindView(R.id.tabs_shop_invoice)
    TabLayout tabLayout;
    @BindView(R.id.pager_shop_invoice)
    ViewPager pagerInvoice;

    private ShopSessionViewModel mViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_invoice_detail);
        ButterKnife.bind(this);

        RestaurantSessionModel data = (RestaurantSessionModel) getIntent().getSerializableExtra(KEY_SESSION_DATA);

        if (data != null) {
            setupAppbarDetails(data);
            setupViewPager();
            setupData(data);
        }
    }

    private void setupData(RestaurantSessionModel data) {
        mViewModel = ViewModelProviders.of(this).get(ShopSessionViewModel.class);
        mViewModel.setSessionPk(data.getPk());
    }

    private void setupAppbarDetails(RestaurantSessionModel data) {
        BriefModel host = data.getHost();
        tvWaiter.setText(String.format(Locale.ENGLISH, "Waiter : %s", host != null ? host.getDisplayName() : getResources().getString(R.string.waiter_unassigned)));

        tvSessionId.setText(data.getHashId());
        tvDate.setText(data.getFormattedDate());
        tvMemberCount.setText(String.valueOf(data.getCountCustomers()));
        tvItemCount.setText(String.format(Locale.ENGLISH, " | %d item(s)", data.getCountOrders()));
        tvTable.setText(data.getTable());
    }

    private void setupViewPager() {
        pagerInvoice.setAdapter(new InvoiceFragmentAdapter(getSupportFragmentManager()));
        tabLayout.setupWithViewPager(pagerInvoice);
    }

    @OnClick(R.id.im_shop_invoice_appbar_back)
    public void onBackClicked() {
        onBackPressed();
    }

    public static class InvoiceFragmentAdapter extends FragmentPagerAdapter {

        public InvoiceFragmentAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return ShopSessionDetailFragment.newInstance();
                case 1:
                    return ShopSessionFeedbackFragment.newInstance();
            }
            return null;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Detail";
                case 1:
                    return "Feedbacks";
            }
            return null;
        }
    }
}
