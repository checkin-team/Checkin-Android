package com.checkin.app.checkin.Manager.Fragment;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.Misc.BaseFragment;
import com.checkin.app.checkin.R;
import com.checkin.app.checkin.Shop.Private.Invoice.RestaurantSessionModel;
import com.checkin.app.checkin.Shop.Private.Invoice.ShopInvoiceDetailActivity;
import com.checkin.app.checkin.Shop.Private.Invoice.ShopInvoiceSessionAdapter;
import com.checkin.app.checkin.Shop.Private.Invoice.ShopInvoiceViewModel;
import com.checkin.app.checkin.Utility.Utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.OnClick;

public class ManagerInvoiceFragment extends BaseFragment implements ShopInvoiceSessionAdapter.ShopInvoiceInteraction  {

    @BindView(R.id.tv_shop_invoice_filter_from)
    TextView tvFilterFrom;
    @BindView(R.id.tv_shop_invoice_filter_to)
    TextView tvFilterTo;
    @BindView(R.id.rv_shop_invoice_sessions)
    RecyclerView rvSessions;

    private ShopInvoiceSessionAdapter mAdapter;
    private ShopInvoiceViewModel mViewModel;

    public ManagerInvoiceFragment() {
    }

    public static ManagerInvoiceFragment newInstance() {
        return  new ManagerInvoiceFragment();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        setupUi();
        mViewModel = ViewModelProviders.of(requireActivity()).get(ShopInvoiceViewModel.class);

        mViewModel.getRestaurantSessions().observe(this, input -> {
            if (input == null)
                return;
            if (input.status == Resource.Status.SUCCESS && input.data != null) {
                mAdapter.setSessionData(input.data);
            } else if (input.status != Resource.Status.LOADING) {
                Utils.toast(requireContext(), input.message);
            }
        });

    }

    private void setupUi() {
        rvSessions.setLayoutManager(new LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false));
        mAdapter = new ShopInvoiceSessionAdapter(this);
        rvSessions.setAdapter(mAdapter);

        tvFilterFrom.setText(Utils.getCurrentFormattedDate());
        tvFilterTo.setText(Utils.getCurrentFormattedDate());
    }

    @Override
    protected int getRootLayout() {
        return R.layout.activity_shop_invoice_list;
    }

    private String updateDate(TextView tvDate, int year, int month, int day) {
        String initialDate = String.format(Locale.ENGLISH, "%04d-%02d-%02d", year, month, day);
        try {
            String result = Utils.formatDate(initialDate, "yyyy-MM-dd", "MMM dd, yyyy");
            tvDate.setText(result);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return initialDate;
    }

    @OnClick({R.id.tv_shop_invoice_filter_from, R.id.tv_shop_invoice_filter_to})
    public void onClick(TextView tv) {
        Calendar cal = Calendar.getInstance();
        try {
            Date date = new SimpleDateFormat("MMM dd, yyyy", Locale.ENGLISH).parse(tv.getText().toString());
            cal.setTime(date);
        } catch (ParseException e) {
        }
        new DatePickerDialog(requireContext(), (view, year, month, dayOfMonth) -> {
            String resultDate = updateDate(tv, year, month + 1, dayOfMonth);
            if (tv.getId() == R.id.tv_shop_invoice_filter_from)
                mViewModel.filterFrom(resultDate);
            else
                mViewModel.filterTo(resultDate);
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show();
    }

    @Override
    public void onClickSession(RestaurantSessionModel data) {
        Intent intent = new Intent(requireContext(), ShopInvoiceDetailActivity.class);
        intent.putExtra(ShopInvoiceDetailActivity.KEY_SESSION_DATA, data);
        startActivity(intent);
    }
}
